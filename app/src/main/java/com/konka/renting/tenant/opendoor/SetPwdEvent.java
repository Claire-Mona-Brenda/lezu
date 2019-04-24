package com.konka.renting.tenant.opendoor;

/**
 * Created by Administrator on 2018/1/13.
 */

public class SetPwdEvent {
    private String pwd;
    private boolean isEmpty;
    Type mType;

    public SetPwdEvent(String pwd, boolean isEmpty, Type type) {
        this.pwd = pwd;
        this.isEmpty = isEmpty;
        mType = type;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public static enum Type{
        ADD,
        UPDATE,
        CLOSE,
        CHECK,
        DetailCheck,
        DetailShareCheck
    }
}
