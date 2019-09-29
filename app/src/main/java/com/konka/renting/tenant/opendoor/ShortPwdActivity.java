package com.konka.renting.tenant.opendoor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.NativePwdBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class ShortPwdActivity extends BaseActivity {
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
    @BindView(R.id.activity_short_pwd_img_refresh)
    ImageView mImgRefresh;
    @BindView(R.id.activity_short_pwd_tv_pwd_1)
    TextView mTvPwd1;
    @BindView(R.id.activity_short_pwd_tv_pwd_2)
    TextView mTvPwd2;
    @BindView(R.id.activity_short_pwd_tv_pwd_3)
    TextView mTvPwd3;
    @BindView(R.id.activity_short_pwd_tv_pwd_4)
    TextView mTvPwd4;
    @BindView(R.id.activity_short_pwd_tv_pwd_5)
    TextView mTvPwd5;
    @BindView(R.id.activity_short_pwd_tv_pwd_6)
    TextView mTvPwd6;

    final String KEY_DEVICE_ID = "device_id";
    final String KEY_ROOM_ID = "room_id";

    String deviceId;
    String room_id;

    public static void toActivity(Context context, String device_id, String room_id) {
        Intent intent = new Intent(context, ShortPwdActivity.class);
        intent.putExtra("device_id", device_id);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_short_pwd;
    }

    @Override
    public void init() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        room_id = getIntent().getStringExtra(KEY_ROOM_ID);

        getNativePwd();
    }


    @OnClick({R.id.iv_back, R.id.activity_short_pwd_img_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_short_pwd_img_refresh:
                getNativePwd();
                break;
        }
    }

    /***************************************************接口********************************************/
    private void getNativePwd() {
        showLoadingDialog();
        Subscription subscription = (SecondRetrofitHelper.getInstance().native_password(room_id)
                .compose(RxUtil.<DataInfo<NativePwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<NativePwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        finish();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<NativePwdBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            if (dataInfo.data().getPassword() != null) {
                                String pwd = dataInfo.data().getPassword();
                                mTvPwd1.setText(pwd.charAt(0) + "");
                                mTvPwd2.setText(pwd.charAt(1) + "");
                                mTvPwd3.setText(pwd.charAt(2) + "");
                                mTvPwd4.setText(pwd.charAt(3) + "");
                                mTvPwd5.setText(pwd.charAt(4) + "");
                                mTvPwd6.setText(pwd.charAt(5) + "");

                            }

                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }
}
