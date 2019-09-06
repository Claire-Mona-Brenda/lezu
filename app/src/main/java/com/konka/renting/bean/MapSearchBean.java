package com.konka.renting.bean;

public class MapSearchBean {
    /**
     * count : 2
     * name : 迈瑞
     * lat : 22.538356
     * lng : 113.955630
     * room_group_id : 10
     */

    private int id;
    private int count;
    private String name;
    private String lat;
    private String lng;
    private int room_group_id;//小区id 地图等级 level >= 15 返回
    private String next_lat;//下一级 纬度 地图等级 level < 15时返回
    private String next_lng;//下一级 经度 地图等级 level < 15时返回

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getRoom_group_id() {
        return room_group_id;
    }

    public void setRoom_group_id(int room_group_id) {
        this.room_group_id = room_group_id;
    }

    public String getNext_lat() {
        return next_lat;
    }

    public void setNext_lat(String next_lat) {
        this.next_lat = next_lat;
    }

    public String getNext_lng() {
        return next_lng;
    }

    public void setNext_lng(String next_lng) {
        this.next_lng = next_lng;
    }
}
