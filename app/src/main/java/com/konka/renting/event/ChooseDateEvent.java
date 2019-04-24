package com.konka.renting.event;

public class ChooseDateEvent {
    public String startDate;
    public String endDate;

    public ChooseDateEvent(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
