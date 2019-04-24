package com.konka.renting.bean;

public class CheckGatewayStatusBean {

    /**
     * power : 0
     * status : 1
     * rf_signal : 0
     * gprs : 22
     * bat : 1%
     */

    private int power;//连接电源 0未接入外电 1接入外电
    private int status;//状态 0空闲 1忙碌
    private int rf_signal;//射频信号强度
    private int gprs;//GPRS信号强度
    private String bat;//电量

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRf_signal() {
        return rf_signal;
    }

    public void setRf_signal(int rf_signal) {
        this.rf_signal = rf_signal;
    }

    public int getGprs() {
        return gprs;
    }

    public void setGprs(int gprs) {
        this.gprs = gprs;
    }

    public String getBat() {
        return bat;
    }

    public void setBat(String bat) {
        this.bat = bat;
    }
}
