package com.konka.renting.bean;


import android.util.Log;

import com.konka.renting.KonkaApplication;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.utils.SDCardUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by Nate on 2016年8月16日
 * 用户登录实体类
 */
public class LoginUserBean implements Serializable, Cloneable {
    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public final static String TAG = "LoginUserBean";
    public final static String BASE_CACHE_PATH = KonkaApplication.getInstance().getFilesDir().getParent() + "/";//缓存的路径;;

    private static LoginUserBean instance;


    private LoginUserBean() {

    }

    public static LoginUserBean getInstance() {
        if (instance == null) {
            Object object = SDCardUtils.restoreObject(BASE_CACHE_PATH + TAG);
            if (object == null) { // App第一次启动，文件不存在，则新建之
                object = new LoginUserBean();
                SDCardUtils.saveObject(BASE_CACHE_PATH + TAG, object);

            }
            instance = (LoginUserBean) object;
        }
        return instance;
    }


    //用户属性
    private boolean isLogin;
    private String access_token;
    //房东或者租客
    private int mType;
    String is_lodge_identity;
    private String mobile;
    private String realname;
    private String identity;
    private String id;
    private String is_verification;
    private String verification_pwd;
    private String cardId;
    private int is_login_pass;//登录密码 1是 0 否
    private int is_withdraw_pass;//	提现密码 1是 0 否

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean is_tenant_identity() {
        return is_lodge_identity.equals("1");
    }

    public String getAccess_token() {
        Log.e(TAG, "getAccess_token: " + access_token);
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public void setIs_lodge_identity(String is_lodge_identity) {
        this.is_lodge_identity = is_lodge_identity;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getIs_lodge_identity() {
        return is_lodge_identity;
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

    public boolean isLandlord() {
        return LoginInfo.isLandlord(mType);
    }

    public void reset() {
        isLogin = false;
        access_token = "";
        is_lodge_identity = "";
        mobile = "";
        verification_pwd = "";
        is_verification = "";
        is_login_pass = 0;
        is_withdraw_pass = 0;
    }

    public void save() {
        instance = this;
        SDCardUtils.saveObject(BASE_CACHE_PATH + TAG, this);
    }

    // -----------以下3个方法用于序列化-----------------
    public LoginUserBean readResolve() throws ObjectStreamException, CloneNotSupportedException {
        instance = (LoginUserBean) this.clone();
        return instance;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }

    public Object Clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
