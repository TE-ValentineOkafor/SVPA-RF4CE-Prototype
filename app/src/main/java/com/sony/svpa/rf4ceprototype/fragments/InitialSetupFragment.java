package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.StartSetupEvent;
import com.sony.svpa.rf4ceprototype.utils.Constants;
import com.sony.svpa.rf4ceprototype.utils.SquareLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class InitialSetupFragment extends Fragment {

    private String hdmiLabel = "";
    private final static String TAG = "SetupFragment";
    private View rootView;
    private FrameLayout containerView;
    private TextView hdmiLabelTop, hdmiLabelBottom, setupTextView, hdmiLabelArc;
    private AVLoadingIndicatorView indicatorView;
    private ImageView hdmiImage;
    private SquareLayout squareLayout;

    private int imageHeight = 0;
    private int imageWidth = 0;

    private int squareLayoutWidth = 0;
    private int squareLayoutHeight = 0;


    public InitialSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            if (getArguments().containsKey(Constants.HDMI_LABEL)){
                hdmiLabel = getArguments().getString(Constants.HDMI_LABEL);
            }
        }
    }

    public static InitialSetupFragment newInstance(String hdmiLabel){
        InitialSetupFragment fragment = new InitialSetupFragment();
        if (!TextUtils.isEmpty(hdmiLabel)){
            Bundle args = new Bundle();
            args.putString(Constants.HDMI_LABEL, hdmiLabel);
            fragment.setArguments(args);
        }
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_initial_setup, container, false);
        hdmiLabelBottom = rootView.findViewById(R.id.text_view_hdmi_label_bottom);
        hdmiLabelTop = rootView.findViewById(R.id.text_view_hdmi_label_top);
        hdmiLabelArc = rootView.findViewById(R.id.text_view_hdmi_label_arc);
        setupTextView = rootView.findViewById(R.id.text_view_setup);
        indicatorView = (AVLoadingIndicatorView)rootView.findViewById(R.id.indicatorView);
        squareLayout = rootView.findViewById(R.id.identifying_container);
        hdmiImage = rootView.findViewById(R.id.hdmi_image);



        ViewTreeObserver vto = hdmiImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hdmiImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageWidth  = hdmiImage.getMeasuredWidth();
                imageHeight = hdmiImage.getMeasuredHeight();

                if (imageHeight > 0){
                    indicatorView.getLayoutParams().height = imageHeight;
                }

                if (imageWidth > 0){
                    indicatorView.getLayoutParams().width = imageWidth;
                }
            }
        });

        ViewTreeObserver vtoRoot = squareLayout.getViewTreeObserver();
        vtoRoot.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                squareLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                squareLayoutWidth  = squareLayout.getMeasuredWidth();
                squareLayoutHeight = squareLayout.getMeasuredHeight();
                if (squareLayoutWidth > 0) {
                    squareLayout.getLayoutParams().width = squareLayoutWidth - 10;
                }

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        hdmiLabelBottom.setText(hdmiLabel);
    }

    public void setHdmiLabelTop(String label){
        hdmiLabelTop.setVisibility(View.VISIBLE);
        hdmiLabelTop.setText(label);
    }

    public void setHdmiArcLabel(String label){
        hdmiLabelArc.setVisibility(View.VISIBLE);
        hdmiLabelArc.setText(label);
    }

    public void setHdmiLabelBottom(String label){
        hdmiLabelBottom.setVisibility(View.VISIBLE);
        hdmiLabelBottom.setText(label);
    }

    public void showSetupLabel(boolean showLabel){
        if (showLabel){
            setupTextView.setVisibility(View.VISIBLE);
        }else {
            setupTextView.setVisibility(View.GONE);
        }
    }

    public void showIdentifyingSpinner(boolean show){
        if (show){
            indicatorView.setVisibility(View.VISIBLE);
            indicatorView.show();
            hdmiImage.setVisibility(View.GONE);
        }else {
            indicatorView.setVisibility(View.GONE);
            indicatorView.hide();
            hdmiImage.setVisibility(View.VISIBLE );
            hdmiImage.setImageResource(R.drawable.antenna);

        }
    }

    public void addBlueBorder(boolean show){
        if (show){
            squareLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.indicator_container_background_blue));
            squareLayout.setClickable(true);
            squareLayout.setFocusable(true);
            squareLayout.requestFocus();
            squareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new StartSetupEvent(Constants.DEVICE_TYPE_CABLE_BOX));
                }
            });
        } else {
            squareLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.indicator_container_background));
        }
    }

    public int getSquareLayoutWidth() {
        return squareLayoutWidth;
    }

    public int getSquareLayoutHeight() {
        return squareLayoutHeight;
    }



    public void onDeviceDetected(int imageRes, String hdmiLabel){
        showIdentifyingSpinner(false);
        hdmiImage.setImageResource(imageRes);
        hdmiImage.setVisibility(View.VISIBLE);
        hdmiImage.getLayoutParams().height = imageHeight;
        hdmiImage.getLayoutParams().width = imageWidth;

        if (!TextUtils.isEmpty(hdmiLabel)){
            hdmiLabelTop.setVisibility(View.VISIBLE);
            hdmiLabelTop.setText(hdmiLabel);
        }
    }
}
