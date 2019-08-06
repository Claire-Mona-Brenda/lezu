package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.BaseApplication;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.MoneyBean;
import com.konka.renting.bean.PayBean;
import com.konka.renting.bean.PromotionCodeBean;
import com.konka.renting.bean.SeverPayListBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.bean.WxPayBean;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.event.UpdataHouseInfoEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.HouseEditActivity;
import com.konka.renting.landlord.house.PaySeverActivity;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.view.BailPayActivity;
import com.konka.renting.landlord.house.view.ChoosePayWayPopup;
import com.konka.renting.landlord.house.view.GeneralizeCodePopup;
import com.konka.renting.landlord.house.view.PayStatusDialog;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;

public class PayAllMoneyActivity extends BaseActivity implements IPayResCall, PayStatusDialog.PayReTry {

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
    @BindView(R.id.tv_choose_tips)
    TextView tvChooseTips;
    @BindView(R.id.recycle_choose_sever)
    RecyclerView recycleChooseSever;
    @BindView(R.id.activity_pay_all_tv_total_choose)
    TextView mTvTotalChoose;
    @BindView(R.id.activity_pay_all_tv_install_pay)
    TextView mTvInstallPay;
    @BindView(R.id.activity_pay_all_ll_install_code)
    LinearLayout mLlInstallCode;
    @BindView(R.id.activity_pay_all_tv_install_code)
    TextView mTvInstallCode;
    @BindView(R.id.activity_pay_all_tv_discounts)
    TextView mTvDiscounts;
    @BindView(R.id.activity_pay_all_tv_total)
    TextView mTvTotal;
    @BindView(R.id.activity_pay_all_rl_total_all)
    RelativeLayout mRlTotalAll;
    @BindView(R.id.activity_pay_all_tv_total_all)
    TextView mTvTotalAll;
    @BindView(R.id.activity_pay_all_btn_pay)
    Button mBtnPay;

    final String UNIT_MONEY = "¥";

    String room_id;
    CommonAdapter<SeverPayListBean> commonAdapter;
    List<SeverPayListBean> datas;
    CommonPopupWindow commonPopupWindow;
    ChoosePayWayPopup choosePayWayPopup;
    GeneralizeCodePopup generalizeCodePopup;

    PromotionCodeBean codeBean;

    float install = 0;
    float discounts = 0;
    float installTotal = 0;

    String service_charge_id;
    float serviceTotal = 0;

    float totalAll = 0;

