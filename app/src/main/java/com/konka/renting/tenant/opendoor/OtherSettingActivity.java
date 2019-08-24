package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.event.AddShareRentEvent;
import com.konka.renting.event.ToGetPwdTimeEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.GatewaySettingActivity;
import com.konka.renting.landlord.gateway.ManagePwdActivity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.KeyPwdPopup;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class OtherSettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activity_other_setting_ll_open_pwd)
    LinearLayout llOpenPwd;
    @BindView(R.id.activity_other_setting_tv_open_pwd)
    TextView tvOpenPwd;
    @BindView(R.id.activity_other_setting_ll_key_pwd)
    LinearLayout llKeyPwd;
    @BindView(R.id.activity_other_setting_tv_key_pwd)
    TextView tvKeyPwd;
    @BindView(R.id.activity_other_setting_ll_gateway_setting)
    LinearLayout llGatewaySetting;
    @BindView(R.id.activity_other_setting_tv_gateway_setting)
    TextView tvGatewaySetting;
    @BindView(R.id.activity_other_setting_ll_gateway_restart)
    LinearLayout llGatewayRestart;
    @BindView(R.id.activity_other_setting_tv_gateway_restart)
    TextView tvGatewayRestart;
    @BindView(R.id.activity_other_setting_ll_fingerprint)
    LinearLayout llFingerprint;
    @BindView(R.id.activity_other_setting_tv_fingerprint)
    TextView tvFingerprint;
    @BindView(R.id.activity_other_setting_ll_ic)
    LinearLayout llIc;
    @BindView(R.id.activity_other_setting_tv_ic)
    TextView tvIc;
    @BindView(R.id.activity_other_setting_ll_sync)
    LinearLayout llSync;
    @BindView(R.id.activity_other_setting_tv_sync)
    TextView tvSync;
    @BindView(R.id.activity_other_setting_ll_manage_pwd)
    LinearLayout llManagePwd;
    @BindView(R.id.activity_other_setting_tv_manage_pwd)
    TextView tvManagePwd;
    @BindView(R.id.activity_other_setting_tv_add_people)
    TextView tvAddPeople;
    @BindView(R.id.activity_other_setting_ll_add_people)
    LinearLayout llAddPeople;

    final int QUERY_TIME_MAX = 10;

    int type;//出租类型，0长租，1短租
    private int queryTime = 0;//查询密码倒计时
    private int queryKeyPwdTime = QUERY_TIME_MAX;//查询钥匙孔密码倒计时


    OpenDoorListbean openDoorListbean;

    private KeyPwdPopup keyPwdPopupwindow;//查看钥匙锁密码
    private ShowOpenPwdPopupwindow showOpenPwdPopupwindow;//查看开锁密码
    CommonPopupWindow popupWindow;


    public static void toActivity(Context context, int type, OpenDoorListbean openDoorListbean) {
        Intent intent = new Intent(context, OtherSettingActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("openDoorListbean", openDoorListbean);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_other_setting;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.open_other_setting);

        type = getIntent().getIntExtra("type", 0);
        openDoorListbean = getIntent().getParcelableExtra("openDoorListbean");

        if (openDoorListbean.getIs_share_rent()==1){
            llAddPeople.setVisibility(View.GONE);
        }else{
            llAddPeople.setVisibility(View.VISIBLE);
        }

        switch (type) {
            case 0:
                llOpenPwd.setVisibility(View.VISIBLE);
                llKeyPwd.setVisibility(View.VISIBLE);
                llGatewaySetting.setVisibility(View.VISIBLE);
                llGatewayRestart.setVisibility(View.GONE);
                llFingerprint.setVisibility(View.GONE);
                llIc.setVisibility(View.GONE);
                break;
            case 1:
                llOpenPwd.setVisibility(View.VISIBLE);
                llKeyPwd.setVisibility(View.VISIBLE);
                llGatewaySetting.setVisibility(View.GONE);
                llGatewayRestart.setVisibility(View.VISIBLE);
                llFingerprint.setVisibility(View.GONE);
                llIc.setVisibility(View.GONE);
                break;
        }

        llManagePwd.setVisibility( View.VISIBLE );

        addRxBusSubscribe(ToGetPwdTimeEvent.class, new Action1<ToGetPwdTimeEvent>() {
            @Override
            public void call(ToGetPwdTimeEvent toGetPwdTimeEvent) {
                GetTimePwdActivity.toActivity(OtherSettingActivity.this, openDoorListbean.getRoom_id());
            }
        });

        addRxBusSubscribe(AddShareRentEvent.class, new Action1<AddShareRentEvent>() {
            @Override
            public void call(AddShareRentEvent addShareRentEvent) {
                    if (openDoorListbean.getOrder_id().equals(addShareRentEvent.getOrder_id())) {
                        openDoorListbean.setIs_rent(addShareRentEvent.getHave());
                    }
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.activity_other_setting_tv_open_pwd, R.id.activity_other_setting_tv_key_pwd,
            R.id.activity_other_setting_tv_gateway_setting, R.id.activity_other_setting_tv_gateway_restart,
            R.id.activity_other_setting_tv_fingerprint, R.id.activity_other_setting_tv_ic,
            R.id.activity_other_setting_ll_sync, R.id.activity_other_setting_tv_manage_pwd, R.id.activity_other_setting_tv_add_people})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_other_setting_tv_add_people://添加使用账号
                if (openDoorListbean.getIs_rent() == 1) {
                    ShareRentListActivity.toActivity(this, openDoorListbean.getOrder_id());
                } else {
                    AddRentPeopleActivity.toActivity(this, openDoorListbean.getOrder_id(), true);
                }
                break;
            case R.id.activity_other_setting_tv_open_pwd://开门密码
                if ( !openDoorListbean.getDevice_id().equals("")) {
                    if (type == 0) {
                        DevicesOpenPasswordActivity.toActivity(this, openDoorListbean.getDevice_id(), openDoorListbean.getRoom_id());
                    } else {
                        checkOpenPwd();//短租查看密码
                    }

                } else if (openDoorListbean.getDevice_id().equals("")) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_device));
                } else {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_on_rent));
                }
                break;
            case R.id.activity_other_setting_tv_key_pwd://钥匙孔密码
                if ( !openDoorListbean.getDevice_id().equals("")) {
                    if (queryKeyPwdTime <= 0 || queryKeyPwdTime >= 10) {
                        showKeyPwdPopup();
                        keyPwdPopupwindow.setPwd(getString(R.string.tips_loading));
                        getKeyPwd();
                    } else {
                        showKeyPwdPopup();
                    }

                } else if (openDoorListbean.getDevice_id().equals("")) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_device));
                } else {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_on_rent));
                }
                break;
            case R.id.activity_other_setting_tv_gateway_setting://设置网关
                if (!TextUtils.isEmpty(openDoorListbean.getGateway_id())) {
                    GatewaySettingActivity.toActivity(this, openDoorListbean.getRoom_id(), openDoorListbean.getGateway_id(), openDoorListbean.getGateway_version(), GatewaySettingActivity.TYPE_TENANT);
                } else if (TextUtils.isEmpty(openDoorListbean.getGateway_id())) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_gateway));
                } else {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_on_rent));
                }
                break;
            case R.id.activity_other_setting_tv_gateway_restart://重启网关
                if ( !TextUtils.isEmpty(openDoorListbean.getGateway_id())) {
                    showReboot();
                } else if (TextUtils.isEmpty(openDoorListbean.getGateway_id())) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_gateway));
                } else {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_on_rent));
                }

                break;
            case R.id.activity_other_setting_tv_manage_pwd://管理员密码
                if (!openDoorListbean.getDevice_id().equals("")) {
                    ManagePwdActivity.toActivity(this, openDoorListbean.getRoom_id());
                } else if (openDoorListbean.getDevice_id().equals("")) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_device));
                } else {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_on_rent));
                }
                break;
            case R.id.activity_other_setting_tv_fingerprint://管理指纹
                ClockSetManageActivity.toActivity(this, ClockSetManageActivity.TYPE_FINGERPRINT, openDoorListbean.getRoom_id());
                break;
            case R.id.activity_other_setting_tv_ic://管理IC卡
                ClockSetManageActivity.toActivity(this, ClockSetManageActivity.TYPE_IC_CARD, openDoorListbean.getRoom_id());
                break;
            case R.id.activity_other_setting_ll_sync://同步服务费时间
                if (!openDoorListbean.getDevice_id().equals("")) {
                    showSyncSeverPopup();
                } else if (openDoorListbean.getDevice_id().equals("")) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_device));
                } else {
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_open_no_on_rent));
                }

                break;

        }
    }

    /**
     * 短租往锁端设置密码
     */
    private void checkOpenPwd() {
        tvOpenPwd.setEnabled(false);
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().shortRentOpenPassword(openDoorListbean.getOrder_id())
                .compose(RxUtil.<DataInfo<PwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        tvOpenPwd.setEnabled(true);
                        showOpenPwdShort("");
                    }

                    @Override
                    public void onNext(DataInfo<PwdBean> dataInfo) {
                        if (dataInfo.success()) {
                            dismiss();
                            tvOpenPwd.setEnabled(true);
                            showOpenPwdShort(dataInfo.data().getPassword());
                        } else if (dataInfo.code() == 2) {
                            timeQuery(dataInfo.data().getPassword_id());
                        } else {
                            dismiss();
                            tvOpenPwd.setEnabled(true);
                            showOpenPwdShort("");
                            ShowToastUtil.showNormalToast(getBaseContext(), dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void timeQuery(final String password_id) {
        if (queryTime > 15) {
            queryTime = 0;
            dismiss();
            tvOpenPwd.setEnabled(true);
            showOpenPwdShort("");
            return;
        }
        queryTime++;
        Subscription subscription = Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {
                        timeQuery(password_id);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (password_id != null) {
                            queryPwdResult(password_id);
                        } else {
                            dismiss();
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    private void queryPwdResult(final String password_id) {

        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo<PwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        timeQuery(password_id);
                    }

                    @Override
                    public void onNext(DataInfo<PwdBean> info) {
                        if (info.success()) {
                            queryTime = 0;
                            dismiss();
                            tvOpenPwd.setEnabled(true);
                            showOpenPwdShort(info.data().getPassword());
                        } else {
                            timeQuery(password_id);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 展示短租开锁密码
     */
    private void showOpenPwdShort(String pwd) {
        if (showOpenPwdPopupwindow == null) {
            showOpenPwdPopupwindow = new ShowOpenPwdPopupwindow(this);
        }
        showOpenPwdPopupwindow.setPwd(pwd);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        showOpenPwdPopupwindow.showAtLocation((View) linTitle.getParent(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        showOpenPwdPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 展示钥匙孔密码
     */
    private void showKeyPwdPopup() {
        if (keyPwdPopupwindow == null) {
            keyPwdPopupwindow = new KeyPwdPopup(this);
            keyPwdPopupwindow.setOnClickListen(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (queryKeyPwdTime <= 0 || queryKeyPwdTime > 10) {
                        keyPwdPopupwindow.dismiss();
                        showPasswordRefreshPopup();
                    }
                }
            });
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        keyPwdPopupwindow.showAtLocation((View) linTitle.getParent(), Gravity.CENTER, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        keyPwdPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private void showReboot() {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.warm_restart_gateway))
                .setRightTextColor("#FF0000")
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reboot();
                        popupWindow.dismiss();
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    /**
     * 是否重置密码
     */
    private void showPasswordRefreshPopup() {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_refresh_pwd))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.setOnDismissListener(null);
                        popupWindow.dismiss();
                        keyPwdPopupwindow.setPwd(getString(R.string.tips_loading));
                        showKeyPwdPopup();
                        passwordRefresh();
                    }
                })
                .create();
        showPopup(popupWindow);

    }

    /**
     * 是否同步服务费时间
     */
    private void showSyncSeverPopup() {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_info_sync_device_content))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        syncServiceExpire();
                    }
                })
                .create();
        showPopup(popupWindow);
    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.8f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) findViewById(R.id.lin_title).getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    /*************************************************接口*****************************************************************/

    private void getKeyPwd() {
        Subscription subscription = SecondRetrofitHelper.getInstance().lockPwd(openDoorListbean.getRoom_id(), "9")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                queryKeyPwdTime = 0;
                                keyPwdPopupwindow.setPwd(info.data().getPassword());
                            } else {
                                queryPwdTimer(5, info.data().getId());
                            }
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void queryPasswordResult(final String password_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        queryPwdTimer(1, password_id);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            getKeyPwd();
                        } else {
                            queryPwdTimer(1, password_id);
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void passwordRefresh() {
        Subscription subscription = SecondRetrofitHelper.getInstance().passwordRefresh(openDoorListbean.getRoom_id(), "9")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                queryKeyPwdTime = 0;
                                keyPwdPopupwindow.setPwd(info.data().getPassword());
                            } else {
                                queryKeyPwdTime = QUERY_TIME_MAX;
                                queryPwdTimer(5, info.data().getId());
                            }
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void reboot() {
        Subscription subscription = SecondRetrofitHelper.getInstance().reStart(openDoorListbean.getGateway_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        showToast(getString(R.string.please_gateway_restart_fail));
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 同步服务费
     */
    private void syncServiceExpire() {

        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().sync_service_expire(openDoorListbean.getRoom_id(), openDoorListbean.getDevice_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        showError(e.getMessage());
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            showToast(getString(R.string.please_info_lock_setting_sync_device));
                        } else {
                            showToast("服务费同步：" + dataInfo.msg());
                        }
                        dismiss();
                    }
                });
        addSubscrebe(subscription);
    }

    /*************************************************计时处理*****************************************************************/


    private void queryPwdTimer(long delay, final String password_id) {
        if (queryKeyPwdTime <= 0) {
            return;
        }
        queryKeyPwdTime--;
        Subscription subscription = Observable.timer(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        queryPasswordResult(password_id);
                    }
                });
        addSubscrebe(subscription);
    }

}
