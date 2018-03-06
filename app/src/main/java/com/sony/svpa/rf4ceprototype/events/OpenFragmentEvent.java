package com.sony.svpa.rf4ceprototype.events;

import android.app.Fragment;

/**
 * Created by valokafor on 1/10/18.
 */

public class OpenFragmentEvent {
    private final Fragment fragment;

    public OpenFragmentEvent(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
