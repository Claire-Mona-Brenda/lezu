package com.konka.renting.base;


import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.konka.renting.R;

/**
 * Created by jzxiang on 04/03/2018.
 */

public abstract class BaseMainActivity extends BaseActivity {
    public View[] mBottomViews;
    public Fragment[] mFragments;
    protected int[] mMenuIds;
    protected int mCurrent;

    public abstract int getFragmentCount();

    public abstract int getDefaultIndex();

    public abstract void initFragment(int index);

    public abstract int[] getMenuIds();

    public abstract boolean menuClicked(int index);

    @Override
    public void init() {

        mCurrent = getDefaultIndex();
        mBottomViews = new View[getFragmentCount()];
        mFragments = new Fragment[getFragmentCount()];
        mMenuIds = getMenuIds();
    }


    protected void menuItemClicked(int index) {
        if (index == mCurrent) {
            return;
        }

        mBottomViews[mCurrent].setSelected(false);
        mBottomViews[index].setSelected(true);
        initFragment(index);
        int lastIndex = mCurrent;
        mCurrent = index;

        if (menuClicked(index))
            return;

        switchContent(lastIndex, mCurrent);
    }

    public void switchContent(int fromIndex, int toIndex) {

        Fragment from = mFragments[fromIndex];
        Fragment to = mFragments[toIndex];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!to.isAdded()) {    // 先判断是否被add过
            if (from != null)
                transaction.hide(from).add(R.id.frame_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            else
                transaction.add(R.id.frame_container, to).commit();
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }

    }
}
