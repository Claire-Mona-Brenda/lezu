package com.konka.renting.landlord.user.withdrawcash;

/**
 * Created by kaite on 2018/4/10.
 */

public class SelectCardEvent {
    private String id;
    private String number;
    private String bank;
    private String imag_url;

    public SelectCardEvent(String id, String number, String bank,String imag_url) {
        this.id = id;
        this.number = number;
        this.bank = bank;
        this.imag_url = imag_url;
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

    public String getImag_url() {
        return imag_url;
    }

    public void setImag_url(String imag_url) {
        this.imag_url = imag_url;
    }
}
