package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RoomGroupListBean  implements Parcelable {
    /**
     * room_group_id : 413
     * name : 西丽地铁站
     * address : 广东省深圳市南山区西丽地铁站
     */

    private int room_group_id;
    private String name;
    private String address;

    protected RoomGroupListBean(Parcel in) {
        room_group_id = in.readInt();
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<RoomGroupListBean> CREATOR = new Creator<RoomGroupListBean>() {
        @Override
        public RoomGroupListBean createFromParcel(Parcel in) {
            return new RoomGroupListBean(in);
        }

        @Override
        public RoomGroupListBean[] newArray(int size) {
            return new RoomGroupListBean[size];
        }
    };

    public int getRoom_group_id() {
        return room_group_id;
    }

    public void setRoom_group_id(int room_group_id) {
        this.room_group_id = room_group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(room_group_id);
        parcel.writeString(name);
        parcel.writeString(address);
    }
}
