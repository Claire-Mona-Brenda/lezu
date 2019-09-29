package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GatewayDetailBean;
import com.konka.renting.event.DelGatewayEvent;
import com.konka.renting.event.EditGatwayEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by kaite on 2018/4/3.
 */

public class GatewayDetailActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_searil)
    TextView mTvSearil;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.btn_check)
    Button mBtnCheck;
    @BindView(R.id.btn_sure)
    Button mBtnSure;

    private String gatewayId;
    private String gatewayName = "";
    private String gatewaySearil = "";
    private String address = "";

    public static void toActivity(Context context, String gatewayId) {
        Intent intent = new Intent(context, GatewayDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", gatewayId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gateway_detail;
    }

    @Override
    public void init() {
        setTitleText(R.string.gateway_info);
        setRightText(R.string.common_edit);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        gatewayId = bundle.getString("id");

        getData();


        addRxBusSubscribe(EditGatwayEvent.class, new Action1<EditGatwayEvent>() {
            @Override
            public void call(EditGatwayEvent editGatwayEvent) {
                RxBus.getDefault().post(new DelGatewayEvent());
                finish();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.btn_sure, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                EditGatewayActivity.toActivity(this, gatewayName,  gatewayId);
                break;
            case R.id.btn_sure:
                delGatway();
                break;
            case R.id.btn_check:
                CheckGatewayStatusActivity.toActivity(this, gatewayId, gatewayName);
                break;
        }
    }

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().detailGateway(gatewayId)
                .compose(RxUtil.<DataInfo<GatewayDetailBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<GatewayDetailBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<GatewayDetailBean> dataInfo) {
                        if (dataInfo.success()) {
                            gatewayName = dataInfo.data().getGateway_name();
                            gatewaySearil = dataInfo.data().getGateway_no();
                            address = dataInfo.data().getAddress();
                            mTvAddress.setText(address);
                            mTvName.setText(gatewayName);
                            mTvSearil.setText(gatewaySearil);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void delGatway() {

        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().unbindGateway(gatewayId)
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
                            RxBus.getDefault().post(new DelGatewayEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


}
