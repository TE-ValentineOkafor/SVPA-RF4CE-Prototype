package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.OpenFragmentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class EspDishFragment extends Fragment {

    private View rootView;
    private Button okButton, cancelButton;


    public EspDishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_esp_dish, container, false);

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

}
