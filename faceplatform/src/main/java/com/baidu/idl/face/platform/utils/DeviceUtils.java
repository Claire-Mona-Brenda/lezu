/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.Permissions;
import java.security.acl.Permission;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 设备信息工具类
 */
public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    public static String getDeviceCode(Context context) {
        String code = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

                code = tm.getDeviceId();
                code = MD5Utils.encryption(code.getBytes());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return code;
    }

    public static String getAndroidID(Context context) {
        String id=null;
        String androidId = Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(androidId) && !"9774d56d682e549c".equals(androidId)) {
            try {
                UUID uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                id = uuid.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(id)) {
            id = getUUID();
        }

        return TextUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
//        try {
////            androidId = MD5Utils.encryption(androidId.getBytes());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////        return androidId;
    }

    public static String getSerialNumber(Context context) {
        return android.os.Build.SERIAL;
    }

    public static String getUUID() {
//        String uniqueID = UUID.randomUUID().toString();
//        return uniqueID;
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial(); // TODO crash in Q
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial"; // 随便一个初始化
        }

        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
