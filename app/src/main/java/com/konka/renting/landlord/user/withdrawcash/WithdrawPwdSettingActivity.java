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
import com.konka.renting.event.SetWithdrawPwdEvent;
import com.konka.renting.widget.PasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class WithdrawPwdSettingActivity extends BaseActivity {
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
    @BindView(R.id.activity_withdraw_set_pwd_pv_pwd)
    PasswordView mPvPwd;
    @BindView(R.id.activity_withdraw_set_pwd_btn_next)
    Button mBtnNext;

    private boolean isFirstPwd = true;
    String code;

    public static void toActivity(Context context, String code) {
        Intent intent = new Intent(context, WithdrawPwdSettingActivity.class);
        intent.putExtra("code", code);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw_pwd_set;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_withdraw_pwd);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        code = getIntent().getStringExtra("code");

        addRxBusSubscribe(SetWithdrawPwdEvent.class, new Action1<SetWithdrawPwdEvent>() {
            @Override
            public void call(SetWithdrawPwdEvent setWithdrawPwdEvent) {
                finish();
            }
        });


    }


    @OnClick({R.id.iv_back, R.id.activity_withdraw_set_pwd_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_withdraw_set_pwd_btn_next://下一步
                String pwd=mPvPwd.getPassword();
                if (TextUtils.isEmpty(pwd)||pwd.length()<6){
                    showToast(R.string.please_input_pwd_number);
                }else{
                    WithdrawPwdSettingAgainActivity.toActivity(this,code,pwd);

                }
                break;
        }
    }
}
