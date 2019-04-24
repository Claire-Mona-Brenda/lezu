package com.konka.renting.landlord.user.bill;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class BillVPAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> listTitle = new ArrayList<>();

    public BillVPAdapter(FragmentManager fm, List<Fragment> fragments, List<String> listTitle) {
        super(fm);
        this.fragmentList=fragments;
        this.listTitle = listTitle;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override   //标题与页面同步
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }
}
