package com.konka.renting.event;

public class PublicCancelEvent {
    String room_id;

    public PublicCancelEvent(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_id() {
        return room_id;
    }
}
