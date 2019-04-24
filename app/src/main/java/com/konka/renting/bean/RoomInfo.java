package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;

public class RoomInfo implements Parcelable {

    /**
     * id : 2
     * housing_price : 4.00
     * measure_area : 1.00
     * building_no : 1
     * door_no : 1
     * image : http://let.tuokemao.com/Public/Common/Images/nopic.gif
     * floor : 1
     * total_floor : 1
     * lease_type : 小区房
     * room_type : 公寓
     */

    public String lease_type;
    public String room_type;
    public String room_description;
    public String name;
    public String room_name;
    public String id;//房屋ID
    public String door_number;//门牌号
    public String housing_price;//每月租金
    public String building_no;//楼栋号
    public String door_no;//看房密码
    public String lock_pwd;//楼门号
    public String image;//房屋图片
    public String floor;//楼层
    public String total_floor;//总楼层
    public String address;//详细地址
    public String is_lease;//0未出租，1已出租
    public String lease_start_time;//出租开始时间
    public String lease_end_time;//出租结束时间
    public String member_count;//租客人数
    public List<Member> memberList;//租客信息
    public String memberListid;//租客ID
    public String totalPages;
    public String measure_area;
    public RoomDescription roomInfo;
    public RoomDescription info;
    public String time;
    public String landlord_id;
    public String lease_type_id;
    public String room_type_id;
    public String orientation_id;
    public String is_hot;
    public String order_no;
    public String is_on_sale;
    public String is_boon;
    public String is_refund_boon;
    public String initial_water;
    public String initial_electric;
    public List<HouseSzHis> lists;
    public String type;
    public String boon_amount;
    public HashMap rentingInfo;//]：总租金
    public String is_lock;//
    public String lng;
    public String lat;
    public String deviceid;
    public String gatewayid;
    public static String TOTAL_PRICE = "total_price";
    public String is_renting;
    public List<Comment> commentList;
    public String commentCount;
    public String renter_real_name;
    public String status;//1未发布，2未租出，3未确认，4已出租
    public String is_cancel;//0可以取消发布，1不可取消发布

    public boolean getIsLock() {
        return is_lock.equals("1");
    }
    public boolean isRenting() {
        return is_renting.equals("1");
    }
//    public boolean isLease() {
//        return is_lease != null && is_lease.equals("1");
//    }
    public boolean isRentOut(){
        return is_lease.equals("1");
    }


    //    lists[type]：出租类型 1短租 2长租
    public String getType() {
        String temp="";
        if (TextUtils.isEmpty(type)) {
            return "无type字段";
        }
        if (type.equals("1")) {
            temp = "短租";
        } else if (type.equals("2")) {
            temp = "长租";
        }
        return temp;
    }

    public boolean isLongRent() {
        return !TextUtils.isEmpty(type) && type.equals("2");
    }

    //    lists[is_on_sale]：上架状态0停止出租1开始出租
//    lists[is_boon]：是否缴纳保证金0否1是
//    lists[is_refund_boon]：是否可退保证金0否1是
    public String getRoomTitle() {
        return room_name;
    }

    //    lists[is_hot]：是否热门0否1是
//    lists[lease_type]：租凭类型;
    //总页码数

    public static class Comment implements Parcelable{
        public String id;//：评论ID
        public String mid;//：评论人ID
        public String content;//：评论内容
        public String score;//：评分
        public String[] image_list;//：评论图集
        public String time;//：评论时间
        public String is_anonymous;//：是否匿名0否1是
        public Member memberInfo;//[id;//：评论人ID

