package com.sony.svpa.rf4ceprototype.events;


import com.sony.svpa.rf4ceprototype.models.Provider;

/**
 * Created by valokafor on 4/25/17.
 */

public class EpgProviderChangedEvent {

    private final Provider provider;

    public EpgProviderChangedEvent(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return provider;
    }
}
