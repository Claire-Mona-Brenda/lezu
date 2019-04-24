package com.konka.renting.bean;

public class HowBean {
    int index;
    String name;
    int imgId;


    public HowBean(int index, String name, int imgId) {
        this.index = index;
        this.name = name;
        this.imgId = imgId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
