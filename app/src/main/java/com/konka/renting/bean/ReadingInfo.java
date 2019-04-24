package com.konka.renting.bean;

/**
 * 抄表
 * Created by jzxiang on 25/03/2018.
 */
public class ReadingInfo {


    /**
     * order_no : 15228279608267
     * start_time : 2018-04-04
     * end_time : 2038-01-19
     * real_amount : 0.00
     * roomInfo : {"id":"23","room_name":"样板房5号","address":"北方大门","building_no":"13","door_no":"283","door_number":"222113","image":"http://let.tuokemao.com/Public/Common/Images/nopic.gif"}
     */
    public String order_no;
    public String start_time;
    public String end_time;
    public String real_amount;
    public RoomInfo roomInfo;

}
