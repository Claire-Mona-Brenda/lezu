package com.konka.renting.event;

public class TenantMainSwitchFragmentEvent {
    int index;

    public TenantMainSwitchFragmentEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
