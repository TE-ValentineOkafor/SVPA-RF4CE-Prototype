package com.sony.svpa.rf4ceprototype.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.OpenFragmentEvent;
import com.sony.svpa.rf4ceprototype.fragments.EspDishFragment;
import com.sony.svpa.rf4ceprototype.fragments.SetupVerifyFragment;
import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;
import com.sony.svpa.rf4ceprototype.uei.MainActivity;
import com.sony.svpa.rf4ceprototype.utils.ChannelChangeManager;
import com.sony.svpa.rf4ceprototype.utils.Constants;
import com.sony.svpa.rf4ceprototype.utils.KeySequenceHelper;
import com.sony.svpa.rf4ceprototype.utils.SuccessListener;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RFSetupActivity extends Activity {

    public final static String TAG = "RFSetupActivity";
    private Activity mActivity;
    private DeviceInformation deviceInformation;
    private String deviceInfo;

    private final KeySequenceHelper keySequenceHelper = new KeySequenceHelper(
            KeySequenceHelper.NUMBER_KEYS,
            KeySequenceHelper.ENTER_KEYS,
            1000L,
            new SuccessListener<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Received key sequence: " + result);
                    boolean success = ChannelChangeManager.getInstance(mActivity).changeChannelNameOrNumber(result);
                    if (success){
                        Toast.makeText(mActivity, "Changing to Channel " + result, Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent setupIntent = new Intent(mActivity, RFSetupActivity.class);
                                setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(setupIntent);
                            }
                        }, 500);

                    }
                }

                @Override
                public void onChannelUp() {
                    channelUp();
                }

                @Override
                public void onChannelDown() {
                    channelDown();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rf_setup);
        mActivity = this;

        if (getIntent() != null && getIntent().hasExtra(Constants.DEVICE_INFORMATION)){
            Log.d(TAG, "RFSetupActivity started with Intent");
            deviceInfo = getIntent().getStringExtra(Constants.DEVICE_INFORMATION);
            if (!TextUtils.isEmpty(deviceInfo)){
                Gson gson = new Gson();
                deviceInformation = gson.fromJson(deviceInfo, DeviceInformation.class);

                String deviceName = deviceInformation.getOsdName();
                Log.d(TAG, "deviceName: " + deviceName);
                Fragment fragment = null;
                if (StringUtils.containsIgnoreCase(deviceName, "Dish")){
                    fragment = new EspDishFragment();
                } else {
                    fragment = new SetupVerifyFragment();
                }
//                switch (rf_type){
//                    case Constants.RF_ZRC_1_1:
//                        //Start RF ZRC 1.1 Fragment
//                        fragment = new RFZRCOneFragment();
//                        break;
//                    case Constants.RF_ZRC_2_0:
//                        //Start RF ZRC 2.0 Fragment
//                        fragment = new RFZRCTwoFragment();
//                        break;
//                    case Constants.RF_MSO:
//                        //Start RF ZRC 2.0 Fragment
//                        fragment = new RFZRCTwoFragment();
//                        break;
//                    case Constants.ESP_DISH:
//                        //Start ESP Dish Fragment
//                        fragment = new EspDishFragment();
//                        break;
//                    default:
//                        fragment = new SetupVerifyFragment();
//                        break;
//                }
                openFragment(fragment);

            }
        } else {
            Log.d(TAG, "No intent found");
            finish();
        }




    }

    private void openFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenFragmentEvent(OpenFragmentEvent event) {
        Fragment fragment = event.getFragment();
        openFragment(fragment);
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_PROG_GREEN:
                Intent mainActivity = new Intent(RFSetupActivity.this, MainActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivity);
                finish();
                break;
            default:
                keySequenceHelper.onKeyDown(keyCode, event);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void channelUp() {
        Log.d(TAG, "TV channel up.");
        ChannelChangeManager.getInstance(mActivity).channelUp(1);
    }

    private void channelDown() {
        Log.d(TAG, "TV channel down.");
        ChannelChangeManager.getInstance(mActivity).channelDown(1);
    }

}
