package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.util.Log;

import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.tenant.findroom.roominfo.ReqRoomActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jbl on 18-3-28.
 */

public class PayOrderRequest extends BasePresenter {
    Context context;
 public   PayOrderRequest(Context context){
        this.context=context;
    }
    PayRentPresent.PRefresh pRefresh;
    public void setRefresh(PayRentPresent.PRefresh pRefresh){
        this.pRefresh=pRefresh;
    }
    public  void orderCancel(String order_id){
        rx.Observable<DataInfo> observable = null;
        observable= SecondRetrofitHelper.getInstance().roomOrderCancel(order_id);

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
    public  void applyCheckout(String order_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().checkOut(order_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        if (dataInfo.success()) {
                            if(pRefresh!=null){
                                pRefresh.refresh();
                            }
                            ShowToastUtil.showSuccessToast(context,dataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(context,dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
