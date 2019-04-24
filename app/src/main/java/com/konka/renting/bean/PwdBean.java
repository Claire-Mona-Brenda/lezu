package com.konka.renting.bean;

public class PwdBean {
    /**
     * password : 885977
     */
    private String password_id;
    private String password;
    private String expire_time;

    public String getPassword_id() {
        return password_id;
    }

    public void setPassword_id(String password_id) {
        this.password_id = password_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }
}
