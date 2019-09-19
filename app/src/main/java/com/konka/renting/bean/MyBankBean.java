package com.konka.renting.bean;

/**
 * Created by kaite on 2018/4/10.
 */

public class MyBankBean {

    /**
     * card_id : 2
     * card_no : 4541**********4452
     * bank_name : 中国建设银行
     * bank_image : fjkajkfjka.png
     */

    private int card_id;
    private String card_no;
    private String bank_name;
    private String bank_image;

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_image() {
        return bank_image;
    }

    public void setBank_image(String bank_image) {
        this.bank_image = bank_image;
    }
}
