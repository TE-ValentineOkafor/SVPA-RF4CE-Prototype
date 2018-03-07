package com.sony.svpa.rf4ceprototype.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.EpgProviderChangedEvent;
import com.sony.svpa.rf4ceprototype.events.EpgZipCodeChangedEvent;
import com.sony.svpa.rf4ceprototype.fragments.ProviderListFragment;
import com.sony.svpa.rf4ceprototype.fragments.ZipCodeFragment;
import com.sony.svpa.rf4ceprototype.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProviderActivity extends Activity {
    private String deviceInformation;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        activity = this;

        if (getIntent() != null && getIntent().hasExtra(Constants.DEVICE_INFORMATION)){
            deviceInformation = getIntent().getStringExtra(Constants.DEVICE_INFORMATION);
            if (!TextUtils.isEmpty(deviceInformation)){
                openFragment(new ZipCodeFragment());
            } else {
                Toast.makeText(activity, "No setup box detected", Toast.LENGTH_LONG).show();
            }

        }




    }


    private void openFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onZipCodeChangeEvent(EpgZipCodeChangedEvent event) {
        if (!TextUtils.isEmpty(event.getZipCode())){
            openFragment(new ProviderListFragment());
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEpgProviderChangedEvent(EpgProviderChangedEvent event) {
        if (event.getProvider() != null){
            Intent setupIntent = new Intent(activity, RFSetupActivity.class);
            setupIntent.putExtra(Constants.DEVICE_INFORMATION, deviceInformation);
            startActivity(setupIntent);
        }
    };
}


