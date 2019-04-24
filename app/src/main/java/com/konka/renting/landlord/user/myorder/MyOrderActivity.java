package com.konka.renting.landlord.user.myorder;

import android.content.Context;
import android.content.Intent;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;


public class MyOrderActivity extends BaseActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MyOrderActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_order;
    }

    @Override
    public void init() {
        setTitleText(R.string.my_order);
    }
}
