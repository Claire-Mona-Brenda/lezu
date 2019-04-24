package com.konka.renting.landlord.home.tenanter;

import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;

import rx.Observable;

/**
 * Created by jzxiang on 25/03/2018.
 */

public class TenanterListRentedFragment extends TenanterFragment {

    public static TenanterListRentedFragment newInstance() {
        TenanterListRentedFragment fragment = new TenanterListRentedFragment();
        return fragment;
    }
    @Override
    public Observable<DataInfo<ListInfo<RoomInfo>>> getObservable(int page) {
        return RetrofitHelper.getInstance().getLandloarRoomListRented(page);
    }
}
