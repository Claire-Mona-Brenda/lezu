package com.konka.renting.tenant.opendoor;

/**
 * Created by kaite on 2018/4/19.
 */

public class CancelSetEvent {
    SetPwdEvent.Type type;
    public CancelSetEvent(SetPwdEvent.Type mType) {
        this.type = mType;
    }

    public SetPwdEvent.Type getType() {
        return type;
    }

    public void setType(SetPwdEvent.Type type) {
        this.type = type;
    }
}
