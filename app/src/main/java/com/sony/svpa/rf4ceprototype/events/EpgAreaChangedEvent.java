package com.sony.svpa.rf4ceprototype.events;


import com.sony.svpa.rf4ceprototype.models.EpgArea;

/**
 * Created by valokafor on 4/25/17.
 */

public class EpgAreaChangedEvent {
    private final EpgArea area;

    public EpgAreaChangedEvent(EpgArea area) {
        this.area = area;
    }

    public EpgArea getArea() {
        return area;
    }
}
