package com.konka.renting.landlord.user.withdrawcash;

/**
 * Created by kaite on 2018/4/10.
 */

public class SelectCardEvent {
    private String id;
    private String number;
    private String bank;

    public SelectCardEvent(String id, String number, String bank) {
        this.id = id;
        this.number = number;
        this.bank = bank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
