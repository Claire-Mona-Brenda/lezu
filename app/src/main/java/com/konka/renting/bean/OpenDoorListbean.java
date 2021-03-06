package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by kaite on 2018/5/22.
 */

public class OpenDoorListbean implements Parcelable {


    /**
     * room_id : 424
     * type : 2
     * status : 3
     * start_time : 2018-12-04
     * end_time : 2019-01-10
     * gateway_id :
     * device_id :
     * service_time : 0
     * room_name : 康佳1808
     * status_text : 入住中
     */

    private String room_id;
    private int type;
    private int status;
    private int is_install;
    private int is_rent;//是否有合租 1有 0否
    private int is_share_rent;//合租订单 1是 0否
    private int is_pub;//0未发布 1已发布
    private int is_generate_password;//0没有计算密码 1有计算密码
    private String start_time;
    private String end_time;
    private String gateway_id;
    private String gateway_version;
    private String device_id;
    private String service_time;
    private String room_name;
    private String status_text;
    private String password;
    private String order_no;
    private String order_id;
    private List<OpenHistoryLogBean> log;

    public OpenDoorListbean() {
    }

    protected OpenDoorListbean(Parcel in) {
        room_id = in.readString();
        type = in.readInt();
        status = in.readInt();
        is_install = in.readInt();
        is_rent = in.readInt();
        is_share_rent = in.readInt();
        is_pub = in.readInt();
        is_generate_password = in.readInt();
        start_time = in.readString();
        end_time = in.readString();
        gateway_id = in.readString();
        gateway_version = in.readString();
        device_id = in.readString();
        service_time = in.readString();
        room_name = in.readString();
        status_text = in.readString();
        password = in.readString();
        order_no = in.readString();
        order_id = in.readString();
        log = in.createTypedArrayList(OpenHistoryLogBean.CREATOR);
    }

    public static final Creator<OpenDoorListbean> CREATOR = new Creator<OpenDoorListbean>() {
        @Override
        public OpenDoorListbean createFromParcel(Parcel in) {
            return new OpenDoorListbean(in);
        }

        @Override
        public OpenDoorListbean[] newArray(int size) {
            return new OpenDoorListbean[size];
        }
    };

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIs_install() {
        return is_install;
    }

    public void setIs_install(int is_install) {
        this.is_install = is_install;
    }

    public int getIs_rent() {
        return is_rent;
    }

    public void setIs_rent(int is_rent) {
        this.is_rent = is_rent;
    }

    public int getIs_share_rent() {
        return is_share_rent;
    }

    public void setIs_share_rent(int is_share_rent) {
        this.is_share_rent = is_share_rent;
    }

    public int getIs_pub() {
        return is_pub;
    }

    public void setIs_pub(int is_pub) {
        this.is_pub = is_pub;
    }

    public int getIs_generate_password() {
        return is_generate_password;
    }

    public void setIs_generate_password(int is_generate_password) {
        this.is_generate_password = is_generate_password;
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

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getGateway_version() {
        return gateway_version;
    }

    public void setGateway_version(String gateway_version) {
        this.gateway_version = gateway_version;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public List<OpenHistoryLogBean> getLog() {
        return log;
    }

    public void setLog(List<OpenHistoryLogBean> log) {
        this.log = log;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(room_id);
        parcel.writeInt(type);
        parcel.writeInt(status);
        parcel.writeInt(is_install);
        parcel.writeInt(is_rent);
        parcel.writeInt(is_share_rent);
        parcel.writeInt(is_pub);
        parcel.writeInt(is_generate_password);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
        parcel.writeString(gateway_id);
        parcel.writeString(gateway_version);
        parcel.writeString(device_id);
        parcel.writeString(service_time);
        parcel.writeString(room_name);
        parcel.writeString(status_text);
        parcel.writeString(password);
        parcel.writeString(order_no);
        parcel.writeString(order_id);
        parcel.writeTypedList(log);
    }
}
