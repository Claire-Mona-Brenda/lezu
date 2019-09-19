package com.konka.renting.tenant.opendoor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.konka.renting.R;
import com.konka.renting.bean.OpenDoorListbean;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;

public class OpenItemAdapter extends PagerAdapter {

    private Context mContext;
    private List<OpenDoorListbean> mData;

    public OpenItemAdapter(Context mContext, List<OpenDoorListbean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.adapter_open_item, null);
        ImageView mImgStatus = view.findViewById(R.id.adapter_open_item_img_status);
        ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
        ImageView mImgOpenAnimation = view.findViewById(R.id.adapter_open_item_img_open_animation);
        TextView mTvName = view.findViewById(R.id.adapter_open_item_tv_name);
        TextView mTvMoreHistory = view.findViewById(R.id.adapter_open_item_tv_more_history);
        TextView mTvHistoryContent1 = view.findViewById(R.id.adapter_open_item_tv_history_content_1);
        TextView mTvHistoryTime1 = view.findViewById(R.id.adapter_open_item_tv_history_time_1);
        TextView mTvHistoryContent2 = view.findViewById(R.id.adapter_open_item_tv_history_content_2);
        TextView mTvHistoryTime2 = view.findViewById(R.id.adapter_open_item_tv_history_time_2);
        TextView mTvOpenPwd = view.findViewById(R.id.adapter_open_item_tv_open_pwd);

        OpenDoorListbean doorListbean = mData.get(position);

        mImgStatus.setImageResource(doorListbean.getStatus() == 2 ? R.mipmap.opendoor_notcheckin_icon : R.mipmap.opendoor_checkin_icon);
        mTvName.setText(doorListbean.getRoom_name());

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface OnCall {
        void onClickOpen(int position);
        void onClickHistoryMore(int position);
        void onClickOpenPwd(int position);
        void onClickGateway(int position);
        void onClickSycn(int position);
        void onClickManager(int position);
    }
}
