package com.konka.renting.bean;

public class PwsOrderDetailsBean {

    /**
     * order_id : 3
     * account : 33232r23r
     * password : 1223424
     * start_time : 2018-11-30 12:00:00
     * end_time : 2018-12-31 12:00:00
     * room_name : 康佳研发大厦b18
     * room_type : 1_1_1
     * address : 康佳研发大厦b18
     */

    private int order_id;
    private String account;
    private String password;
    private String start_time;
    private String end_time;
    private String room_name;
    private String room_type;
    private String address;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
