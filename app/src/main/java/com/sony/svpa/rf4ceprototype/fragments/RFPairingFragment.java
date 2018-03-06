package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sony.svpa.rf4ceprototype.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RFPairingFragment extends Fragment {


    public RFPairingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rfpairing, container, false);
    }

}
