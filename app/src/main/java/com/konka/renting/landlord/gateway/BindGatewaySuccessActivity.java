package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;

import com.konka.renting.R;


public class BindGatewaySuccessActivity extends BindResultActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, BindGatewaySuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getTitleTextId() {
        return R.string.device_gateway_result_title;
    }

    @Override
    public int getIconId() {
        return R.mipmap.icon_success;
    }

    @Override
    public int getContentTextId() {
        return R.string.device_bind_success;
    }
}
