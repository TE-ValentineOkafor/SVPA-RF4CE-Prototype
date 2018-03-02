/* 
 * Universal Electronics Inc.
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.uei.control.*;
import com.uei.encryption.helpers.CallerHelper;
import com.uei.quickset.QuickSet;
import com.uei.quickset.QuickSetCompatManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The Class FirmwareUpdateActivity.
 */
public class FirmwareUpdateActivity extends RemoteBaseActivity {

	/** The Constant DIALOG_KEY. */
	private static final int DIALOG_KEY = 0;

	/** The ISetup. */
	private ISetup _setup = null;

	/** The progress bar. */
	private ProgressBar _progressBar = null;
	
	/** The status text. */
	private TextView _txtStatus = null;
	
	/** The authenticator. */
	private CallerHelper _authenticator = new CallerHelper();

	private boolean _updatingFirmware = false;

	private AlertDialog _popDialog = null;

	private ProgressDialog _waitDialog = null;

	private boolean _isVisible = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//set to landscape mode for large screen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.firmwareupdate);
		// keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		bindRemotesList();

		this._setup = QuicksetSampleApplication.getSetup();
		this._progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
		this._progressBar.setMax(100);
		this._txtStatus = (TextView)this.findViewById(R.id.txtPercentage);
		Button btn = (Button) this.findViewById(R.id.btnSecureFlash);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				try {
					// reset status and progress bar
					_progressBar.setProgress(0);
					_txtStatus.setText(String.format("%d / 100", 0));
					
					EditText fileText = (EditText)findViewById(R.id.editFile);
					File inputFile = new File(fileText.getText().toString());
					if (inputFile.length() >= 0) {
						byte[] dataBuffer = new byte[0];
						int byteRead = 0;
						if(inputFile.length() > 0) {
							InputStream is = new FileInputStream(inputFile);
							dataBuffer = new byte[is.available()];
							byteRead = is.read(dataBuffer);
							is.close();
						}

						if(_setup != null ){
							_remoteId = getRemoteId();
							final QuickSetCompatManager compatManager = (QuickSetCompatManager) QuickSet.getInstance().getManager(QuickSet.QUICKSET_COMPAT_MGR);
							int status = _setup.updateFirmware(QuicksetSampleApplication.getSession(),
								compatManager.getAuthenticationKey(), _remoteId,
								byteRead, dataBuffer, _firmwareUpdatesCallback);

							Log.d(QuicksetSampleActivity.LOGTAG, "updateFirmware status: " + ResultCode.getString(status));
							_txtStatus.setText( "Update firmware Started: " + ResultCode.getString(status));
							if(status == ResultCode.SUCCESS) {
								_updatingFirmware = true;
							}
						}
					} else {
						Log.d(QuicksetSampleActivity.LOGTAG, "No valid input file");
					}
				}
				catch (Exception ex) {
					Log.d(QuicksetSampleActivity.LOGTAG, "updateFirmware exception: " + ex.toString());
				}
			}
		});
		
		btn = (Button) this.findViewById(R.id.btnBrowse);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FirmwareUpdateActivity.this, FilePickerActivity.class);
				// Start the file picker activity
				startActivityForResult(intent, DIALOG_KEY);
			}
		});

		btn = (Button)this.findViewById(R.id.btnCancel);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelOTAUpdate();
			}
		});
	}

	@Override
	protected void onPause() {
		//stopVoice();
		super.onPause();
		cancelOTAUpdate();
		this._isVisible = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this._isVisible = true;
	}

	/**
	 * Cancel OTA update process
	 */
	private void cancelOTAUpdate(){
		if(this._updatingFirmware) {
			try {
				if(_setup != null ){
					_remoteId = getRemoteId();
					int status = _setup.cancelUpdateFirmware(_remoteId);

					Log.d(QuicksetSampleActivity.LOGTAG, "cancelUpdateFirmware status: " + ResultCode.getString(status));
				}
			} catch (Exception ex) {
				Log.d(QuicksetSampleActivity.LOGTAG, "cancelUpdateFirmware exception: " + ex.toString());
			}
		}
	}

	/**
	 * Update Progress bar status.
	 */
	private void updateProgressBar(final int progress) {
		Log.d(QuicksetSampleActivity.LOGTAG, "updateProgressBar: " + progress);
		this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(progress >= 0 && progress <= _progressBar.getMax()) {
							_updatingFirmware = true;
							_progressBar.setProgress(progress);
							_txtStatus.setText(String.format("Firmware update in progress (%d / 100). Please do not turn off your remote!", progress));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case DIALOG_KEY:
				if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					// Get the file path
					File f = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
					EditText fileText = (EditText)findViewById(R.id.editFile);
					String fileName = f.getPath();
					// Set the file path text view
					fileText.setText(fileName);
					Log.d(QuicksetSampleActivity.LOGTAG, "Firmware file: " + fileName);
				}
			}
		}
	}

	/**
	 * Show waiting dialog.
	 */
	private void showWaitingDialog(final String message) {

		if (this._waitDialog != null) {
			stopWaitDialog();
		}
		if(this._isVisible) {
			try {
				this._waitDialog = new ProgressDialog(FirmwareUpdateActivity.this);
				this._waitDialog.setMessage(message);
				this._waitDialog.setIndeterminate(true);
				this._waitDialog.setCancelable(false);
				this._waitDialog.show();
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.e(QuicksetSampleActivity.LOGTAG, "showWaitingDialog: " + ex.toString());
			}
		}
	}

	/**
	 * Stop waiting dialog.
	 */
	private void stopWaitDialog() {
		try {
			if (FirmwareUpdateActivity.this._waitDialog != null) {
				FirmwareUpdateActivity.this._waitDialog.dismiss();
				FirmwareUpdateActivity.this._waitDialog = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(QuicksetSampleActivity.LOGTAG, "stopWaitDialog: " + ex.toString());
		}
	}

	/**
	 * Handle firmware update result
	 */
	private void handleFirmwareDownloadFinishedResult(final int resultCode) {
		Log.d(QuicksetSampleActivity.LOGTAG, "handleFirmwareDownloadFinishedResult: " + resultCode);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(resultCode == ResultCode.SUCCESS) {
						showWaitingDialog("Please wait, remote is updating new firmware...");
						_txtStatus.setText("Please wait, remote is updating new firmware...");
					} else {
						_txtStatus.setText("Firmware download failed: " + ResultCode.getString(resultCode));
					}
				}catch (Exception ex){
					Log.e(QuicksetSampleApplication.LOGTAG, "handleFirmwareUpdateResult: " + ex.toString());
				}
			}
		});
	}

	/**
	 * Handle firmware update result
	 */
	private void handleFirmwareUpdateResult(final int resultCode) {
		Log.d(QuicksetSampleActivity.LOGTAG, "handleFirmwareUpdateResult: " + resultCode);

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (_popDialog != null) {
						_popDialog.dismiss();
						_popDialog = null;
					}
					stopWaitDialog();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							FirmwareUpdateActivity.this);
					String message = "";
					if (resultCode == ResultCode.SUCCESS) {
						message = "Firmware updated successfully!";
					} else {
						message = "Firmware update failed: " + ResultCode.getString(resultCode);
					}
					// update status message
					_txtStatus.setText(message);
					_updatingFirmware = false;
					builder.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("Firmware Update Finished")
							.setMessage(message)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
															int whichButton) {
											dialog.dismiss();
											FirmwareUpdateActivity.this._txtStatus.setText("");
										}
									}).create();
					_popDialog = builder.create();
					_popDialog.show();
				}catch (Exception ex){
					Log.e(QuicksetSampleApplication.LOGTAG, "handleFirmwareUpdateResult: " + ex.toString());
				}
			}
		});
	}

	/**
	 * Callback to update firmware download status.
	 * 
	 * @see IFirmwareUpdateCallback
	 **/
	private IFirmwareUpdateCallback _firmwareUpdatesCallback = new IFirmwareUpdateCallback.Stub() {

		/**
	     * Report current firmware update progress. The progress is the range from 0 to 100.
	     * @param progress Current progress value.
	     */     
		@Override
		public void onUpdateProgress(int progress) {
			updateProgressBar(progress);
		}
		
		/**
		 * Report the statue of OTA download process.
		 * @param result Result code
		 * @see com.uei.control.ResultCode
		 */
		public void onDownloadFinished(int result) { handleFirmwareDownloadFinishedResult(result);}

		/**
		 * Report the firmware update result.
		 * @param result Result code
		 * @see com.uei.control.ResultCode
		 */
		@Override
		public void onUpdateFinished(int result) {
			handleFirmwareUpdateResult(result);
		}

	};
}
