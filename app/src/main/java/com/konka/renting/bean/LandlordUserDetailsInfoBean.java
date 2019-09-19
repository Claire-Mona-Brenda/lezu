package com.konka.renting.bean;

public class LandlordUserDetailsInfoBean {
    /**
     * real_name : xxx
     * headimgurl : 15438896638019.jpg
     * sex : 未知
     * age :
     * phone : *******1765
     * is_identity : 1
     * identity : 4*************5315
     * identity_just : https://lettest.youlejiakeji.com/Uploads/Image/App/20180712/15313620762605.jpg
     * identity_back : https://lettest.youlejiakeji.com/Uploads/Image/App/20180712/15313620617559.jpg
     */

    private String real_name;
    private String headimgurl;
    private String thumb_headimgurl;
    private String sex;
    private String age;
    private String phone;
    private int is_identity;//	1已认证 0 未认证
    private String identity;
    private String identity_just;
    private String identity_back;
    private String start_time;
    private String end_time;
    private int is_login_pass;//登录密码 1是 0 否
    private int is_withdraw_pass;//	提现密码 1是 0 否

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

    public String getThumb_headimgurl() {
        return thumb_headimgurl;
    }

    public void setThumb_headimgurl(String thumb_headimgurl) {
        this.thumb_headimgurl = thumb_headimgurl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIs_identity() {
        return is_identity;
    }

    public void setIs_identity(int is_identity) {
        this.is_identity = is_identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentity_just() {
        return identity_just;
    }

    public void setIdentity_just(String identity_just) {
        this.identity_just = identity_just;
    }

    public String getIdentity_back() {
        return identity_back;
    }

    public void setIdentity_back(String identity_back) {
        this.identity_back = identity_back;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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
}