        public Comment() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.mid);
            dest.writeString(this.content);
            dest.writeString(this.score);
            dest.writeStringArray(this.image_list);
            dest.writeString(this.time);
            dest.writeString(this.is_anonymous);
            dest.writeParcelable(this.memberInfo, flags);
        }

        protected Comment(Parcel in) {
            this.id = in.readString();
            this.mid = in.readString();
            this.content = in.readString();
            this.score = in.readString();
            this.image_list = in.createStringArray();
            this.time = in.readString();
            this.is_anonymous = in.readString();
            this.memberInfo = in.readParcelable(Member.class.getClassLoader());
        }

        public static final Creator<Comment> CREATOR = new Creator<Comment>() {
            @Override
            public Comment createFromParcel(Parcel source) {
                return new Comment(source);
            }

            @Override
            public Comment[] newArray(int size) {
                return new Comment[size];
            }
        };
    }
    public static class Member implements Parcelable {
        public String headimgurl;

        /**
         * id : 16
         * real_name : dsq
         * sex : 2
         * birthday : 5
         * tel : 631231
         */

        public String id;
        public String real_name;
        public String nickname;
        public int sex;
        public String birthday;
        public String tel;

        public String getGender() {
            switch (sex){
                case 1:
                    return "男";
                case 2:
                    return "女";

                default:
                        return "保密";
            }
        }

        public Member() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.headimgurl);
            dest.writeString(this.id);
            dest.writeString(this.real_name);
            dest.writeString(this.nickname);
            dest.writeInt(this.sex);
            dest.writeString(this.birthday);
            dest.writeString(this.tel);
        }

        protected Member(Parcel in) {
            this.headimgurl = in.readString();
            this.id = in.readString();
            this.real_name = in.readString();
            this.nickname = in.readString();
            this.sex = in.readInt();
            this.birthday = in.readString();
            this.tel = in.readString();
        }

        public static final Creator<Member> CREATOR = new Creator<Member>() {
            @Override
            public Member createFromParcel(Parcel source) {
                return new Member(source);
            }

            @Override
            public Member[] newArray(int size) {
                return new Member[size];
            }
        };
    }

    public static class HouseSzHis implements Parcelable {
        public String id;//交租订单ID
        public String start_time;//入住开始时间
        public String end_time;//入住结束时间
        public String housing_price;//本次租金
        public String nickname;//交租人

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.start_time);
            dest.writeString(this.end_time);
            dest.writeString(this.housing_price);
            dest.writeString(this.nickname);
        }

        public HouseSzHis() {
        }

        protected HouseSzHis(Parcel in) {
            this.id = in.readString();
            this.start_time = in.readString();
            this.end_time = in.readString();
            this.housing_price = in.readString();
            this.nickname = in.readString();
        }

        public static final Creator<HouseSzHis> CREATOR = new Creator<HouseSzHis>() {
            @Override
            public HouseSzHis createFromParcel(Parcel source) {
                return new HouseSzHis(source);
            }

            @Override
            public HouseSzHis[] newArray(int size) {
                return new HouseSzHis[size];
            }
        };
    }

    public static class RoomDescription implements Parcelable {
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
        public List<Member> memberList;
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
        public String[] image;
        public String type;
        public String room_no;
        public String is_authentication;//是否通过认证0否1是
        public String[] authentication_image;//房屋认证图片
        public String province_name;//]：省份名
        public String city_name;//]：城市名
        public String area_name;//]：县区名
        public String total_price;
        public String old_room_name;
        public String[] midArr;
        public String renter_real_name;
        public String is_renting;
        public boolean isRenting() {
            return is_renting.equals("1");
        }
        public String getType() {
            String temp;
            if (type.equals("1")) {
                temp = "短租";
            } else if (type.equals("2")) {
                temp = "长租";
            } else {
                temp = "已出租";
            }
            return temp;
        }


        public RoomDescription() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.room_description);
            dest.writeString(this.room_name);
            dest.writeString(this.name);
            dest.writeString(this.id);
            dest.writeString(this.door_number);
            dest.writeString(this.housing_price);
            dest.writeString(this.building_no);
            dest.writeString(this.door_no);
            dest.writeString(this.floor);
            dest.writeString(this.total_floor);
            dest.writeString(this.address);
            dest.writeString(this.is_lease);
            dest.writeString(this.lease_start_time);
            dest.writeString(this.lease_end_time);
            dest.writeString(this.member_count);
            dest.writeTypedList(this.memberList);
            dest.writeString(this.memberListid);
            dest.writeString(this.totalPages);
            dest.writeString(this.measure_area);
            dest.writeString(this.area_id);
            dest.writeString(this.monthly_fee);
            dest.writeString(this.year_fee);
            dest.writeString(this.login_name);
            dest.writeString(this.province);
            dest.writeString(this.city);
            dest.writeString(this.area);
            dest.writeString(this.water_rent);
            dest.writeString(this.electric_rent);
            dest.writeString(this.litter_rent);
            dest.writeString(this.property_rent);
            dest.writeString(this.cost_rent);
            dest.writeString(this.config);
            dest.writeStringArray(this.configdata);
            dest.writeString(this.lease_type);
            dest.writeString(this.room_type);
            dest.writeString(this.landlord_real_name);
            dest.writeString(this.landlord_tel);
            dest.writeString(this.orientation);
            dest.writeString(this.time);
            dest.writeString(this.lock_deposit);
            dest.writeString(this.landlord_id);
            dest.writeString(this.lease_type_id);
            dest.writeString(this.room_type_id);
            dest.writeString(this.orientation_id);
            dest.writeString(this.initial_water);
            dest.writeString(this.initial_electric);
            dest.writeString(this.gateway_account);
            dest.writeString(this.gateway_pwd);
            dest.writeString(this.province_id);
            dest.writeString(this.city_id);
            dest.writeStringArray(this.image);
            dest.writeString(this.type);
            dest.writeString(this.room_no);
            dest.writeString(this.is_authentication);
            dest.writeStringArray(this.authentication_image);
            dest.writeString(this.province_name);
            dest.writeString(this.city_name);
            dest.writeString(this.area_name);
            dest.writeString(this.total_price);
            dest.writeString(this.old_room_name);
            dest.writeStringArray(this.midArr);
            dest.writeString(this.renter_real_name);
        }

        protected RoomDescription(Parcel in) {
            this.room_description = in.readString();
            this.room_name = in.readString();
            this.name = in.readString();
            this.id = in.readString();
            this.door_number = in.readString();
            this.housing_price = in.readString();
            this.building_no = in.readString();
            this.door_no = in.readString();
            this.floor = in.readString();
            this.total_floor = in.readString();
            this.address = in.readString();
            this.is_lease = in.readString();
            this.lease_start_time = in.readString();
            this.lease_end_time = in.readString();
            this.member_count = in.readString();
            this.memberList = in.createTypedArrayList(Member.CREATOR);
            this.memberListid = in.readString();
            this.totalPages = in.readString();
            this.measure_area = in.readString();
            this.area_id = in.readString();
            this.monthly_fee = in.readString();
            this.year_fee = in.readString();
            this.login_name = in.readString();
            this.province = in.readString();
            this.city = in.readString();
            this.area = in.readString();
            this.water_rent = in.readString();
            this.electric_rent = in.readString();
            this.litter_rent = in.readString();
            this.property_rent = in.readString();
            this.cost_rent = in.readString();
            this.config = in.readString();
            this.configdata = in.createStringArray();
            this.lease_type = in.readString();
            this.room_type = in.readString();
            this.landlord_real_name = in.readString();
            this.landlord_tel = in.readString();
            this.orientation = in.readString();
            this.time = in.readString();
            this.lock_deposit = in.readString();
            this.landlord_id = in.readString();
            this.lease_type_id = in.readString();
            this.room_type_id = in.readString();
            this.orientation_id = in.readString();
            this.initial_water = in.readString();
            this.initial_electric = in.readString();
            this.gateway_account = in.readString();
            this.gateway_pwd = in.readString();
            this.province_id = in.readString();
            this.city_id = in.readString();
            this.image = in.createStringArray();
            this.type = in.readString();
            this.room_no = in.readString();
            this.is_authentication = in.readString();
            this.authentication_image = in.createStringArray();
            this.province_name = in.readString();
            this.city_name = in.readString();
            this.area_name = in.readString();
            this.total_price = in.readString();
            this.old_room_name = in.readString();
            this.midArr = in.createStringArray();
            this.renter_real_name = in.readString();
        }

        public static final Creator<RoomDescription> CREATOR = new Creator<RoomDescription>() {
            @Override
            public RoomDescription createFromParcel(Parcel source) {
                return new RoomDescription(source);
            }

            @Override
            public RoomDescription[] newArray(int size) {
                return new RoomDescription[size];
            }
        };
    }

    public RoomInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lease_type);
        dest.writeString(this.room_type);
        dest.writeString(this.room_description);
        dest.writeString(this.name);
        dest.writeString(this.room_name);
        dest.writeString(this.id);
        dest.writeString(this.door_number);
        dest.writeString(this.housing_price);
        dest.writeString(this.building_no);
        dest.writeString(this.door_no);
        dest.writeString(this.lock_pwd);
        dest.writeString(this.image);
        dest.writeString(this.floor);
        dest.writeString(this.total_floor);
        dest.writeString(this.address);
        dest.writeString(this.is_lease);
        dest.writeString(this.lease_start_time);
        dest.writeString(this.lease_end_time);
        dest.writeString(this.member_count);
        dest.writeTypedList(this.memberList);
        dest.writeString(this.memberListid);
        dest.writeString(this.totalPages);
        dest.writeString(this.measure_area);
        dest.writeParcelable(this.roomInfo, flags);
        dest.writeParcelable(this.info, flags);
        dest.writeString(this.time);
        dest.writeString(this.landlord_id);
        dest.writeString(this.lease_type_id);
        dest.writeString(this.room_type_id);
        dest.writeString(this.orientation_id);
        dest.writeString(this.is_hot);
        dest.writeString(this.order_no);
        dest.writeString(this.is_on_sale);
        dest.writeString(this.is_boon);
        dest.writeString(this.is_refund_boon);
        dest.writeString(this.initial_water);
        dest.writeString(this.initial_electric);
        dest.writeTypedList(this.lists);
        dest.writeString(this.type);
        dest.writeString(this.boon_amount);
        dest.writeSerializable(this.rentingInfo);
        dest.writeString(this.is_lock);
        dest.writeString(this.lng);
        dest.writeString(this.lat);
        dest.writeString(this.deviceid);
        dest.writeString(this.gatewayid);
        dest.writeString(this.is_renting);
        dest.writeTypedList(this.commentList);
        dest.writeString(this.commentCount);
        dest.writeString(this.renter_real_name);
        dest.writeString(this.status);
        dest.writeString(this.is_cancel);
    }

    protected RoomInfo(Parcel in) {
        this.lease_type = in.readString();
        this.room_type = in.readString();
        this.room_description = in.readString();
        this.name = in.readString();
        this.room_name = in.readString();
        this.id = in.readString();
        this.door_number = in.readString();
        this.housing_price = in.readString();
        this.building_no = in.readString();
        this.door_no = in.readString();
        this.lock_pwd = in.readString();
        this.image = in.readString();
        this.floor = in.readString();
        this.total_floor = in.readString();
        this.address = in.readString();
        this.is_lease = in.readString();
        this.lease_start_time = in.readString();
        this.lease_end_time = in.readString();
        this.member_count = in.readString();
        this.memberList = in.createTypedArrayList(Member.CREATOR);
        this.memberListid = in.readString();
        this.totalPages = in.readString();
        this.measure_area = in.readString();
        this.roomInfo = in.readParcelable(RoomDescription.class.getClassLoader());
        this.info = in.readParcelable(RoomDescription.class.getClassLoader());
        this.time = in.readString();
        this.landlord_id = in.readString();
        this.lease_type_id = in.readString();
        this.room_type_id = in.readString();
        this.orientation_id = in.readString();
        this.is_hot = in.readString();
        this.order_no = in.readString();
        this.is_on_sale = in.readString();
        this.is_boon = in.readString();
        this.is_refund_boon = in.readString();
        this.initial_water = in.readString();
        this.initial_electric = in.readString();
        this.lists = in.createTypedArrayList(HouseSzHis.CREATOR);
        this.type = in.readString();
        this.boon_amount = in.readString();
        this.rentingInfo = (HashMap) in.readSerializable();
        this.is_lock = in.readString();
        this.lng = in.readString();
        this.lat = in.readString();
        this.deviceid = in.readString();
        this.gatewayid = in.readString();
        this.is_renting = in.readString();
        this.commentList = in.createTypedArrayList(Comment.CREATOR);
        this.commentCount = in.readString();
        this.renter_real_name = in.readString();
        this.status = in.readString();
        this.is_cancel = in.readString();
    }

    public static final Creator<RoomInfo> CREATOR = new Creator<RoomInfo>() {
        @Override
        public RoomInfo createFromParcel(Parcel source) {
            return new RoomInfo(source);
        }

        @Override
        public RoomInfo[] newArray(int size) {
            return new RoomInfo[size];
        }
    };
}
