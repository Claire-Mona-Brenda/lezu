package com.konka.renting.event;

import com.konka.renting.bean.HouseConfigBean;

import java.util.ArrayList;

public class CheckConfigSureEvent {
    private ArrayList<HouseConfigBean> configList;

    public CheckConfigSureEvent(ArrayList<HouseConfigBean> configList) {
        this.configList = configList;
    }

    public ArrayList<HouseConfigBean> getConfigList() {
        return configList;
    }
}
