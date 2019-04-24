package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.CommonSurePopupWindow;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ManagePwdActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.activity_manage_pwd_tv_pwd)
    TextView tvPwd;
    @BindView(R.id.activity_manage_pwd_tv_refresh)
    TextView tvRefresh;

    final int QUERY_TIME_MAX = 10;

    int queryTime = QUERY_TIME_MAX;
    String room_id;

    private CommonPopupWindow commonPopupWindow;


    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, ManagePwdActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manage_pwd;
    }

    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");
        getPwd();
    }

    @OnClick({R.id.ll_title, R.id.activity_manage_pwd_tv_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_title:
                finish();
                break;
            case R.id.activity_manage_pwd_tv_refresh:
                showPasswordRefreshPopup(getString(R.string.tips_refresh_pwd));

                break;
        }
    }


    private void getPwd() {
        showLoadingDialog();
        mProgressDialog.setCanceledOnTouchOutside(false);
        Subscription subscription = SecondRetrofitHelper.getInstance().lockPwd(room_id, "0")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                dismiss();
                                tvPwd.setText(info.data().getPassword());
                            } else {
                                queryPwdTimer(5, info.data().getId());
                            }
                        } else {
                            dismiss();
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void passwordRefresh() {
        showLoadingDialog();
        mProgressDialog.setCanceledOnTouchOutside(false);
        Subscription subscription = SecondRetrofitHelper.getInstance().passwordRefresh(room_id, "0")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                dismiss();
                                tvPwd.setText(info.data().getPassword());
                            } else {
                                queryPwdTimer(5, info.data().getId());
                            }
                        } else {
                            dismiss();
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void queryPasswordResult(final String password_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo<PwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        queryPwdTimer(1, password_id);
                    }

                    @Override
                    public void onNext(DataInfo<PwdBean> dataInfo) {
                        if (dataInfo.success()) {
                            dismiss();
                            tvPwd.setText(dataInfo.data().getPassword());
                        } else {
                            queryPwdTimer(1, password_id);
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /*************************************************计时处理*****************************************************************/


    private void queryPwdTimer(long delay, final String password_id) {
        if (queryTime <= 0) {
            dismiss();
            showToast(R.string.open_tips_get_pwd_time_fail);
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

/*********************************************************************************************************************/
    /**
     * 重置密码
     */
    private void showPasswordRefreshPopup(String content) {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(content)
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPopupWindow.dismiss();
                        passwordRefresh();
                    }
                })
                .create();
        showPopup(commonPopupWindow);

    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) llTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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

}
