/*
 * Copyright 2014 Sony Corporation
 */
package com.sony.svpa.rf4ceprototype.hotplug.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;

import java.util.List;


/**
 * Support static method for some purpose
 */
public final class Utils {
    private final static String LOG_TAG = "Utils";

    private Utils() {
    }

    /**
     * Compare 2 device list
     * @param deviceListA list a to compare
     * @param deviceListB list b to compare
     * @return
     */
    public static boolean compareDeviceList(List<DeviceInformation> deviceListA,
            List<DeviceInformation> deviceListB) {

        if (deviceListA != null && deviceListB != null) {
            int lengthA = deviceListA.size();
            int lengthB = deviceListB.size();
            if (lengthA != lengthB) {
                Log.w(LOG_TAG, "compareDeviceList false");
                return false;
            }

            if (lengthA > 0 && lengthB > 0) {
                for (int i = 0; i < lengthA; i++) {
                    DeviceInformation aItem = deviceListA.get(i);
                    boolean checkOne = false;
                    for (int j = 0; j < lengthB; j++) {
                        if (aItem.equal(deviceListB.get(j))) {
                            checkOne = true;
                            break;
                        }
                    }
                    if (checkOne == false) {
                        Log.w(LOG_TAG,  "compareDeviceList false");
                        return false;
                    }
                }
            }
        } else {
            Log.w(LOG_TAG, "compareDeviceList false");
            return false;
        }
        Log.i(LOG_TAG,  "compareDeviceList true");
        return true;
    }

    public static boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager am =
                (AccessibilityManager) context
                        .getSystemService(Context.ACCESSIBILITY_SERVICE);
        return am.isEnabled();
    }

    /**
     * Get device provisioned
     * @param context of class
     * @return 0 if QuickSetup of first boot has not completed yet
     * return 1 if QuickSetup of first boot has completed yet
     */
    public static int getDeviceProvisioned(Context context) {
        int device_provisioned = Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);
        Log.i(LOG_TAG, "getDeviceProvisioned: device_provisioned = " + device_provisioned);
        return device_provisioned;
    }

    /**
     * Show notify
     * @param context of class
     * @param msg content of notification
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
