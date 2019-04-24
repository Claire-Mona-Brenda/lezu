package com.konka.renting.bean;

/**
 * Created by jzxiang on 7/6/17.
 */

public class DataInfo<T> {
    T data;
    String msg;
    String info;
    int status;

    public boolean success() {
        return status == 1;
    }

    public String msg() {
        if (msg != null)
            return msg;
        else
            return info;
    }

    public int code() {
        return status;
    }

    public T data() {
        return data;
    }

    public boolean isNeedLogin() {
        return status == -1;
    }

}
