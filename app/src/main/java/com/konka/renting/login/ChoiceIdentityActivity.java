package com.konka.renting.login;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.OnClick;

public class ChoiceIdentityActivity extends BaseActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, ChoiceIdentityActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choice_identity;
    }

    @Override
    public void init() {

    }

    @OnClick({R.id.btn_landlord, R.id.btn_tenant})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_landlord:
                LoginActivity.toLandlordActivity(this);
                finish();
                break;
            case R.id.btn_tenant:
                LoginActivity.toTenantActivity(this);
                finish();
                break;
        }
    }
}
