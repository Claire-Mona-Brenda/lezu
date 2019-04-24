package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbl on 18-3-27.
 */

public class PayOrder implements Parcelable {
//    lists：订单数据
    public String id;
//：订单ID
    public String merge_order_no;
//合并订单号
    public String landlord_two_id;
//管理员ID
    public String mid;
//租客ID
    public String room_id;

    public String deviceid;

    public String gatewayid;
//房产ID
    public String status;
    public String order_status;
    public String refund_status;
//订单状态0申请中1入住中2退房中3已过期4已拒绝
    public String status_name;
//订单状态（中文）
    public String start_time;
//开始时间
    public String end_time;
//结束时间
    public String housing_price;
    public String  deposit_price;
    public String  apply_status;
    public String  order_no;
    public String  apply_renew;
    public String   boon_amount;
    public String   renting_mode;
    public String   is_comment;
    public String pay_time;
    public  boolean is_commented(){
        return is_comment.equals("1");
    }

//租金
//房产数据
    public RoomInfo roomInfo;
    public ArrayList<RoomInfo.Member> memberList;
    public List <PayOrder>lists;
    public String  pay_status;
    public PayOrder() {
    }

    public boolean isOnLine(){
        return renting_mode.equals("1");
    }
    public String getrenting_mode(){
        if(renting_mode.equals("1")) {
            return "线上支付";
        }else{
            return "线下支付";
        }
    }
public boolean getPay_status(){
        return  pay_status.equals("1");
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.merge_order_no);
        dest.writeString(this.landlord_two_id);
        dest.writeString(this.mid);
        dest.writeString(this.room_id);
        dest.writeString(this.deviceid);
        dest.writeString(this.gatewayid);
        dest.writeString(this.status);
        dest.writeString(this.order_status);
        dest.writeString(this.refund_status);
        dest.writeString(this.status_name);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeString(this.housing_price);
        dest.writeString(this.deposit_price);
        dest.writeString(this.apply_status);
        dest.writeString(this.order_no);
        dest.writeString(this.apply_renew);
        dest.writeString(this.boon_amount);
        dest.writeString(this.renting_mode);
        dest.writeString(this.is_comment);
        dest.writeString(this.pay_time);
        dest.writeParcelable(this.roomInfo, flags);
        dest.writeTypedList(this.memberList);
        dest.writeTypedList(this.lists);
        dest.writeString(this.pay_status);
    }

    protected PayOrder(Parcel in) {
        this.id = in.readString();
        this.merge_order_no = in.readString();
        this.landlord_two_id = in.readString();
        this.mid = in.readString();
        this.room_id = in.readString();
        this.deviceid = in.readString();
        this.gatewayid = in.readString();
        this.status = in.readString();
        this.order_status = in.readString();
        this.refund_status = in.readString();
        this.status_name = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.housing_price = in.readString();
        this.deposit_price = in.readString();
        this.apply_status = in.readString();
        this.order_no = in.readString();
        this.apply_renew = in.readString();
        this.boon_amount = in.readString();
        this.renting_mode = in.readString();
        this.is_comment = in.readString();
        this.pay_time = in.readString();
        this.roomInfo = in.readParcelable(RoomInfo.class.getClassLoader());
        this.memberList = in.createTypedArrayList(RoomInfo.Member.CREATOR);
        this.lists = in.createTypedArrayList(PayOrder.CREATOR);
        this.pay_status = in.readString();
    }

    public static final Creator<PayOrder> CREATOR = new Creator<PayOrder>() {
        @Override
        public PayOrder createFromParcel(Parcel source) {
            return new PayOrder(source);
        }

        @Override
        public PayOrder[] newArray(int size) {
            return new PayOrder[size];
        }
    };
}
