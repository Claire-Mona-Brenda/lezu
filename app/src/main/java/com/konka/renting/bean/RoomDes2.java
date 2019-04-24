package com.konka.renting.bean;

import android.os.Parcelable;

import java.util.List;

/**
 * Created by ATI on 2018/5/5.
 */

public class RoomDes2 {
        //        roomInfo：租房信息
//        roomInfo[water_rent]：水费
//        roomInfo[electric_rent]：电费
//        roomInfo[litter_rent]：垃圾处理费
//        roomInfo[property_rent]：物业费
//        roomInfo[cost_rent]：网费
//        roomInfo[monthly_fee]：门锁流量月费
//        roomInfo[year_fee]：门锁流量年费
//        roomInfo[lock_deposit]：门锁押金
//        roomInfo[login_name]：电话号码
        public String room_description;
        public String room_name;
        public String name;
        public String id;
        //房屋ID
        public String door_number;
        //门牌号
        public String housing_price;
        //每月租金
        public String building_no;
        //楼栋号
        public String door_no;
        //楼门号
        //房屋图片
        public String floor;
        //楼层
        public String total_floor;
        //总楼层
        public String address;
        //详细地址
        public String is_lease;
        //是否已租出0否1是
        public String lease_start_time;
        //出租开始时间
        public String lease_end_time;
        //出租结束时间
        public String member_count;
        //租客人数
        public List<RoomInfo.Member> memberList;
        //租客信息
        public String memberListid;
        //租客ID
        public String totalPages;
        public String measure_area;
        public String area_id, monthly_fee, year_fee, login_name;
        public String province;
        public String city;
        public String area;
        public String water_rent;
        public String electric_rent;
        public String litter_rent;
        public String property_rent;
        public String cost_rent;
        public String config;
        public String configdata[];
        public String lease_type;
        public String room_type;
        public String landlord_real_name;
        public String landlord_tel;
        public String orientation;
        public String time;
        public String lock_deposit;
        public String landlord_id;
        public String lease_type_id;
        public String room_type_id;
        public String orientation_id;
        public String initial_water;
        public String initial_electric;
        public String gateway_account;
        public String gateway_pwd;
        public String province_id;
        public String city_id;
        public String image;
        public String type;
        public String room_no;
        public String is_authentication;//是否通过认证0否1是
        public String[] authentication_image;//房屋认证图片
        public String province_name;//]：省份名
        public String city_name;//]：城市名
        public String area_name;//]：县区名
        public String total_price;
        public String memberCount;
    public List<RoomInfo.HouseSzHis> lists;
    public RoomDes2 roomInfo;
}
