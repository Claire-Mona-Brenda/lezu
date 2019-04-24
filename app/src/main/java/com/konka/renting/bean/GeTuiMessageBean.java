package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Nate Robinson
 * @Time 2018/1/22
 */

public class GeTuiMessageBean implements Parcelable {


    /**
     * id : 17
     * title : 测试1234
     * content : 测试1234
     * created_at : 2018-01-23 15:25:46
     */

    private String id;
    private String title;
    private String content;
    private String created_at;
    private boolean isRead;
    /**
     * all_system_msg：系统推送全部用户消息，title和content都不定
     single_user_system_msg：系统推送单个用户消息，title和content都不定
     share_device：共享设备
     new_order：新订单
     */
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.created_at);
        dest.writeString(this.type);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
    }

    public GeTuiMessageBean() {
    }

    protected GeTuiMessageBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.created_at = in.readString();
        this.type = in.readString();
        this.isRead = in.readByte() != 0;
    }

    public static final Creator<GeTuiMessageBean> CREATOR = new Creator<GeTuiMessageBean>() {
        @Override
        public GeTuiMessageBean createFromParcel(Parcel source) {
            return new GeTuiMessageBean(source);
        }

        @Override
        public GeTuiMessageBean[] newArray(int size) {
            return new GeTuiMessageBean[size];
        }
    };
}
