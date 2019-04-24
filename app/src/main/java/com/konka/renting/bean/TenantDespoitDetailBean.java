package com.konka.renting.bean;

import java.util.List;

/**
 * Created by kaite on 2018/4/9.
 */

public class TenantDespoitDetailBean {

    /**
     * rentingInfo : {"id":"34","merge_order_no":"15232637775539","room_id":"19","state":"1","start_time":"2018-04-09","end_time":"2018-08-09","lock_deposit":"150.00","status_name":"申请中","roomInfo":{"id":"19","room_name":"样板房1号 13号楼283门222113","address":"西大东北门","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif"}}
     * lists : [{"id":"6","start_time":"2018-04-04","end_time":"2017-12-15","amount":"22.00"},{"id":"5","start_time":"2018-04-04","end_time":"2017-12-15","amount":"22.00"},{"id":"4","start_time":"2018-04-04","end_time":"2017-12-15","amount":"22.00"}]
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
         * id : 34
         * merge_order_no : 15232637775539
         * room_id : 19
         * state : 1
         * start_time : 2018-04-09
         * end_time : 2018-08-09
         * lock_deposit : 150.00
         * status_name : 申请中
         * roomInfo : {"id":"19","room_name":"样板房1号 13号楼283门222113","address":"西大东北门","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif"}
         */

        private String id;
        private String merge_order_no;
        private String room_id;
        private String state;
        private String start_time;
        private String end_time;
        private String lock_deposit;
        private String status_name;
        private RoomInfoBean roomInfo;

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

        public String getLock_deposit() {
            return lock_deposit;
        }

        public void setLock_deposit(String lock_deposit) {
            this.lock_deposit = lock_deposit;
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

        public static class RoomInfoBean {
            /**
             * id : 19
             * room_name : 样板房1号 13号楼283门222113
             * address : 西大东北门
             * image : http://let.tuokemao.com/Public/Common/Images/nopic.gif
             */

            private String id;
            private String room_name;
            private String address;
            private String image;

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

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }
    }

    public static class ListsBean {
        /**
         * id : 6
         * start_time : 2018-04-04
         * end_time : 2017-12-15
         * amount : 22.00
         */

        private String id;
        private String start_time;
        private String end_time;
        private String amount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

}
