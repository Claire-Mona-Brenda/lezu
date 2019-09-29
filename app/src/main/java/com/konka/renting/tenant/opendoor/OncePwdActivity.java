package com.konka.renting.tenant.opendoor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GeneratePwdBean;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.ShareAppPopup;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class OncePwdActivity extends BaseActivity {


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
    @BindView(R.id.activity_once_pwd_tv_pwd)
    TextView mTvPwd;
    @BindView(R.id.activity_once_pwd_tv_copy)
    TextView mTvCopy;

    final String KEY_DEVICE_ID = "device_id";
    final String KEY_ROOM_ID = "room_id";

    final int QUERY_TIME_MAX = 10;

    int queryTime = QUERY_TIME_MAX;

    String deviceId;
    String room_id;
    String managePwd;
    GeneratePwdBean generatePwdBean;


    public static void toActivity(Context context, String device_id, String room_id) {
        Intent intent = new Intent(context, OncePwdActivity.class);
        intent.putExtra("device_id", device_id);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_once_pwd;
    }

    @Override
    public void init() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        room_id = getIntent().getStringExtra(KEY_ROOM_ID);

        getManagePwd();
    }


    @OnClick({R.id.iv_back, R.id.activity_once_pwd_tv_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_once_pwd_tv_copy:
                if (!TextUtils.isEmpty(managePwd) && generatePwdBean != null) {
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", generatePwdBean.getPassword());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    showToast(R.string.toach_copy_to_clipboard);
                }
                break;
        }
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

    /***************************************************接口************************************************/
    private void getManagePwd() {
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
                                managePwd = info.data().getPassword();
                                addGeneratePassword();
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
                            managePwd = dataInfo.data().getPassword();
                            addGeneratePassword();
                        } else {
                            queryPwdTimer(1, password_id);
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void addGeneratePassword() {
        Subscription subscription = SecondRetrofitHelper.getInstance().addGeneratePassword("1", managePwd, deviceId,"1", "", "")
                .compose(RxUtil.<DataInfo<GeneratePwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<GeneratePwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<GeneratePwdBean> info) {
                        dismiss();
                        if (info.success()) {
                            generatePwdBean = info.data();
                            mTvPwd.setText(info.data().getPassword());
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }
}
