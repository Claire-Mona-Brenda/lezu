package com.konka.renting.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jzxiang on 04/05/2018.
 */

public class WxPayInfo {
    public InfoBean info;

    public class InfoBean {


        /**
         * appid : wx41ae0e6e386d1607
         * partnerid : 1501979691
         * prepayid : wx04002519922642791d1378130289823814
         * package : Sign=WXPay
         * noncestr : dyhxsuggf35ciy6qrqa5hr50f65iuhje
         * timestamp : 1525364719
         * sign : A964643EE69BC1A8D701D59D25CAD7CC
         * package_android : Sign=WXPay
         */

        public String appid;
        public String partnerid;
        public String prepayid;

        @SerializedName("package")
        public String packageX;
        public String noncestr;
        public long timestamp;
        public String sign;
        public String package_android;
    }
}
