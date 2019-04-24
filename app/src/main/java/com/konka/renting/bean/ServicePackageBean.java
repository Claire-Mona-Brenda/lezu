package com.konka.renting.bean;

import java.util.List;

/**
 * Created by kaite on 2018/5/9.
 */

public class ServicePackageBean {

    private List<ListsBean> lists;

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class ListsBean {
        /**
         * number : 6
         * price : 100
         * text : 6个月 100元
         */

        private String number;
        private String price;
        private String text;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
