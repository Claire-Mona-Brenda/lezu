package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by jzxiang on 01/01/2018.
 */
public class DeviceInfo implements Parcelable {

    /**
     * id : 5
     * device_name : ggg
     * name : ggg
     * address : ggv
     * type : 0
     * status : true
     * state : false
     * created_at : 2018-01-13 16:44:04
     */
//    @DatabaseField(id = true,columnDefinition = "id")
//    public String id;
//    @DatabaseField(columnName = "device_name")
//    public String device_name;
//    @DatabaseField(columnName = "name")
//    public String name;
//    @DatabaseField(columnName = "address")
//    public String address;
////    @DatabaseField(columnName = "type")
//    public MachineInfo type;
//    @DatabaseField(columnName = "status")
//    public boolean status;
//    @DatabaseField(columnName = "state")
//    public boolean state;
//    @DatabaseField(columnName = "created_at")
//    public String created_at;
//    @DatabaseField(columnName = "is_alarm")
//    public boolean is_alarm;


    /**
     * id : 5
     * name : 咯哦哦
     * type_id : 155
     * status : 2
     * address :
     * time : 2018.05.15 00:07:14
     * end_time : 1970.01.01 08:00:00
     * image : http://let.tuokemao.com/Uploads/Image/Admin/20180328/15222215648415.jpg
     */
    public GatewayInfo gateway;
    public String id;
    public String name;
    public String type_id;
    public String type_name;
    public String time;
    public String image;
    private String status = "0";
//    public String end_time;
//    public String device_name;
//    public String address;
//
//    public String gateway_id;
//    public String device_address;
//
//    public String deviceid;
//    public String gatewayid;

    public boolean status() {
        return status.equals("0");
    }

    public String statusText() {
        if (status.equals("0")) {
            return "正常";
        } else if (status.equals("1")) {
            return "异常";
        } else {
            return "离线";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof DeviceInfo)) {
            return false;
        }

        if (id.equals(((DeviceInfo) obj).id))
            return true;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    public DeviceInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.gateway, flags);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type_id);
        dest.writeString(this.type_name);
        dest.writeString(this.status);
        dest.writeString(this.time);
        dest.writeString(this.image);
//        dest.writeString(this.address);
//        dest.writeString(this.end_time);
//        dest.writeString(this.device_name);
//        dest.writeString(this.gateway_id);
//        dest.writeString(this.device_address);
//        dest.writeString(this.deviceid);
//        dest.writeString(this.gatewayid);
    }

    protected DeviceInfo(Parcel in) {
        this.gateway = in.readParcelable(GatewayInfo.class.getClassLoader());
        this.id = in.readString();
        this.name = in.readString();
        this.type_id = in.readString();
        this.type_name = in.readString();
        this.status = in.readString();
        this.time = in.readString();
        this.image = in.readString();
//        this.address = in.readString();
//        this.end_time = in.readString();
//        this.device_name = in.readString();
//        this.gateway_id = in.readString();
//        this.device_address = in.readString();
//        this.deviceid = in.readString();
//        this.gatewayid = in.readString();
    }

    public static final Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel source) {
            return new DeviceInfo(source);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };
}
