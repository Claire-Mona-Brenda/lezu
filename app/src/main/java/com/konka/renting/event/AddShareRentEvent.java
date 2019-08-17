package com.konka.renting.event;

public class AddShareRentEvent {
    int have = 0;
    String order_id;

    public AddShareRentEvent(int have, String order_id) {
        this.have = have;
        this.order_id = order_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getHave() {
        return have;
    }

    public void setHave(int have) {
        this.have = have;
    }
}
