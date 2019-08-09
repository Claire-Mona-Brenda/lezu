package com.konka.renting.landlord.user.bill;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.BillListBean;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class BillListRecycleAdapter extends RecyclerView.Adapter<BillListRecycleAdapter.VH> {
    Context mContext;
    List<BillListBean> listBeans;
    Set<String> dateSet;
    OnItemClickListen mOnItemClickListen;

    public BillListRecycleAdapter(Context mContext, List<BillListBean> listBeans, Set<String> dateSet) {
        this.mContext = mContext;
        this.listBeans = listBeans;
        this.dateSet = dateSet;
    }

    public void setmOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.adapter_bill_list, null);
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, final int i) {
        final BillListBean bean = listBeans.get(i);

        String[] time = bean.getCreate_time().split(" ")[0].split("-");
        String year = time[0];
        String mon = time[1];
        String day = time[2];
        if (isHave(year + "-" + mon)) {
            vh.rlMon.setVisibility(View.GONE);
        } else {
            dateSet.add(year + "-" + mon);
            vh.rlMon.setVisibility(View.VISIBLE);
            vh.time.setText(getDateString(year, mon));
        }

        vh.type.setText(bean.getTitle());
        String str = "";
        if (bean.getType() == 1)
            str = "";
        else if (bean.getType() == 4)
            str = "+";
        else
            str = "-";
        vh.money.setText(str + bean.getAmount());
        vh.date.setText(bean.getCreate_time());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListen != null) {
                    mOnItemClickListen.onClick(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listBeans == null ? 0 : listBeans.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView time;
        TextView type;
        TextView money;
        TextView date;
        RelativeLayout rlMon;

        public VH(@NonNull View itemView) {
            super(itemView);
            rlMon = itemView.findViewById(R.id.adapter_rl_mon);
            time = itemView.findViewById(R.id.adapter_tv_time);
            type = itemView.findViewById(R.id.adapter_tv_type);
            money = itemView.findViewById(R.id.adapter_tv_money);
            date = itemView.findViewById(R.id.adapter_tv_date);
        }
    }

    private boolean isHave(String date) {
        for (String str : dateSet) {
            if (date.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private String getDateString(String dateYear, String dateMon) {
        String d = "";
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        if (!dateYear.equals(year + "")) {
            d = dateYear + "-" + dateMon;
        } else if (!dateMon.equals(month + "") && !dateMon.equals("0" + month)) {
            d = dateMon + "月";
        } else {
            d = "本月";
        }
        return d;
    }

    public interface OnItemClickListen {
        void onClick(int position);
    }
}
