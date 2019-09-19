package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CheckWithdrawPwdBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class WithdrawSetPwdCheckLoginPwdActivity extends BaseActivity {
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
    @BindView(R.id.activity_withdraw_set_pwd_check_login_pwd_edt_pwd)
    EditText mEdtPwd;
    @BindView(R.id.activity_withdraw_set_pwd_check_login_pwd_btn_next)
    Button mBtnNext;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, WithdrawSetPwdCheckLoginPwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activitty_withdraw_set_pwd_check_login_pwd;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_check_login);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);
    }


    @OnClick({R.id.iv_back, R.id.activity_withdraw_set_pwd_check_login_pwd_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_withdraw_set_pwd_check_login_pwd_btn_next:
                if (!TextUtils.isEmpty(mEdtPwd.getText().toString())){
                    checkPassword();
                }
                break;
        }
    }

    /***********************************************接口****************************************************/
    private void checkPassword() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().checkPassword("1", mEdtPwd.getText().toString())
                .compose(RxUtil.<DataInfo<CheckWithdrawPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<CheckWithdrawPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<CheckWithdrawPwdBean> dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            WithdrawPwdSettingActivity.toActivity(mActivity,dataInfo.data().getCode());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
