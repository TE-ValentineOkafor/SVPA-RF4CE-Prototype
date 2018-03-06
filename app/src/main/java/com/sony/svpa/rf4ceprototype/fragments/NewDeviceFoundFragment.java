package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.utils.Constants;
import com.sony.svpa.rf4ceprototype.utils.SquareLayout;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewDeviceFoundFragment extends Fragment {

    private static final long DELAY = 2000; // one minute
    private View rootView;
    private SquareLayout square_layout_1, square_layout_2;
    private AVLoadingIndicatorView indicatorView;
    private TextView identifyingTextView, newDeviceFoundTextView;
    private ImageView newDeviceFoundImageView;


    public NewDeviceFoundFragment() {
        // Required empty public constructor
    }


    public static NewDeviceFoundFragment newInstance(String deviceInfo){
        NewDeviceFoundFragment fragment = new NewDeviceFoundFragment();
        if (!TextUtils.isEmpty(deviceInfo)){
            Bundle args = new Bundle();
            args.putString(Constants.DEVICE_INFORMATION, deviceInfo);
            fragment.setArguments(args);
        }
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_device_found, container, false);

        square_layout_1 = rootView.findViewById(R.id.identifying_container_1);
        square_layout_2 = rootView.findViewById(R.id.identifying_container_2);
        indicatorView = (AVLoadingIndicatorView)rootView.findViewById(R.id.indicatorView);
        identifyingTextView = rootView.findViewById(R.id.text_view_identifying);
        newDeviceFoundTextView = rootView.findViewById(R.id.text_view_new_device_detected);
        newDeviceFoundImageView = rootView.findViewById(R.id.image_view_hdmi);
        indicatorView.show();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        square_layout_1.setVisibility(View.VISIBLE);
        square_layout_2.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            square_layout_1.setVisibility(View.GONE);
            square_layout_2.setVisibility(View.VISIBLE);
        }, DELAY);

    }
}
