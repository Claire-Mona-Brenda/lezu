package com.konka.renting.landlord.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.konka.renting.bean.BannerListbean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jzxiang on 18/03/2018.
 */

public class BannerAdapter extends LoopPagerAdapter {

    private Context mContext;
    private List<BannerListbean> mBannerAdvListInfos;

    public BannerAdapter(RollPagerView viewPager, Context context, List<BannerListbean> bannerAdvListInfos) {
        super(viewPager);
        mContext = context;
        mBannerAdvListInfos = bannerAdvListInfos;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        Picasso.get().load(mBannerAdvListInfos.get(position).getImage()).into(view);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getRealCount() {
        return mBannerAdvListInfos.size();
    }
}
