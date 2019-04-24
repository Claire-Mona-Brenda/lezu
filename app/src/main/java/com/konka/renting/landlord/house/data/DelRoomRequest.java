package com.konka.renting.landlord.house.data;

import android.content.Context;
import android.util.Log;

import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.IHouseRefresh;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.tenant.payrent.BasePresenter;
import com.konka.renting.utils.RxUtil;

import rx.Subscription;

/**
 * Created by jbl on 18-3-28.
 */

public class DelRoomRequest extends BasePresenter {
    Context context;
    IHouseRefresh iHouseRefresh;
 public DelRoomRequest(Context context, IHouseRefresh iHouseRefresh){
        this.context=context;
        this.iHouseRefresh=iHouseRefresh;
    }
    public  void delRoom(String roomid
                                   ){
        rx.Observable<DataInfo> observable = null;
        observable= RetrofitHelper.getInstance().deleteRoom(roomid);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia","");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo homeInfoDataInfo) {

                        if (homeInfoDataInfo.success()) {
                            ShowToastUtil.showSuccessToast(context,"删除成功");
                            if(iHouseRefresh!=null){
                                iHouseRefresh.houseRefresh(null);
                            }
                        } else {
                            ShowToastUtil.showWarningToast(context,homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }
}
