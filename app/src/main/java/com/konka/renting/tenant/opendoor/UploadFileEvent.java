package com.konka.renting.tenant.opendoor;

public class UploadFileEvent {
    public final int UPDATE=0;
    public final int UPLOAD=11;

    private int type;

    public UploadFileEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
