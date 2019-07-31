package com.konka.renting.landlord.home.bill;




import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.http.RetrofitHelper;

import rx.Observable;

/**
 */
public class BillunOrderedFragment extends BaseBillFragment {

    public static BillunOrderedFragment newInstance() {
        BillunOrderedFragment fragment = new BillunOrderedFragment();
        return fragment;
    }

    @Override
    Observable<DataInfo<ListInfo<OrderInfo>>> getObservable(int page) {
//        return RetrofitHelper.getInstance().getUnOrdered(page);
        return null;
    }

    @Override
    boolean isPayed() {
        return false;
    }


}
