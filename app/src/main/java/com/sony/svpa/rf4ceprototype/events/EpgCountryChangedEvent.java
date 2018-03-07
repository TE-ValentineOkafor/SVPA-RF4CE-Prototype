package com.sony.svpa.rf4ceprototype.events;


import com.sony.svpa.rf4ceprototype.models.EpgCountry;

/**
 * Created by valokafor on 4/25/17.
 */

public class EpgCountryChangedEvent {
    private final EpgCountry country;

    public EpgCountryChangedEvent(EpgCountry country) {
        this.country = country;
    }

    public EpgCountry getCountry() {
        return country;
    }
}
