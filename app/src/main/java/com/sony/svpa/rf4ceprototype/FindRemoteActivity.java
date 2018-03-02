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
import android.widget.RadioButton;

import com.uei.control.IFindRemoteCallback;
import com.uei.control.ResultCode;

/**
 * Find Remote Activity
 */
public class FindRemoteActivity extends RemoteBaseActivity {

	/** Mid Alert */
	private static final int Level_MidAlert = 1;

	/** High Alert */
	private static final int Level_HighAlert = 2;

	/** Mid alert */
	private RadioButton _rdMidAlert = null;

	/** High alert */
	private RadioButton _rdHighAlert = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set to landscape mode for large screen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.findremoteview);

		bindRemotesList();
		this._rdMidAlert = (RadioButton) this.findViewById(R.id.radioMidalert);
		this._rdHighAlert = (RadioButton) this.findViewById(R.id.radioHighAlert);
		this._rdMidAlert.setChecked(true);

		// Find Remote button handler
		Button btn = (Button)this.findViewById(R.id.btnFindRemote);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findRemote();
			}
		});

		// cancel find remote action
		btn = (Button)this.findViewById(R.id.btnCancel);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelFindRemote();
			}
		});
	}
	
	@Override
	protected void onPause() {
		cancelFindRemote();
		super.onPause();
	}

	
	@Override
	public void onBackPressed() {
		cancelFindRemote();
		super.onBackPressed();
	}

	/**
	 * Start find remote action
	 */
	private void findRemote() {
		try {
			int level = Level_HighAlert;
			if(this._rdMidAlert.isChecked()) {
				level = Level_MidAlert;
			}

			// read remote Id
			this._remoteId = getRemoteId();

			int result = QuicksetSampleApplication.getControl().findRemote(FindRemoteActivity.this._remoteId,
					level, this._findRemoteCallback);
			Log.d(QuicksetSampleActivity.LOGTAG, "Start Find Remote result = " + result);
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
	}

	/**
	 * Cancel find remote.
	 */
	private void cancelFindRemote() {
		try {
			int result = QuicksetSampleApplication.getControl().cancelFindRemote(FindRemoteActivity.this._remoteId);
			Log.d(QuicksetSampleActivity.LOGTAG, "Cancel FindRemote result = " + result);
		} catch(Exception ex) {
			Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
		}
	}

	/** Callback to receive the FindRemote command result
	 * @see IFindRemoteCallback
	 **/
	private IFindRemoteCallback _findRemoteCallback = new IFindRemoteCallback.Stub() {

		/**
		 * Called when the find remote status is updated.
		 * @param remoteId the remote ID of the findRemote status coming from.
		 * @param resultCode the Result code.
		 */
		@Override
		public void onStatusUpdated(int remoteId, int resultCode) {
			try {
				Log.d(QuicksetSampleActivity.LOGTAG, "Find Remote status: " + resultCode + " - " +
						ResultCode.getString(resultCode));
			} catch(Exception ex) {
				Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
			}
		}
	};
}
