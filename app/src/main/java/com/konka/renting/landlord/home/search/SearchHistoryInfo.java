package com.konka.renting.landlord.home.search;

/**
 * Created by jzxiang on 26/03/2018.
 */

public class SearchHistoryInfo {

    public String content;
    public String key;

    public SearchHistoryInfo(String key, String content) {
        this.content = content;
        this.key = key;
    }
}
