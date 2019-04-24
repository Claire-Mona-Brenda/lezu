package com.konka.renting.guide;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by naterobinson on 2017/2/24.
 */

public class GuideFragmentsAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public GuideFragmentsAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mFragments = fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

}
