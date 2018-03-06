package com.sony.svpa.rf4ceprototype.utils;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by valokafor on 1/5/18.
 */

public class EventTimer {
    private static EventTimer INSTANCE;
    private Context context;
    private final long DELAY = 4000;
    private String deviceType;
    private String deviceName = "";
    private int rf_type = 0;


    private EventTimer(Context context) {
        this.context = context;
    }

    public static EventTimer getTimer(Context context) {
        if (INSTANCE == null) {
            // ensure application context is used to prevent leaks
            INSTANCE = new EventTimer(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public void startTimer() {
        List<String> sampleDeviceType = new ArrayList<String>(Arrays.asList(Constants.sampleDevicesTypes));
        int randomInt = new Random().nextInt(sampleDeviceType.size());
        deviceType = sampleDeviceType.get(0);


        if (deviceType.equalsIgnoreCase(Constants.DEVICE_TYPE_GAME)){
            deviceName = Constants.DEVICE_TYPE_GAME;
        } else if (deviceType.equalsIgnoreCase(Constants.DEVICE_TYPE_BLUE_RAY)){
            deviceName = Constants.DEVICE_TYPE_BLUE_RAY;
        } else if (deviceType.equalsIgnoreCase(Constants.DEVICE_TYPE_AUDIO)){
            deviceName = Constants.DEVICE_TYPE_AUDIO;
        } else {
            deviceType = Constants.DEVICE_TYPE_CABLE_BOX;
            List<String> sampleCableProviders = new ArrayList<String>(Arrays.asList(Constants.sampleSetupBoxes));
            int randomInt2 = new Random().nextInt(sampleCableProviders.size());
            deviceName = sampleCableProviders.get(randomInt2);
        }


        String finalDeviceName = deviceName;
        new Handler().postDelayed(() -> {
//            NewDeviceIdentifiedEvent event = new NewDeviceIdentifiedEvent(finalDeviceName, deviceType, 0);
//            EventBus.getDefault().post(event);
        }, DELAY);
    }

    public void startDeviceSetup(){


    }

    public void startInitialDeviceDiscovery(){
        Handler handler = new Handler();




    }




}
