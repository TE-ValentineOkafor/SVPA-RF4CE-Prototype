package com.sony.svpa.rf4ceprototype.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.HotPlugDetectedEvent;
import com.sony.svpa.rf4ceprototype.events.NewDeviceIdentifiedEvent;
import com.sony.svpa.rf4ceprototype.events.StartSetupEvent;
import com.sony.svpa.rf4ceprototype.fragments.InitialSetupFragment;
import com.sony.svpa.rf4ceprototype.utils.Constants;
import com.sony.svpa.rf4ceprototype.utils.EventTimer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class InitialSetupActivity extends Activity {
    private FrameLayout container1, container2, container3, container4, container5;
    private final static String FRAGMENT_ONE_TAG = "hdmi_1";
    private final static String FRAGMENT_TWO_TAG = "hdmi_2";
    private final static String FRAGMENT_THREE_TAG = "hdmi_3";
    private final static String FRAGMENT_FOUR_TAG = "hdmi_4";
    private final static String FRAGMENT_FIVE_TAG = "hdmi_5";
    private Activity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);
        activity = this;
        container1 = findViewById(R.id.container1);
        container2 = findViewById(R.id.container2);
        container3 = findViewById(R.id.container3);
        container4 = findViewById(R.id.container4);
        container5 = findViewById(R.id.container5);


    }

    @Override
    protected void onResume() {
        super.onResume();
        openFragment(InitialSetupFragment.newInstance(getString(R.string.hdmi_label_1)), R.id.container1, FRAGMENT_ONE_TAG);
        openFragment(InitialSetupFragment.newInstance(getString(R.string.hdmi_label_1)), R.id.container2, FRAGMENT_TWO_TAG);
        openFragment(InitialSetupFragment.newInstance(getString(R.string.hdmi_label_2)), R.id.container3, FRAGMENT_THREE_TAG);
        openFragment(InitialSetupFragment.newInstance(getString(R.string.hdmi_label_3)), R.id.container4, FRAGMENT_FOUR_TAG);
        openFragment(InitialSetupFragment.newInstance(getString(R.string.hdmi_label_4)), R.id.container5, FRAGMENT_FIVE_TAG);



        new Handler().postDelayed(() -> {
            InitialSetupFragment fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_ONE_TAG);
            fragment.setHdmiLabelTop(getString(R.string.antenna));
            fragment.setHdmiLabelBottom("12 Channels");
            fragment.showIdentifyingSpinner(true);

            new Handler().postDelayed(() -> {
                InitialSetupFragment fragment2 = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_ONE_TAG);
                fragment2.setHdmiLabelTop(getString(R.string.antenna));
                fragment2.setHdmiLabelBottom("147 Channels found");
                fragment2.showIdentifyingSpinner(false);
            }, 8000);
        }, 500);

        EventTimer timer = EventTimer.getTimer(this);
        timer.startInitialDeviceDiscovery();

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
    public void onMessageEvent(NewDeviceIdentifiedEvent event) {
        InitialSetupFragment fragment;
//        switch (event.getHdmiPort()){
//            case Constants.HDMI_PORT_1:
//                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_TWO_TAG);
//                fragment.onDeviceDetected(R.drawable.set_top_box,getString(R.string.hdmi_label_1));
//                fragment.setHdmiLabelBottom(getString(R.string.label_dish));
//                fragment.showSetupLabel(true);
//                fragment.addBlueBorder(true);
//
//                break;
//            case Constants.HDMI_PORT_2:
//                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_THREE_TAG);
//                fragment.onDeviceDetected(R.drawable.blue_ray,getString(R.string.hdmi_label_2));
//                fragment.setHdmiLabelBottom(event.getDeviceName());
//                break;
//            case Constants.HDMI_PORT_3:
//                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_FOUR_TAG);
//                fragment.onDeviceDetected(R.drawable.playstation_icon2,getString(R.string.hdmi_label_3));
//                fragment.setHdmiLabelBottom(event.getDeviceName());
//                break;
//            case Constants.HDMI_PORT_4:
//                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_FIVE_TAG);
//                fragment.onDeviceDetected(R.drawable.soundbar,getString(R.string.hdmi_label_4));
//                fragment.setHdmiLabelBottom(getString(R.string.label_sound_bar));
//                fragment.setHdmiArcLabel(getString(R.string.hdmi_label_arc));
//                break;
//
//        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHdmiHotPlugDetectedEvent(HotPlugDetectedEvent event) {
        InitialSetupFragment fragment;
        switch (event.getHdmiPort()){
            case Constants.HDMI_PORT_1:
                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_TWO_TAG);
                fragment.setHdmiLabelTop(getString(R.string.hdmi_label_1));
                fragment.showIdentifyingSpinner(true);
                fragment.setHdmiLabelBottom(getString(R.string.detecting));
                break;
            case Constants.HDMI_PORT_2:
                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_THREE_TAG);
                fragment.setHdmiLabelTop(getString(R.string.hdmi_label_2));
                fragment.showIdentifyingSpinner(true);
                fragment.setHdmiLabelBottom(getString(R.string.detecting));;
                break;
            case Constants.HDMI_PORT_3:
                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_FOUR_TAG);
                fragment.setHdmiLabelTop(getString(R.string.hdmi_label_3));
                fragment.showIdentifyingSpinner(true);
                fragment.setHdmiLabelBottom(getString(R.string.detecting));
                break;
            case Constants.HDMI_PORT_4:
                fragment = (InitialSetupFragment)getFragmentManager().findFragmentByTag(FRAGMENT_FIVE_TAG);
                fragment.setHdmiLabelTop(getString(R.string.hdmi_label_4));
                fragment.showIdentifyingSpinner(true);
                fragment.setHdmiLabelBottom(getString(R.string.detecting));;
                break;

        }
    }

    private void openFragment(Fragment fragment, int container, String tag){
        getFragmentManager()
                .beginTransaction()
                .add(container, fragment, tag)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetupDeviceEvent(StartSetupEvent event) {
        EventTimer.getTimer(activity).startDeviceSetup();
        finish();
    };

}
