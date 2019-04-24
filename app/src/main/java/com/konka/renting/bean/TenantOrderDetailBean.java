package com.konka.renting.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kaite on 2018/5/6.
 */

public class TenantOrderDetailBean implements Serializable {

    /**
     * info : {"id":"57","merge_order_no":"15255121153816","room_id":"123","lease_type":"2","housing_price":"0.01","service_time":"0","state":"0","time":"2018-05-05","start_time":"1970-01-01","end_time":"1970-01-01","lock_deposit":"0.00","lock_fee_type":"","lock_fee":"0.00","litter_amount":"0.00","property_amount":"0.00","cost_amount":"0.00","rent_type":null,"room_name":" 号楼门","roomInfo":{"id":"123","room_name":"焦测试","type":"2","address":"北沙滩大屯路","building_no":"36","door_no":"1","door_number":"1205","image":"http://let.tuokemao.com/Public/Common/Images/head.jpg","floor":"12","total_floor":"22","initial_water":"500.00","initial_electric":"600.00","water_rent":"10.00","electric_rent":"1.00","lease_type":"小区房","room_type":"两房一厅","landlordInfo":{"id":"34","real_name":"原磊","headimgurl":"http://let.tuokemao.com/Uploads/Image/App/20180424/15245608350508.jpg","tel":"18636163349"}},"memberList":[],"buyer_remark":"","service_state":"2","state_name":"申请中","total":0.01}
     * total : 0.01
     */

