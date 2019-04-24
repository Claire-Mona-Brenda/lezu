package com.konka.renting.landlord.user.userinfo;

/**
 * Created by kaite on 2018/4/25.
 */

public class BindMobileEvent {
    private String phone;

    public BindMobileEvent(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
