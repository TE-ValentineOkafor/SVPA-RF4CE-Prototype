package com.sony.svpa.rf4ceprototype.events;

import android.support.annotation.Nullable;

import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;

/**
 * Created by valokafor on 1/5/18.
 */

public class NewDeviceIdentifiedEvent {
    @Nullable
    private final DeviceInformation deviceInformation;

    public NewDeviceIdentifiedEvent(DeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }


    public DeviceInformation getDeviceInformation() {
        return deviceInformation;
    }


}
