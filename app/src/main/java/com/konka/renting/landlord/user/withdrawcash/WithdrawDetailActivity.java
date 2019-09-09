package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class WithdrawDetailActivity extends BaseActivity {

    @BindView(R.id.tv_bank_card)
    TextView mTvBankCard;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
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
    @BindView(R.id.tv_withdraw_money)
    TextView tvWithdrawMoney;
    @BindView(R.id.tv_rmb)
    TextView tvRmb;
    @BindView(R.id.img_bank_icon)
    ImageView imgBankIcon;
    @BindView(R.id.tv_select_bank)
    TextView tvSelectBank;
    @BindView(R.id.rl_select_bank)
    RelativeLayout rlSelectBank;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;
    private String id;
    private String balance;

    public static void toActivity(Context context, String balance) {
        Intent intent = new Intent(context, WithdrawDetailActivity.class);
        intent.putExtra("balance", balance);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw_detail;
    }

    @Override
    public void init() {
        setTitleText(R.string.withdraw);
        balance = getIntent().getStringExtra("balance");
        mTvMoney.setText(balance);
        addRxBusSubscribe(SelectCardEvent.class, new Action1<SelectCardEvent>() {

            @Override
            public void call(SelectCardEvent selectCardEvent) {
                String number = selectCardEvent.getNumber();
                String band = selectCardEvent.getBank() + "(" + number.substring(number.length() - 4) + ")";
                mTvBankCard.setText(band);
                id = selectCardEvent.getId();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_withdraw, R.id.rl_select_bank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_withdraw:
                String ammount = mEtMoney.getText().toString();
                String bankCard = mTvBankCard.getText().toString();
                if (!TextUtils.isEmpty(ammount) && !TextUtils.isEmpty(bankCard))
                    applyWithDraw();
                else if (TextUtils.isEmpty(bankCard)) {
                    showToast("请选择到账银行");
                } else
                    showToast("请输入提现金额");
                break;
            case R.id.rl_select_bank:
                SelectBankCardActivity.toActivity(this);
                break;
        }
    }

    private void applyWithDraw() {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().applyWithDraw(id, mEtMoney.getText().toString())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            showToast(dataInfo.msg());
                            RxBus.getDefault().post(new WithdrawEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


}
