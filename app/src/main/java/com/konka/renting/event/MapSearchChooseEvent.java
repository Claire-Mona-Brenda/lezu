package com.konka.renting.event;

public class MapSearchChooseEvent {
    private String name;
    private String address;
    private String lat;
    private String lng;
    private int level;//3区 4区 5区
    private int count;

    public MapSearchChooseEvent(String name, String address, String lat, String lng, int level, int count) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.level = level;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getLevel() {
        return level;
    }

    public int getCount() {
        return count;
    }
}
