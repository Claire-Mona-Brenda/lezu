package com.konka.renting.event;

public class ChooseCityEvent {
    String cityName;

    public ChooseCityEvent(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }
}
