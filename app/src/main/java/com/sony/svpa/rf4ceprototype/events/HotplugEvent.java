package com.sony.svpa.rf4ceprototype.events;

import com.sony.svpa.rf4ceprototype.hotplug.model.DeviceInformation;

/**
 * Created by valokafor on 2/20/18.
 */

public class HotplugEvent {
    private final DeviceInformation deviceInformation;

    public HotplugEvent(DeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public DeviceInformation getDeviceInformation() {
        return deviceInformation;
    }
}
