package com.konka.renting.landlord.gateway.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.MachineInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzxiang on 27/12/2017.
 */
public class ChoiceDeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<MachineInfo> mDeviceInfos;

    public ChoiceDeviceAdapter(Context context, List<MachineInfo> deviceInfos) {
        mContext = context;
        mDeviceInfos = deviceInfos;
    }

    @Override
    public int getCount() {
        return mDeviceInfos.size();
    }

    @Override
    public MachineInfo getItem(int position) {
        return mDeviceInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choice_device, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MachineInfo deviceInfo = getItem(position);
        if (!TextUtils.isEmpty(deviceInfo.logo))
            Picasso.get().load(deviceInfo.logo).into(holder.mIvIcon);
        holder.mTvDes.setText(deviceInfo.name);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_des)
        TextView mTvDes;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
