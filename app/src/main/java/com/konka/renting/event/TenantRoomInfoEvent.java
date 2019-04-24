package com.konka.renting.event;

/**
 * 租客端搜索房屋详情
 */
public class TenantRoomInfoEvent {
    public final int TYPE_FINISH = -11;
    public final int TYPE_GETDATA = 11;
    public final int TYPE_INTENT_ACTIVITY = 22;

    boolean isGetData = false;
    boolean isFinish = false;
    boolean isStartActivity = false;

    public TenantRoomInfoEvent(int... type){
        int len=type.length;
        for (int i = 0; i < len; i++) {
            init(type[i]);
        }
    }

    private void init(int type) {
        switch (type){
            case TYPE_FINISH:
                isFinish=true;
                break;
            case TYPE_GETDATA:
                isGetData=true;
                break;
            case TYPE_INTENT_ACTIVITY:
                isStartActivity=true;
                break;
        }
    }

    public boolean isGetData() {
        return isGetData;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public boolean isStartActivity() {
        return isStartActivity;
    }
}
