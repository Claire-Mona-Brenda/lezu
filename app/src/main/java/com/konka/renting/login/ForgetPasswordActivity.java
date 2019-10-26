package com.konka.renting.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
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
import butterknife.OnClick;
import rx.Subscription;

public class ForgetPasswordActivity extends BaseActivity implements TextWatcher {

    public static final int SMS_CODE_VALID_SECOND = 60;

    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.edit_num)
    EditText mEditNum;
    @BindView(R.id.edit_password_new)
    EditText mEditPasswordNew;
    @BindView(R.id.btn_get_num)
    Button mBtnGetNum;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;

    private int mType;
    private String phone;


    public static void toActivity(Context context, int type, String phone) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void init() {
        mType = getIntent().getIntExtra("type", LoginInfo.LANDLORD);
        phone = getIntent().getStringExtra("phone");
        setTitleText(R.string.forget_password_title);

        if (!phone.equals("")) {
            mEditPhone.setText(phone);
            mEditPhone.setEnabled(false);
        }
        mBtnCommit.setEnabled(false);
        mEditPhone.addTextChangedListener(this);
        mEditNum.addTextChangedListener(this);
        mEditPasswordNew.addTextChangedListener(this);
        mEditPasswordNew.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

            }
        });
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
                resetPassword();
                break;
        }
    }

    private void resetPassword() {


        if (UIUtils.showHint(mEditPhone, mEditPasswordNew, mEditNum)) {
            return;
        }

        if (!mEditPhone.getText().toString().matches("\\d{11}")) {
            showToast(getString(R.string.phone_num_is_wrong_txt));
            return;
        }

        String password = mEditPasswordNew.getText().toString();

        if (password.length() < 6) {
            showToast(R.string.regist_password_is_wrong_lenght);
            return;
        }
        showLoadingDialog();

        String mobile = mEditPhone.getText().toString();
        String newPwd = mEditPasswordNew.getText().toString();
        String verifyCode = mEditNum.getText().toString();

        Subscription subscription = SecondRetrofitHelper.getInstance().resetPassword(mobile, newPwd, verifyCode)
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
                        showToast(info.msg());
                        if (info.success()) {
                            if (mType == LoginInfo.LANDLORD)
                                LoginNewActivity.toLandlordActivity(mActivity);
                            else
                                LoginNewActivity.toTenantActivity(mActivity);
                            finish();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void sendCode() {
        if (UIUtils.showHint(mEditPhone)) {
            return;
        }

        if (!mEditPhone.getText().toString().matches("\\d{11}")) {
            showToast(getString(R.string.phone_num_is_wrong_txt));
            return;
        }

        Subscription subscription = SecondRetrofitHelper.getInstance().getVerify(mEditPhone.getText().toString(), "1")
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

    private boolean checkEnableRegist() {
        if (mEditPhone.getText().toString().length() < 11)
            return false;
        if (mEditNum.getText().toString().equals(""))
            return false;
        if (mEditPasswordNew.getText().toString().length()<6)
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
     */
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
