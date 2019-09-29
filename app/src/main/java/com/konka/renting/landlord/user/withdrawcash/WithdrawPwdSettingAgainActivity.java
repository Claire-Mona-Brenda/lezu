package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CheckWithdrawPwdBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.event.SetWithdrawPwdEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.PasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class WithdrawPwdSettingAgainActivity extends BaseActivity {
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
    @BindView(R.id.activity_withdraw_set_pwd_again_tv_tips_1)
    TextView mTvTips1;
    @BindView(R.id.activity_withdraw_set_pwd_again_tv_tips_2)
    TextView mTvTips2;
    @BindView(R.id.activity_withdraw_set_pwd_again_pv_pwd)
    PasswordView mPvPwd;
    @BindView(R.id.activity_withdraw_set_pwd_again_btn_next)
    Button mBtnNext;

    String code;
    String pwd;

    public static void toActivity(Context context, String code, String pwd) {
        Intent intent = new Intent(context, WithdrawPwdSettingAgainActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("pwd", pwd);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw_pwd_set_again;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_withdraw_pwd);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        code = getIntent().getStringExtra("code");
        pwd = getIntent().getStringExtra("pwd");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPvPwd.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPvPwd.showInput();
            }
        }, 500);
    }


    @OnClick({R.id.iv_back, R.id.activity_withdraw_set_pwd_again_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_withdraw_set_pwd_again_btn_next://下一步
                String pwd2 = mPvPwd.getPassword();
                if (TextUtils.isEmpty(pwd2) || pwd2.length() < 6) {
                    showToast(R.string.please_input_pwd_number);
                } else if (!pwd.equals(pwd2)) {
                    showToast(R.string.regist_password_is_wrong_txt);
                }else{
                    setWithdrawPassword();
                }
                break;
        }
    }

    /*******************************************************接口******************************************/
    private void setWithdrawPassword() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().setWithdrawPassword(code,pwd)
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
                            RxBus.getDefault().post(new SetWithdrawPwdEvent());
                            ResultActivity.toActivity(mActivity, getString(R.string.title_withdraw_pwd), getString(R.string.setting_result_success), "", true);
                            LoginUserBean.getInstance().setIs_withdraw_pass(1);
                            LoginUserBean.getInstance().save();
                            finish();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
