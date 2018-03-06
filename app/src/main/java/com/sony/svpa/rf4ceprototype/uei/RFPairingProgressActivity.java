/*
 * Universal Electronics Inc.
 * Copyright 1999-2018 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype.uei;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.uei.quickset.DevicePairingListener;
import com.uei.quickset.DevicePairingManager;
import com.uei.quickset.QuickSet;
import com.uei.quickset.SetupDetails;
import com.uei.quickset.type.data.Status;


public class RFPairingProgressActivity extends Activity implements PairingUserPromptCallback {
    private static final String TAG = RFPairingProgressActivity.class.getSimpleName();
    private static final String DEVICE_ID_KEY = "deviceId";

    private static final int MSG_UPDATE_INSTRUCTIONS = 1;
    private static final int MSG_PROMPT_TO_PAIR = 2;
    private static final int MSG_PAIR = 3;

    private Handler mEventUIHandler;

    @Override
    public void proceedWithInitialization() {

    }

    @Override
    public void proceedWithPairing(final String deviceId) {
        pair(deviceId);
    }

    @Override
    public void stopPairing() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rf_pairing_progress);

        final Intent intent = getIntent();
        final String deviceId = intent.getStringExtra(DEVICE_ID_KEY);

        final QuickSet quickSet = QuickSet.getInstance();
        final DevicePairingManager pairingMgr = (DevicePairingManager) quickSet.getManager(QuickSet.DEVICE_PAIRING_MGR);

        mEventUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                final ProgressBar progressBar = findViewById(R.id.pairingProgressBar);
                final TextView pairingInstructionsTextView = findViewById(R.id.pairingInstructionsTextView);
                progressBar.setVisibility(View.INVISIBLE);

                switch (msg.what) {
                    case MSG_UPDATE_INSTRUCTIONS:
                        pairingInstructionsTextView.setText((String) msg.obj);
                        break;
                    case MSG_PROMPT_TO_PAIR:
                        RFStartPairingDialogFragment startPairingFragment = new RFStartPairingDialogFragment();
                        startPairingFragment.setPairingUserPromptCallback(RFPairingProgressActivity.this);
                        final Bundle bundle = new Bundle();
                        bundle.putString("deviceId", (String) msg.obj);
                        startPairingFragment.setArguments(bundle);
                        startPairingFragment.show(getFragmentManager(), "RF Pairing");
                        break;
                    case MSG_PAIR:
                        pairingMgr.pair((String) msg.obj, null);
                        break;
                }
            }
        };

        pairingMgr.register(new DevicePairingListener() {
            @Override
            public void onPairableFound(final String rid, final String deviceId) {
                // TODO: Determine what to do if another RF device is found while on this screen
                // Do nothing. This activity is visible after the user asked to pair. No
                // additional RF devices are handled currently
            }

            @Override
            public void onInitialized(final String rid, final String deviceId, final SetupDetails setupDetails) {
                Log.d(TAG, "Received initialization message");
                updateInstructions(setupDetails.instructions);
                promptToPair(deviceId);
            }

            @Override
            public void onPaired(final String rid) {
                Log.d(TAG, "Received pairing complete message");
                updateInstructions(getResources().getString(R.string.pairing_completed_successfully));
            }

            @Override
            public void onFailed(final String rid, final Status status) {
                Log.d(TAG, "Received pairing failed message");
                updateInstructions(getResources().getString(R.string.pairing_failed));
            }
        });

        final Status status = pairingMgr.initialize(deviceId, null);
        Log.d(TAG, "Pairing initialization status = " + status);

        if (Status.Code.SUCCESS != status.mCode) {
            updateInstructions("Failed pairing initialization");
        }

        final ProgressBar progressBar = findViewById(R.id.pairingProgressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void updateInstructions(final String instructions) {
        final Message msg = mEventUIHandler.obtainMessage(MSG_UPDATE_INSTRUCTIONS);
        msg.obj = instructions;
        msg.sendToTarget();
    }

    private void promptToPair(final String deviceId) {
        final Message msg = mEventUIHandler.obtainMessage(MSG_PROMPT_TO_PAIR);
        msg.obj = deviceId;
        msg.sendToTarget();
    }

    private void pair(final String deviceId) {
        final Message msg = mEventUIHandler.obtainMessage(MSG_PAIR);
        msg.obj = deviceId;
        msg.sendToTarget();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        // TODO: Cancel RF pairing?
        super.onBackPressed();
    }

    /**
     * Called when you are no longer visible to the user.  You will next
     * receive either {@link #onRestart}, {@link #onDestroy}, or nothing,
     * depending on later user activity.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestart
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onDestroy
     */
    @Override
    protected void onStop() {
        // TODO: Cancel RF pairing?
        super.onStop();
    }
}
