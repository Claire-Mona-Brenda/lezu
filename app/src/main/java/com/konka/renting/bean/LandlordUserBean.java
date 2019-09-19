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
    private int is_auth;
    private int is_login_pass;//登录密码 1是 0 否
    private int is_withdraw_pass;//提现密码 1是 0 否
    private int withdraw_num;//提现剩余次数
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

    public int getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(int is_auth) {
        this.is_auth = is_auth;
    }

    public int getIs_login_pass() {
        return is_login_pass;
    }

    public void setIs_login_pass(int is_login_pass) {
        this.is_login_pass = is_login_pass;
    }

    public int getIs_withdraw_pass() {
        return is_withdraw_pass;
    }

    public void setIs_withdraw_pass(int is_withdraw_pass) {
        this.is_withdraw_pass = is_withdraw_pass;
    }

    public int getWithdraw_num() {
        return withdraw_num;
    }

    public void setWithdraw_num(int withdraw_num) {
        this.withdraw_num = withdraw_num;
    }

    public String getThumb_headimgurl() {
        return thumb_headimgurl;
    }

    public void setThumb_headimgurl(String thumb_headimgurl) {
        this.thumb_headimgurl = thumb_headimgurl;
    }
}
