package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.ForgetPasswordActivity;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class UpdatePhoneCodeActivity extends BaseActivity {

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
    @BindView(R.id.activity_update_phone_code_tv_new_phone)
    TextView mTvNewPhone;
    @BindView(R.id.activity_update_code_edit_code)
    EditText mEditCode;
    @BindView(R.id.activity_update_phone_code_tv_sure)
    TextView mTvSure;

    private int mType;
    private String newPhone;

    public static void toActivity(Context context, int type, String newPhone) {
        Intent intent = new Intent(context, UpdatePhoneCodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("newPhone", newPhone);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_phone_code;
    }

    @Override
    public void init() {
        mType = getIntent().getIntExtra("type", LoginInfo.LANDLORD);
        newPhone = getIntent().getStringExtra("newPhone");
        tvTitle.setText(R.string.update_phone_title);

        mTvNewPhone.setText(newPhone);

    }


    @OnClick({R.id.iv_back, R.id.activity_update_phone_code_tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_update_phone_code_tv_sure:
                String code = mEditCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    showToast(R.string.update_phone_input_code_hint);
                } else {
                    sure(code);
                }
                break;
        }
    }

    /****************************************接口********************************************************/
    private void sure(String code) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().updateBindPhone(newPhone, code)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        doFailed();
                        finish();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        super.onNext(dataInfo);
                        dismiss();
                        if (dataInfo.success()) {
                            if (mType == LoginInfo.LANDLORD)
                                LoginNewActivity.toLandlordActivity(mActivity);
                            else
                                LoginNewActivity.toTenantActivity(mActivity);
                            showToast(R.string.update_phone_success);
                        } else {
                            showToast(R.string.update_phone_warm);
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
