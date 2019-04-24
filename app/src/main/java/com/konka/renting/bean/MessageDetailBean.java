package com.konka.renting.bean;

/**
 * Created by kaite on 2018/4/13.
 */

public class MessageDetailBean {

    /**
     * info : {"id":"57","image":"","title":"测试","content":"测试测试测试测试","time":"18.04.13 16:27:45"}
     */

    private InfoBean info;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * id : 57
         * image :
         * title : 测试
         * content : 测试测试测试测试
         * time : 18.04.13 16:27:45
         */

        private String id;
        private String image;
        private String title;
        private String content;
        private String time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

}
