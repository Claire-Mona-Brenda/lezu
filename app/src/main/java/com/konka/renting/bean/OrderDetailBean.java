package com.konka.renting.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kaite on 2018/4/9.
 */

public class OrderDetailBean implements Serializable {

    /**
     * info : {"id":"201","merge_order_no":"15312896762346","landlord_id":"1522855306","room_id":"303","renting_mode":"0","service_time":"0","state":"3","time":"2018-07-11","start_time":"2018-07-11","end_time":"2018-07-13","housing_price":"1200.00","apply_status":"2","rent_type":"两年付","order_no":"15312896930935","pay_status":"1","roomInfo":{"id":"303","mid":"","room_name":"深大 0号楼0门0","address":"深大北门","building_no":"0","door_no":"0","door_number":"0","image":"http://let.tuokemao.com/Uploads/Image/App/20180710/15312157521566.gif","floor":"12","total_floor":"20","water_rent":"1.00","electric_rent":"1.00","litter_rent":"12.00","property_rent":"15.00","cost_rent":"20.00","type":"2"},"state_name":"已过期","buyer_remark":"","service_state":"2","start_service_time":"2018-07-11","end_service_time":0,"landlordInfo":{"id":"1522855306","nickname":"18318701765","real_name":"杨俊豪","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"18318701765"},"memberList":[{"id":"82","nickname":"杨俊豪","real_name":"杨俊豪","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"18318701765","sex":"1"}]}
     */

    private InfoBean info;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean implements Serializable {
        /**
         * id : 201
         * merge_order_no : 15312896762346
         * landlord_id : 1522855306
         * room_id : 303
         * renting_mode : 0
         * service_time : 0
         * state : 3
         * time : 2018-07-11
         * start_time : 2018-07-11
         * end_time : 2018-07-13
         * housing_price : 1200.00
         * apply_status : 2
         * rent_type : 两年付
         * order_no : 15312896930935
         * pay_status : 1
         * roomInfo : {"id":"303","mid":"","room_name":"深大 0号楼0门0","address":"深大北门","building_no":"0","door_no":"0","door_number":"0","image":"http://let.tuokemao.com/Uploads/Image/App/20180710/15312157521566.gif","floor":"12","total_floor":"20","water_rent":"1.00","electric_rent":"1.00","litter_rent":"12.00","property_rent":"15.00","cost_rent":"20.00","type":"2"}
         * state_name : 已过期
         * buyer_remark :
         * service_state : 2
         * start_service_time : 2018-07-11
         * end_service_time : 0
         * landlordInfo : {"id":"1522855306","nickname":"18318701765","real_name":"杨俊豪","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"18318701765"}
         * memberList : [{"id":"82","nickname":"杨俊豪","real_name":"杨俊豪","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"18318701765","sex":"1"}]
         */

        private String id;
        private String merge_order_no;
        private String landlord_id;
        private String room_id;
        private String renting_mode;
        private String service_time;
        private String state;
        private String time;
        private String start_time;
        private String end_time;
        private String housing_price;
        private String apply_status;
        private String rent_type;
        private String order_no;
        private String pay_status;
        private RoomInfoBean roomInfo;
        private String state_name;
        private String buyer_remark;
        private String service_state;
        private String start_service_time;
        private int end_service_time;
        private LandlordInfoBean landlordInfo;
        private List<MemberListBean> memberList;

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

        public String getLandlord_id() {
            return landlord_id;
        }

        public void setLandlord_id(String landlord_id) {
            this.landlord_id = landlord_id;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getRenting_mode() {
            return renting_mode;
        }

        public void setRenting_mode(String renting_mode) {
            this.renting_mode = renting_mode;
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

        public String getHousing_price() {
            return housing_price;
        }

        public void setHousing_price(String housing_price) {
            this.housing_price = housing_price;
        }

        public String getApply_status() {
            return apply_status;
        }

        public void setApply_status(String apply_status) {
            this.apply_status = apply_status;
        }

        public String getRent_type() {
            return rent_type;
        }

        public void setRent_type(String rent_type) {
            this.rent_type = rent_type;
        }

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public String getPay_status() {
            return pay_status;
        }

        public void setPay_status(String pay_status) {
            this.pay_status = pay_status;
        }

        public RoomInfoBean getRoomInfo() {
            return roomInfo;
        }

        public void setRoomInfo(RoomInfoBean roomInfo) {
            this.roomInfo = roomInfo;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
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

        public String getStart_service_time() {
            return start_service_time;
        }

        public void setStart_service_time(String start_service_time) {
            this.start_service_time = start_service_time;
        }

        public int getEnd_service_time() {
            return end_service_time;
        }

        public void setEnd_service_time(int end_service_time) {
            this.end_service_time = end_service_time;
        }

        public LandlordInfoBean getLandlordInfo() {
            return landlordInfo;
        }

        public void setLandlordInfo(LandlordInfoBean landlordInfo) {
            this.landlordInfo = landlordInfo;
        }

        public List<MemberListBean> getMemberList() {
            return memberList;
        }

        public void setMemberList(List<MemberListBean> memberList) {
            this.memberList = memberList;
        }

        public static class RoomInfoBean implements Serializable {
            /**
             * id : 303
             * mid :
             * room_name : 深大 0号楼0门0
             * address : 深大北门
             * building_no : 0
             * door_no : 0
             * door_number : 0
             * image : http://let.tuokemao.com/Uploads/Image/App/20180710/15312157521566.gif
             * floor : 12
             * total_floor : 20
             * water_rent : 1.00
             * electric_rent : 1.00
             * litter_rent : 12.00
             * property_rent : 15.00
             * cost_rent : 20.00
             * type : 2
             */

            private String id;
            private String mid;
            private String room_name;
            private String address;
            private String building_no;
            private String door_no;
            private String door_number;
            private String image;
            private String floor;
            private String total_floor;
            private String water_rent;
            private String electric_rent;
            private String litter_rent;
            private String property_rent;
            private String cost_rent;
            private String type;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMid() {
                return mid;
            }

            public void setMid(String mid) {
                this.mid = mid;
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

            public String getLitter_rent() {
                return litter_rent;
            }

            public void setLitter_rent(String litter_rent) {
                this.litter_rent = litter_rent;
            }

            public String getProperty_rent() {
                return property_rent;
            }

            public void setProperty_rent(String property_rent) {
                this.property_rent = property_rent;
            }

            public String getCost_rent() {
                return cost_rent;
            }

            public void setCost_rent(String cost_rent) {
                this.cost_rent = cost_rent;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        public static class LandlordInfoBean implements Serializable {
            /**
             * id : 1522855306
             * nickname : 18318701765
             * real_name : 杨俊豪
             * headimgurl : http://let.tuokemao.com/Public/Common/Images/head.jpg
             * tel : 18318701765
             */

            private String id;
            private String nickname;
            private String real_name;
            private String headimgurl;
            private String tel;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
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

        public static class MemberListBean implements Serializable {
            /**
             * id : 82
             * nickname : 杨俊豪
             * real_name : 杨俊豪
             * headimgurl : http://let.tuokemao.com/Public/Common/Images/head.jpg
             * tel : 18318701765
             * sex : 1
             */

            private String id;
            private String nickname;
            private String real_name;
            private String headimgurl;
            private String tel;
            private String sex;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
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

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }
        }
    }


}

