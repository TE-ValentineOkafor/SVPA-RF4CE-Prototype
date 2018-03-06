package com.sony.svpa.rf4ceprototype.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.fragments.DeviceIdentifiedFragment;
import com.sony.svpa.rf4ceprototype.utils.Constants;

public class WizardActivity extends Activity {

    private static final String TAG = WizardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        if (getIntent() != null) {
            Log.d(TAG, "WizardActivity started with Intent");
            if (getIntent().hasExtra(Constants.DEVICE_INFORMATION)) {
                Log.d(TAG, "DeviceIdentifiedFragment called");
                String deviceInfo = getIntent().getStringExtra(Constants.DEVICE_INFORMATION);
                if (!TextUtils.isEmpty(deviceInfo)) {
                    Fragment fragment = DeviceIdentifiedFragment.newInstance(deviceInfo);
                    openFragment(fragment);
                } else {
                    Log.d(TAG, "Empty device info");
                }
            }
        } else {
            Log.d(TAG, "DeviceIdentifiedFragment: false");
            finish();
        }
    }


    private void openFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }
}
