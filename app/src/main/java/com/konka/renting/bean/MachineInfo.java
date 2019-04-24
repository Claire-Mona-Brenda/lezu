package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jzxiang on 27/12/2017.
 */

public class MachineInfo implements Parcelable {


    /**
     * id : 155
     * name : 门锁
     * logo : http://let.tuokemao.com/Uploads/Image/Admin/20180329/15222909186314.png
     */

    public String id;
    public String name;
    public String logo;


    public static MachineInfo createDoorLock(){
        MachineInfo machineInfo = new MachineInfo();
        machineInfo.id = "1";
        machineInfo.name = "门锁";
        return machineInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.logo);
    }

    public MachineInfo() {
    }

    protected MachineInfo(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.logo = in.readString();
    }

    public static final Creator<MachineInfo> CREATOR = new Creator<MachineInfo>() {
        @Override
        public MachineInfo createFromParcel(Parcel source) {
            return new MachineInfo(source);
        }

        @Override
        public MachineInfo[] newArray(int size) {
            return new MachineInfo[size];
        }
    };
}
