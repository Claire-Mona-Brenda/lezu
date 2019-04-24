package com.konka.renting.base;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class BaseTabViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private String[] mTabIds;
    private FragmentManager mFragmentManager;

    public BaseTabViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] tabIds) {
        super(fm);
        this.mFragments = fragments;
        this.mTabIds = tabIds;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override   //标题与页面同步
    public CharSequence getPageTitle(int position) {
        return mTabIds[position];
    }
}
