package com.konka.renting.landlord.house;

import com.amap.api.services.core.PoiItem;

public class ChooseLocationEvent {
    PoiItem poiItem;

    public ChooseLocationEvent(PoiItem poiItem) {
        this.poiItem = poiItem;
    }

    public PoiItem getPoiItem() {
        return poiItem;
    }
}
