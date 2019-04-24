package com.konka.renting.landlord.user.bill;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BillActivity extends BaseActivity {
    @BindView(R.id.tab_bill)
    TabLayout mTabBill;
    @BindView(R.id.vp_bill)
    ViewPager mVpBill;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, BillActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill;
    }

    @Override
    public void init() {
        setTitleText(R.string.my_bill);
        initTab();
    }

    private void initTab() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> listTab = new ArrayList<>();
        listTab.add(getString(R.string.non_payment));
        listTab.add(getString(R.string.alredy_paid));
        for (int i = 0; i < listTab.size(); i++) {
            mTabBill.addTab(mTabBill.newTab().setText(listTab.get(0)));
        }
        fragmentList.add(NonPayFragment.newInstance());  // 未付账单
        fragmentList.add(AlreadyPayFragment.newInstance());   // 已付账单
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
