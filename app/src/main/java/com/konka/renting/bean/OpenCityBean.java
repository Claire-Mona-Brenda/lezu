package com.konka.renting.bean;

public class OpenCityBean {
    /**
     * city_id : 60
     * city_name : 厦门市
     * is_hot : 0
     * pinyin : xiamen
     * province : 4
     * index : X
     */

    private int city_id;
    private String city_name;
    private int is_hot;
    private String pinyin;
    private int province;
    private String index;

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
