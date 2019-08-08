package com.zaaach.citypicker.model;

import android.os.Parcelable;

public class HotCity extends City implements Parcelable {

    public HotCity(String name, String province, String code) {
        super(name, province, "热门城市", code);
    }
}
