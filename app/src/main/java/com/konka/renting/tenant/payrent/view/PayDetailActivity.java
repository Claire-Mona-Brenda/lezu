package com.konka.renting.tenant.payrent.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.AlipayBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.bean.WxPayInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.view.PayStatusDialog;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class PayDetailActivity extends Activity implements View.OnClickListener, PayStatusDialog.PayReTry, IPayResCall {
    LinearLayout linearLayout;
    TextView xh;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    AppCompatImageView imageView;
    PayOrder missionEnity;
    Button tf;
    private IWXAPI api;

    private ProgressDialog mProgressDialog;

    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    public void dismiss() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }
    public static void toActivity(Context context, PayOrder payOrder){
        Intent intent = new Intent(context, PayDetailActivity.class);
        intent.putExtra("pay", payOrder);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_paydetail_activity);
        missionEnity = (PayOrder) getIntent().getParcelableExtra("pay");

        xh = (TextView) findViewById(R.id.xh);
        status = (TextView) findViewById(R.id.status);
        adress = (TextView) findViewById(R.id.adress);
        price = (TextView) findViewById(R.id.price);
        status = (TextView) findViewById(R.id.status);
        dis_time = (TextView) findViewById(R.id.dis_time);
        imageView = (AppCompatImageView) findViewById(R.id.img_house);

        ali = findViewById(R.id.alipay);
        wechat = findViewById(R.id.wechat);
        other = findViewById(R.id.other);
        tf = findViewById(R.id.next);
        tf.setOnClickListener(this);
        bindData();
        getData();
        initPay();
        ali.setChecked(true);
    }


    RadioButton ali, wechat, other;
    byte mode = 0;

    public void initPay() {
        ali.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mode = 0;
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
                    ali.setChecked(false);
                    other.setChecked(false);
                }
            }
        });
        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mode = 2;
                    wechat.setChecked(false);
                    ali.setChecked(false);
                }
            }
        });
    }

    private CompositeSubscription mCompositeSubscription;

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public void bindData() {
        Picasso.get().load(missionEnity.roomInfo.image).into(imageView);
        xh.setText("订单编号：" + missionEnity.merge_order_no);
        adress.setText(missionEnity.roomInfo.room_name);
        price.setText("￥" + missionEnity.housing_price);
        status.setText(missionEnity.status_name);
        dis_time.setText(missionEnity.start_time + "-" + missionEnity.end_time);

    }

    public void bindData2Order(RoomInfo.RoomDescription room) {

    }

    public void getData() {
        showLoadingDialog();
        rx.Observable<DataInfo<RoomInfo>> observable = null;
        observable = RetrofitHelper.getInstance().getRoomInfo(missionEnity.room_id);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<RoomInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomInfo>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                        ShowToastUtil.showWarningToast(PayDetailActivity.this, "请求失败");
                    }

                    @Override
                    public void onNext(DataInfo<RoomInfo> homeInfoDataInfo) {
                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            homeInfoDataInfo.data();
                            RoomInfo roomInfo = homeInfoDataInfo.data();
                            bindData2Order(roomInfo.info);
                        } else {
//                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    public void payali() {
        rx.Observable<DataInfo<AlipayBean>> observable = null;
        observable = RetrofitHelper.getInstance().orderAlipayPay(missionEnity.order_no);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<AlipayBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AlipayBean>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                        ShowToastUtil.showWarningToast(PayDetailActivity.this, "请求失败");
                    }

                    @Override
                    public void onNext(DataInfo<AlipayBean> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
//                                ali(homeInfoDataInfo.msg());
                            ali(homeInfoDataInfo.data().info);

                        } else {
                            ShowToastUtil.showWarningToast(PayDetailActivity.this, homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    private static final String TAG = "PayDetailActivity";

    public void paywechat() {
        ShowToastUtil.showLoadingDialog(this);
        rx.Observable<DataInfo<WxPayInfo>> observable = null;
//        observable = RetrofitHelper.getInstance().boonOrderWechatPay(missionEnity.order_no);
        Log.e(TAG, "paywechat: " + missionEnity.order_no);
        observable = RetrofitHelper.getInstance().orderWechatPay(missionEnity.order_no);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<WxPayInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<WxPayInfo>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                        ShowToastUtil.dismiss();
                        ShowToastUtil.showWarningToast(PayDetailActivity.this, "请求网络失败");
                    }

                    @Override
                    public void onNext(DataInfo<WxPayInfo> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {

                            wechat(homeInfoDataInfo.data());

                        } else {
                            ShowToastUtil.showWarningToast(PayDetailActivity.this, homeInfoDataInfo.msg());
                        }
                        ShowToastUtil.dismiss();
                    }
                }));
        addSubscrebe(subscription);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            this.finish();
        } else if (view.getId() == R.id.next) {
            KonkaApplication.getInstance().curPay = this;
            if (mode == 0) {
//                                ali(homeInfoDataInfo.msg());
                payali();
            } else {
                paywechat();
            }

//            AliPayUtil
//            Intent intent=new Intent(this,PaySuccessActivity.class);
//            this.startActivity(intent);
//            this.finish();
        }else  if (view.getId() == R.id.g_ali){
            mode = 0;
            ali.setChecked(true);
            wechat.setChecked(false);
            other.setChecked(false);
        }else  if (view.getId() == R.id.g_wechat){
            mode = 1;
            wechat.setChecked(true);
            ali.setChecked(false);
            other.setChecked(false);
        }else  if (view.getId() == R.id.g_card){
            mode = 2;
            other.setChecked(true);
            wechat.setChecked(false);
            ali.setChecked(false);
        }
    }

    public void ali(String info) {
        Log.e("info121",info);
        AliPayUtil.ALiPayBuilder builder = new AliPayUtil.ALiPayBuilder();
        builder.build().toALiPay(PayDetailActivity.this, info);
    }

    public void wechat(WxPayInfo wepayInfo) {
        WxPayInfo data = wepayInfo;
//        appid：应用ID
//        partnerid：商户号
//        prepayid：预支付交易会话ID
//        package：扩展字段
//        noncestr：随机字符串
//        timestamp：时间戳
//        sign：签名
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        String appid = data.info.appid;//应用ID
        String partnerid = data.info.partnerid;//商户号
        String prepayid = data.info.prepayid;//预支付交易会话ID
        String _package = data.info.packageX;//扩展字段w
        String noncestr = data.info.noncestr;//随机字符串
        String timestamp = String.valueOf(data.info.timestamp);//时间戳
        String sign = data.info.sign;//签名
//        sign：签名
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
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void reTry() {
        if (mode == 0) {
            payali();
        } else {
            paywechat();
        }
    }

    PayStatusDialog payStatusDialog;

    @Override
    public void payResCall(int type, String reason) {
        switch (type) {
            case 0:
                RxBus.getDefault().post(new PayRentRefreshEvent());
                // TODO: 2018/4/30 成功
                UIUtils.displayToast("支付成功");
                new PayStatusDialog(this, true, "100.00").show();
                break;
            case -1:

                if (!(payStatusDialog != null && payStatusDialog.isShowing())) {
                    payStatusDialog = new PayStatusDialog(this, false, "100.00").setFailReason(reason).setPayReTry(this);
                    payStatusDialog.show();
                }//                UIUtils.displayToast("支付失败");
                break;
            case -2:
//                Toast.makeText()
//                UIUtils.displayToast("支付取消!!!");
                break;
        }
    }
}
