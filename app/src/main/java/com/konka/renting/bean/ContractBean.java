package com.konka.renting.bean;

import java.util.List;

public class ContractBean {

    /**
     * id : 1
     * status : 2
     * order_no : 22222
     * status_text : 驳回
     * images : ["url1","url2"]
     * content : 图片模糊
     */

    private String id;
    private String status;
    private String order_no;
    private String status_text;
    private String content;
    private List<String> images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
