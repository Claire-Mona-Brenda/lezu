package com.konka.renting.event;

public class ChooseOtherWiFiEvent {
    public boolean isClick;
    public String chooseWiFiName;

    public ChooseOtherWiFiEvent(boolean isClick, String chooseWiFiName) {
        this.isClick = isClick;
        this.chooseWiFiName = chooseWiFiName;
    }
}
