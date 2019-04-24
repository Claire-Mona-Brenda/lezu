package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.MoneyBean;
import com.konka.renting.bean.PayBean;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.bean.WxPayBean;
import com.konka.renting.event.UpdataHouseInfoEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.GetManagePwdAuthorityActivity;
import com.konka.renting.landlord.house.HouseInfoActivity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;
import com.konka.renting.widget.CommonPopupWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class BailPayActivity extends BaseActivity implements IPayResCall, PayStatusDialog.PayReTry {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_pay_money)
    TextView tvPayMoney;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.alipay)
    RadioButton alipay;
    @BindView(R.id.g_ali)
    RelativeLayout gAli;
    @BindView(R.id.wechat)
    RadioButton wechat;
    @BindView(R.id.g_wechat)
    RelativeLayout gWechat;
    @BindView(R.id.card)
    TextView card;
    @BindView(R.id.other)
    RadioButton other;
    @BindView(R.id.g_card)
    RelativeLayout gCard;
    @BindView(R.id.next)
    Button next;

    String bond;
    String room_id;
    String address;
    int mode = 2;

    CommonPopupWindow commonPopupWindow;

    public static void toActivity(Context context, String room_id, String address) {
        Intent intent = new Intent(context, BailPayActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.lib_bail_pay_activity;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.pay_setting);
        room_id = getIntent().getStringExtra("room_id");
        address = getIntent().getStringExtra("address");
        tvAddress.setText(address);

        initPay();
        getMoney();
        alipay.setChecked(true);
    }


    public void initPay() {
        alipay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mode = 2;
                    wechat.setChecked(false);
                    other.setChecked(false);
                }
            }
        });
        wechat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mode = 1;
                    alipay.setChecked(false);
                    other.setChecked(false);
                }
            }
        });
        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mode = 3;
                    wechat.setChecked(false);
                    alipay.setChecked(false);
                }
            }
        });
    }

    //获取安装费金额
    private void getMoney() {
        Subscription subscription = SecondRetrofitHelper.getInstance().installCharge()
                .compose(RxUtil.<DataInfo<MoneyBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<MoneyBean>>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo<MoneyBean> dataInfo) {
                        if (dataInfo.success()) {
                            tvPayMoney.setText("¥ " + dataInfo.data().getMoney());
                        } else {
                            ShowToastUtil.showWarningToast(BailPayActivity.this, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    public void ali(String info) {
        AliPayUtil.ALiPayBuilder builder = new AliPayUtil.ALiPayBuilder();
        builder.build().toALiPay(this, info);
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
                .build().toWXPayNotSign(BailPayActivity.this, appid);
    }

    private void pay() {
        Subscription subscription = SecondRetrofitHelper.getInstance().installOrderPay(room_id, mode + "")
//        Subscription subscription = SecondRetrofitHelper.getInstance().serviceChargePay("1",missionEnity.id,mode+"")
                .compose(RxUtil.<DataInfo<PayBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PayBean>>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo<PayBean> dataInfo) {
                        if (dataInfo.success()) {
                            switch (mode) {
                                case 2://支付宝支付
                                    ali(dataInfo.data().getData().toString());
                                    break;
                                case 1://微信支付
                                    WxPayBean wxPayInfo = new Gson().fromJson(new Gson().toJson(dataInfo.data().getData()), WxPayBean.class);
                                    wechat(wxPayInfo);
                                    break;
                                case 3://余额支付
                                    ShowToastUtil.showSuccessToast(BailPayActivity.this, dataInfo.msg());
                                    payResCall(0, dataInfo.msg());
                                    break;

                            }
                        } else {
                            ShowToastUtil.showWarningToast(BailPayActivity.this, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @Override
    public void reTry() {
        pay();
    }

    PayStatusDialog payStatusDialog;

    @Override
    public void payResCall(int type, String reason) {
        switch (type) {
            case 0:
                // TODO: 2018/4/30 成功
                UIUtils.displayToast("支付成功");
                new PayStatusDialog(this, true, bond).show();
                RxBus.getDefault().post(new UpdataHouseInfoEvent());
                RxBus.getDefault().post(new UpdateEvent());
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

    @OnClick({R.id.iv_back, R.id.next, R.id.g_ali, R.id.g_wechat, R.id.g_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                this.finish();
                break;
            case R.id.next://支付
                KonkaApplication.getInstance().curPay = this;
                if (mode != 3)
                    pay();
                else
                    showOtherPayPopup();
                break;
            case R.id.g_ali:
                mode = 2;
                alipay.setChecked(true);
                wechat.setChecked(false);
                other.setChecked(false);
                break;
            case R.id.g_wechat:
                mode = 1;
                wechat.setChecked(true);
                alipay.setChecked(false);
                other.setChecked(false);
                break;
            case R.id.g_card:
                mode = 3;
                other.setChecked(true);
                wechat.setChecked(false);
                alipay.setChecked(false);
                break;
        }
    }

    /********************************************************************************************************/
    /**
     * 钱包支付
     */
    private void showOtherPayPopup() {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.please_balance_pay))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPopupWindow.dismiss();
                        pay();
                    }
                })
                .create();
        showPopup(commonPopupWindow);

    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent().getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }
}
