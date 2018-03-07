package com.sony.svpa.rf4ceprototype.events;

/**
 * Created by valokafor on 3/7/18.
 */

public class EpgZipCodeChangedEvent {
    private final String zipCode;

    public EpgZipCodeChangedEvent(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return zipCode;
    }
}
