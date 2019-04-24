package com.konka.renting.event;

/**
 * Created by kaite on 2018/5/3.
 */

public class RebootEvent {
    String id;
    public RebootEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
