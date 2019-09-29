package com.konka.renting.landlord.user.withdrawcash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CheckWithdrawPwdBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.LoginVerifyActivity;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class CheckPhoneActivity extends BaseActivity {
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
    @BindView(R.id.activity_check_phone_tv_phone_number)
    TextView mTvPhoneNumber;
    @BindView(R.id.activity_check_phone_edt_code)
    EditText mEdtCode;
    @BindView(R.id.tv_getVerify)
    Button tvGetVerify;
    @BindView(R.id.activity_check_phone_btn_next)
    Button mBtnNext;

    final int SMS_CODE_VALID_SECOND = 60;
    String phone;

    public static void toActivity(Context context, String phone) {
        Intent intent = new Intent(context, CheckPhoneActivity.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_phone;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_check_phone);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        phone = getIntent().getStringExtra("phone");
        mTvPhoneNumber.setText(phone);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEdtCode.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEdtCode.setFocusable(true);
                mEdtCode.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mEdtCode, 0);
            }
        },500 );
    }


    @OnClick({R.id.iv_back, R.id.tv_getVerify, R.id.activity_check_phone_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.tv_getVerify://获取验证码
                sendCode();
                break;
            case R.id.activity_check_phone_btn_next://下一步
                String verify = mEdtCode.getText().toString();
                if (!TextUtils.isEmpty(verify)) {
                    checkPhone(verify);
                } else {
                    showToast(R.string.regist_verfication_num_hint);
                }
                break;
        }
    }

    /*******************************************************接口******************************************/
    private void checkPhone(String verify) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().checkVerify(verify)
                .compose(RxUtil.<DataInfo<CheckWithdrawPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<CheckWithdrawPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<CheckWithdrawPwdBean> info) {
                        dismiss();
                        if (info.success()) {
                            WithdrawPwdSettingActivity.toActivity(mActivity,info.data().getCode());
                            finish();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void sendCode() {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getVerify(phone, "1")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        if (info.success()) {
                            countTimer(tvGetVerify);
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 验证码获取成功后 开启倒计时
     *
     * @param sendCodeBtn
     * @return
     */
    public ValueAnimator countTimer(final TextView sendCodeBtn) {
        sendCodeBtn.setEnabled(false);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(SMS_CODE_VALID_SECOND, 0).setDuration(SMS_CODE_VALID_SECOND * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sendCodeBtn.setText(animation.getAnimatedValue() + "s");
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                sendCodeBtn.setText("重新获取");
                sendCodeBtn.setEnabled(true);
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        return valueAnimator;
    }
}
