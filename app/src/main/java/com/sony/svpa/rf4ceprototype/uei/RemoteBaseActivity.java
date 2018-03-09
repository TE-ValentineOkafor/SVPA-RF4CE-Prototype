/*
 * Universal Electronics Inc.
 * Copyright 1999-2016 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype.uei;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.activities.MainActivity;
import com.sony.svpa.rf4ceprototype.app.QuicksetSampleApplication;
import com.uei.control.Remote;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Find Remote Activity
 */
public class RemoteBaseActivity extends Activity {

	/** Remote Id */
	protected int _remoteId = 0;

	/** The remotes list spinner */
	protected Spinner _lstRemotes = null;

	/** Current bonded Sling remotes */
	protected ArrayList<Remote> _remoteList= new ArrayList<>();

	/** The remotes adapter. */
	protected RemoteListAdapter _remotesAdapter = null;


	/** Attach handlers for bonded remote controls. */
	protected void bindRemotesList() {
		this._lstRemotes = (Spinner) findViewById(R.id.lstRemotes);
		this._remotesAdapter = new RemoteListAdapter(this, android.R.layout.simple_spinner_dropdown_item,
				this._remoteList);

		try
		{
			if(QuicksetSampleApplication.getSetup() != null) {
				Remote[] remotes = QuicksetSampleApplication.getSetup().getAllRemotes(
						MainActivity.getSingleton().getAuthenticationKey());

				if (remotes != null) {
					this._remoteList.addAll(Arrays.asList(remotes));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this._lstRemotes.setAdapter(this._remotesAdapter);
		this._lstRemotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
									   View view, int position, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {

			}
		});
	}

	/**
	 * Retrieve current selected remote Id
	 * @return
     */
	protected int getRemoteId() {
		try {
			// read remote Id
			this._remoteId = 0;
			Object item = this._lstRemotes.getSelectedItem();
			if(item != null) {
				Remote remote = (Remote)item;
				if(remote != null) {
					this._remoteId = remote.Id;
				}
			}
		} catch(Exception ex) {
			Log.e(MainActivity.LOGTAG, ex.toString());
		}
		return this._remoteId;
	}
}
