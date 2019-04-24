package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.GatewayInfo;

import butterknife.BindView;
import butterknife.OnClick;

public class BindGatewayDesActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_serial)
    TextView mTvSerial;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    private GatewayInfo mGatewayInfo;

    public static void toActivity(Context context, GatewayInfo gatewayInfo) {
        Intent intent = new Intent(context, BindGatewayDesActivity.class);
        intent.putExtra(GatewayInfo.class.getSimpleName(), gatewayInfo);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bind_gateway_des;
    }

    @Override
    public void init() {
        setTitleText(R.string.gateway_des_title);
        mGatewayInfo = getIntent().getParcelableExtra(GatewayInfo.class.getSimpleName());
        mTvName.setText(String.format(getString(R.string.gateway_name),mGatewayInfo.getGateway_name()));
//        mTvSerial.setText(String.format(getString(R.string.gateway_serial),mGatewayInfo.getG));
//        mTvAddress.setText(String.format(getString(R.string.gateway_address),mGatewayInfo.address));
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
