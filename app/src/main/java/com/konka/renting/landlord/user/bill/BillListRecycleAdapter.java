package com.konka.renting.landlord.user.bill;

import android.content.Context;
import android.text.TextUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BillListRecycleAdapter extends RecyclerView.Adapter<BillListRecycleAdapter.VH> {
    Context mContext;
    List<BillListBean> listBeans;
    Set<String> dateSet;
    HashMap<String ,String> dateMap;
    OnItemClickListen mOnItemClickListen;

    public BillListRecycleAdapter(Context mContext, List<BillListBean> listBeans, Set<String> dateSet, HashMap<String ,String> dateMap) {
        this.mContext = mContext;
        this.listBeans = listBeans;
        this.dateSet = dateSet;
        this.dateMap = dateMap;
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

        if (!TextUtils.isEmpty(bean.getCreate_time())){
            String[] time = bean.getCreate_time().split(" ")[0].split("-");
            String year = time[0];
            String mon = time[1];
            String day = time[2];
            String key=year + "-" + mon;
            if (!isHave(key)){
                dateSet.add(key);
                dateMap.put(key,bean.getId());
                vh.rlMon.setVisibility(View.VISIBLE);
                vh.time.setText(getDateString(year, mon));
            }else if (isExist(key,bean.getId())){
                vh.rlMon.setVisibility(View.VISIBLE);
                vh.time.setText(getDateString(year, mon));
            }else{
                vh.rlMon.setVisibility(View.GONE);
            }
        }


        vh.type.setText(bean.getTitle());
        String str = payType(bean.getType());
        if (str.equals("-")) {
            vh.money.setTextColor(mContext.getResources().getColor(R.color.text_ren));
        } else {
            vh.money.setTextColor( mContext.getResources().getColor(R.color.text_green));
        }
        vh.money.setText(str + bean.getAmount());
        vh.date.setText(bean.getCreate_time());
        vh.rlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListen != null) {
                    mOnItemClickListen.onClick(i);
                }
            }
        });
        vh.rlMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListen != null) {
                    mOnItemClickListen.onMonClick(i);
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
        RelativeLayout rlContent;

        public VH(@NonNull View itemView) {
            super(itemView);
            rlMon = itemView.findViewById(R.id.adapter_rl_mon);
            rlContent = itemView.findViewById(R.id.adapter_bill_list_rl_content);
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
    private boolean isExist(String key,String id) {
        String d=dateMap.get(key);
        return id.equals(d);
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


    private String payType(int type) {
        String s = "";
        switch (type) {
            case 1://充值
            case 4://服务费退款
            case 6://房租
                s = "+";
                break;
            case 2://服务费支付
            case 3://安装费
            case 5://提现
                s = "-";
                break;
        }
        return s;
    }

    public interface OnItemClickListen {
        void onClick(int position);

        void onMonClick(int position);
    }
}
