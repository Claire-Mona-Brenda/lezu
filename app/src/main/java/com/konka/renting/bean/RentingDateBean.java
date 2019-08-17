package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RentingDateBean  implements Parcelable {

    /**
     * start_time : 2018-11-29 14:00:00
     * end_time : 20189-11-29 14:00:00
     */

    private String start_time;
    private String end_time;

    protected RentingDateBean(Parcel in) {
        start_time = in.readString();
        end_time = in.readString();
    }

    public static final Creator<RentingDateBean> CREATOR = new Creator<RentingDateBean>() {
        @Override
        public RentingDateBean createFromParcel(Parcel in) {
            return new RentingDateBean(in);
        }

        @Override
        public RentingDateBean[] newArray(int size) {
            return new RentingDateBean[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(start_time);
        dest.writeString(end_time);
    }
}
