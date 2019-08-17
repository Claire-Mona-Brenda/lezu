package com.konka.renting.bean;

import java.util.List;

public class RenterOrderInfoBean {
    /**
     * order_id : 3
     * order_no : 154357705088047691
     * room_id : 423
     * status : 0
     * type : 2
     * start_time : 2018-11-30
     * end_time : 2018-12-31
     * housing_price : 1800.00
     * create_time : 2018-11-30 19:24:10
     * confirm_time : 0
     * renting_mode : 0
     * water_rent : 0.00
     * electric_rent : 0.00
     * litter_rent : 0.00
     * property_rent : 0.00
     * cost_rent : 0.00
     * config : 床
     * remark :
     * initial_water : 0
     * initial_electric : 0
     * order_type : 1
     * thumb_image : https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/thumb_6d2a3b3078cca849098a7858dc9864e2.jpg
     * room_name : 康佳研发大厦b18
     * landlord : {"real_name":"杨俊豪","phone":"18318701765"}
     * member : [{"real_name":"杨俊豪","phone":"18927477564","headimgurl":""}]
     */

    private String order_id;//订单id
    private String order_no;//订单编号
    private String room_id;//房产id
    private int status;//订单状态 1申请中 2已确认 3入住中 4申请退租 5过期
    private int type;//出租类型 1 短租 2长租
    private String start_time;//开始日期
    private String end_time;//结束日期
    private String housing_price;//租金
    private String create_time;//添加时间
    private int confirm_time;//确认时间
    private int renting_mode;//0 线下 1线上
    private String water_rent;//水费
    private String electric_rent;//电费
    private String litter_rent;//垃圾处理费
    private String property_rent;//物业费
    private String cost_rent;//网费
    private List<HouseConfigBean> config;//房间配置信息
    private String remark;//租客备注
    private int initial_water;//初始水
    private int initial_electric;//初始电
    private int order_type;//订单类型 1首租 2续租
    private String thumb_image;//房产缩略图
    private String room_name;//房产名称
    private LandlordBean landlord;//房东信息
    private List<MemberBean> member;//租客列表
    private int service_status;//服务费状态 1正常 0过期
    private String service_date;//服务费到期日期
    private int is_share;//合租发布是否开启 0关闭 1开启
    private String room_type;//房型
    private String address;//地址
    private String floor;//楼层
    private String total_floor;//楼层
    private String measure_area;//面积
    private int is_online;// 0线下

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(int confirm_time) {
        this.confirm_time = confirm_time;
    }

    public int getRenting_mode() {
        return renting_mode;
    }

    public void setRenting_mode(int renting_mode) {
        this.renting_mode = renting_mode;
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

    public List<HouseConfigBean> getConfig() {
        return config;
    }

    public void setConfig(List<HouseConfigBean> config) {
        this.config = config;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getInitial_water() {
        return initial_water;
    }

    public void setInitial_water(int initial_water) {
        this.initial_water = initial_water;
    }

    public int getInitial_electric() {
        return initial_electric;
    }

    public void setInitial_electric(int initial_electric) {
        this.initial_electric = initial_electric;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public LandlordBean getLandlord() {
        return landlord;
    }

    public void setLandlord(LandlordBean landlord) {
        this.landlord = landlord;
    }

    public List<MemberBean> getMember() {
        return member;
    }

    public void setMember(List<MemberBean> member) {
        this.member = member;
    }

    public int getService_status() {
        return service_status;
    }

    public void setService_status(int service_status) {
        this.service_status = service_status;
    }

    public String getService_date() {
        return service_date;
    }

    public void setService_date(String service_date) {
        this.service_date = service_date;
    }

    public int getIs_share() {
        return is_share;
    }

    public void setIs_share(int is_share) {
        this.is_share = is_share;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getMeasure_area() {
        return measure_area;
    }

    public void setMeasure_area(String measure_area) {
        this.measure_area = measure_area;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public static class LandlordBean {
        /**
         * real_name : 杨俊豪
         * phone : 18318701765
         */

        private String real_name;
        private String phone;
        private String thumb_headimgurl;
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

        public String getThumb_headimgurl() {
            return thumb_headimgurl;
        }

        public void setThumb_headimgurl(String thumb_headimgurl) {
            this.thumb_headimgurl = thumb_headimgurl;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }
    }

    public static class MemberBean {
        /**
         * real_name : 杨俊豪
         * phone : 18927477564
         * headimgurl :
         */

        private String real_name;
        private String phone;
        private String sex;
        private String headimgurl;
        private String thumb_headimgurl;

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

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getThumb_headimgurl() {
            return thumb_headimgurl;
        }

        public void setThumb_headimgurl(String thumb_headimgurl) {
            this.thumb_headimgurl = thumb_headimgurl;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }
    }
}
