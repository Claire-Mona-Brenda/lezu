package com.konka.renting.bean;

public class RoomPriceAreaBean {
    /**
     * id : 6
     * name : 1000以下
     * min_price : 0
     * max_price : 1000
     */

    private int id;
    private String name;
    private int min_price;
    private int max_price;

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

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }

    public int getMax_price() {
        return max_price;
    }

    public void setMax_price(int max_price) {
        this.max_price = max_price;
    }
}