    private InfoBean info;
    private double total;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public static class InfoBean implements Serializable {
        /**
         * id : 57
         * merge_order_no : 15255121153816
         * room_id : 123
         * lease_type : 2
         * housing_price : 0.01
         * service_time : 0
         * state : 0
         * time : 2018-05-05
         * start_time : 1970-01-01
         * end_time : 1970-01-01
         * lock_deposit : 0.00
         * lock_fee_type :
         * lock_fee : 0.00
         * litter_amount : 0.00
         * property_amount : 0.00
         * cost_amount : 0.00
         * rent_type : null
         * room_name :  号楼门
         * roomInfo : {"id":"123","room_name":"焦测试","type":"2","address":"北沙滩大屯路","building_no":"36","door_no":"1","door_number":"1205","image":"http://let.tuokemao.com/Public/Common/Images/head.jpg","floor":"12","total_floor":"22","initial_water":"500.00","initial_electric":"600.00","water_rent":"10.00","electric_rent":"1.00","lease_type":"小区房","room_type":"两房一厅","landlordInfo":{"id":"34","real_name":"原磊","headimgurl":"http://let.tuokemao.com/Uploads/Image/App/20180424/15245608350508.jpg","tel":"18636163349"}}
         * memberList : []
         * buyer_remark :
         * service_state : 2
         * state_name : 申请中
         * total : 0.01
         */

        private String id;
        private String merge_order_no;
        private String room_id;
        private String lease_type;
        private String housing_price;
        private String service_time;
        private String state;
        private String time;
        private String start_time;
        private String end_time;
        private String lock_deposit;
        private String lock_fee_type;
        private String lock_fee;
        private String litter_amount;
        private String property_amount;
        private String cost_amount;
        //private String rent_type;
        private String room_name;
        private RoomInfoBean roomInfo;
        private String buyer_remark;
        private String service_state;
        private String state_name;
        private double total;
        private String member_nickname;
        private String member_tel;
        private String renting_mode;
        private List<MemberListBean> memberList;

        public String getRenting_mode() {
            return renting_mode;
        }

        public void setRenting_mode(String renting_mode) {
            this.renting_mode = renting_mode;
        }

        public String getMember_nickname() {
            return member_nickname;
        }

        public void setMember_nickname(String member_nickname) {
            this.member_nickname = member_nickname;
        }

        public String getMember_tel() {
            return member_tel;
        }

        public void setMember_tel(String member_tel) {
            this.member_tel = member_tel;
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

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getLease_type() {
            return lease_type;
        }

        public void setLease_type(String lease_type) {
            this.lease_type = lease_type;
        }

        public String getHousing_price() {
            return housing_price;
        }

        public void setHousing_price(String housing_price) {
            this.housing_price = housing_price;
        }

        public String getService_time() {
            return service_time;
        }

        public void setService_time(String service_time) {
            this.service_time = service_time;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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

        public String getLock_fee_type() {
            return lock_fee_type;
        }

        public void setLock_fee_type(String lock_fee_type) {
            this.lock_fee_type = lock_fee_type;
        }

        public String getLock_fee() {
            return lock_fee;
        }

        public void setLock_fee(String lock_fee) {
            this.lock_fee = lock_fee;
        }

        public String getLitter_amount() {
            return litter_amount;
        }

        public void setLitter_amount(String litter_amount) {
            this.litter_amount = litter_amount;
        }

        public String getProperty_amount() {
            return property_amount;
        }

        public void setProperty_amount(String property_amount) {
            this.property_amount = property_amount;
        }

        public String getCost_amount() {
            return cost_amount;
        }

        public void setCost_amount(String cost_amount) {
            this.cost_amount = cost_amount;
        }

       /* public String getRent_type() {
            return rent_type;
        }

        public void setRent_type(String rent_type) {
            this.rent_type = rent_type;
        }*/

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public RoomInfoBean getRoomInfo() {
            return roomInfo;
        }

        public void setRoomInfo(RoomInfoBean roomInfo) {
            this.roomInfo = roomInfo;
        }

        public String getBuyer_remark() {
            return buyer_remark;
        }

        public void setBuyer_remark(String buyer_remark) {
            this.buyer_remark = buyer_remark;
        }

        public String getService_state() {
            return service_state;
        }

        public void setService_state(String service_state) {
            this.service_state = service_state;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public List<MemberListBean> getMemberList() {
            return memberList;
        }

        public void setMemberList(List<MemberListBean> memberList) {
            this.memberList = memberList;
        }
        public static class MemberListBean implements Serializable{
            private String id;
            private String headimgurl;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getHeadimgurl() {
                return headimgurl;
            }

            public void setHeadimgurl(String headimgurl) {
                this.headimgurl = headimgurl;
            }
        }

        public static class RoomInfoBean implements Serializable {
            /**
             * id : 123
             * room_name : 焦测试
             * type : 2
             * address : 北沙滩大屯路
             * building_no : 36
             * door_no : 1
             * door_number : 1205
             * image : http://let.tuokemao.com/Public/Common/Images/head.jpg
             * floor : 12
             * total_floor : 22
             * initial_water : 500.00
             * initial_electric : 600.00
             * water_rent : 10.00
             * electric_rent : 1.00
             * lease_type : 小区房
             * room_type : 两房一厅
             * landlordInfo : {"id":"34","real_name":"原磊","headimgurl":"http://let.tuokemao.com/Uploads/Image/App/20180424/15245608350508.jpg","tel":"18636163349"}
             */

            private String id;
            private String room_name;
            private String type;
            private String address;
            private String building_no;
            private String door_no;
            private String door_number;
            private String image;
            private String floor;
            private String total_floor;
            private String initial_water;
            private String initial_electric;
            private String water_rent;
            private String electric_rent;
            private String lease_type;
            private String room_type;
            private LandlordInfoBean landlordInfo;

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

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
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

            public String getInitial_water() {
                return initial_water;
            }

            public void setInitial_water(String initial_water) {
                this.initial_water = initial_water;
            }

            public String getInitial_electric() {
                return initial_electric;
            }

            public void setInitial_electric(String initial_electric) {
                this.initial_electric = initial_electric;
            }

            public String getWater_rent() {
                return water_rent;
            }

            public void setWater_rent(String water_rent) {
                this.water_rent = water_rent;
            }

            public String getElectric_rent() {
                return electric_rent;
            }

            public void setElectric_rent(String electric_rent) {
                this.electric_rent = electric_rent;
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

            public LandlordInfoBean getLandlordInfo() {
                return landlordInfo;
            }

            public void setLandlordInfo(LandlordInfoBean landlordInfo) {
                this.landlordInfo = landlordInfo;
            }

            public static class LandlordInfoBean implements Serializable {
                /**
                 * id : 34
                 * real_name : 原磊
                 * headimgurl : http://let.tuokemao.com/Uploads/Image/App/20180424/15245608350508.jpg
                 * tel : 18636163349
                 */

                private String id;
                private String real_name;
                private String headimgurl;
                private String tel;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getReal_name() {
                    return real_name;
                }

                public void setReal_name(String real_name) {
                    this.real_name = real_name;
                }

                public String getHeadimgurl() {
                    return headimgurl;
                }

                public void setHeadimgurl(String headimgurl) {
                    this.headimgurl = headimgurl;
                }

                public String getTel() {
                    return tel;
                }

                public void setTel(String tel) {
                    this.tel = tel;
                }
            }
        }
    }

}
