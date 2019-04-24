package com.konka.renting.event;

public class HouseInfoSettingPopupEvent {
    public static final int TYPE_PAY = 0;//安装付费
    public static final int TYPE_EDIT = 1;//编辑房产
    public static final int TYPE_BIND = 2;//绑定设备
    public static final int TYPE_GATEWAY = 3;//网关列表
    public static final int TYPE_KEYPWD = 4;//钥匙孔密码
    public static final int TYPE_DEL = 5;//申请删除
    public static final int TYPE_PWD = 6;//临时密码

    private int type;

    public HouseInfoSettingPopupEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
