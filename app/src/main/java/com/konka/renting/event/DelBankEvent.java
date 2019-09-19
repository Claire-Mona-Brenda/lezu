package com.konka.renting.event;

public class DelBankEvent {
    private String card_id;

    public DelBankEvent(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }
}
