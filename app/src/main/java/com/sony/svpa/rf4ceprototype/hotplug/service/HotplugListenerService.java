package com.sony.svpa.rf4ceprototype.hotplug.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sony.svpa.rf4ceprototype.activities.WizardActivity;
import com.sony.svpa.rf4ceprototype.events.HotPlugDetectedEvent;
import com.sony.svpa.rf4ceprototype.events.NewDeviceIdentifiedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HotplugListenerService extends Service {
    private CountDownTimer waitForCecTimer;
    private final static String TAG = "HotplugListener";
    private final int CEC_DISCOVERY_DELAY = 10000;

    public HotplugListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        EventBus.getDefault().register(this);
        waitForCecTimer = new CountDownTimer(CEC_DISCOVERY_DELAY, CEC_DISCOVERY_DELAY) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Detected device does not have CEC");
            }
        };
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HotPlugDetectedEvent event) {
        if (event.isConnected()){
            waitForCecTimer.start();
            Log.d(TAG, "Something was connected");
        } else {
            Log.d(TAG, "Something was disconnected");
        }
    }

    @Subscribe()
    public void onHotPlugEventDetected(NewDeviceIdentifiedEvent event){
        Log.d(TAG, "NewDeviceIdentifiedEvent " + " posted");
        if (event.getDeviceInformation() != null && !TextUtils.isEmpty(event.getDeviceInformation().getOsdName())){
            waitForCecTimer.cancel();
            Log.d(TAG, event.getDeviceInformation().getOsdName() + " was connected");

            Gson gson = new Gson();
            String deviceInfo = gson.toJson(event.getDeviceInformation());
            Intent intent = new Intent(getApplicationContext(), WizardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(com.sony.svpa.rf4ceprototype.utils.Constants.DEVICE_INFORMATION, deviceInfo);
            getApplicationContext().startActivity(intent);
        }
    }
}
