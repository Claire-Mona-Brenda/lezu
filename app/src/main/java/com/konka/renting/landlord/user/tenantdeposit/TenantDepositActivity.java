package com.konka.renting.landlord.user.tenantdeposit;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class TenantDepositActivity extends BaseActivity {

    @BindView(R.id.tv_deposit)
    TextView mTvDeposit;

    public static void toActivity(Context context, String despoit) {
        Intent intent = new Intent(context, TenantDepositActivity.class);
        intent.putExtra("despoit", despoit);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tenant_deposit;
    }

    @Override
    public void init() {

        setTitleText(R.string.tenant_deposit);
        String despoi = getIntent().getStringExtra("despoit");
        mTvDeposit.setText("￥"+despoi);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
