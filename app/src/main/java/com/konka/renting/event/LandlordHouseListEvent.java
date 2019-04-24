package com.konka.renting.event;

public class LandlordHouseListEvent {
    public final int TYPE_UPDATA = 11;

    private boolean isUpdata=false;

    public LandlordHouseListEvent(int... type) {
        int len=type.length;
        for (int i = 0; i <len ; i++) {
            init(type[i]);
        }
    }
    private void init(int type){
        switch (type){
            case TYPE_UPDATA:
                isUpdata=true;
                break;
        }
    }

    public boolean isUpdata() {
        return isUpdata;
    }
}
