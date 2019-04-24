package com.konka.renting.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @date 创建时间：2017/3/25
 * @author 郭阳鹏
 * @description
 */
public class NetWorkUtil {
    /**
     * 检查WIFI是否连接
     */
    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) UIUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null;
    }
    /**
     * 检查手机网络(4G/3G/2G)是否连接
     */
    public static boolean isMobileNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) UIUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobileNetworkInfo != null;
    }
    /**
     * 检查是否有可用网络
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) UIUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