    int mode = 1;
    String bond;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, PayAllMoneyActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_all;
    }

    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");

        tvTitle.setText(R.string.pay_sever_title);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        datas = new ArrayList<>();

        commonAdapter = new CommonAdapter<SeverPayListBean>(this, datas, R.layout.adapter_pay_sever_list) {
            @Override
            public void convert(ViewHolder viewHolder, SeverPayListBean severPayListBean) {
                viewHolder.setSelected(R.id.adapter_pay_sever_ll, severPayListBean.getService_charge_id().equals(service_charge_id));
                viewHolder.setText(R.id.adapter_pay_sever_tv_price, "¥" + (int) Float.parseFloat(severPayListBean.getPrice()));
                viewHolder.setText(R.id.adapter_pay_sever_tv_date, severPayListBean.getName());
                viewHolder.setOnClickListener(R.id.adapter_pay_sever_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        service_charge_id = severPayListBean.getService_charge_id();
                        serviceTotal = Float.valueOf(severPayListBean.getPrice()).intValue();
                        mTvTotalChoose.setText(UNIT_MONEY + serviceTotal);
                        notifyDataSetChanged();
                        totalAll = serviceTotal + installTotal;
                        mTvTotalAll.setText(UNIT_MONEY + totalAll);
                    }
                });
            }
        };
        recycleChooseSever.setLayoutManager(new GridLayoutManager(this, 3));
        recycleChooseSever.setAdapter(commonAdapter);

        ((BaseApplication) getApplication()).curPay = this;

        getMoney();
        getSeverList();
    }

    @Override
    protected void onDestroy() {
        ((BaseApplication) getApplication()).curPay = null;
        super.onDestroy();
    }

    @OnClick({R.id.iv_back, R.id.activity_pay_all_ll_install_code, R.id.activity_pay_all_btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_pay_all_ll_install_code:
                showInputCodePopup();
                break;
            case R.id.activity_pay_all_btn_pay:
                if (install == 0) {
                    showToast(R.string.error_no_install_pay);
                } else if (datas.size() <= 0) {
                    showToast(R.string.error_no_choose_sever_type);
                } else if (serviceTotal == 0) {
                    showToast(R.string.warm_no_choose_sever_type);
                } else {
                    showPayWayPopup();
                }
                break;
        }
    }


    /********************************************************************************************************/
    /**
     * 输入推广码
     */
    private void showInputCodePopup() {
        if (generalizeCodePopup == null) {
            generalizeCodePopup = new GeneralizeCodePopup(this);
            generalizeCodePopup.setOnCall(new GeneralizeCodePopup.OnCall() {
                @Override
                public void onClick(String code) {
                    if (TextUtils.isEmpty(code)) {
                        showToast(R.string.please_input_install_pay_code);
                    } else {
                        checkCode(code);
                    }
                }
            });
        }
        showPopup(generalizeCodePopup, Gravity.CENTER);

    }

    /**
     * 支付方式选择
     */
    private void showPayWayPopup() {
        if (choosePayWayPopup == null) {
            choosePayWayPopup = new ChoosePayWayPopup(this);
            choosePayWayPopup.setOnCall(new ChoosePayWayPopup.OnCall() {
                @Override
                public void aliPay() {
                    mode = 2;
                    pay();
                }

                @Override
                public void wxPay() {
                    mode = 1;
                    pay();
                }

                @Override
                public void qianBaoPay() {
                    mode = 3;
                    choosePayWayPopup.dismiss();
                    showOtherPayPopup();
                }
            });
        }
        showPopup(choosePayWayPopup, Gravity.BOTTOM);

    }

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
        showPopup(commonPopupWindow, Gravity.CENTER);
    }

    private void showPopup(PopupWindow popupWindow, int gravity) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent().getParent(), gravity, 0, 0);     // 第一个参数popup显示activity页面
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

    /**************************************************接口***************************************************/

    //获取服务费选项
    private void getSeverList() {
        Subscription subscription = SecondRetrofitHelper.getInstance().serviceCharge()
                .compose(RxUtil.<DataInfo<List<SeverPayListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<SeverPayListBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo<List<SeverPayListBean>> dataInfo) {
                        if (dataInfo.success()) {
                            datas.clear();
                            datas.addAll(dataInfo.data());
                            if (datas.size() > 0) {
                                service_charge_id = datas.get(0).getService_charge_id();
                                serviceTotal = Float.valueOf(datas.get(0).getPrice());
                                mTvTotalChoose.setText(UNIT_MONEY + serviceTotal);
                                totalAll = serviceTotal + installTotal;
                                mTvTotalAll.setText(UNIT_MONEY + totalAll);
                            }
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            ShowToastUtil.showWarningToast(mActivity, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
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
                            install = dataInfo.data().getMoney();
                            installTotal = install;
                            mTvInstallPay.setText(UNIT_MONEY + install);
                            mTvTotal.setText(UNIT_MONEY + install);
                            totalAll = serviceTotal + installTotal;
                            mTvTotalAll.setText(UNIT_MONEY + totalAll);
                        } else {
                            ShowToastUtil.showWarningToast(mActivity, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void checkCode(String code) {
        showLoadingDialog();
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance()
                .promotionCodeDetail(code)
                .compose(RxUtil.<DataInfo<PromotionCodeBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PromotionCodeBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        e.printStackTrace();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<PromotionCodeBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            generalizeCodePopup.dismiss();
                            codeBean = dataInfo.data();
                            discounts = dataInfo.data().getPrice();
                            installTotal = install - discounts;
                            totalAll = installTotal + serviceTotal;
                            mTvDiscounts.setText("-" + UNIT_MONEY + discounts);
                            mTvInstallCode.setText(code);
                            mTvTotal.setText(UNIT_MONEY + installTotal);
                            mTvTotalAll.setText(UNIT_MONEY + totalAll);
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void pay() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().installOrderPay2(mode + "", room_id, codeBean == null ? "0" : codeBean.getId() + "", service_charge_id)
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
                            switch (mode) {
                                case 2://支付宝支付
                                    ali(dataInfo.data().getData().toString());
                                    break;
                                case 1://微信支付
                                    WxPayBean wxPayInfo = new Gson().fromJson(new Gson().toJson(dataInfo.data().getData()), WxPayBean.class);
                                    wechat(wxPayInfo);
                                    break;
                                case 3://余额支付
                                    ShowToastUtil.showSuccessToast(mActivity, dataInfo.msg());
                                    payResCall(0, dataInfo.msg());
                                    break;

                            }
                        } else {
                            ShowToastUtil.showWarningToast(mActivity, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /*****************************************************************************************/

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
                .build().toWXPayNotSign(mActivity, appid);
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
}
