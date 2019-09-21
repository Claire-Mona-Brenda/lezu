package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class OpenHistoryLogBean implements Parcelable {

    /**
     * id : 1801
     * details : APP开门
     * time : 2019-09-19 14:35
     */

    private int id;
    private String details;
    private String time;

    protected OpenHistoryLogBean(Parcel in) {
        id = in.readInt();
        details = in.readString();
        time = in.readString();
    }

    public static final Creator<OpenHistoryLogBean> CREATOR = new Creator<OpenHistoryLogBean>() {
        @Override
        public OpenHistoryLogBean createFromParcel(Parcel in) {
            return new OpenHistoryLogBean(in);
        }

        @Override
        public OpenHistoryLogBean[] newArray(int size) {
            return new OpenHistoryLogBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(details);
        parcel.writeString(time);
    }
}
