package com.konka.renting.landlord.user.collection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.ReminderListBean;

import java.util.List;

public class CollectionRecycleAdapter extends RecyclerView.Adapter<CollectionRecycleAdapter.VH> {
    Context mContext;
    List<ReminderListBean> list;
    OnItemClickListener itemClickListener;

    public CollectionRecycleAdapter(Context context, List<ReminderListBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.adapter_collection, null);
        VH vh = new VH(view);
        return vh;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setList(List<ReminderListBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final VH vh, final int i) {
        ReminderListBean bean = list.get(i);
        vh.address.setText(bean.getRoom_name());
        vh.date.setText(bean.getDay()==0?"1号":bean.getDay()+ "号");
        if (bean.getStatus() == 0) {
            vh.date.setEnabled(true);
            vh.mSwitch.setSelected(true);
            vh.mSwitch.setText(R.string.collection_open);
        } else {
            vh.date.setEnabled(false);
            vh.mSwitch.setSelected(false);
            vh.mSwitch.setText(R.string.collection_close);
        }

        vh.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onClick(i);
            }
        });
        vh.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.date.setEnabled(false);
                boolean isChecked=!vh.mSwitch.isSelected();
                vh.mSwitch.setSelected(isChecked);
                vh.mSwitch.setText(isChecked ? R.string.collection_close : R.string.collection_open);
                if (itemClickListener != null)
                    itemClickListener.onChoose(i, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView address;
        TextView date;
        TextView mSwitch;

        public VH(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.adapter_collection_tv_address);
            date = itemView.findViewById(R.id.adapter_collection_tv_date);
            mSwitch = itemView.findViewById(R.id.adapter_collection_switch);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onChoose(int position, boolean isCheck);
    }
}
