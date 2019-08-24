package com.konka.renting.event;

public class HousePublishEvent {
    String room_id;
    int type;//1短租  2长租
    String price;

    public HousePublishEvent(String room_id, int type,String price) {
        this.room_id = room_id;
        this.type = type;
        this.price = price;
    }

    public String getRoom_id() {
        return room_id;
    }

    public int getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }
}
