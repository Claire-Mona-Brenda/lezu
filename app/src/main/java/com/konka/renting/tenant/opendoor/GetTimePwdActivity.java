package com.konka.renting.tenant.opendoor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import butterknife.OnClick;
import rx.Subscription;

public class GetTimePwdActivity extends BaseActivity {
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
    @BindView(R.id.activity_get_time_pwd_tv_pwd)
    TextView tvPwd;

    String room_id;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, GetTimePwdActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_get_time_pwd;
    }

    @Override
    public void init() {
        tvTitle.setVisibility(View.GONE);
        room_id = getIntent().getStringExtra("room_id");

        getNativePwd();
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

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
                                tvPwd.setText(pwd);

                            }

                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }

}
