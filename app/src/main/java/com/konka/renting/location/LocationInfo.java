package com.konka.renting.location;

import com.google.gson.Gson;

/**
 * Created by jzxiang on 05/04/2018.
 */

public class LocationInfo {
    public String city_id;

    public String pinyin;

    public String name;

    public double lat;
    public double lng;

    public double baseLat;
    public double baseLng;



    public void setLatLng(LatLng latLng){
        lat = latLng.lat;
        lng = latLng.lng;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class LatLng{

        /**
         * lat : 40.22077
         * lng : 116.23128
         */

        public double lat;
        public double lng;
    }
}
