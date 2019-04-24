package com.konka.renting.bean;

import java.util.List;

/**
 * Created by jzxiang on 18/03/2018.
 */

public class HomeInfo {


    /**
     * message : {"is_msg":0}
     * bannerAdvList : [{"id":"45","link":"","image":"http://let.tuokemao.com/Uploads/Image/Admin/20180302/15199549355890.jpg"},{"id":"46","link":"","image":"http://let.tuokemao.com/Uploads/Image/Admin/20180302/15199549355890.jpg"}]
     * roomList : [{"id":"2","housing_price":"4.00","measure_area":"1.00","building_no":"1","door_no":"1","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif","floor":"1","total_floor":"1","lease_type":"小区房","room_type":"公寓"}]
     * totalPages : 1
     */

    public MessageInfo message;
    public int totalPages;
    public List<BannerAdvListInfo> bannerAdvList;
    public List<NoticeInfo> noticeList;
    public List<RoomInfo> roomList;
    public List<ArticleInfo> articleList;
    public String articleList_url;

    public static class MessageInfo {
        /**
         * is_msg : 0
         * 是否有未读信息0否1是
         */

        public int is_msg;
    }

    public static class BannerAdvListInfo {
        /**
         * id : 45
         * link :
         * image : http://let.tuokemao.com/Uploads/Image/Admin/20180302/15199549355890.jpg
         */

        public String id;
        public String link;
        public String image;
    }

    public static class NoticeInfo {
        public String content;
    }



}
