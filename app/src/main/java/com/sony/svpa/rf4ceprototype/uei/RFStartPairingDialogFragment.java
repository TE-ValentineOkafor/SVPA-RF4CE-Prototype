/*
 * Universal Electronics Inc.
 * Copyright 1999-2018 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype.uei;

/*
 * Universal Electronics Inc.
 * Copyright 1999-2018 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;


/**
 */
public class RFStartPairingDialogFragment extends DialogFragment {
    private static final String TAG = RFStartPairingDialogFragment.class.getSimpleName();
    private static final String DEVICE_ID_KEY = "deviceId";

    private PairingUserPromptCallback mCallback;

    public void setPairingUserPromptCallback(final PairingUserPromptCallback callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle args = getArguments();

        final String deviceId = args.getString(DEVICE_ID_KEY, "");

        builder.setMessage("Start RF Pairing with device (" + deviceId + ")?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        if (null != mCallback) {
                            mCallback.proceedWithPairing(deviceId);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Declined pairing");
                        if (null != mCallback) {
                            mCallback.stopPairing();
                        }
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
