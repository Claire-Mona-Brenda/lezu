package com.konka.renting.event;

/**
 * Created by jzxiang on 27/03/2018.
 */

public class ToSearchResultEvent {
    public String content;
    public String city;

    public ToSearchResultEvent(String content,String city) {
        this.content = content;
        this.city = city;
    }
}
