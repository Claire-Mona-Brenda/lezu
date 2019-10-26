package com.konka.renting.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class RegistActivity extends BaseActivity implements TextWatcher {


    public static final int SMS_CODE_VALID_SECOND = 60;

    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.edit_num)
    EditText mEditNum;
    @BindView(R.id.edit_password)
    EditText mEditPwd;
    @BindView(R.id.btn_get_num)
    Button mBtnGetNum;
    @BindView(R.id.checkbox_agree)
    CheckBox checkAgree;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;

    private int mType;

    public static void toActivity(Context context, int type) {
        Intent intent = new Intent(context, RegistActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    public void init() {
        setTitleText(R.string.regist_title);
        mType = getIntent().getIntExtra("type", 1);
        mBtnCommit.setEnabled(false);
        mEditPhone.addTextChangedListener(this);
        mEditPwd.addTextChangedListener(this);
        mEditNum.addTextChangedListener(this);
        checkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBtnCommit.setEnabled(checkEnableRegist());
            }
        });
        mEditPwd.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_get_num, R.id.btn_commit,R.id.tv_sever_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_get_num:
                sendCode();
                break;
            case R.id.btn_commit:
                next();
                break;
            case R.id.tv_sever_protocol:
                UserProtocolActivity.toActivity(this);
                break;
        }
    }

    private void next() {
        if (UIUtils.showHint(mEditPhone, mEditNum)) {
            showToast(R.string.warm_regist_please_input);
            return;
        }
        if (!checkAgree.isChecked()) {
            showToast(R.string.warm_regist_please_agree);
            return;
        }
        final String phone = mEditPhone.getText().toString();
        final String code = mEditNum.getText().toString();
        String password = mEditPwd.getText().toString();

        if (password.length()<6){
            showToast(R.string.regist_password_is_wrong_lenght);
            return;
        }

        showLoadingDialog();
        Subscription subscription =SecondRetrofitHelper.getInstance().register(phone, password, code)
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
                            //执行一个登录操作
                            doSuccess();
                            toLogin();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void toLogin() {
        if (LoginInfo.isLandlord(mType)) {
            LoginNewActivity.toLandlordActivity(this);
        }else {
            LoginNewActivity.toTenantActivity(this);
        }
        finish();
    }


    private void sendCode() {
        if (UIUtils.showHint(mEditPhone)) {
            return;
        }

        if (!mEditPhone.getText().toString().matches("\\d{11}")) {
            showToast(getString(R.string.phone_num_is_wrong_txt));
            return;
        }

        Subscription subscription = SecondRetrofitHelper.getInstance().getVerify(mEditPhone.getText().toString(), "2")
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

    private boolean checkEnableRegist(){
        if (mEditPhone.getText().toString().length()<11)
            return false;
        if (mEditNum.getText().toString().equals(""))
            return false;
        if (mEditPwd.getText().toString().length()<6)
            return false;
        if (!checkAgree.isChecked())
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
            mBtnCommit.setEnabled(checkEnableRegist());
    }

    /**
     * 将密码转换成*显示
     * */
    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        public char charAt(int index) {
            //这里返回的char，就是密码的样式，注意，是char类型的
            return '*';
        }

        public int length() {
            return mSource.length();
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}
