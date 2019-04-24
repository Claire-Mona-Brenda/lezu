package com.konka.renting.bean;

/**
 * Created by kaite on 2018/4/10.
 */

public class BillBean {

    /**
     * order_no : 15232636926690
     * room_id : 22
     * start_time : 1970-01-01
     * end_time : 1970-01-01
     * real_amount : 95.00
     * roomInfo : {"id":"22","room_name":"样板房4号 13号楼283门222113","address":"北方大门","building_no":"13","door_no":"283","door_number":"222113","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif","lease_type":"民房","room_type":"两房一厅"}
     * status : 已付款
     */

    private String order_no;
    private String room_id;
    private String start_time;
    private String end_time;
    private String real_amount;
    private RoomInfoBean roomInfo;
    private String status;

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

    public String getReal_amount() {
        return real_amount;
    }

    public void setReal_amount(String real_amount) {
        this.real_amount = real_amount;
    }

    public RoomInfoBean getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RoomInfoBean roomInfo) {
        this.roomInfo = roomInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class RoomInfoBean {
        /**
         * id : 22
         * room_name : 样板房4号 13号楼283门222113
         * address : 北方大门
         * building_no : 13
         * door_no : 283
         * door_number : 222113
         * image : http://let.tuokemao.com/Public/Common/Images/nopic.gif
         * lease_type : 民房
         * room_type : 两房一厅
         */

        private String id;
        private String room_name;
        private String address;
        private String building_no;
        private String door_no;
        private String door_number;
        private String image;
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

