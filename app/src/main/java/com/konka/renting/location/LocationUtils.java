package com.konka.renting.location;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.konka.renting.utils.SharedPreferenceUtil;
import com.konka.renting.utils.UIUtils;

/**
 * Created by jzxiang on 05/04/2018.
 */

public class LocationUtils {
    private static final String TAG = "LocationUtils";
    private static final String KEY = "KONKA_LOCATION";

    private static LocationInfo mLocationInfo;

    public static synchronized LocationInfo getInstance(){
        if (mLocationInfo == null){
            mLocationInfo = getLocationInfo();
        }

        if (mLocationInfo == null){
            mLocationInfo = new LocationInfo();
        }
        return mLocationInfo;
    }

    public static boolean isChoiceCity() {
        String cache = getCache();
        return !TextUtils.isEmpty(cache);
    }

    private static String getCache() {
        return SharedPreferenceUtil.getString(UIUtils.getContext(), KEY, "");
    }


    private static LocationInfo getLocationInfo() {
        String cache = getCache();
        if (TextUtils.isEmpty(cache))
            return null;

        Gson gson = new Gson();
        return gson.fromJson(cache, LocationInfo.class);
    }

    public static void save(LocationInfo locationInfo) {
        if (locationInfo == null)
            return;

        if (mLocationInfo != null){
            mLocationInfo.city_id = locationInfo.city_id;
            mLocationInfo.lat = locationInfo.lat;
            mLocationInfo.lng = locationInfo.lng;
            mLocationInfo.name = locationInfo.name;
        }

        SharedPreferenceUtil.setString(UIUtils.getContext(), KEY, mLocationInfo.toString());
        mLocationInfo = getLocationInfo();

    }

    public static void saveBase(LocationInfo locationInfo) {
        if (locationInfo == null)
            return;

        if (mLocationInfo != null){
            mLocationInfo.baseLat = locationInfo.baseLat;
            mLocationInfo.baseLng = locationInfo.baseLng;
        }else {
            mLocationInfo = locationInfo;
        }

        SharedPreferenceUtil.setString(UIUtils.getContext(), KEY, mLocationInfo.toString());
        mLocationInfo = getLocationInfo();

    }




}
