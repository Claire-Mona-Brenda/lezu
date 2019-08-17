package com.konka.renting.bean;

public class RentListBean {

    /**
     * order_id : 3
     * start_time : 2018-11-30
     * end_time : 2018-12-31
     * real_name : 世界级
     * phone : 18542212321
     */

    private int order_id;
    private String start_time;
    private String end_time;
    private String real_name;
    private String phone;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
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

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
