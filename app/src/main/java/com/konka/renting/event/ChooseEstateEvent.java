package com.konka.renting.event;

import com.konka.renting.bean.RoomGroupListBean;

public class ChooseEstateEvent {
    private RoomGroupListBean roomGroupListBean;

    public ChooseEstateEvent(RoomGroupListBean roomGroupListBean) {
        this.roomGroupListBean = roomGroupListBean;
    }

    public RoomGroupListBean getRoomGroupListBean() {
        return roomGroupListBean;
    }
}
