package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RoomSearchInfoBean implements Parcelable {
    /**
     * room_id : 423
     * room_no : 154354404186141759
     * room_name : 康佳研发大厦b18
     * water_rent : 0.00
     * electric_rent : 0.00
     * litter_rent : 0.00
     * property_rent : 0.00
     * cost_rent : 0.00
     * address : 广东省深圳市宝安区粤海街道高新园康佳研发大厦B座18
     * total_floor : 30
     * room_type_id : 137
     * floor : 18
     * measure_area : 60
     * config : 床
     * explain : 康佳集团
     * image : ["https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/6d2a3b3078cca849098a7858dc9864e2.jpg","https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/f3d03c53e282baa715e35337bc6cd294.jpg","https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/95bad314501554a8393633c584547eec.jpg"]
     * auth_image : []
     * room_status : 4
     * housing_price : 1800.00
     * type : 2
     * publish_time : 2018-11-30 10:14:01
     * thumb_auth_image : []
     * thumb_image : ["https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/thumb_6d2a3b3078cca849098a7858dc9864e2.jpg","https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/thumb_f3d03c53e282baa715e35337bc6cd294.jpg","https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/30/thumb_95bad314501554a8393633c584547eec.jpg"]
     * landlord_name : 杨俊豪
     * landlord_phone : 18318701765
     * room_type : 一房一厅
     */

    private String room_id;
    private String room_no;
    private String room_name;
    private String water_rent;
    private String electric_rent;
    private String litter_rent;
    private String property_rent;
    private String cost_rent;
    private String address;
    private int total_floor;
    private int room_type_id;
    private int floor;
    private int measure_area;
    private List<HouseConfigBean> config;
    private String remark;
    private String explain;
    private int room_status;
    private String housing_price;
    private int type;
    private String publish_time;
    private String landlord_name;
    private String landlord_phone;
    private String member_name;
    private String member_phone;
    private String room_type;
    private List<String> image;
    private List<String> auth_image;
    private List<String> thumb_auth_image;
    private List<String> thumb_image;
    private int is_rent;//判断租客是否具有租此房源的条件 0不可以申请 1可以申请


    protected RoomSearchInfoBean(Parcel in) {
        room_id = in.readString();
        room_no = in.readString();
        room_name = in.readString();
        water_rent = in.readString();
        electric_rent = in.readString();
        litter_rent = in.readString();
        property_rent = in.readString();
        cost_rent = in.readString();
        address = in.readString();
        total_floor = in.readInt();
        room_type_id = in.readInt();
        floor = in.readInt();
        measure_area = in.readInt();
        config = in.createTypedArrayList(HouseConfigBean.CREATOR);
        remark=in.readString();
        explain = in.readString();
        room_status = in.readInt();
        housing_price = in.readString();
        type = in.readInt();
        publish_time = in.readString();
        landlord_name = in.readString();
        landlord_phone = in.readString();
        member_name = in.readString();
        member_phone = in.readString();
        room_type = in.readString();
        image = in.createStringArrayList();
        auth_image = in.createStringArrayList();
        thumb_auth_image = in.createStringArrayList();
        thumb_image = in.createStringArrayList();
        is_rent = in.readInt();
    }

    public static final Creator<RoomSearchInfoBean> CREATOR = new Creator<RoomSearchInfoBean>() {
        @Override
        public RoomSearchInfoBean createFromParcel(Parcel in) {
            return new RoomSearchInfoBean(in);
        }

        @Override
        public RoomSearchInfoBean[] newArray(int size) {
            return new RoomSearchInfoBean[size];
        }
    };

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotal_floor() {
        return total_floor;
    }

    public void setTotal_floor(int total_floor) {
        this.total_floor = total_floor;
    }

    public int getRoom_type_id() {
        return room_type_id;
    }

    public void setRoom_type_id(int room_type_id) {
        this.room_type_id = room_type_id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getMeasure_area() {
        return measure_area;
    }

    public void setMeasure_area(int measure_area) {
        this.measure_area = measure_area;
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

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getRoom_status() {
        return room_status;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public String getHousing_price() {
        return housing_price;
    }

    public void setHousing_price(String housing_price) {
        this.housing_price = housing_price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return publish_time;
    }

    public void setTime(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getLandlord_name() {
        return landlord_name;
    }

    public void setLandlord_name(String landlord_name) {
        this.landlord_name = landlord_name;
    }

    public String getLandlord_phone() {
        return landlord_phone;
    }

    public void setLandlord_phone(String landlord_phone) {
        this.landlord_phone = landlord_phone;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public List<String> getAuth_image() {
        return auth_image;
    }

    public void setAuth_image(List<String> auth_image) {
        this.auth_image = auth_image;
    }

    public List<String> getThumb_auth_image() {
        return thumb_auth_image;
    }

    public void setThumb_auth_image(List<String> thumb_auth_image) {
        this.thumb_auth_image = thumb_auth_image;
    }

    public List<String> getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(List<String> thumb_image) {
        this.thumb_image = thumb_image;
    }

    public int getIs_rent() {
        return is_rent;
    }

    public void setIs_rent(int is_rent) {
        this.is_rent = is_rent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(room_id);
        dest.writeString(room_no);
        dest.writeString(room_name);
        dest.writeString(water_rent);
        dest.writeString(electric_rent);
        dest.writeString(litter_rent);
        dest.writeString(property_rent);
        dest.writeString(cost_rent);
        dest.writeString(address);
        dest.writeInt(total_floor);
        dest.writeInt(room_type_id);
        dest.writeInt(floor);
        dest.writeInt(measure_area);
        dest.writeTypedList(config);
        dest.writeString(remark);
        dest.writeString(explain);
        dest.writeInt(room_status);
        dest.writeString(housing_price);
        dest.writeInt(type);
        dest.writeString(publish_time);
        dest.writeString(landlord_name);
        dest.writeString(landlord_phone);
        dest.writeString(member_name);
        dest.writeString(member_phone);
        dest.writeString(room_type);
        dest.writeStringList(image);
        dest.writeStringList(auth_image);
        dest.writeStringList(thumb_auth_image);
        dest.writeStringList(thumb_image);
        dest.writeInt(is_rent);
    }
}
