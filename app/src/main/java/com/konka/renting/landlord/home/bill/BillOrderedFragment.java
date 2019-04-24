package com.konka.renting.landlord.home.bill;




import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.http.RetrofitHelper;

import rx.Observable;

/**
 */
public class BillOrderedFragment extends BaseBillFragment {

    public static BillOrderedFragment newInstance() {
        BillOrderedFragment fragment = new BillOrderedFragment();
        return fragment;
    }

    @Override
    Observable<DataInfo<ListInfo<OrderInfo>>> getObservable(int page) {
        return RetrofitHelper.getInstance().getOrdered(page);
    }

    @Override
    boolean isPayed() {
        return true;
    }

}
