package com.konka.renting.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.event.LogInAgainEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.MainActivity;
import com.konka.renting.location.AMapHelp;
import com.konka.renting.utils.AppManager;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.konka.renting.login.LoginInfo.isLandlord;
import static com.konka.renting.utils.UIUtils.showHint;

public class LoginNewActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher {
    @BindView(R.id.activity_new_login_rb_tenant)
    RadioButton rbTenant;
    @BindView(R.id.activity_new_login_rb_landlord)
    RadioButton rbLandlord;
    @BindView(R.id.activity_new_login_rg_group)
    RadioGroup rgGroup;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.iv_show_passowrd)
    ImageView ivShowPassowrd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_regist)
    TextView btnRegist;
    @BindView(R.id.btn_forget)
    TextView btnForget;
    @BindView(R.id.activity_new_login_tv_tenant)
    TextView tvTenant;
    @BindView(R.id.activity_new_login_tv_landlord)
    TextView tvLandlord;


    int mType;

    private boolean mIsVisiable = true;

    public static void toLandlordActivity(Context context) {
        toActivity(context, LoginInfo.LANDLORD);
    }

    public static void toTenantActivity(Context context) {
        toActivity(context, LoginInfo.TENANT);
    }

    private static void toActivity(Context context, int type) {
        Intent intent = new Intent(context, LoginNewActivity.class);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_login;
    }

    @Override
    public void init() {
        mType = getIntent().getIntExtra("type", LoginInfo.TENANT);
        if (LoginInfo.isLandlord(mType)) {
            rbLandlord.setChecked(true);
            tvLandlord.setEnabled(true);
            tvTenant.setEnabled(false);
            changeLayoutParams(rbLandlord, 80);
        } else {
            rbTenant.setChecked(true);
            tvLandlord.setEnabled(false);
            tvTenant.setEnabled(true);
            changeLayoutParams(rbTenant, 80);
        }
        rbLandlord.setOnCheckedChangeListener(this);
        rbTenant.setOnCheckedChangeListener(this);
        editPhone.addTextChangedListener(this);
        editPassword.addTextChangedListener(this);
        editPassword.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

            }
        });

        initPermissions();
    }

    private void initPermissions() {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            AMapHelp aMapHelp = new AMapHelp(mActivity.getApplicationContext());
                        }
                    }
                });
    }

    private void changeLayoutParams(View view, int dp) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = UIUtils.dip2px(dp);
        lp.height = UIUtils.dip2px(dp);
        view.setLayoutParams(lp);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            switch (buttonView.getId()) {
                case R.id.activity_new_login_rb_landlord:
                    mType = LoginInfo.LANDLORD;
                    changeLayoutParams(rbLandlord, 80);
                    changeLayoutParams(rbTenant, 60);
                    rbTenant.setChecked(false);
                    tvLandlord.setEnabled(true);
                    tvTenant.setEnabled(false);
                    break;
                case R.id.activity_new_login_rb_tenant:
                    mType = LoginInfo.TENANT;
                    changeLayoutParams(rbLandlord, 60);
                    changeLayoutParams(rbTenant, 80);
                    rbLandlord.setChecked(false);
                    tvLandlord.setEnabled(false);
                    tvTenant.setEnabled(true);
                    break;
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (KonkaApplication.isAgainLogin) {
            Toast.makeText(this, getString(R.string.log_in_again), Toast.LENGTH_LONG).show();
            KonkaApplication.isAgainLogin = false;
        }
    }

    @OnClick({R.id.iv_show_passowrd, R.id.btn_login, R.id.btn_regist, R.id.btn_forget, R.id.tv_login_verify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_show_passowrd:
                mIsVisiable = !mIsVisiable;
                editPassword.setInputType(mIsVisiable ?
                        InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT :
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                editPassword.setSelection(editPassword.getText().toString().length());
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_regist:
                RegistActivity.toActivity(this, mType);
                break;
            case R.id.btn_forget:
                ForgetPasswordActivity.toActivity(this, mType, "");
                break;
            case R.id.tv_login_verify:
                LoginVerifyActivity.toActivity(this, mType);
                break;
        }
    }

    private void login() {
        if (showHint(editPhone, editPassword)) {
            return;
        }
        showLoadingDialog();
        final String phone = editPhone.getText().toString();
        String password = editPassword.getText().toString();
        Subscription subscription =
                SecondRetrofitHelper.getInstance().login(phone, password, "", "1", isLandlord(mType) ? "0" : "1")
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

    private boolean checkEnableLogin() {
        if (editPhone.getText().toString().length() < 11)
            return false;
        if (editPassword.getText().toString().equals(""))
            return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getInstance().killAllActivity();
    }
}
