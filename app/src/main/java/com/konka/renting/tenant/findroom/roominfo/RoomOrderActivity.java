package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.BaseApplication;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.PayBean;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.RentingDateBean;
import com.konka.renting.bean.RoomOederPriceBean;
import com.konka.renting.bean.RoomSearchInfoBean;
import com.konka.renting.bean.WxPayBean;
import com.konka.renting.event.ChooseDateEvent;
import com.konka.renting.event.PublicCancelEvent;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.ChooseDateActivity;
import com.konka.renting.landlord.house.view.ChoosePayWayPopup;
import com.konka.renting.landlord.house.view.PayStatusDialog;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.landlord.user.withdrawcash.ResultActivity;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.CircleTransform;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class RoomOrderActivity extends BaseActivity implements IPayResCall, PayStatusDialog.PayReTry {

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
    @BindView(R.id.activity_room_order_iv_icon)
    ImageView mIvIcon;
    @BindView(R.id.activity_room_order_card)
    CardView mCardView;
    @BindView(R.id.activity_room_order_tv_room_name)
    TextView mTvRoomName;
    @BindView(R.id.activity_room_order_tv_info)
    TextView mTvInfo;
    @BindView(R.id.activity_room_order_tv_price)
    TextView mTvPrice;
    @BindView(R.id.activity_room_order_tv_price_unit)
    TextView mTvPriceUnit;
    @BindView(R.id.activity_create_order_ll_choose_date)
    LinearLayout mLlChooseDate;
    @BindView(R.id.activity_create_order_tv_start)
    TextView mTvStart;
    @BindView(R.id.activity_create_order_ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.activity_create_order_tv_end)
    TextView mTvEnd;
    @BindView(R.id.activity_create_order_ll_end)
    LinearLayout mLlEnd;
    @BindView(R.id.activity_room_order_img_landlord)
    ImageView mImgLandlord;
    @BindView(R.id.activity_room_order_tv_landlord_name)
    TextView mTvLandlordName;
    @BindView(R.id.activity_room_order_img_certificate)
    ImageView mImgCertificate;
    @BindView(R.id.activity_room_order_tv_landlord_call)
    TextView mTvLandlordCall;
    @BindView(R.id.activity_create_order_tv_name)
    TextView mTvName;
    @BindView(R.id.activity_create_order_tv_id_card)
    TextView mTvIdCard;
    @BindView(R.id.activity_create_order_tv_phone)
    TextView mTvPhone;
    @BindView(R.id.activity_room_order_tv_pay_money)
    TextView mTvPayMoney;
    @BindView(R.id.activity_room_order_tv_to_pay_money)
    TextView mTvToPayMoney;

    RoomSearchInfoBean infoBean;
    CommonPopupWindow callPopupWindow;
    ChoosePayWayPopup choosePayWayPopup;
    ArrayList<RentingDateBean> rentingDates;

    String bond;
    PayStatusDialog payStatusDialog;
    int payment = 0;

    String startDate;
    String endDate;


    public static void toActivity(Context context, RoomSearchInfoBean infoBean) {
        Intent intent = new Intent(context, RoomOrderActivity.class);
        intent.putExtra("infoBean", infoBean);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_room_order;
    }

    @Override
    public void init() {
        infoBean = getIntent().getParcelableExtra("infoBean");

        tvTitle.setText(R.string.order_fill);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        rentingDates = new ArrayList<>();
        ((BaseApplication) getApplication()).curPay = this;

        addRxBusSubscribe(ChooseDateEvent.class, new Action1<ChooseDateEvent>() {
            @Override
            public void call(ChooseDateEvent chooseDateEvent) {
                startDate = chooseDateEvent.startDate;
                endDate = chooseDateEvent.endDate;
                mTvStart.setText(startDate);
                mTvEnd.setText(endDate);
                mTvStart.setTextColor(getResources().getColor(R.color.text_black));
                mTvEnd.setTextColor(getResources().getColor(R.color.text_black));
                if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                    rentOrderPrice();
                }
            }
        });

        if (infoBean != null) {
            initData();
            getRentDate();
        }
    }

    @Override
    protected void onDestroy() {
        ((BaseApplication) getApplication()).curPay = null;
        super.onDestroy();
    }

    private void initData() {
        Log.e("123123", "-initData----");
        String room_type;
        if (infoBean.getRoom_type().contains("_")) {
            String[] t = infoBean.getRoom_type().split("_");
            room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
        } else {
            room_type = infoBean.getRoom_type();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(room_type + "|");
        spannableStringBuilder.append(infoBean.getMeasure_area() + getString(R.string.unit_m2));
        spannableStringBuilder.append("|" + infoBean.getFloor() + "/" + infoBean.getTotal_floor() + "层");
        mTvInfo.setText(spannableStringBuilder);
        mTvRoomName.setText(infoBean.getRoom_name());
        mTvPrice.setText(infoBean.getHousing_price());

        if (infoBean.getImage() != null && infoBean.getImage().size() > 0 && !TextUtils.isEmpty(infoBean.getImage().get(0))) {
            Picasso.get().load(infoBean.getImage().get(0)).error(R.mipmap.fangchan_jiazai).into(mIvIcon);
        }

        if (!TextUtils.isEmpty(infoBean.getLandlord_headimgurl())) {
            Picasso.get().load(infoBean.getLandlord_headimgurl()).error(R.mipmap.fangchan_jiazai).transform(new CircleTransform()).into(mImgLandlord);
        }
        mTvLandlordName.setText(infoBean.getLandlord_name());
        Picasso.get().load(R.mipmap.attestation_icon).into(mImgCertificate);

        mTvName.setText(LoginUserBean.getInstance().getRealname());
        mTvIdCard.setText(LoginUserBean.getInstance().getIdentity());
        mTvPhone.setText(LoginUserBean.getInstance().getMobile());
    }

    @OnClick({R.id.iv_back, R.id.activity_create_order_ll_choose_date, R.id.activity_room_order_tv_landlord_call, R.id.activity_room_order_tv_to_pay_money})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_create_order_ll_choose_date:
                if (infoBean != null) {
                    ChooseDateActivity.toActivity(this, rentingDates);
                }
                break;
            case R.id.activity_room_order_tv_landlord_call:
                if (infoBean != null) {
                    showCalll(infoBean.getLandlord_phone());
                }
                break;
            case R.id.activity_room_order_tv_to_pay_money:
                if (infoBean != null && !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                    showPayWayPopup();
                } else if (infoBean != null) {
                    showToast(R.string.please_choose_date);
                }

                break;
        }
    }


    /*********************************************弹窗***************************************************/
    /**
     * 是否联系ta
     */
    private void showCalll(String tel) {
        if (callPopupWindow == null)
            callPopupWindow = new CommonPopupWindow.Builder(mActivity)
                    .setTitle(getString(R.string.tips))
                    .setContent(getString(R.string.apply_house_call_landlord))
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callPopupWindow.dismiss();
                            PhoneUtil.call(tel, mActivity);
                        }
                    })
                    .create();
        showPopup(callPopupWindow, Gravity.CENTER);
    }

    /**
     * 支付方式选择
     */
    private void showPayWayPopup() {
        if (choosePayWayPopup == null) {
            choosePayWayPopup = new ChoosePayWayPopup(this);
            choosePayWayPopup.setLlQianBaoVisibility(false);
            choosePayWayPopup.setOnCall(new ChoosePayWayPopup.OnCall() {
                @Override
                public void aliPay() {
                    payment = 2;
                    rentOrder();
                }

                @Override
                public void wxPay() {
                    payment = 1;
                    rentOrder();
                }

                @Override
                public void qianBaoPay() {

                }
            });
        }
        showPopup(choosePayWayPopup, Gravity.BOTTOM);

    }

    private void showPopup(PopupWindow popupWindow, int gravity) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent().getParent(), gravity, 0, -200);     // 第一个参数popup显示activity页面
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

    /************************************************接口************************************************/
    /**
     * 租客下单
     */
    public void rentOrder() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rentOrder(infoBean.getRoom_id(), startDate + " 12:00:00", endDate + " 12:00:00", payment + "")
                .compose(RxUtil.<DataInfo<PayBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PayBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<PayBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            switch (payment) {
                                case 2://支付宝支付
                                    ali(dataInfo.data().getData().toString());
                                    break;
                                case 1://微信支付
                                    WxPayBean wxPayInfo = new Gson().fromJson(new Gson().toJson(dataInfo.data().getData()), WxPayBean.class);
                                    wechat(wxPayInfo);
                                    break;

                            }
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 价格计算
     */
    public void rentOrderPrice() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rentOrderPrice(infoBean.getRoom_id(), startDate, endDate)
                .compose(RxUtil.<DataInfo<RoomOederPriceBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomOederPriceBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<RoomOederPriceBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            mTvPayMoney.setText(dataInfo.data().getTotal_price());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取已出租日期
     */
    private void getRentDate() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rentingDate2(infoBean.getRoom_id())
                .compose(RxUtil.<DataInfo<List<RentingDateBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<RentingDateBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<List<RentingDateBean>> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            rentingDates.addAll(dataInfo.data());
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
    public void payResCall(int type, String reason) {
        switch (type) {
            case 0:
                // 支付成功
                RxBus.getDefault().post(new PayRentRefreshEvent());
                ResultActivity.toActivity(mActivity, getString(R.string.pay_result), getString(R.string.pay_success), getString(R.string.pay_success_tips), true);
                finish();
                break;
            case -1:
//                if (!(payStatusDialog != null && payStatusDialog.isShowing())) {
//                    payStatusDialog = new PayStatusDialog(this, false, bond).setFailReason(reason).setPayReTry(this);
//                    payStatusDialog.show();
//                }
                showToast(getString(R.string.pay_fail));

//                UIUtils.displayToast("支付失败");
                break;
            case -2:
                showToast(getString(R.string.pay_cancel));
//                Toast.makeText()
//                UIUtils.displayToast("支付取消!!!");
                break;
        }
    }

    @Override
    public void reTry() {
        rentOrder();
    }
}
