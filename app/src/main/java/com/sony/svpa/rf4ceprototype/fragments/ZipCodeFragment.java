package com.sony.svpa.rf4ceprototype.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.events.EpgZipCodeChangedEvent;
import com.sony.svpa.rf4ceprototype.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZipCodeFragment extends Fragment {
    private EditText zipCodeEditText;
    private View rootView;
    private SettingsHelper settingsHelper;


    public ZipCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_zip_code, container, false);
        settingsHelper = SettingsHelper.getHelper(getContext());
        zipCodeEditText = rootView.findViewById(R.id.edit_text_zip_code);
       // zipCodeEditText.setInputType(settingsHelper.getEpgCountry().zipContainsLetters() ? InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS : InputType.TYPE_CLASS_NUMBER);
        zipCodeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zipCodeEditText.getText().length() > 0){
                    String zip = zipCodeEditText.getText().toString().toUpperCase();
                    zip = zip.replaceAll("[^A-Z0-9]","");
                    settingsHelper.setEpgZipCode(zip);
                    EventBus.getDefault().post(new EpgZipCodeChangedEvent(zip));
                }

            }
        });

        return rootView;
    }

}
