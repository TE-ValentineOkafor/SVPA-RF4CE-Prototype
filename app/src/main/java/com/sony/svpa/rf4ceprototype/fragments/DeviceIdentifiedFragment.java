package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.activities.ProviderActivity;
import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;
import com.sony.svpa.rf4ceprototype.utils.Constants;
import com.sony.svpa.rf4ceprototype.utils.SquareLayout;

import static com.sony.svpa.rf4ceprototype.hotplug.util.Constants.DEVICETYPE_AUDIOSYS;
import static com.sony.svpa.rf4ceprototype.hotplug.util.Constants.DEVICETYPE_PLAYER;
import static com.sony.svpa.rf4ceprototype.hotplug.util.Constants.DEVICETYPE_RECORDER;
import static com.sony.svpa.rf4ceprototype.hotplug.util.Constants.DEVICETYPE_TUNER;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceIdentifiedFragment extends Fragment {

    private DeviceInformation deviceInformation;
    private String deviceInfo;
    private final static String TAG = "DeviceIdentifiedFrag";


    private View rootView;
    private SquareLayout squareLayout;
    private TextView deviceNameTextView, setupTextView;
    private ImageView newDeviceFoundImageView;
    private String deviceType;


    public DeviceIdentifiedFragment() {
        // Required empty public constructor
    }

    public void setDeviceInformation(DeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public static DeviceIdentifiedFragment newInstance(String deviceInfo){
        DeviceIdentifiedFragment fragment = new DeviceIdentifiedFragment();
        if (!TextUtils.isEmpty(deviceInfo)){
            Bundle args = new Bundle();
            args.putString(Constants.DEVICE_INFORMATION, deviceInfo);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(Constants.DEVICE_INFORMATION)){
            deviceInfo = getArguments().getString(Constants.DEVICE_INFORMATION);
            if (!TextUtils.isEmpty(deviceInfo)){
                Gson gson = new Gson();
                deviceInformation = gson.fromJson(deviceInfo, DeviceInformation.class);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_device_identified, container, false);
        squareLayout = rootView.findViewById(R.id.indicatorContainer);
        deviceNameTextView = rootView.findViewById(R.id.text_view_device_name);
        setupTextView = rootView.findViewById(R.id.text_view_setup);
        newDeviceFoundImageView = rootView.findViewById(R.id.image_view_detected_device);

        squareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareLayout.setVisibility(View.GONE);
                Intent setupIntent = new Intent(getActivity(), ProviderActivity.class);
                setupIntent.putExtra(Constants.DEVICE_INFORMATION, deviceInfo);
                startActivity(setupIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (deviceInformation != null){
            int deviceType = deviceInformation.getDevType();
            String deviceName = deviceInformation.getOsdName();
            deviceNameTextView.setText(deviceName);

            Log.d(TAG, "deviceType: " + deviceType + ", deviceName: " + deviceName);
            switch (deviceType){
                case DEVICETYPE_RECORDER:
                    //Recorder
                    break;
                case DEVICETYPE_TUNER:
                    //Tuner
                    newDeviceFoundImageView.setImageResource(R.drawable.set_top_box);
                    setupTextView.setVisibility(View.VISIBLE);
                    setupTextView.setText("Setup your");
                    break;
                case DEVICETYPE_PLAYER:
                    //BluRay Player
                    newDeviceFoundImageView.setImageResource(R.drawable.streaming_box);
                    break;
                case  DEVICETYPE_AUDIOSYS:
                    //Audio
                    newDeviceFoundImageView.setImageResource(R.drawable.soundbar);
                    newDeviceFoundImageView.setImageResource(R.drawable.playstation_icon2);
                    break;
                default:
                        break;
            }

        }





    }
}
