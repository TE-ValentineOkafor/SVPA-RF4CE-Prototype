package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.OpenFragmentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class RFZRCTwoFragment extends Fragment {

    private View rootView;
    private ImageView pinImageView;




    public RFZRCTwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_rf_zrc_two, container, false);
        pinImageView = rootView.findViewById(R.id.remote_pin_layout);

        pinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SetupVerifyFragment();
                EventBus.getDefault().post(new OpenFragmentEvent(fragment));
            }
        });
        return rootView;
    }



}
