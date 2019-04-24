package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;

import com.konka.renting.R;


public class BindGatewayFaildedActivity extends BindResultActivity {
    String result;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, BindGatewayFaildedActivity.class);
        intent.putExtra("result", "");
        context.startActivity(intent);
    }

    public static void toActivity(Context context, String result) {
        Intent intent = new Intent(context, BindGatewayFaildedActivity.class);
        intent.putExtra("result", result);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        result = getIntent().getStringExtra("result");
        if (!result.equals(""))
            setContentText(result);
    }

    @Override
    public int getTitleTextId() {
        return R.string.gateway_connection_title;
    }

    @Override
    public int getIconId() {
        return R.mipmap.icon_failed;
    }

    @Override
    public int getContentTextId() {
        return R.string.device_bind_failed;
    }
}
