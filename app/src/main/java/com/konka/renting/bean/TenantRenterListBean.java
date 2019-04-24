package com.konka.renting.bean;

import java.util.List;

public class TenantRenterListBean {
    /**
     * member : {"real_name":"杨俊豪","phone":"18927477564","headimgurl":"https://lezuxiaowo-test.youlejiakeji.com/public/common/images/nopic.gif"}
     * list : [{"id":2,"real_name":"杨俊豪","phone":"18318701765","headimgurl":"https://lettest.youlejiakeji.com/Uploads/Image/App/20181116/15423528568279.jpg","status":0}]
     */

    private MemberBean member;//	承租人信息
    private List<ListBean> list;//	合租人信息

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class MemberBean {
        /**
         * real_name : 杨俊豪
         * phone : 18927477564
         * headimgurl : https://lezuxiaowo-test.youlejiakeji.com/public/common/images/nopic.gif
         */

        private String real_name;
        private String phone;
        private String headimgurl;

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

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }
    }

    public static class ListBean {
        /**
         * id : 2
         * real_name : 杨俊豪
         * phone : 18318701765
         * headimgurl : https://lettest.youlejiakeji.com/Uploads/Image/App/20181116/15423528568279.jpg
         * status : 0
         */

        private String id;
        private String real_name;
        private String phone;
        private String headimgurl;
        private int status;//合租状态 0申请中 1已同意

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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
