package com.konka.renting.landlord.house.data;

import android.content.Context;
import android.util.Log;

import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.TenantUserinfoBean;
import com.konka.renting.bean.UserInfoBean;
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

public class CheckUserRequest extends BasePresenter {
    Context context;
    IUserCheck iHouseRefresh;

    public CheckUserRequest(Context context, IUserCheck iHouseRefresh) {
        this.context = context;
        this.iHouseRefresh = iHouseRefresh;
    }

    public void checkTent() {
        ShowToastUtil.showLoadingDialog(context);
        Subscription subscription = RetrofitHelper.getInstance().getTenantUserInfo(LoginUserBean.getInstance().getAccess_token())
                .compose(RxUtil.<DataInfo<TenantUserinfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<TenantUserinfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        ShowToastUtil.dismiss();
                        Log.e("showerror", e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<TenantUserinfoBean> userInfoBeanDataInfo) {
                        ShowToastUtil.dismiss();
                        if (userInfoBeanDataInfo.success()) {
                            if (userInfoBeanDataInfo.data() != null) {

                                TenantUserinfoBean userInfoBean = userInfoBeanDataInfo.data();
                                if (iHouseRefresh != null) {
                                    iHouseRefresh.userCall(userInfoBean);
                                }

                            } else {
                                ShowToastUtil.showWarningToast(context, userInfoBeanDataInfo.msg());
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public void checkLandord() {
        ShowToastUtil.showLoadingDialog(context);
        Subscription subscription = RetrofitHelper.getInstance().getUserInfo(LoginUserBean.getInstance().getAccess_token())
                .compose(RxUtil.<DataInfo<UserInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<UserInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        ShowToastUtil.dismiss();
                        Log.e("showerror", e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<UserInfoBean> userInfoBeanDataInfo) {
                        ShowToastUtil.dismiss();
                        if (userInfoBeanDataInfo.success()) {
                            if (userInfoBeanDataInfo.data() != null) {

                                UserInfoBean userInfoBean = userInfoBeanDataInfo.data();
                                if (iHouseRefresh != null) {
                                    iHouseRefresh.userCall(userInfoBean);
                                }

                            } else {
                                ShowToastUtil.showWarningToast(context, userInfoBeanDataInfo.msg());
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public interface IUserCheck {
        public void userCall(Object obj);
    }
}