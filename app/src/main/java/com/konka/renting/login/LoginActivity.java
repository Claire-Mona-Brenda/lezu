package com.konka.renting.login;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.MainActivity;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

import static com.konka.renting.login.LoginInfo.isLandlord;
import static com.konka.renting.utils.UIUtils.showHint;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.edit_password)
    EditText mEditPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    int mType;

    private boolean mIsVisiable = true;

    public static void toLandlordActivity(Context context) {
        toActivity(context, LoginInfo.LANDLORD);
    }

    public static void toTenantActivity(Context context) {
        toActivity(context, LoginInfo.TENANT);
    }

    private static void toActivity(Context context, int type) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        mType = getIntent().getIntExtra("type", LoginInfo.LANDLORD);

        setTitleText(R.string.login_title);
        mBtnLogin.setText(isLandlord(mType) ? R.string.login_landlord : R.string.login_tenant);
//        mEditPhone.setText("18318701765 ");
//        mEditPassword.setText("123123");
    }

    @OnClick({R.id.iv_back, R.id.iv_show_passowrd, R.id.btn_login, R.id.btn_regist, R.id.btn_forget, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_show_passowrd:
                mIsVisiable = !mIsVisiable;
                mEditPassword.setInputType(mIsVisiable ?
                        InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT :
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mEditPassword.setSelection(mEditPassword.getText().toString().length());
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_regist:
                RegistActivity.toActivity(this, mType);
                finish();
                break;
            case R.id.btn_forget:
                ForgetPasswordActivity.toActivity(this, mType,"");
                finish();
                break;
            case R.id.btn_back:
                ChoiceIdentityActivity.toActivity(this);
                finish();
                break;
        }
    }

    private void login() {
        if (showHint(mEditPhone, mEditPassword)) {
            return;
        }

        showLoadingDialog();

        String phone = mEditPhone.getText().toString();
        String password = mEditPassword.getText().toString();

        Subscription subscription = (isLandlord(mType) ?
                RetrofitHelper.getInstance().login_landlord(phone, password) :
                RetrofitHelper.getInstance().login_renter(phone, password))
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
                            LoginUserBean.getInstance().setMobile(info.data().login_name);
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

}
