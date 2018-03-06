package com.sony.svpa.rf4ceprototype.events;

/**
 * Created by valokafor on 1/17/18.
 */

public class StartSetupEvent {
    private final String typeOfDevice;

    public StartSetupEvent(String typeOfDevice) {
        this.typeOfDevice = typeOfDevice;
    }

    public String getTypeOfDevice() {
        return typeOfDevice;
    }
}
