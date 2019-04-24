package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.konka.renting.R;
import com.konka.renting.bean.WithDrawRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaite on 2018/3/13.
 */

public class WithdrawAdapter extends BaseAdapter {
    Context context;
    List<WithDrawRecordBean> mData = new ArrayList<>();

    public WithdrawAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<WithDrawRecordBean> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        WithdrawViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_withdraw_record,viewGroup,false);
            holder = new WithdrawViewHolder();
            holder.tvMoney = view.findViewById(R.id.tv_money);
            holder.tvBank = view.findViewById(R.id.tv_withdraw_to);
            holder.tvTime = view.findViewById(R.id.tv_withdraw_time);
            view.setTag(holder);
        }else {
            holder = (WithdrawViewHolder) view.getTag();
        }
        holder.tvMoney.setText(mData.get(i).amount);
        holder.tvTime.setText(mData.get(i).time);
        holder.tvBank.setText(mData.get(i).bank_name+"("+mData.get(i).number+")");

        return view;
    }

    class WithdrawViewHolder{
        TextView tvMoney,tvBank,tvTime;
    }
}
