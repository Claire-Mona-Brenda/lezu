package com.konka.renting.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @author 郭阳鹏
 * @date 创建时间：2017/3/30
 * @description SharedPreferenceUtil
 */
public class SharedPreferenceUtil {

    private static final String SP_NAME = "SAFE_SP";

    private static SharedPreferences sp;

    private static SharedPreferences getPreferences(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sp;
    }

    /**
     * 获取boolean的缓存数据，没有的话默认值是false
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getBoolean(key, false);
    }

    /**
     * 获取boolean的缓存数据
     *
     * @param context
     * @param key
     * @param defValue : 没有时的默认值
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = getPreferences(context);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 设置boolean类型的缓存
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getPreferences(context);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取String的缓存数据，没有的话默认值是""
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getString(key, "");
    }

    /**
     * 获取String的缓存数据
     *
     * @param context
     * @param key
     * @param defValue : 没有时的默认值
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = getPreferences(context);
        return sp.getString(key, defValue);
    }

    /**
     * 设置String类型的缓存
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = getPreferences(context);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取int的缓存数据，没有的话默认值是""
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getInt(key, -1);
    }

    /**
     * 获取long的缓存数据，没有的话默认值是""
     *
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getLong(key, -1);
    }

    /**
     * 设置long类型的缓存
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setLong(Context context, String key, long value) {
        SharedPreferences sp = getPreferences(context);
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 获取int的缓存数据
     *
     * @param context
     * @param key
     * @param defValue : 没有时的默认值
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = getPreferences(context);
        return sp.getInt(key, defValue);
    }

    /**
     * 设置int类型的缓存
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = getPreferences(context);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences sp = getPreferences(context);
        Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
