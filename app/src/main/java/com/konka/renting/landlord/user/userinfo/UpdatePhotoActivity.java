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
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.ForgetPasswordActivity;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.utils.RxUtil;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class UpdatePhotoActivity extends BaseActivity {
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
    @BindView(R.id.activity_update_phone_tv_phone)
    TextView mTvPhone;
    @BindView(R.id.activity_update_phone_edit_new_phone)
    EditText mEditNewPhone;
    @BindView(R.id.activity_update_phone_tv_next)
    TextView mTvNext;

    private int mType;
    private String phone;

    public static void toActivity(Context context, int type, String phone) {
        Intent intent = new Intent(context, UpdatePhotoActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_photo;
    }

    @Override
    public void init() {
        mType = getIntent().getIntExtra("type", LoginInfo.LANDLORD);
        phone = getIntent().getStringExtra("phone");
        tvTitle.setText(R.string.update_phone_title);

        mTvPhone.setText(phone);


    }


    @OnClick({R.id.iv_back, R.id.activity_update_phone_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_update_phone_tv_next:
                String newPhone = mEditNewPhone.getText().toString();
                if (TextUtils.isEmpty(newPhone)) {
                    showToast(R.string.update_phone_input_phone_hint);
                } else if (newPhone.length() < 11) {
                    showToast(R.string.please_input_phone);
                } else {
                    phoneCheck(newPhone);
                }
                break;
        }
    }

//    private boolean isPhone(String phone) {
//        boolean is = false;
//        is = Pattern.matches("^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$", phone);
//        return is;
//    }

    /****************************************接口********************************************************/
    private void phoneCheck(String phone) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().phoneCheck(phone)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        showToast(R.string.update_phone_code_warm);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        super.onNext(dataInfo);
                        if (dataInfo.success()) {
                            getVerify(phone);
                        } else {
                            dismiss();
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getVerify(String phone) {
        Subscription subscription = SecondRetrofitHelper.getInstance().getVerify(phone, "2")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        showToast(R.string.update_phone_code_warm);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        super.onNext(dataInfo);
                        dismiss();
                        if (dataInfo.success()) {
                            UpdatePhoneCodeActivity.toActivity(mActivity, mType, phone);
                        } else {
                            showToast(R.string.update_phone_code_warm);
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
