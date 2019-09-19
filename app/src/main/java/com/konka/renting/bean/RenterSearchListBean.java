package com.konka.renting.bean;

public class RenterSearchListBean {

    /**
     * room_id : 423
     * room_no : 154354404186141759
     * room_name : 康佳研发大厦b18
     * address : 广东省深圳市宝安区粤海街道高新园康佳研发大厦B座18
     * room_status : 4
     * type : 2
     * housing_price : 1800.00
     * total_floor : 30
     * floor : 18
     * measure_area : 60
     * room_type : 一房一厅
     * thumb_image : https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/thumb_6d2a3b3078cca849098a7858dc9864e2.jpg
     */

    private String room_id;
    private String room_no;
    private String room_name;
    private String address;
    private int room_status;
    private int type;//出租类型 1短租 2长租
    private String housing_price;
    private int total_floor;
    private int floor;
    private int measure_area;
    private String room_type;
    private String thumb_image;
    private String image;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRoom_status() {
        return room_status;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHousing_price() {
        return housing_price;
    }

    public void setHousing_price(String housing_price) {
        this.housing_price = housing_price;
    }

    public int getTotal_floor() {
        return total_floor;
    }

    public void setTotal_floor(int total_floor) {
        this.total_floor = total_floor;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getMeasure_area() {
        return measure_area;
    }

    public void setMeasure_area(int measure_area) {
        this.measure_area = measure_area;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
