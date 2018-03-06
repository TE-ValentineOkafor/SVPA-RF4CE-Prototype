package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.OpenFragmentEvent;
import com.sony.svpa.rf4ceprototype.utils.SquareLayout;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class RFZRCOneFragment extends Fragment {

    private View rootView;
    private SquareLayout squareLayout;
    private TextView deviceNameTextView, setupTextView;
    private ImageView newDeviceFoundImageView;
    private Button okButton, cancelButton;


    public RFZRCOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_rf_zrc_one, container, false);

        squareLayout = rootView.findViewById(R.id.indicatorContainer);
        deviceNameTextView = rootView.findViewById(R.id.text_view_device_name);
        setupTextView = rootView.findViewById(R.id.text_view_setup);
        newDeviceFoundImageView = rootView.findViewById(R.id.image_view_detected_device);
        okButton = rootView.findViewById(R.id.button_ok);
        cancelButton = rootView.findViewById(R.id.button_cancel);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SetupVerifyFragment();
                EventBus.getDefault().post(new OpenFragmentEvent(fragment));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        okButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    okButton.setTextColor(Color.BLACK);
                } else {
                    okButton.setTextColor(Color.WHITE);
                }
            }
        });

        cancelButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    cancelButton.setTextColor(Color.BLACK);
                } else {
                    cancelButton.setTextColor(Color.WHITE);
                }
            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        okButton.requestFocus();
        newDeviceFoundImageView.setImageResource(R.drawable.set_top_box);
        setupTextView.setVisibility(View.VISIBLE);
        setupTextView.setText("Setup your");
        deviceNameTextView.setText("Direct TV");
    }





    }
