package com.konka.renting.landlord.home.tenanter;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.konka.renting.R;
import com.konka.renting.base.BaseTabListActivity;

import java.util.ArrayList;
import java.util.List;

public class TenantListActivity extends BaseTabListActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, TenantListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tenant_list;
    }

    @Override
    public void init() {
        super.init();
        setTitleText(R.string.tenanter_title);
    }

    @Override
    public String[] getTabStringIds() {
        return getResources().getStringArray(R.array.landlord_tenanters);
    }

    @Override
    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TenanterListRentedFragment.newInstance());
        fragments.add(TenanterListUnRentedFragment.newInstance());
        fragments.add(TenanterListSoonExpireFragment.newInstance());
        return fragments;
    }
}
