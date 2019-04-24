package com.konka.renting.bean;

import com.google.gson.annotations.SerializedName;

public class WxPayBean {
    /**
     * appid : wx41ae0e6e386d1607
     * partnerid : 1501979691
     * prepayid : wx2113361529658394d7569f752700354928
     * package : Sign=WXPay
     * noncestr : spdnQrBXglvZdTVT5ZqjL3Fid0LVpQK8
     * timestamp : 1542778575
     * sign : 4446444638227503A53EECA98A8B19BF
     */

    public String appid;
    public String partnerid;
    public String prepayid;
    @SerializedName("package")
    public String packageX;
    public String noncestr;
    public double timestamp;
    public String sign;
    public String package_android;


}
