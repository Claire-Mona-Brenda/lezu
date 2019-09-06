package com.konka.renting.bean;

public class MapLocationSearchBean {
    /**
     * name : 南山区
     * address : 南山区
     * lat : 22.528499
     * lng : 113.923552
     * level : 3
     * count : 7
     */

    private String name;
    private String address;
    private String lat;
    private String lng;
    private int level;//3区 4区 5区
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
