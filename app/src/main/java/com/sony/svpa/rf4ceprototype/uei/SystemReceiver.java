package com.sony.svpa.rf4ceprototype.uei;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sony.svpa.rf4ceprototype.activities.MainActivity;

public class SystemReceiver extends BroadcastReceiver
{
    public static final String INTENT_FIRMWAREUPDATE = "FirmwareUpdateActivity";
    public static final String INTENT_VOICE = "com.uei.sample.VoiceActivity";
    private static final String INTENT_MAIN = "MainActivity";


    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (INTENT_FIRMWAREUPDATE.compareToIgnoreCase(action) == 0 ||
                INTENT_VOICE.compareToIgnoreCase(action) == 0) {
            try {
                Log.d(QuicksetSampleApplication.LOGTAG, "--- Start activity: " + action);
                Intent appIntent = new Intent(context, MainActivity.class);
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                appIntent.putExtra("mode", action);
                context.startActivity(appIntent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

