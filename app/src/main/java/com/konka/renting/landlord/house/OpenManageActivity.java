package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseDetailsInfoBean;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.GatewaySettingActivity;
import com.konka.renting.landlord.gateway.GetManagePwdAuthorityActivity;
import com.konka.renting.landlord.gateway.ManagePwdActivity;
import com.konka.renting.tenant.opendoor.ClockSetManageActivity;
import com.konka.renting.tenant.opendoor.DevicesOpenPasswordActivity;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.CommonSurePopupWindow;
import com.konka.renting.widget.KeyPwdPopup;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class OpenManageActivity extends BaseActivity {
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
    @BindView(R.id.activity_open_manage_tv_manage_pwd)
    TextView tvManagePwd;
    @BindView(R.id.activity_open_manage_rl_manage_pwd)
    RelativeLayout rlManagePwd;
    @BindView(R.id.activity_open_manage_tv_set_pwd)
    TextView tvSetPwd;
    @BindView(R.id.activity_open_manage_rl_set_pwd)
    RelativeLayout rlSetPwd;
    @BindView(R.id.activity_open_manage_tv_temporary_pwd)
    TextView tvTemporaryPwd;
    @BindView(R.id.activity_open_manage_rl_temporary_pwd)
    RelativeLayout rlTemporaryPwd;
    @BindView(R.id.activity_open_manage_tv_key_pwd)
    TextView tvKeyPwd;
    @BindView(R.id.activity_open_manage_rl_key_pwd)
    RelativeLayout rlKeyPwd;
    @BindView(R.id.activity_open_manage_tv_fingerPrint)
    TextView tvFingerPrint;
    @BindView(R.id.activity_open_manage_rl_fingerPrint)
    RelativeLayout rlFingerPrint;
    @BindView(R.id.activity_open_manage_tv_ic_card)
    TextView tvIcCard;
    @BindView(R.id.activity_open_manage_rl_ic_card)
    RelativeLayout rlIcCard;
    @BindView(R.id.activity_open_manage_tv_set_gateway)
    TextView tvSetGateway;
    @BindView(R.id.activity_open_manage_rl_set_gateway)
    RelativeLayout rlSetGateway;
    @BindView(R.id.activity_open_manage_tv_sync_service)
    TextView tvSyncService;
    @BindView(R.id.activity_open_manage_rl_sync_service)
    RelativeLayout rlSyncService;

    final int QUERY_TIME_MAX = 10;

    private KeyPwdPopup keyPwdPopupwindow;//查看钥匙锁密码
    private CommonPopupWindow commonPopupWindow;
    private CommonSurePopupWindow commonTipsPopup;

    int queryTime = QUERY_TIME_MAX;
    HouseDetailsInfoBean2 bean;

    public static void toActivity(Context context, HouseDetailsInfoBean2 bean) {
        Intent intent = new Intent(context, OpenManageActivity.class);
        intent.putExtra(HouseDetailsInfoBean2.class.getSimpleName(), bean);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_open_manage;
    }

    @Override
    public void init() {
        bean = getIntent().getParcelableExtra(HouseDetailsInfoBean2.class.getSimpleName());

        tvTitle.setText(R.string.house_info_setting_clock_manage);

//        rlManagePwd.setVisibility(bean.getStatus() == 2 ? View.GONE : View.VISIBLE);


    }

    @OnClick({R.id.iv_back, R.id.activity_open_manage_rl_manage_pwd, R.id.activity_open_manage_rl_sync_service,
            R.id.activity_open_manage_rl_set_pwd, R.id.activity_open_manage_rl_temporary_pwd,
            R.id.activity_open_manage_rl_key_pwd, R.id.activity_open_manage_rl_fingerPrint,
            R.id.activity_open_manage_rl_ic_card, R.id.activity_open_manage_rl_set_gateway})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_open_manage_rl_manage_pwd://管理员密码
                if (bean.getStatus() < 2) {
                    ManagePwdActivity.toActivity(this, bean.getRoom_id());
                } else {
//                    showGetAuthorityPopup();
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_set_pwd://设置开锁密码
                if (bean.getStatus() < 2) {
                    DevicesOpenPasswordActivity.toActivity(this, bean.getDevice_id(), bean.getRoom_id());
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_temporary_pwd://临时密码
                if (bean.getStatus() < 2) {
                    TemporaryPwdActivity.toActivity(this, bean.getRoom_id());
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_key_pwd://钥匙孔密码
                if (bean.getStatus() < 2&& (queryTime <= 0 || queryTime >= 10)) {
                    showKeyPwdPopup();
                    keyPwdPopupwindow.setPwd(getString(R.string.tips_loading));
                    getKeyPwd();
                } else if (bean.getStatus() < 2) {
                    showKeyPwdPopup();
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_set_gateway://设置网关
                if (bean.getStatus() < 2) {
                    GatewaySettingActivity.toActivity(this, bean.getRoom_id(), bean.getGateway_id(), TextUtils.isEmpty(bean.getGateway_version()) ? "2" : bean.getGateway_version(), GatewaySettingActivity.TYPE_LANDLORD);
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_fingerPrint://指纹管理
                if (bean.getStatus() < 2) {
                    ClockSetManageActivity.toActivity(this, ClockSetManageActivity.TYPE_FINGERPRINT, bean.getRoom_id());
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_ic_card://IC卡管理
                if (bean.getStatus() < 2) {
                    ClockSetManageActivity.toActivity(this, ClockSetManageActivity.TYPE_IC_CARD, bean.getRoom_id());
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }
                break;
            case R.id.activity_open_manage_rl_sync_service://同步服务费时间
                if (bean.getStatus() < 2) {
                    showSyncSeverPopup();
                } else {
                    showTips(getString(R.string.tips_get_authority_content));
                }

                break;
        }
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
                    if (queryTime <= 0 || queryTime > 10) {
                        keyPwdPopupwindow.dismiss();
                        showPasswordRefreshPopup();
                    }
                }
            });
        }
        showPopup(keyPwdPopupwindow);

    }

    /**
     * 已出租房产请求获取管理员密码权限
     */
    private void showGetAuthorityPopup() {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_get_authority_content))
                .setLeftBtnString(getString(R.string.get_authority))
                .setLeftTextColor("#4A90E1")
                .setLeftBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetManagePwdAuthorityActivity.toActivity(mActivity, bean.getRoom_id());
                        commonPopupWindow.dismiss();
                    }
                })
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPopupWindow.dismiss();
                    }
                })
                .create();
        showPopup(commonPopupWindow);

    }

    /**
     * 是否重置密码
     */
    private void showPasswordRefreshPopup() {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_refresh_pwd))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPopupWindow.setOnDismissListener(null);
                        commonPopupWindow.dismiss();
                        keyPwdPopupwindow.setPwd(getString(R.string.tips_loading));
                        showKeyPwdPopup();
                        passwordRefresh();
                    }
                })
                .create();
        showPopup(commonPopupWindow);
    }

    /**
     * 是否同步服务费时间
     */
    private void showSyncSeverPopup() {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_info_sync_device_content))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        commonPopupWindow.setOnDismissListener(null);
                        commonPopupWindow.dismiss();
                        syncServiceExpire();
                    }
                })
                .create();
        showPopup(commonPopupWindow);
    }

    /**
     * 提示
     */
    private void showTips(String content) {
        commonTipsPopup = new CommonSurePopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(content)
                .create();
        commonTipsPopup.setFocusable(true);
        commonTipsPopup.setOutsideTouchable(true);
        showPopup(commonTipsPopup);

    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) linTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
        Subscription subscription = SecondRetrofitHelper.getInstance().lockPwd(bean.getRoom_id(), "9")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                queryTime = 0;
                                keyPwdPopupwindow.setPwd(info.data().getPassword());
                            } else {
                                queryTime = QUERY_TIME_MAX;
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
        Subscription subscription = SecondRetrofitHelper.getInstance().passwordRefresh(bean.getRoom_id(), "9")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                queryTime = 0;
                                keyPwdPopupwindow.setPwd(info.data().getPassword());
                            } else {
                                queryTime = QUERY_TIME_MAX;
                                queryPwdTimer(5, info.data().getId());
                            }
                        } else {
                            showToast(info.msg());
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
        Subscription subscription = SecondRetrofitHelper.getInstance().sync_service_expire(bean.getRoom_id(), bean.getDevice_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
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
        if (queryTime <= 0) {
            return;
        }
        queryTime--;
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
