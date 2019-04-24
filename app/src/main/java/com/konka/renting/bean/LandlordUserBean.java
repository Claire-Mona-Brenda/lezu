package com.konka.renting.bean;

public class LandlordUserBean  {
    /**
     * real_name : xxx
     * headimgurl : 15438896638019.jpg
     * balance : 6210.03
     * unread : 1
     */

    private String real_name;
    private String headimgurl;
    private String balance;
    private String phone;
    private int unread;
    private String thumb_headimgurl;

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getThumb_headimgurl() {
        return thumb_headimgurl;
    }

    public void setThumb_headimgurl(String thumb_headimgurl) {
        this.thumb_headimgurl = thumb_headimgurl;
    }
}
