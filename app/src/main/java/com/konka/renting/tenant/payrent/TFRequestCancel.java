package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.util.Log;

import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;

import rx.Subscription;

/**
 * Created by jbl on 18-3-28.
 */

public class TFRequestCancel extends BasePresenter {
    Context context;
 public TFRequestCancel(Context context){
        this.context=context;
    }
    PayRentPresent.PRefresh pRefresh;
    public void setRefresh(PayRentPresent.PRefresh pRefresh){
        this.pRefresh=pRefresh;
    }
    public  void orderCancel(String order_id){
        rx.Observable<DataInfo> observable = null;
        observable= SecondRetrofitHelper.getInstance().cancelCheckOut(order_id);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo homeInfoDataInfo) {

                        if (homeInfoDataInfo.success()) {
                            if(pRefresh!=null){
                                pRefresh.refresh();
                            }
                            ShowToastUtil.showSuccessToast(context,homeInfoDataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(context,homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

}
