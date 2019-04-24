package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HouseConfigBean implements Parcelable {

    /**
     * id : 2
     * name : 洗衣机
     */

    private int id;
    private String name;
    private String own_logo;
    private String un_own_logo;
    private String selected_logo;
    private String un_selected_logo;
    private int status;//0未选择， 1已选择


    protected HouseConfigBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        own_logo = in.readString();
        un_own_logo = in.readString();
        selected_logo = in.readString();
        un_selected_logo = in.readString();
        status = in.readInt();
    }

    public static final Creator<HouseConfigBean> CREATOR = new Creator<HouseConfigBean>() {
        @Override
        public HouseConfigBean createFromParcel(Parcel in) {
            return new HouseConfigBean(in);
        }

        @Override
        public HouseConfigBean[] newArray(int size) {
            return new HouseConfigBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwn_logo() {
        return own_logo;
    }

    public void setOwn_logo(String own_logo) {
        this.own_logo = own_logo;
    }

    public String getUn_own_logo() {
        return un_own_logo;
    }

    public void setUn_own_logo(String un_own_logo) {
        this.un_own_logo = un_own_logo;
    }

    public String getSelected_logo() {
        return selected_logo;
    }

    public void setSelected_logo(String selected_logo) {
        this.selected_logo = selected_logo;
    }

    public String getUn_selected_logo() {
        return un_selected_logo;
    }

    public void setUn_selected_logo(String un_selected_logo) {
        this.un_selected_logo = un_selected_logo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(own_logo);
        dest.writeString(un_own_logo);
        dest.writeString(selected_logo);
        dest.writeString(un_selected_logo);
        dest.writeInt(status);
    }


}
