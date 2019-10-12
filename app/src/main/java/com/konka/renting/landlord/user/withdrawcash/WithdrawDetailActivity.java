package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CheckWithdrawPwdBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.MyBankBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.event.DelBankEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.PasswordView;
import com.konka.renting.widget.WarmPopupwindow;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class WithdrawDetailActivity extends BaseActivity implements PasswordView.PasswordListener {


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
    @BindView(R.id.tv_bank_card)
    TextView mTvBankCard;
    @BindView(R.id.rl_select_bank)
    RelativeLayout rlSelectBank;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private final String BANK_CARD = "bank_card";
    private final String BANK_CARD_ID = "bank_card_id";

    private String card_id;
    private String balance;
    int num;
    WithdrawcashPwdPopup withdrawcashPwdPopup;
    WarmPopupwindow warmPopupwindow;

    SharedPreferences sharedPreferences;

    public static void toActivity(Context context, String balance, int num) {
        Intent intent = new Intent(context, WithdrawDetailActivity.class);
        intent.putExtra("balance", balance);
        intent.putExtra("num", num);
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
        num = getIntent().getIntExtra("num", 0);

        mTvMoney.setText(balance);
        tvTime.setText(String.format(getString(R.string.withdraw_num), num));

        sharedPreferences = getSharedPreferences(BANK_CARD, MODE_PRIVATE);
        card_id = sharedPreferences.getString(BANK_CARD_ID, "");

        addRxBusSubscribe(SelectCardEvent.class, new Action1<SelectCardEvent>() {

            @Override
            public void call(SelectCardEvent selectCardEvent) {

                tvSelectBank.setText(selectCardEvent.getNumber());
                mTvBankCard.setText(selectCardEvent.getBank());
                card_id = selectCardEvent.getId();
                if (!TextUtils.isEmpty(selectCardEvent.getImag_url())) {
                    Picasso.get().load(selectCardEvent.getImag_url()).into(imgBankIcon);
                } else {
                    Picasso.get().load(R.mipmap.bank_other).into(imgBankIcon);
                }
            }
        });
        addRxBusSubscribe(DelBankEvent.class, new Action1<DelBankEvent>() {
            @Override
            public void call(DelBankEvent delBankEvent) {
                if (!TextUtils.isEmpty(card_id) && card_id.equals(delBankEvent.getCard_id())) {
                    card_id = "";
                    tvSelectBank.setText("");
                    mTvBankCard.setText("");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(BANK_CARD_ID, card_id);
                    editor.commit();
                }
            }
        });

        getBankCardList();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        card_id = sharedPreferences.getString(BANK_CARD_ID, "");
    }

    @OnClick({R.id.iv_back, R.id.btn_withdraw, R.id.img_tips, R.id.rl_select_bank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.btn_withdraw://提现
                if (TextUtils.isEmpty(card_id)) {
                    showToast(R.string.please_choose_bank_card);
                } else if (TextUtils.isEmpty(balance)) {
                    showToast(R.string.no_balance);
                } else if (Float.valueOf(balance) <= 0) {
                    showToast(R.string.no_balance);
                }else if (num<=0){
                    showToast(R.string.withdraw_num_no);
                }else {
                    showPop();
                }
//                if (!TextUtils.isEmpty(ammount) && !TextUtils.isEmpty(bankCard)) {
////                    applyWithDraw();
//                    showPop();
//                }else if (TextUtils.isEmpty(bankCard)) {
//                    showToast(R.string.select_to);
//                } else
//                    showToast(R.string.please_input_withdraw_money);
                break;
            case R.id.rl_select_bank://选择银行卡
                SelectBankCardActivity.toActivity(this, true);
                break;
            case R.id.img_tips://提示
                showTipsPop();
                break;
        }
    }

    /***********************************************接口****************************************************/
    private void checkPassword(String pwd) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().checkPassword("2", pwd)
                .compose(RxUtil.<DataInfo<CheckWithdrawPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<CheckWithdrawPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<CheckWithdrawPwdBean> dataInfo) {


                        if (dataInfo.success()) {
                            applyWithDraw(dataInfo.data().getCode());
                        } else {
                            dismiss();
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void applyWithDraw(String code) {
        Subscription subscription = SecondRetrofitHelper.getInstance().withdraw(code, card_id)
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
                            RxBus.getDefault().post(new WithdrawEvent());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(BANK_CARD_ID, card_id);
                            editor.commit();
                            ResultActivity.toActivity(mActivity, getString(R.string.title_withdraw_result), getString(R.string.tips_withdraw_result_success), getString(R.string.tips_withdraw_result_tips), true);
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getBankCardList() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getBankCardList("1")
                .compose(RxUtil.<DataInfo<PageDataBean<MyBankBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<MyBankBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        card_id = null;
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<MyBankBean>> listInfoDataInfo) {
                        if (listInfoDataInfo.success()) {
                            List<MyBankBean> list = listInfoDataInfo.data().getList();
                            if (TextUtils.isEmpty(card_id)) {
                                if (list != null && list.size() > 0) {
                                    MyBankBean myBankBean = list.get(0);
                                    card_id = myBankBean.getCard_id() + "";
                                    tvSelectBank.setText(myBankBean.getBank_name());
                                    mTvBankCard.setText(myBankBean.getCard_no());
                                    if (!TextUtils.isEmpty(myBankBean.getBank_image())) {
                                        Picasso.get().load(myBankBean.getBank_image()).into(imgBankIcon);
                                    } else {
                                        Picasso.get().load(R.mipmap.bank_other).into(imgBankIcon);
                                    }
                                }
                            } else {
                                boolean isHave = false;
                                int size = list.size();
                                for (int i = 0; i < size; i++) {
                                    MyBankBean myBankBean = list.get(i);
                                    if (card_id.equals(myBankBean.getCard_id() + "")) {
                                        isHave = true;
                                        tvSelectBank.setText(myBankBean.getBank_name());
                                        mTvBankCard.setText(myBankBean.getCard_no());
                                        if (!TextUtils.isEmpty(myBankBean.getBank_image())) {
                                            Picasso.get().load(myBankBean.getBank_image()).into(imgBankIcon);
                                        } else {
                                            Picasso.get().load(R.mipmap.bank_other).into(imgBankIcon);
                                        }
                                        break;
                                    }
                                }
                                if (!isHave && list != null && list.size() > 0) {
                                    MyBankBean myBankBean = list.get(0);
                                    card_id = myBankBean.getCard_id() + "";
                                    tvSelectBank.setText(myBankBean.getBank_name());
                                    mTvBankCard.setText(myBankBean.getCard_no());
                                    if (!TextUtils.isEmpty(myBankBean.getBank_image())) {
                                        Picasso.get().load(myBankBean.getBank_image()).into(imgBankIcon);
                                    } else {
                                        Picasso.get().load(R.mipmap.bank_other).into(imgBankIcon);
                                    }
                                } else if (!isHave) {
                                    card_id = null;
                                }
                            }
                        } else {
                            card_id = null;
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    /*******************************************弹窗***********************************************************/
    private void showPop() {
        withdrawcashPwdPopup = new WithdrawcashPwdPopup(mActivity);
        withdrawcashPwdPopup.setListent(this);
        withdrawcashPwdPopup.setTvForgetEnable(true);
        withdrawcashPwdPopup.setForgetListent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (withdrawcashPwdPopup != null)
                    withdrawcashPwdPopup.dismiss();
                CheckPhoneActivity.toActivity(mActivity, LoginUserBean.getInstance().getMobile());
            }
        });
        showPopup(withdrawcashPwdPopup);
        withdrawcashPwdPopup.setPasswordViewFouse();
    }

    private void showTipsPop() {
        if (warmPopupwindow == null) {
            warmPopupwindow = new WarmPopupwindow(mActivity);
        }
        showPopup(warmPopupwindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);

            }
        });
    }

    @Override
    public void passwordChange(String changeText) {

    }

    @Override
    public void passwordComplete(String password) {
        if (withdrawcashPwdPopup != null)
            withdrawcashPwdPopup.dismiss();
        checkPassword(password);
    }


    @Override
    public void keyEnterPress(String password, boolean isComplete) {

    }

    public void hideInput(){
        InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
