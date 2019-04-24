package com.konka.renting.bean;

import java.util.List;

/**
 * Created by kaite on 2018/4/25.
 */

public class TenantManagerBean {


    /**
     * data : {"memberInfo":{"id":"34","nickname":"18636163349","headimgurl":"http://let.tuokemao.com/Uploads/Image/App/20180409/15232883577105.jpg","tel":"18636163349"},"lists":[{"id":"30","nickname":"15510151700","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"15510151700","apply_id":"5","state":"0"},{"id":"29","nickname":"13557889551","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"13557889551","apply_id":"6","state":"0"}]}
     * status : 1
     * info :
     */

    private DataBean data;
    private int status;
    private String info;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static class DataBean {
        /**
         * memberInfo : {"id":"34","nickname":"18636163349","headimgurl":"http://let.tuokemao.com/Uploads/Image/App/20180409/15232883577105.jpg","tel":"18636163349"}
         * lists : [{"id":"30","nickname":"15510151700","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"15510151700","apply_id":"5","state":"0"},{"id":"29","nickname":"13557889551","headimgurl":"http://let.tuokemao.com/Public/Common/Images/head.jpg","tel":"13557889551","apply_id":"6","state":"0"}]
         */

        private MemberInfoBean memberInfo;
        private List<ListsBean> lists;

        public MemberInfoBean getMemberInfo() {
            return memberInfo;
        }

        public void setMemberInfo(MemberInfoBean memberInfo) {
            this.memberInfo = memberInfo;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class MemberInfoBean {
            /**
             * id : 34
             * nickname : 18636163349
             * headimgurl : http://let.tuokemao.com/Uploads/Image/App/20180409/15232883577105.jpg
             * tel : 18636163349
             */

            private String id;
            private String nickname;
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

        public static class ListsBean {
            /**
             * id : 30
             * nickname : 15510151700
             * headimgurl : http://let.tuokemao.com/Public/Common/Images/head.jpg
             * tel : 15510151700
             * apply_id : 5
             * state : 0
             */

            private String id;
            private String nickname;
            private String headimgurl;
            private String tel;
            private String apply_id;
            private String state;

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

            public String getApply_id() {
                return apply_id;
            }

            public void setApply_id(String apply_id) {
                this.apply_id = apply_id;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }
        }
    }
}
