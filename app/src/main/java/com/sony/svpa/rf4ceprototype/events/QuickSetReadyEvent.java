package com.sony.svpa.rf4ceprototype.events;

/**
 * Created by valokafor on 3/2/18.
 */

public class QuickSetReadyEvent {
    private final boolean isReady;

    public QuickSetReadyEvent(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }
}
