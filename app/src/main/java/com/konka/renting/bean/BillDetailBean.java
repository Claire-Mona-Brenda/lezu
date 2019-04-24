package com.konka.renting.bean;

public class BillDetailBean {
    /**
     * id : 107
     * order_no : 154294107211635199
     * type : 2
     * amount : 0.02
     * create_time : 2018-11-23 10:44:37
     * remark : 服务费支付宝支付
     * payment : 2
     * start_date : 2018-11-23
     * end_date : 2018-12-22
     */

    private int id;//账单id
    private String order_no;//账单编号
    private int type;//账单类型 1充值 2服务费支付 3安装费 4服务费退款
    private String amount;//金额
    private String create_time;//产生时间
    private String remark;//描述
    private int payment;//支付方式 1微信 支付宝 3余额
    private String start_date;//	服务费开始日期
    private String end_date;//	服务费结束日期

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
