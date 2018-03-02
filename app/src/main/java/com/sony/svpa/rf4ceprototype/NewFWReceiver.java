package com.sony.svpa.rf4ceprototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NewFWReceiver extends BroadcastReceiver {
    public static final String BROADCASTINTENT_MESSAGE_REMOTEID = "ID";
    public static final String BROADCASTINTENT_MESSAGE_MAC = "MAC";
    public static final String BROADCASTINTENT_MESSAGE_VERSION = "Version";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int remoteId = intent.getIntExtra(BROADCASTINTENT_MESSAGE_REMOTEID, 0);
            String macAddress = intent.getStringExtra(BROADCASTINTENT_MESSAGE_MAC);
            String version = intent.getStringExtra(BROADCASTINTENT_MESSAGE_VERSION);
            String text = String.format("New Firmware available for Remote Id: %d, Address: %s, New Version: %s",
                    remoteId, macAddress, version);

            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

        } catch(Exception ex) {
            Log.e(QuicksetSampleActivity.LOGTAG, ex.toString());
        }
    }

}
