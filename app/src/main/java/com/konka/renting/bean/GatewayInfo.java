package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by jzxiang on 26/01/2018.
 */
@DatabaseTable(tableName = "gateway")
public class GatewayInfo implements Parcelable {


    /**
     * id : 316
     * gateway_name : 测试1
     * gateway_version : 1
     * network : 0
     */

    private String id;
    private String gateway_name;
    private String gateway_version;
    private int network;//联网方式 1:2g 2:wifi 3:以太网

    protected GatewayInfo(Parcel in) {
        id = in.readString();
        gateway_name = in.readString();
        gateway_version = in.readString();
        network = in.readInt();
    }

    public static final Creator<GatewayInfo> CREATOR = new Creator<GatewayInfo>() {
        @Override
        public GatewayInfo createFromParcel(Parcel in) {
            return new GatewayInfo(in);
        }

        @Override
        public GatewayInfo[] newArray(int size) {
            return new GatewayInfo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGateway_name() {
        return gateway_name;
    }

    public void setGateway_name(String gateway_name) {
        this.gateway_name = gateway_name;
    }

    public String getGateway_version() {
        return gateway_version;
    }

    public void setGateway_version(String gateway_version) {
        this.gateway_version = gateway_version;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(gateway_name);
        dest.writeString(gateway_version);
        dest.writeInt(network);
    }
}
