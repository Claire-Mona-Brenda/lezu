package com.konka.renting.bean;

public class UploadPicBean {

    /**
     * filename : 2018/11/27/37c136f0472a27af8fe97f0fa2c7764a.jpg
     * url : https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/27/37c136f0472a27af8fe97f0fa2c7764a.jpg
     * thumb_url : https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/27/thumb_37c136f0472a27af8fe97f0fa2c7764a.jpg
     */

    private String filename;
    private String url;
    private String thumb_url;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
}
