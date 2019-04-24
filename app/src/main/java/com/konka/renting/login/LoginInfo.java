package com.konka.renting.login;


public class LoginInfo {

    public static final int LANDLORD = 1;
    public static final int TENANT = 0;
    /**
     * mid : 19
     * login_name : 18500361906
     * token : landlord_b3d9c0a4bb3170e5a5e71ef22a0a834f
     */

    public int mid;
    public String login_name;
    public String token;

    public static boolean isLandlord(int type){
        return type == LANDLORD;
    }



}
