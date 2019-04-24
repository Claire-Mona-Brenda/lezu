package com.konka.renting.bean;

public class BillListBean {

    /**
     * id : 123
     * type : 4
     * amount : 30.00
     * create_time : 2018-11-26 17:13:37
     * title : 服务费退款
     */

    private String id;
    private int type;//账单类型 1充值 2服务费支付 3安装费 4服务费退款
    private String amount;
    private String create_time;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
