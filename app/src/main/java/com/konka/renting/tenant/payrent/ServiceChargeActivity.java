package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.AlipayBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.ServicePackageBean;
import com.konka.renting.bean.WxPayInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.user.collection.NoScrollGridView;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class ServiceChargeActivity extends BaseActivity implements ServiceGridAdapter.Listener,IPayResCall {

    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.m_flow_card_package)
    NoScrollGridView mGrid;
    @BindView(R.id.re_alipay)
    RelativeLayout mReAlipay;
    @BindView(R.id.re_wxpay)
    RelativeLayout mReWxpay;
    ServiceGridAdapter mAdapter;
    List<ServicePackageBean.ListsBean> mData = new ArrayList<>();
    @BindView(R.id.icon_confirm_alipay)
    ImageView mIconConfirmAlipay;
    @BindView(R.id.icon_confirm_wxpay)
    ImageView mIconConfirmWxpay;
    private String month;
    private String payType = "alipay";
    private String orderNum;

    public static void toActivity(Context context,  String orderNum,String serviceTime,String service_state) {
        Intent intent = new Intent(context, ServiceChargeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("service",serviceTime);
        bundle.putString("ordernum",orderNum);
        bundle.putString("serviceState",service_state);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_service_charge;
    }

    @Override
    public void init() {

        setTitleText("服务费充值");
        Bundle bundle = getIntent().getExtras();
        orderNum = bundle.getString("ordernum");
        mTvTime.setText(bundle.getString("service"));
        String serviceState = bundle.getString("serviceState");
        if (serviceState.equals("1"))
            mTvStatus.setText("正常");
        if (serviceState.equals("2"))
            mTvStatus.setText("异常");
        if (serviceState.equals("3"))
            mTvStatus.setText("离线");
        initgrid();
        initPackage();
    }

    private void initPackage() {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().getServicePackage()
                .compose(RxUtil.<DataInfo<ServicePackageBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ServicePackageBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<ServicePackageBean> servicePackageBeanDataInfo) {

                        dismiss();
                        if (servicePackageBeanDataInfo.success()) {
                            mData.addAll(servicePackageBeanDataInfo.data().getLists());
                            mAdapter.setData(servicePackageBeanDataInfo.data().getLists());
                        } else {
                            showToast(servicePackageBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void initgrid() {
        mAdapter = new ServiceGridAdapter(this);
        mAdapter.setListener(this);
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.changeState(i);
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.re_alipay, R.id.re_wxpay, R.id.confirm_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.re_alipay:
                mIconConfirmAlipay.setVisibility(View.VISIBLE);
                mIconConfirmWxpay.setVisibility(View.GONE);
                payType = "alipay";
                break;
            case R.id.re_wxpay:
                mIconConfirmAlipay.setVisibility(View.GONE);
                mIconConfirmWxpay.setVisibility(View.VISIBLE);
                payType = "wxpay";
                break;
            case R.id.confirm_pay:
                KonkaApplication.getInstance().curPay = this;
                if (payType.equals("alipay")){
                    serviceAliPay();
                }else
                    serviceWechatPay();
                break;
        }
    }

    @Override
    public void setConfirmText(int posistion) {
        month = mData.get(posistion).getNumber();
    }
    private void serviceAliPay(){
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().serviceOrderAliPay(orderNum,month)
                .compose(RxUtil.<DataInfo<AlipayBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AlipayBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        showError(e.getMessage());
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<AlipayBean> dataInfo) {

                        dismiss();
                        if (dataInfo.success()){
                            AliPayUtil.ALiPayBuilder builder = new AliPayUtil.ALiPayBuilder();
                            builder.build().toALiPay(ServiceChargeActivity.this, dataInfo.data().info);

                        }else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
    private void serviceWechatPay(){
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().serviceOrderWechatPay(orderNum,month)
                .compose(RxUtil.<DataInfo<WxPayInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<WxPayInfo>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<WxPayInfo> weChatDataInfo) {

                        dismiss();
                        if (weChatDataInfo.success()){
                            WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
                            String appid = weChatDataInfo.data().info.appid;//应用ID
                            String partnerid = weChatDataInfo.data().info.partnerid;//商户号
                            String prepayid = weChatDataInfo.data().info.prepayid;//预支付交易会话ID
                            String _package = weChatDataInfo.data().info.packageX;//扩展字段w
                            String noncestr = weChatDataInfo.data().info.noncestr;//随机字符串
                            String timestamp = String.valueOf(weChatDataInfo.data().info.timestamp);//时间戳
                            String sign = weChatDataInfo.data().info.sign;//签名
//        sign：签名
                            builder.setAppId(appid)
                                    .setPartnerId(partnerid)
                                    .setPrepayId(prepayid)
                                    .setPackageValue(_package)
                                    .setNonceStr(noncestr)
                                    .setTimeStamp(timestamp)
                                    .setSign(sign)
                                    .build().toWXPayNotSign(ServiceChargeActivity.this, appid);
                        }else
                            showToast(weChatDataInfo.msg());
                    }
                });
        addSubscrebe(subscription);
    }
    @Override
    public void payResCall(int type, String reason) {
        switch (type) {
            case 0:
                RxBus.getDefault().post(new PayRentRefreshEvent());
                UIUtils.displayToast("支付成功");
                finish();
                break;
            case -1:
               showToast("支付失败");
                break;
            case -2:
//                Toast.makeText()
//                UIUtils.displayToast("支付取消!!!");
                showToast("支付取消");
                break;
        }
    }
}
