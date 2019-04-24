package com.konka.renting.landlord.user.rentoutincome;

/**
 * Created by kaite on 2018/3/7.
 */

public class RentOutIncomeBean {

    public String id;
    public String type;//0是收入，1是支出
    public String remark;//备注
    public String amount;//记录操作金额
    public String time;//记录时间
    public String room;//记录来源房源
    public String balance;//当时操作后余额
}
