package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HouseOrderInfoBean implements Parcelable {
    /**
     * room_id : 418
     * room_no : 15427865732160
     * room_name : rrr
     * address : 浙江省杭州市滨江区rrr
     * image : https://lettest.youlejiakeji.com/Uploads/Image/App/20181121/15427865736859.jpg
     * room_status : 0
     * is_del : 1
     * type : 0
     * service_date : 0
     * "device_id": "",
     * "gateway_id": ""
     */

    private int room_id;//	房产id
    private String room_no;//	房产编号
    private String room_name;//	房产名称
    private String address;//	详细地址
    private String image;//	房产图片
    private String thumb_image;//房产缩列图
    private int room_status;//	房产状态 1未缴纳安装费 2待安装认证 3待发布 4已发布 5已出租
    private int is_del;//		1正常 2申请删除
    private int type;//		出租类型 1短租 2长租
    private int service_status;//		服务费状态 0已过期 1正常
    private String service_date;//		服务费到期时间
    private String device_id;//设备id
    private String gateway_id;//网关id
    private int is_install;//0未缴纳安装费 1已缴纳安装费

    protected HouseOrderInfoBean(Parcel in) {
        room_id = in.readInt();
        room_no = in.readString();
        room_name = in.readString();
        address = in.readString();
        image = in.readString();
        room_status = in.readInt();
        is_del = in.readInt();
        type = in.readInt();
        service_status = in.readInt();
        service_date = in.readString();
        device_id = in.readString();
        gateway_id = in.readString();
        is_install = in.readInt();
    }

    public static final Creator<HouseOrderInfoBean> CREATOR = new Creator<HouseOrderInfoBean>() {
        @Override
        public HouseOrderInfoBean createFromParcel(Parcel in) {
            return new HouseOrderInfoBean(in);
        }

        @Override
        public HouseOrderInfoBean[] newArray(int size) {
            return new HouseOrderInfoBean[size];
        }
    };

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public int getRoom_status() {
        return room_status;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public int getIs_install() {
        return is_install;
    }

    public void setIs_install(int is_install) {
        this.is_install = is_install;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(room_id);
        dest.writeString(room_no);
        dest.writeString(room_name);
        dest.writeString(address);
        dest.writeString(image);
        dest.writeInt(room_status);
        dest.writeInt(is_del);
        dest.writeInt(type);
        dest.writeInt(service_status);
        dest.writeString(service_date);
        dest.writeString(device_id);
        dest.writeString(gateway_id);
        dest.writeInt(is_install);
    }
}
