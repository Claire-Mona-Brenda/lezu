package com.konka.renting.bean;

public class GatewayDetailBean {
    /**
     * id : 316
     * gateway_no : 869300000000000
     * address : 康佳
     * gateway_name : 别名
     */

    private int id;
    private String gateway_no;
    private String address;
    private String gateway_name;
    private String wifi_name;
    private String wifi_password;
    private String network;
    private int version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGateway_no() {
        return gateway_no;
    }

    public void setGateway_no(String gateway_no) {
        this.gateway_no = gateway_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGateway_name() {
        return gateway_name;
    }

    public void setGateway_name(String gateway_name) {
        this.gateway_name = gateway_name;
    }

    public String getWifi_name() {
        return wifi_name;
    }

    public void setWifi_name(String wifi_name) {
        this.wifi_name = wifi_name;
    }

    public String getWifi_password() {
        return wifi_password;
    }

    public void setWifi_password(String wifi_password) {
        this.wifi_password = wifi_password;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
