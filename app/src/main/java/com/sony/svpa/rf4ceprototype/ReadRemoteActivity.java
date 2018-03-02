/*
 * Universal Electronics Inc.
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uei.control.IRemoteDeviceInfoCallback;
import com.uei.control.RemoteDeviceInfo;

/**
 * Read Remote Device info Activity
 */
public class ReadRemoteActivity extends RemoteBaseActivity {

	private TextView _textView = null;
	private boolean _aborted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set to landscape mode for large screen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.readremoteview);

		bindRemotesList();
		_textView = (TextView)this.findViewById(R.id.txtRemoteDeviceInfo);

		// read Remote device info button handler
		Button btn = (Button)this.findViewById(R.id.btnReadRemote);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					RemoteDeviceInfo deviceInfo = QuicksetSampleApplication.getControl().getRemoteDeviceInfo(ReadRemoteActivity.this._remoteId);
					readRemoteDeviceInfo(deviceInfo);
				} catch(Exception ex) {
					Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
				}
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		_aborted = true;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		//stopVoice();
		super.onPause();
		try {
			// unregister callback
			QuicksetSampleApplication.getControl().unregisterRemoteDeviceInfoListener(_RemoteDeviceInfoCallback);
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		// read remote Id
		this._remoteId = getRemoteId();
		try {
			// unregister callback
			QuicksetSampleApplication.getControl().registerRemoteDeviceInfoListener(_RemoteDeviceInfoCallback);
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
	}
	/**
	 * Start reading remote device info
	 */
	private void readRemoteDeviceInfo(RemoteDeviceInfo deviceInfo) {
		try {
			// read remote Id
			this._remoteId = getRemoteId();
			this._textView.setText("");

			if(deviceInfo != null) {
				StringBuilder sb = new StringBuilder();

				sb.append("Battery Level: " + deviceInfo.BatteryLevel + "%");
				sb.append(System.lineSeparator());
				sb.append("Manufacture: ");
				sb.append(deviceInfo.Manufacture);
				sb.append(System.lineSeparator());

				sb.append("Software Version: ");
				sb.append(deviceInfo.SoftwareVersion);
				sb.append(System.lineSeparator());

				sb.append("Hardware Version: ");
				sb.append(deviceInfo.HardwareVersion);
				sb.append(System.lineSeparator());

				sb.append("Firmware Version: ");
				sb.append(deviceInfo.FirmwareVersion);
				sb.append(System.lineSeparator());

				sb.append("Model Number: ");
				sb.append(deviceInfo.ModelNumber);
				sb.append(System.lineSeparator());

				sb.append("Serial Number: ");
				sb.append(deviceInfo.SerialNumber);
				sb.append(System.lineSeparator());
				_textView.setText(sb.toString());
			}
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
	}

	/** Callback to receive remote device info
	 * @see IRemoteDeviceInfoCallback
	 **/
	private IRemoteDeviceInfoCallback _RemoteDeviceInfoCallback = new IRemoteDeviceInfoCallback.Stub() {
		@Override
		public void onDeviceInfoAvailable(int remoteId, RemoteDeviceInfo deviceInfo) {
			try {
				Log.e(QuicksetSampleActivity.LOGTAG, String.format("----- Remote device info: Remote Id %d",
						remoteId));
				final RemoteDeviceInfo newDeviceInfo = deviceInfo;
				ReadRemoteActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						readRemoteDeviceInfo(newDeviceInfo);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

}
