package com.konka.renting.event;

public class TentantOpenDoorEvent {
    public final int TYPE_FINISH = -11;
    public final int TYPE_UPDATA = 11;
    public final int TYPE_GETDATA = 22;

    private boolean isFinish=false;
    private boolean isUpdata=false;
    private boolean isGetdata=false;

    public TentantOpenDoorEvent(int... type) {
        int len=type.length;
        for (int i = 0; i <len ; i++) {
            init(type[i]);
        }
    }

    private void init(int type){
        switch (type){
            case TYPE_FINISH:
                isFinish=true;
                break;
            case TYPE_UPDATA:
                isUpdata=true;
                break;
            case TYPE_GETDATA:
                isGetdata=true;
                break;
        }
    }

    public boolean isFinish() {
        return isFinish;
    }

    public boolean isUpdata() {
        return isUpdata;
    }

    public boolean isGetdata() {
        return isGetdata;
    }
}
