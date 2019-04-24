package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class WithdrawcashActivity extends BaseActivity {

    @BindView(R.id.tv_money)
    TextView mTvMoney;
    private String balance;

    public static void toActivity(Context context, String balance) {
        Intent intent = new Intent(context, WithdrawcashActivity.class);
        intent.putExtra("balance", balance);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdrawcash;
    }

    @Override
    public void init() {
        setTitleText(R.string.withdraw_cash);
        setRightText(R.string.withdraw_record);
        balance = getIntent().getStringExtra("balance");
        mTvMoney.setText(balance);
        addRxBusSubscribe(WithdrawEvent.class, new Action1<WithdrawEvent>() {
            @Override
            public void call(WithdrawEvent withdrawEvent) {
                finish();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.btn_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                WithdrawRecordActivity.toActivity(this);
                break;
            case R.id.btn_withdraw:
                WithdrawDetailActivity.toActivity(this,balance);
                break;
        }
    }
}
