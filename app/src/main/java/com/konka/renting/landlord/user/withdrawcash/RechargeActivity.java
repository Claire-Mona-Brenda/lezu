package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.BaseApplication;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.AlipayBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayBean;
import com.konka.renting.bean.WxPayBean;
import com.konka.renting.bean.WxPayInfo;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.view.BailPayActivity;
import com.konka.renting.landlord.house.view.PayStatusDialog;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class RechargeActivity extends BaseActivity implements IPayResCall, PayStatusDialog.PayReTry {

    @BindView(R.id.et_money)
    EditText mEtMoney;
    @BindView(R.id.alipay)
    RadioButton mAlipay;
    @BindView(R.id.wechat)
    RadioButton mWechat;
    private int payType;

    String bond;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, RechargeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    public void init() {

        setTitleText("余额充值");
        mAlipay.setChecked(true);
        payType = 2;
        addRxBusSubscribe(RechargeEvent.class, new Action1<RechargeEvent>() {
            @Override
            public void call(RechargeEvent rechargeEvent) {
                finish();
            }
        });
        mAlipay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWechat.setChecked(false);
                    mAlipay.setChecked(true);
                    payType = 2;
                }

            }
        });
        mWechat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAlipay.setChecked(false);
                    mWechat.setChecked(true);
                    payType = 1;
                }


            }
        });
        ((BaseApplication) getApplication()).curPay = this;
        mEtMoney.setFocusable(true);
        mEtMoney.setFocusableInTouchMode(true);
        mEtMoney.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.iv_back, R.id.re_alipay, R.id.re_wechat, R.id.btn_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.re_alipay:
                mWechat.setChecked(false);
                mAlipay.setChecked(true);
                payType = 2;
                break;
            case R.id.re_wechat:
                mWechat.setChecked(true);
                mAlipay.setChecked(false);
                payType = 1;
                break;
            case R.id.btn_withdraw:
                if (TextUtils.isEmpty(mEtMoney.getText().toString())) {
                    showToast("请输入充值金额");
                } else if (Float.valueOf(mEtMoney.getText().toString()) == 0) {
                    showToast("充值金额不能为零");
                } else {
                    pay();
                }
                break;
        }
    }

    private void pay() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rechargePay(mEtMoney.getText().toString(), payType + "")
                .compose(RxUtil.<DataInfo<PayBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PayBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<PayBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            switch (dataInfo.data().getPayment()) {
                                case "1"://微信支付
                                    WxPayBean wxPayInfo = new Gson().fromJson(new Gson().toJson(dataInfo.data().getData()), WxPayBean.class);
                                    wechat(wxPayInfo);
                                    break;
                                case "2"://支付宝支付
                                    AliPayUtil.ALiPayBuilder builder = new AliPayUtil.ALiPayBuilder();
                                    builder.build().toALiPay(RechargeActivity.this, dataInfo.data().getData().toString());
                                    break;
                            }
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public void wechat(WxPayBean data) {

//        appid：应用ID
//        partnerid：商户号
//        prepayid：预支付交易会话ID
//        package：扩展字段
//        noncestr：随机字符串
//        timestamp：时间戳
//        sign：签名

        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        String appid = data.appid;//应用ID
        String partnerid = data.partnerid;//商户号
        String prepayid = data.prepayid;//预支付交易会话ID
        String _package = data.packageX;//扩展字段w
        String noncestr = data.noncestr;//随机字符串
        String timestamp = new Double(data.timestamp).longValue() + "";//时间戳
        String sign = data.sign;//签名

        builder.setAppId(appid)
                .setPartnerId(partnerid)
                .setPrepayId(prepayid)
                .setPackageValue(_package)
                .setNonceStr(noncestr)
                .setTimeStamp(timestamp)
                .setSign(sign)
                .build().toWXPayNotSign(this, appid);
    }


    @Override
    protected void onDestroy() {
        ((BaseApplication) getApplication()).curPay = null;
        RxBus.getDefault().post(new UpdateEvent());
        super.onDestroy();
    }

    PayStatusDialog payStatusDialog;

    @Override
    public void payResCall(int type, String reason) {
        switch (type) {
            case 0:
                // TODO: 2018/4/30 成功
                UIUtils.displayToast("支付成功");
//                new PayStatusDialog(this, true, bond).show();
                RxBus.getDefault().post(new UpdateEvent());
                RxBus.getDefault().post(new TentantOpenDoorEvent(11));
                finish();
                break;
            case -1:
                if (!(payStatusDialog != null && payStatusDialog.isShowing())) {
                    payStatusDialog = new PayStatusDialog(this, false, bond).setFailReason(reason).setPayReTry(this);
                    payStatusDialog.show();
                }

//                UIUtils.displayToast("支付失败");
                break;
            case -2:
//                Toast.makeText()
//                UIUtils.displayToast("支付取消!!!");
                break;
        }
    }

    @Override
    public void reTry() {
        pay();
    }
}
