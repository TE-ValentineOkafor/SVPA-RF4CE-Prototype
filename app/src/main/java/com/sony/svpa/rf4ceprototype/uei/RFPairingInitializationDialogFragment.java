/*
 * Universal Electronics Inc.
 * Copyright 1999-2018 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype.uei;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.uei.quickset.DevicePairingManager;
import com.uei.quickset.QuickSet;


/**
 */
public class RFPairingInitializationDialogFragment extends DialogFragment {
    private static final String TAG = RFPairingInitializationDialogFragment.class.getSimpleName();
    private static final String DEVICE_ID_KEY = "deviceId";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle args = getArguments();

        final String deviceId = args.getString(DEVICE_ID_KEY, "");

        final DevicePairingManager pairingMgr = (DevicePairingManager) QuickSet.getInstance().getManager(QuickSet.DEVICE_PAIRING_MGR);

        builder.setMessage("Prepare device (" + deviceId + ") for RF pairing?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        final Intent rfPairingIntent = new Intent(getActivity(), RFPairingProgressActivity.class);
                        rfPairingIntent.putExtra(DEVICE_ID_KEY, deviceId);
                        startActivity(rfPairingIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Declined RF pairing preparation");
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
