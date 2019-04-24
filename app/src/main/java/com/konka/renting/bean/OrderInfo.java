package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jzxiang on 25/03/2018.
 */

public class OrderInfo implements Parcelable {

    /**
     * order_no : 15208376707376
     * room_id : 1
     * start_time : 2018-03-12
     * end_time : 2018-02-14
     * real_amount : 3.00
     * roomInfo : {"id":"OrderInfo1","address":"上地西路","building_no":"1","door_no":"1","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif","lease_type":"小区房","room_type":"公寓"}
     * status : 已付款
     */

    public String order_no;
    public String merge_order_no;
    public String room_id;
    public String start_time;
    public String end_time;
    public String real_amount;
    public String status;

    public String start_electric;
    public String start_water;
    public RoomInfo roomInfo;


    public OrderInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.order_no);
        dest.writeString(this.merge_order_no);
        dest.writeString(this.room_id);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeString(this.real_amount);
        dest.writeString(this.status);
        dest.writeString(this.start_electric);
        dest.writeString(this.start_water);
        dest.writeParcelable(this.roomInfo, flags);
    }

    protected OrderInfo(Parcel in) {
        this.order_no = in.readString();
        this.merge_order_no = in.readString();
        this.room_id = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.real_amount = in.readString();
        this.status = in.readString();
        this.start_electric = in.readString();
        this.start_water = in.readString();
        this.roomInfo = in.readParcelable(RoomInfo.class.getClassLoader());
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel source) {
            return new OrderInfo(source);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };
}
