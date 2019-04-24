package com.konka.renting.tenant.payrent.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by ATI on 2018/4/1.
 */

public class PayViewPager extends PagerAdapter {
    private List<View> mViewList;
    List<String>mTitleList;
    public PayViewPager(List<View> mViewList,List<String>mTitleList) {
        this.mViewList = mViewList;
        this.mTitleList=mTitleList;
    }

    @Override
    public int getCount() {
        return mViewList.size();//页卡数
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//官方推荐写法
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));//添加页卡
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));//删除页卡
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //这里这个一定要写，不然卡片标题显示不出来
        return mTitleList.get(position);//页卡标题
    }
}



