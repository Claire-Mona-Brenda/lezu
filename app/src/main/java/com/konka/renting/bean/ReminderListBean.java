package com.konka.renting.bean;

public class ReminderListBean {
    /**
     * room_name : 西丽地铁站
     * room_id : 413
     * status : 1
     * day : 25
     * num : 1
     */

    private String room_name;//房产名称
    private int room_id;//房产id
    private int status;//1设置催账 0未设置
    private int day;//	每月日期
    private int num;//	单月已设置次数

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
