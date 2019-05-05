package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayBean;
import com.konka.renting.bean.SeverPayListBean;
import com.konka.renting.bean.WxPayBean;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.view.PayStatusDialog;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.utils.AliPayUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.utils.WXPayUtils;
import com.konka.renting.widget.CommonPopupWindow;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class PaySeverActivity extends BaseActivity implements IPayResCall, PayStatusDialog.PayReTry {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_endtime)
    TextView tvEndtime;
    @BindView(R.id.recycle_choose_sever)
    RecyclerView recycleChooseSever;
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

    final int TYPE_LANDLORD = 1;
    final int TYPE_TENANT = 2;

    String bond;
    String room_id;
    String address;
    String service_date;
    int isLandlord;
    int service_charge_id = 0;
    int mode = 2;
    List<SeverPayListBean> mList;

    CommonPopupWindow commonPopupWindow;

    public static void toActivity(Context context, String room_id, String address, String service_date, int isLandlord) {
        Intent intent = new Intent(context, PaySeverActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("address", address);
        intent.putExtra("service_date", service_date);
        intent.putExtra("isLandlord",isLandlord);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_sever;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.pay_sever_title);
        room_id = getIntent().getStringExtra("room_id");
        address = getIntent().getStringExtra("address");
        service_date = getIntent().getStringExtra("service_date");
        isLandlord=getIntent().getIntExtra("isLandlord",TYPE_LANDLORD);

        switch (isLandlord){
            case TYPE_LANDLORD:
                gCard.setVisibility(View.VISIBLE);
                break;
            case TYPE_TENANT:
                gCard.setVisibility(View.GONE);
                break;
        }

        tvAddress.setText(address);
        tvEndtime.setText(service_date);
        initRecycle();

        initPay();
        getData();
        alipay.setChecked(true);
    }

    private void initRecycle() {
        recycleChooseSever.setLayoutManager(new GridLayoutManager(this, 3));
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

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().serviceCharge()
                .compose(RxUtil.<DataInfo<List<SeverPayListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<SeverPayListBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo<List<SeverPayListBean>> dataInfo) {
                        if (dataInfo.success()) {
                            mList = dataInfo.data();
                            recycleChooseSever.setAdapter(new PaySeverAdapter(PaySeverActivity.this));
                        } else {
                            ShowToastUtil.showWarningToast(PaySeverActivity.this, dataInfo.msg());
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
                .build().toWXPayNotSign(PaySeverActivity.this, appid);
    }

    private void pay() {
        Subscription subscription = SecondRetrofitHelper.getInstance().serviceChargePay(mList.get(service_charge_id).getService_charge_id() + "", room_id, mode + "")
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
                                    ShowToastUtil.showSuccessToast(PaySeverActivity.this, dataInfo.msg());
                                    payResCall(0, dataInfo.msg());
                                    break;

                            }
                        } else {
                            ShowToastUtil.showWarningToast(PaySeverActivity.this, dataInfo.msg());
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

    @OnClick({R.id.iv_back, R.id.next, R.id.g_ali, R.id.g_wechat, R.id.g_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.next:
                if(mList==null||mList.size()==0){
                    showToast(R.string.warm_no_choose_sever_type);
                    return;
                }
                KonkaApplication.getInstance().curPay = this;
                if (mode==3){
                    showOtherPayPopup();
                }else {
                    pay();
                }
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

    class PaySeverAdapter extends RecyclerView.Adapter<PaySeverAdapter.VH> {
        Context mContext;

        public PaySeverAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public PaySeverAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = View.inflate(PaySeverActivity.this, R.layout.adapter_pay_sever_list, null);
            PaySeverAdapter.VH vh = new PaySeverAdapter.VH(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PaySeverAdapter.VH vh, final int i) {
            SeverPayListBean bean = mList.get(i);
            vh.ll.setSelected(service_charge_id == i);
            vh.price.setText("¥" + (int)Float.parseFloat(bean.getPrice()));
            vh.date.setText(bean.getName());
            vh.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    service_charge_id = i;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            LinearLayout ll;
            TextView price;
            TextView date;

            public VH(@NonNull View itemView) {
                super(itemView);
                ll = itemView.findViewById(R.id.adapter_pay_sever_ll);
                price = itemView.findViewById(R.id.adapter_pay_sever_tv_price);
                date = itemView.findViewById(R.id.adapter_pay_sever_tv_date);
            }
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
