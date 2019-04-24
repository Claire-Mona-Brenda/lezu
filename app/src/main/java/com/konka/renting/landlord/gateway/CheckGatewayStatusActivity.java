package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CheckGatewayStatusBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class CheckGatewayStatusActivity extends BaseActivity {
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
    @BindView(R.id.activity_checkgateway_tv_status)
    TextView tvStatus;
    @BindView(R.id.activity_checkgateway_tv_power)
    TextView tvPower;
    @BindView(R.id.activity_checkgateway_tv_bat)
    TextView tvBat;
    @BindView(R.id.activity_checkgateway_tv_gprs)
    TextView tvGprs;
    @BindView(R.id.activity_checkgateway_tv_rf)
    TextView tvRf;

    String gateway_id,gateway_name;

    public static void toActivity(Context context,String gateway_id,String gateway_name) {
        Intent intent = new Intent(context, CheckGatewayStatusActivity.class);
        intent.putExtra("gateway_id", gateway_id);
        intent.putExtra("gateway_name", gateway_name);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_gateway_status;
    }

    @Override
    public void init() {
        gateway_id=getIntent().getStringExtra("gateway_id");
        gateway_name=getIntent().getStringExtra("gateway_name");

        tvTitle.setText(gateway_name);
        check();
    }

    private void check(){
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().queryGatewayStatus(gateway_id)
                .compose(RxUtil.<DataInfo<CheckGatewayStatusBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<CheckGatewayStatusBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<CheckGatewayStatusBean> info) {
                        dismiss();
                        if (info.success()){
                            doSuccess();
                            tvStatus.setText("状态: "+(info.data().getStatus()==0?"空闲":"忙碌"));
                            tvPower.setText("电源来源: "+(info.data().getPower()==0?"未接入外电":"接入外电"));
                            tvBat.setText("电量: "+info.data().getBat());
                            tvGprs.setText("GPRS信号强度: "+info.data().getGprs());
                            tvRf.setText("射频信号强度: "+info.data().getRf_signal());
                        }else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
