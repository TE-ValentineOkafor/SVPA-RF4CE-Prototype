/*
 * Universal Electronics Inc.
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.uei.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uei.control.IControl;
import com.uei.control.IKeyEventCallback;
import com.uei.control.IVoiceDataCallback;
import com.uei.control.ResultCode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceSearchActivity extends RemoteBaseActivity {

	private static final String VoiceDataFile = "qs_voice";
	private static final String VoiceDataFile_Extension = ".pcm";
	// streaming voice for 30 seconds
	private static final int StreamingVoiceTime = 30000;

	private File _file = null;

	private DataOutputStream _writer = null;

	private AudioTrack _audioTrack = null;

	private Timer _timerStreamingVoice = null;

	private boolean _streamingVoice = false;

	private static int _Counter = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set to landscape mode for large screen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.voicesearchview);

		bindRemotesList();

		if(QuicksetSampleActivity.HasPermissionToAccessStorage) {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// Media is counted
				try {
					this._file = new File(Environment.getExternalStorageDirectory(), VoiceDataFile);
				} catch (Exception ex) {
					Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
				}
			}
		}

		TextView txtPath = (TextView)this.findViewById(R.id.txtAudioFile);
		txtPath.setText("Audio File: " + this._file.getAbsolutePath());

		// Find Remote button handler
		Button btn = (Button)this.findViewById(R.id.btnStarVoice);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startVoice();
			}
		});

		// cancel find remote action
		btn = (Button)this.findViewById(R.id.btnCancel);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopVoice();
			}
		});
		if(QuicksetSampleApplication.getControl() != null) {
			try {
				QuicksetSampleApplication.getControl().registerKeyEventListener(this._BLEKeyEventCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		//stopVoice();
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		stopVoice();
		super.onBackPressed();
	}

	@Override
	public void onDestroy(){
		if(QuicksetSampleApplication.getControl() != null) {
			try {
				QuicksetSampleApplication.getControl().unregisterKeyEventListener(this._BLEKeyEventCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		stopVoice();
		super.onDestroy();
	}

	@Override
	public void onResume(){
		super.onResume();
		// read remote Id
		this._remoteId = getRemoteId();
	}
	/**
	 * Start voice search to receive data from the remote
	 */
	private void startVoice() {
		try {
			int result = QuicksetSampleApplication.getControl().startVoice(VoiceSearchActivity.this._remoteId,
					 _voiceSearchCallback);
			Log.d(QuicksetSampleActivity.LOGTAG, "Start Voice result = " + result);

			if(result == ResultCode.SUCCESS) {
				closeFile();
				createNewVoiceFile();
				_streamingVoice = true;
				this._writer = new DataOutputStream(new FileOutputStream(this._file));

				if(this._audioTrack == null) {
					int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
					this._audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 16000,
							AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
							bufferSize, AudioTrack.MODE_STREAM);
				}
				this._audioTrack.play();
				// recording voice for one minute
				startTimer();

				Toast.makeText(VoiceSearchActivity.this,
						"Listening...",
						Toast.LENGTH_LONG).show();
			}  else {
				Toast.makeText(VoiceSearchActivity.this,
						"Failed to start voice...",
						Toast.LENGTH_SHORT).show();
			}
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
	}

	/**
	 * Stop voice search
	 */
	private void stopVoice() {
		try {
			int result = QuicksetSampleApplication.getControl().stopVoice(VoiceSearchActivity.this._remoteId);
			Log.d(QuicksetSampleActivity.LOGTAG, "Stop Voice result = " + result);
			if(this._audioTrack != null){
				this._audioTrack.stop();
			}
			_streamingVoice = false;
			stopTimer();
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
		closeFile();
	}

	private void createNewVoiceFile(){

		if(QuicksetSampleActivity.HasPermissionToAccessStorage) {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// Media is counted
				try {
					String fileName = String.format("%s_%d%s", VoiceDataFile, _Counter, VoiceDataFile_Extension);
					_Counter++;
					this._file = new File(Environment.getExternalStorageDirectory(), fileName);
				} catch (Exception ex) {
					Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
				}
			}
		}
	}

	/**
	 * Close output file
	 */
	private void closeFile() {
		try {
			if (this._writer != null) {
				this._writer.close();
			}
		}catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
		this._writer = null;
	}

	/**
	 * Start timer.
	 */
	private void startTimer() {
		stopTimer();
		if (this._timerStreamingVoice == null) {
			this._timerStreamingVoice = new Timer();
			this._timerStreamingVoice.schedule(new StreamingVoiceTimerTask(), StreamingVoiceTime);
		}
	}

	/**
	 * Stop timer.
	 */
	private void stopTimer() {
		if (this._timerStreamingVoice != null) {
			try {
				this._timerStreamingVoice.cancel();
				this._timerStreamingVoice.purge();
				this._timerStreamingVoice = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The Class StreamingVoiceTimerTask.
	 */
	private class StreamingVoiceTimerTask extends TimerTask {

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {

			try {
				if (_streamingVoice == true) {
					stopVoice();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** Callback when receiving new streaming voice data or when voice search is finished.
	 * @see IVoiceDataCallback
	 **/
	private IVoiceDataCallback _voiceSearchCallback = new IVoiceDataCallback.Stub() {
		/**
		 * Called when there is voice data available.
		 * @param remoteId the remote ID of the voice data coming from.
		 * @param voiceData A byte array of voice data.
		 */
		@Override
		public void onVoiceDataAvailable(int remoteId, byte[] voiceData){
			if(voiceData != null) {
				try {
					// Log.d(QuicksetSampleActivity.LOGTAG, "----- Voice: " + voiceData.length);
					if(VoiceSearchActivity.this._writer != null) {
						VoiceSearchActivity.this._writer.write(voiceData, 0, voiceData.length);
						VoiceSearchActivity.this._writer.flush();
					}
					if(_audioTrack != null) {
						_audioTrack.write(voiceData, 0, voiceData.length);
						_audioTrack.flush();
					}
				} catch(Exception ex) {
					Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
				}
			}
		}
		/**
		 * Called when voice record is completed.
		 * @param result The result code.
		 */
		@Override
		public void onFinished(int result) {
			closeFile();
			try {
				Log.d(QuicksetSampleActivity.LOGTAG, "Voice Search status: " + result + " - " +
						ResultCode.getString(result));
				if(result == ResultCode.SUCCESS) {
					VoiceSearchActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								Toast.makeText(VoiceSearchActivity.this,
										"Voice is stopped",	Toast.LENGTH_SHORT).show();
							} catch(Exception ex) {
								Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
							}
						}
					});
				}
			} catch(Exception ex) {
				Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
			}
		}
	};

	/** Callback to receive key events
	 * @see IKeyEventCallback
	 **/
	private IKeyEventCallback _BLEKeyEventCallback = new IKeyEventCallback.Stub() {
		@Override
		public void onKeyEvent(int remoteId, String address, int keyValue) {
			try {
				Log.e(QuicksetSampleActivity.LOGTAG, String.format("----- BLE Key Event: Remote Id %d, Address: %s, KeyCode = %04X",
						remoteId, address, keyValue));
				if(keyValue == 0x0221) {
					// microphone key press
					_remoteId = remoteId;
					startVoice();
				} else if(keyValue == 0 && _streamingVoice) {
					//stopVoice();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
