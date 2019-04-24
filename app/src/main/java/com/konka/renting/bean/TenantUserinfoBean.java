package com.konka.renting.bean;

import java.io.Serializable;

/**
 * Created by kaite on 2018/3/26.
 */

public class TenantUserinfoBean implements Serializable {

    /**
     * nickname : 18636163349
     * headimgurl : http://let.tuokemao.com/Public/Common/Images/head.jpg
     * real_name :
     * sex : 3
     * birthday : 0
     * tel : 18636163349
     * identity :
     * identity_just : http://let.tuokemao.com/Public/Common/Images/nopic.gif
     * identity_back : http://let.tuokemao.com/Public/Common/Images/nopic.gif
     * identity_just_filename :
     * identity_back_filename :
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
    private String identity_just_filename;
    private String identity_back_filename;
    private String is_lodge_identity;
    private String lodge_identity_status;
    private String id;
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
    public String getLodge_identity_status() {
        return lodge_identity_status;
    }

    public void setLodge_identity_status(String lodge_identity_status) {
        this.lodge_identity_status = lodge_identity_status;
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

    public String getIs_lodge_identity() {
        return is_lodge_identity;
    }

    public void setIs_lodge_identity(String is_lodge_identity) {
        this.is_lodge_identity = is_lodge_identity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
