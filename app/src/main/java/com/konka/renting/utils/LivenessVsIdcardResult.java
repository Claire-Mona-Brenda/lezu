/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.konka.renting.utils;

import com.baidu.ocr.sdk.model.ResponseResult;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class LivenessVsIdcardResult extends ResponseResult {

    /**
     * error_code : 0
     * error_msg : SUCCESS
     * log_id : 3634853462
     * timestamp : 1528787007
     * cached : 0
     * result : {"score":91.11631775}
     */

    private int error_code;
    private String error_msg;
    private long log_id;
    private int timestamp;
    private int cached;
    private ResultBean result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getCached() {
        return cached;
    }

    public void setCached(int cached) {
        this.cached = cached;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * score : 91.11631775
         */

        @SerializedName("score")
        private double scoreX;

        public double getScoreX() {
            return scoreX;
        }

        public void setScoreX(double scoreX) {
            this.scoreX = scoreX;
        }
    }
}

