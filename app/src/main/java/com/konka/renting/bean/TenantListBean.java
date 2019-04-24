package com.konka.renting.bean;

import java.util.List;

public class TenantListBean {
    /**
     * room_name : 西乡客运站
     * renter : [{"id":85,"real_name":"周伟昕","phone":"17607619699"},{"id":92,"real_name":"董治中","phone":"18642197555"},{"id":96,"real_name":"李立山","phone":"18820216055"},{"id":119,"real_name":"杞耀军","phone":"13723723982"}]
     */

    private String room_name;
    private List<RenterBean> renter;

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public List<RenterBean> getRenter() {
        return renter;
    }

    public void setRenter(List<RenterBean> renter) {
        this.renter = renter;
    }

    public class RenterBean {
        /**
         * id : 85
         * real_name : 周伟昕
         * phone : 17607619699
         */

        private int id;
        private String real_name;
        private String phone;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
}
