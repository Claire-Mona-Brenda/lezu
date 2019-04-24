package com.konka.renting.bean;

import java.io.Serializable;

/**
 * Created by kaite on 2018/3/26.
 */

public class UserInfoBean implements Serializable {

    /**
     * nickname : 18636163349
     * headimgurl : http://let.tuokemao.com/Public/Common/Images/head.jpg
     * real_name :
     * sex : 3
     * birthday :
     * tel : 18636163349
     * identity :
     * identity_just : http://let.tuokemao.com/Public/Common/Images/nopic.gif
     * identity_back : http://let.tuokemao.com/Public/Common/Images/nopic.gif
     * is_identity : 0
     * code : 348792
     * identity_just_filename :
     * identity_back_filename :
     * is_address : 0
     */

    private String nickname;
    private String headimgurl;
    private String real_name;
    private String sex;
    private String birthday;
    private String tel;
    private String identity;
    private String identity_just;
    private String identity_back;
    private String is_identity;
    private String code;
    private String balance;
    private String total_deposit;
    private String identity_just_filename;
    private String identity_back_filename;
    private int is_address;
    private String identity_status;
    private String is_verification;
    private String verification_pwd;

    public String getIs_verification() {
        return is_verification;
    }

    public void setIs_verification(String is_verification) {
        this.is_verification = is_verification;
    }

    public String getVerification_pwd() {
        return verification_pwd;
    }

    public void setVerification_pwd(String verification_pwd) {
        this.verification_pwd = verification_pwd;
    }

    public String getIdentity_status() {
        return identity_status;
    }

    public void setIdentity_status(String identity_status) {
        this.identity_status = identity_status;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTotal_deposit() {
        return total_deposit;
    }

    public void setTotal_deposit(String total_deposit) {
        this.total_deposit = total_deposit;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public String getIs_identity() {
        return is_identity;
    }

    public void setIs_identity(String is_identity) {
        this.is_identity = is_identity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIdentity_just_filename() {
        return identity_just_filename;
    }

    public void setIdentity_just_filename(String identity_just_filename) {
        this.identity_just_filename = identity_just_filename;
    }

    public String getIdentity_back_filename() {
        return identity_back_filename;
    }

    public void setIdentity_back_filename(String identity_back_filename) {
        this.identity_back_filename = identity_back_filename;
    }

    public int getIs_address() {
        return is_address;
    }

    public void setIs_address(int is_address) {
        this.is_address = is_address;
    }

}
