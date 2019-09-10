package com.konka.renting.bean;

public class RenterOrderListBean {
    /**
     * order_id : 3
     * order_no : 154357705088047691
     * room_id : 423
     * status : 0
     * type : 2
     * start_time : 2018-11-30
     * end_time : 2018-12-31
     * housing_price : 1800.00
     * sponsor_type : 2
     * order_type : 1
     * thumb_image : https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/thumb_6d2a3b3078cca849098a7858dc9864e2.jpg
     * room_name : 康佳研发大厦b18
     * gateway_id : 161
     * device_id : 383
     */

    private String order_id;//	订单id
    private String order_no;//	订单编号
    private String room_id;//	房产id
    private int status;//	订单状态 1申请中 2已确认 3入住中 4申请退租 5过期
    private int rent_type;//	出租类型 0未发布  1 短租 2长租
    private String start_time;//	开始日期
    private String end_time;//	结束日期
    private String housing_price;//	租金
    private int sponsor_type;//	发起订单人 1房东 2租房
    private int order_type;//	订单类型 1首租 2续租
    private String thumb_image;//	房产缩略图
    private String room_name;//房产名称
    private String gateway_id;//	网关id
    private String device_id;//	设备id
    private String landlord_phone;//房东电话
    private String member_phone;//租客电话
    private String service_date;//服务费到期日期
    private String room_type;//房型
    private String address;//地址
    private String floor;//楼层
    private String total_floor;//楼层
    private String measure_area;//面积


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRent_type() {
        return rent_type;
    }

    public void setRent_type(int rent_type) {
        this.rent_type = rent_type;
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

    public String getHousing_price() {
        return housing_price;
    }

    public void setHousing_price(String housing_price) {
        this.housing_price = housing_price;
    }

    public int getSponsor_type() {
        return sponsor_type;
    }

    public void setSponsor_type(int sponsor_type) {
        this.sponsor_type = sponsor_type;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getLandlord_phone() {
        return landlord_phone;
    }

    public void setLandlord_phone(String landlord_phone) {
        this.landlord_phone = landlord_phone;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getService_date() {
        return service_date;
    }

    public void setService_date(String service_date) {
        this.service_date = service_date;
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

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getTotal_floor() {
        return total_floor;
    }

    public void setTotal_floor(String total_floor) {
        this.total_floor = total_floor;
    }

    public String getMeasure_area() {
        return measure_area;
    }

    public void setMeasure_area(String measure_area) {
        this.measure_area = measure_area;
    }
}
