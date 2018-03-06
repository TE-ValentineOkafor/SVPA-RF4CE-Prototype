package com.sony.svpa.rf4ceprototype.events;

/**
 * Created by valokafor on 1/25/18.
 */

public class HotPlugDetectedEvent {
    private final int hdmiPort;
    private final boolean connected;

    public HotPlugDetectedEvent(int hdmiPort, boolean connected) {
        this.hdmiPort = hdmiPort;
        this.connected = connected;
    }

    public int getHdmiPort() {
        return hdmiPort;
    }

    public boolean isConnected() {
        return connected;
    }
}
