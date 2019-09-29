package com.konka.renting.bean;

public class PayBean {
    /**
     * payment : 2
     * data : alipay_sdk=alipay-sdk-php-20180705&app_id=2018031402368292&biz_content=%7B%22body%22%3A%22%E4%B9%90%E7%A7%9F%E5%B0%8F%E7%AA%9D-%E6%9C%8D%E5%8A%A1%E8%B4%B9%E6%94%AF%E4%BB%98%22%2C%22subject%22%3A+%22%E4%B9%90%E7%A7%9F%E5%B0%8F%E7%AA%9D-%E6%9C%8D%E5%8A%A1%E8%B4%B9%E6%94%AF%E4%BB%98%22%2C%22out_trade_no%22%3A+%22serviceOrder_154270872764097445%22%2C%22timeout_express%22%3A+%2230m%22%2C%22total_amount%22%3A+%221%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Flezuxiaowo-test.youlejiakeji.com%2FService%2FPay%2FaliPayNotify&sign_type=RSA2&timestamp=2018-11-20+18%3A12%3A07&version=1.0&sign=Ycd%2BgvLzRlaI16zQSOPAiBH6JjylHXlhLMhIz3FwGWaePiWfSE3wwWPoqus0Avs3qNULyIo3tJVW8K6C609TJXGxOiZdpx4b8KnlDClizvSgYSnLitzvqexjyHJS7qYcFecXetZIQI%2FpygAHUYF9kYOtcXkgY1TY9ohFBjV%2BrMVBX5EkpGd5h6bHLqPeGyqUKmYvJ8%2FKr2TojyseENxrBZmiDMyCnMpO4LLtwT0Ym3LMQqhwZWGirXSV4c4LulNEurE0ZJAVXqoLsfYo%2BG9MZ%2F9BIU3PZEp%2FV%2FHPRf%2Bkx1PSPsLf6fleM%2BTv4gJ%2FOuJxZcnDfuZ%2BkOt7rl7dh0WHpg%3D%3D
     */

    private String payment;
    private String order_id;
    private Object data;

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
