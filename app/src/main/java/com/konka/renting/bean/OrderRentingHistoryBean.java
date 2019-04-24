package com.konka.renting.bean;

import java.util.List;

/**
 * Created by kaite on 2018/4/8.
 */

public class OrderRentingHistoryBean {


    /**
     * rentingInfo : {"id":"23","merge_order_no":"152302330199793","room_id":"22","state":"0","start_time":"1970-01-01","end_time":"1970-01-01","housing_price":"0.00","status_name":"申请中","roomInfo":{"id":"22","landlord_two_id":"30","room_name":"样板房4号 13号楼283门222113","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif"},"memberCount":1,"memberList":[{"id":"30","headimgurl":"","real_name":"","sex":"3","birthday":"","tel":"15510151700"}],"total_price":"3000.00"}
     * lists : [{"id":"56","landlord_two_id":"31","start_time":"1970-01-01","end_time":"1970-01-01","housing_price":"0.00","nickname":"15311431111"},{"id":"55","landlord_two_id":"31","start_time":"2018-04-20","end_time":"2018-12-20","housing_price":"1000.00","nickname":"15311431111"},{"id":"54","landlord_two_id":"31","start_time":"2018-04-20","end_time":"2018-12-20","housing_price":"1000.00","nickname":"15311431111"},{"id":"53","landlord_two_id":"31","start_time":"2018-04-20","end_time":"2018-12-20","housing_price":"1000.00","nickname":"15311431111"}]
     * totalPages : 1
     */

    private RentingInfoBean rentingInfo;
    private int totalPages;
    private List<ListsBean> lists;

    public RentingInfoBean getRentingInfo() {
        return rentingInfo;
    }

    public void setRentingInfo(RentingInfoBean rentingInfo) {
        this.rentingInfo = rentingInfo;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class RentingInfoBean {
        /**
         * id : 23
         * merge_order_no : 152302330199793
         * room_id : 22
         * state : 0
         * start_time : 1970-01-01
         * end_time : 1970-01-01
         * housing_price : 0.00
         * status_name : 申请中
         * roomInfo : {"id":"22","landlord_two_id":"30","room_name":"样板房4号 13号楼283门222113","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif"}
         * memberCount : 1
         * memberList : [{"id":"30","headimgurl":"","real_name":"","sex":"3","birthday":"","tel":"15510151700"}]
         * total_price : 3000.00
         */

        private String id;
        private String merge_order_no;
        private String room_id;
        private String state;
        private String start_time;
        private String end_time;
        private String housing_price;
        private String status_name;
        private RoomInfoBean roomInfo;
        private int memberCount;
        private String total_price;
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

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }

        public RoomInfoBean getRoomInfo() {
            return roomInfo;
        }

        public void setRoomInfo(RoomInfoBean roomInfo) {
            this.roomInfo = roomInfo;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public List<MemberListBean> getMemberList() {
            return memberList;
        }

        public void setMemberList(List<MemberListBean> memberList) {
            this.memberList = memberList;
        }

        public static class RoomInfoBean {
            /**
             * id : 22
             * landlord_two_id : 30
             * room_name : 样板房4号 13号楼283门222113
             * image : http://let.tuokemao.com/Public/Common/Images/nopic.gif
             */

            private String id;
            private String landlord_two_id;
            private String room_name;
            private String image;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLandlord_two_id() {
                return landlord_two_id;
            }

            public void setLandlord_two_id(String landlord_two_id) {
                this.landlord_two_id = landlord_two_id;
            }

            public String getRoom_name() {
                return room_name;
            }

            public void setRoom_name(String room_name) {
                this.room_name = room_name;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }

        public static class MemberListBean {
            /**
             * id : 30
             * headimgurl :
             * real_name :
             * sex : 3
             * birthday :
             * tel : 15510151700
             */

            private String id;
            private String headimgurl;
            private String real_name;
            private String sex;
            private String birthday;
            private String tel;

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

            public String getReal_name() {
                return real_name;
            }

            public void setReal_name(String real_name) {
                this.real_name = real_name;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getTel() {
                return tel;
            }

            public void setTel(String tel) {
                this.tel = tel;
            }
        }
    }

    public static class ListsBean {
        /**
         * id : 56
         * landlord_two_id : 31
         * start_time : 1970-01-01
         * end_time : 1970-01-01
         * housing_price : 0.00
         * nickname : 15311431111
         */

        private String id;
        private String landlord_two_id;
        private String start_time;
        private String end_time;
        private String housing_price;
        private String nickname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLandlord_two_id() {
            return landlord_two_id;
        }

        public void setLandlord_two_id(String landlord_two_id) {
            this.landlord_two_id = landlord_two_id;
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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}

