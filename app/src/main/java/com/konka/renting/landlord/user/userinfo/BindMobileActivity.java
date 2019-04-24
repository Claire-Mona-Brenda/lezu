package com.konka.renting.landlord.user.userinfo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class BindMobileActivity extends BaseActivity {

    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.btn_get_num)
    Button mBtnGetNum;
    @BindView(R.id.edit_num)
    EditText mEditNum;
    public static final int SMS_CODE_VALID_SECOND = 60;


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, BindMobileActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bind_mobile;
    }

    @Override
    public void init() {
        setTitleText(R.string.bind_mobile);

    }

    private void bind() {
        if (UIUtils.showHint(mEditPhone, mEditNum)) {
            return;
        }

        if (!mEditPhone.getText().toString().matches("\\d{11}")) {
            showToast(getString(R.string.phone_num_is_wrong_txt));
            return;
        }
        showLoadingDialog();
        final String phone = mEditPhone.getText().toString();
        String verCode = mEditNum.getText().toString().trim();
//
//        if (LoginUserBean.getInstance().getAccess_token().contains("landlord")) {
//            Log.e("verCode",verCode);
            Subscription subscription = SecondRetrofitHelper.getInstance().updateBindPhone(phone, verCode)
                    .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                    .subscribe(new CommonSubscriber<DataInfo>() {
                        @Override
                        public void onError(Throwable e) {
                            dismiss();
                            doFailed();
                        }

                        @Override
                        public void onNext(DataInfo info) {
                            dismiss();
                            if (info.success()) {
                                finish();
                                RxBus.getDefault().post(new BindMobileEvent(phone));
                            } else {
                                showToast(info.msg());
                            }

                        }
                    });
            addSubscrebe(subscription);
//        } else if (LoginUserBean.getInstance().getAccess_token().contains("renter")) {
//            Log.e("verCode",verCode);
//            Subscription subscription = RetrofitHelper.getInstance().renterbindMobile(phone, verCode)
//                    .compose(RxUtil.<DataInfo>rxSchedulerHelper())
//                    .subscribe(new CommonSubscriber<DataInfo>() {
//                        @Override
//                        public void onError(Throwable e) {
//                            dismiss();
//                            doFailed();
//                        }
//
//                        @Override
//                        public void onNext(DataInfo info) {
//                            dismiss();
//                            if (info.success()) {
//                                finish();
//                                RxBus.getDefault().post(new BindMobileEvent(phone));
//                            } else {
//                                showToast(info.msg());
//                            }
//
//                        }
//                    });
//            addSubscrebe(subscription);
//        }

    }

    private void sendCode() {
        if (UIUtils.showHint(mEditPhone)) {
            return;
        }

        if (!mEditPhone.getText().toString().matches("\\d{11}")) {
            showToast(getString(R.string.phone_num_is_wrong_txt));
            return;
        }
        Log.e("gettype",LoginUserBean.getInstance().getType()+"");

        Subscription subscription = SecondRetrofitHelper.getInstance().getVerify( mEditPhone.getText().toString(), "2")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        dismiss();
                        if (info.success()) {
                            doSuccess();
                            countTimer(mBtnGetNum);
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
    public static ValueAnimator countTimer(final TextView sendCodeBtn) {
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

    @OnClick({R.id.iv_back, R.id.btn_get_num, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_get_num:
                sendCode();
                break;
            case R.id.btn_commit:
                bind();
                break;
        }
    }



}
