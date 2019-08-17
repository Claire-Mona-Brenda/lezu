package com.konka.renting.event;

public class CancelOrderEvent {
    String Order_id;

    public CancelOrderEvent(String order_id) {
        Order_id = order_id;
    }

    public String getOrder_id() {
        return Order_id;
    }

    public void setOrder_id(String order_id) {
        Order_id = order_id;
    }
}
