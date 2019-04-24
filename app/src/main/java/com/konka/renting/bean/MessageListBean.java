package com.konka.renting.bean;

public class MessageListBean {

    /**
     * id : 64
     * intro : 新的订单
     * image : null
     * title : 系统通知
     * content : 详细内容
     * is_read : 0
     * time : 2018-11-27 17:36:39
     */

    private int id;
    private String intro;//简介
    private String image;//	图片链接
    private String title;
    private String content;//	详细内容
    private int is_read;//	0 未读 1已读
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
