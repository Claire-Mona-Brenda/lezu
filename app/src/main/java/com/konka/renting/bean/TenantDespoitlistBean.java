package com.konka.renting.bean;

/**
 * Created by kaite on 2018/4/9.
 */

public class TenantDespoitlistBean {

    /**
     * id : 35
     * merge_order_no : 15232637779601
     * room_id : 18
     * start_time : 2018-04-09
     * end_time : 2018-08-09
     * lock_deposit : 150.00
     * roomInfo : {"id":"18","room_name":"拉 5号楼6门8","address":"拉","building_no":"5","door_no":"6","door_number":"8","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif","floor":"5","total_floor":"5","lease_type":"民房","room_type":"一房一厅"}
     * status_name : 申请中
     */

    private String id;
    private String merge_order_no;
    private String room_id;
    private String start_time;
    private String end_time;
    private String lock_deposit;
    private RoomInfoBean roomInfo;
    private String status_name;

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

    public String getLock_deposit() {
        return lock_deposit;
    }

    public void setLock_deposit(String lock_deposit) {
        this.lock_deposit = lock_deposit;
    }

    public RoomInfoBean getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RoomInfoBean roomInfo) {
        this.roomInfo = roomInfo;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public static class RoomInfoBean {
        /**
         * id : 18
         * room_name : 拉 5号楼6门8
         * address : 拉
         * building_no : 5
         * door_no : 6
         * door_number : 8
         * image : http://let.tuokemao.com/Public/Common/Images/nopic.gif
         * floor : 5
         * total_floor : 5
         * lease_type : 民房
         * room_type : 一房一厅
         */

        private String id;
        private String room_name;
        private String address;
        private String building_no;
        private String door_no;
        private String door_number;
        private String image;
        private String floor;
        private String total_floor;
        private String lease_type;
        private String room_type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getLease_type() {
            return lease_type;
        }

        public void setLease_type(String lease_type) {
            this.lease_type = lease_type;
        }

        public String getRoom_type() {
            return room_type;
        }

        public void setRoom_type(String room_type) {
            this.room_type = room_type;
        }
    }

}
