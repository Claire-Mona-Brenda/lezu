package com.konka.renting.login;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.KInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class RegistPasswordActivity extends BaseActivity {

    @BindView(R.id.edit_password)
    EditText mEditPassword;
    @BindView(R.id.edit_password_sure)
    EditText mEditPasswordSure;

    private int mType;
    private String mPhone;
    private String mCode;


    public static void toActivity(Context context, String phone, String verCode, int type) {
        Intent intent = new Intent(context, RegistPasswordActivity.class);
        intent.putExtra("code", verCode);
        intent.putExtra("type", type);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist_password;
    }

    @Override
    public void init() {
        setTitleText(R.string.login_regist);
        mCode = getIntent().getStringExtra("code");
        mPhone = getIntent().getStringExtra("phone");
        mType = getIntent().getIntExtra("type", 1);
    }

    private void register() {
        if (UIUtils.showHint(mEditPassword, mEditPasswordSure)) {
            return;
        }


        String password = mEditPassword.getText().toString();
        String password1 = mEditPasswordSure.getText().toString();

        if (password.length()<6){
            showToast(R.string.regist_password_is_wrong_lenght);
            return;
        }

        if (!password.equals(password1)) {
            showToast(R.string.regist_password_is_wrong_txt);
            return;
        }

        showLoadingDialog();
        Subscription subscription =SecondRetrofitHelper.getInstance().register(mPhone, password, mCode)
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

    private void toMain() {
        if (LoginInfo.isLandlord(mType)) {
            LoginActivity.toLandlordActivity(this);
        }else {
            LoginActivity.toTenantActivity(this);
        }

//        MainActivity.toMainActivity(this, mType);
        finish();
    }

    private void toLogin() {
        if (LoginInfo.isLandlord(mType)) {
            LoginNewActivity.toLandlordActivity(this);
        }else {
            LoginNewActivity.toTenantActivity(this);
        }

//        MainActivity.toMainActivity(this, mType);
        finish();
    }

    @OnClick({R.id.iv_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_commit:
                register();
                break;
        }
    }
}
