package com.konka.renting.bean;

import java.io.Serializable;

/**
 * Created by kaite on 2018/4/7.
 */

public class OrderBean implements Serializable {

    /**
     * id : 12
     * merge_order_no : 15228087280317
     * state : 1
     * apply_status : 0
     * renting_mode : 1
     * apply_renting_mode : 1
     * room_id : 21
     * start_time : 2018-04-04
     * end_time : 2018-08-04
     * status_name : 入住中
     * renting_mode_text : 申请变更交租方式为线下付款,等待房东确认
     * sponsor_type : 1房东发起  2租户发起
     * roomInfo : {"id":"21","address":"北方大门","building_no":"13","door_no":"283","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif","floor":"3","total_floor":"15","housing_price":"1500.00","deposit_price":"0.00"}
     */

    private String id;
    private String merge_order_no;
    private String state;
    private String apply_status;
    private String renting_mode;
    private String apply_renting_mode;
    public  String apply_renew;
    private String room_id;
    private String start_time;
    private String end_time;
    private String status_name;
    private String renting_mode_text;
    private String order_no;
    private RoomInfoBean roomInfo;
    private String sponsor_type;

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getApply_renew() {
        return apply_renew;
    }

    public void setApply_renew(String apply_renew) {
        this.apply_renew = apply_renew;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerge_order_no() {
        return merge_order_no;
    }

    public void setMerge_order_no(String merge_order_no) {
        this.merge_order_no = merge_order_no;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApply_status() {
        return apply_status;
    }

    public void setApply_status(String apply_status) {
        this.apply_status = apply_status;
    }

    public String getRenting_mode() {
        return renting_mode;
    }

    public void setRenting_mode(String renting_mode) {
        this.renting_mode = renting_mode;
    }

    public String getApply_renting_mode() {
        return apply_renting_mode;
    }

    public void setApply_renting_mode(String apply_renting_mode) {
        this.apply_renting_mode = apply_renting_mode;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
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

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getRenting_mode_text() {
        return renting_mode_text;
    }

    public void setRenting_mode_text(String renting_mode_text) {
        this.renting_mode_text = renting_mode_text;
    }

    public RoomInfoBean getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RoomInfoBean roomInfo) {
        this.roomInfo = roomInfo;
    }

    public String getSponsor_type() {
        return sponsor_type;
    }

    public void setSponsor_type(String sponsor_type) {
        this.sponsor_type = sponsor_type;
    }

    public static class RoomInfoBean
    implements Serializable{
        /**
         * id : 21
         * address : 北方大门
         * building_no : 13
         * door_no : 283
         * door_number:1201
         * image : http://let.tuokemao.com/Public/Common/Images/nopic.gif
         * floor : 3
         * total_floor : 15
         * housing_price : 1500.00
         * deposit_price : 0.00
         */

        private String id;
        private String address;
        private String building_no;
        private String door_no;
        private String door_number;
        private String image;
        private String floor;
        private String total_floor;
        private String housing_price;
        private String deposit_price;
        private String room_name;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBuilding_no() {
            return building_no;
        }

        public void setBuilding_no(String building_no) {
            this.building_no = building_no;
        }

        public String getDoor_no() {
            return door_no;
        }

        public void setDoor_no(String door_no) {
            this.door_no = door_no;
        }

        public String getDoor_number() {
            return door_number;
        }

        public void setDoor_number(String door_number) {
            this.door_number = door_number;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getHousing_price() {
            return housing_price;
        }

        public void setHousing_price(String housing_price) {
            this.housing_price = housing_price;
        }

        public String getDeposit_price() {
            return deposit_price;
        }

        public void setDeposit_price(String deposit_price) {
            this.deposit_price = deposit_price;
        }
    }

}
