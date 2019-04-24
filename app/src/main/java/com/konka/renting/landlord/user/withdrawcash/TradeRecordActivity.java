package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.landlord.user.bill.BillVPAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TradeRecordActivity extends BaseActivity {
    @BindView(R.id.tab_bill)
    TabLayout mTabBill;
    @BindView(R.id.vp_bill)
    ViewPager mVpBill;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, TradeRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill;
    }

    @Override
    public void init() {
        setTitleText("交易记录");
        initTab();
    }

    private void initTab() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> listTab = new ArrayList<>();
        listTab.add("充值记录");
        listTab.add("提现记录");
        for (int i = 0; i < listTab.size(); i++) {
            mTabBill.addTab(mTabBill.newTab().setText(listTab.get(0)));
        }
        fragmentList.add(RechargeRecordFragment.newInstance());  // 未付账单
        fragmentList.add(WithdrawRecordFragment.newInstance());   // 已付账单
        FragmentManager manager = getSupportFragmentManager();
        BillVPAdapter adapter = new BillVPAdapter(manager, fragmentList, listTab);
        mVpBill.setAdapter(adapter);
        mTabBill.setupWithViewPager(mVpBill);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
