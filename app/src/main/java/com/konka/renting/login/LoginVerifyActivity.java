package com.konka.renting.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.MainActivity;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

import static com.konka.renting.login.LoginInfo.isLandlord;
import static com.konka.renting.utils.UIUtils.showHint;

public class LoginVerifyActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.tv_getVerify)
    Button tvGetVerify;
    @BindView(R.id.edit_verify)
    EditText editVerify;
    @BindView(R.id.btn_login)
    Button btnLogin;

    final int SMS_CODE_VALID_SECOND = 60;

    int mType;

    public static void toActivity(Context context, int type) {
        Intent intent = new Intent(context, LoginVerifyActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_verify;
    }

    @Override
    public void init() {

        mType = getIntent().getIntExtra("type", LoginInfo.TENANT);
        editPhone.addTextChangedListener(this);
        editVerify.addTextChangedListener(this);
    }


    @OnClick({R.id.img_back, R.id.tv_getVerify, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_getVerify:
                sendCode();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }
    private void sendCode() {
        if (UIUtils.showHint(editPhone)) {
            return;
        }

        if (!editPhone.getText().toString().matches("\\d{11}")) {
            showToast(getString(R.string.phone_num_is_wrong_txt));
            return;
        }

        Subscription subscription = SecondRetrofitHelper.getInstance().getVerify(editPhone.getText().toString(), "3")
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
    public  ValueAnimator countTimer(final TextView sendCodeBtn) {
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

    private void login() {
        if (showHint(editPhone, editVerify)) {
            showToast(R.string.warm_regist_please_input);
            return;
        }
        showLoadingDialog();
        final String phone = editPhone.getText().toString();
        String verify = editVerify.getText().toString();
        Subscription subscription =
                SecondRetrofitHelper.getInstance().login(phone, "",verify,"2",isLandlord(mType) ?"0":"1")
                        .compose(RxUtil.<DataInfo<LoginInfo>>rxSchedulerHelper())
                        .subscribe(new CommonSubscriber<DataInfo<LoginInfo>>() {
                            @Override
                            public void onError(Throwable e) {
                                dismiss();
                                doFailed();
                            }

                            @Override
                            public void onNext(DataInfo<LoginInfo> info) {
                                dismiss();
                                if (info.success()) {
                                    LoginUserBean.getInstance().setLogin(true);
                                    LoginUserBean.getInstance().setType(mType);
                                    LoginUserBean.getInstance().setAccess_token(info.data().token);
                                    LoginUserBean.getInstance().setMobile(phone);
                                    LoginUserBean.getInstance().save();
                                    toMain();
                                } else {
                                    showToast(info.msg());
                                }
                            }
                        });
        addSubscrebe(subscription);
    }

    private void toMain() {
        MainActivity.toMainActivity(this, mType);
        finish();
    }

    private boolean checkEnableLogin(){
        if (editPhone.getText().toString().length()<11)
            return false;
        if (editVerify.getText().toString().equals(""))
            return false;
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        btnLogin.setEnabled(checkEnableLogin());
    }
}
