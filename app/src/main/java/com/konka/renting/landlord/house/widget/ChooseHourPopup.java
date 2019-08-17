package com.konka.renting.landlord.house.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.HouseConfigBean;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseHourPopup extends PopupWindow {

    private Context mContext;
    private View mView;
    private RecyclerView recyclerView;

    List<Integer> hours;
    CommonAdapter<Integer> commonAdapter;
    OnCall onCall;

    public ChooseHourPopup(Context context) {
        super(context);
        this.mContext = context;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_choose_hour, null);    //绑定布局
        recyclerView = mView.findViewById(R.id.pop_choose_hour_rv_hour);
        hours = new ArrayList<>();
        commonAdapter = new CommonAdapter<Integer>(mContext, hours, R.layout.adapter_choose_hour) {
            @Override
            public void convert(ViewHolder viewHolder, Integer s) {
                viewHolder.setText(R.id.adapter_choose_hour_tv_item, s+"");
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCall != null)
                            onCall.onClick(s);
                        dismiss();
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(commonAdapter);

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.dp_85));  //弹出窗宽度
        this.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dp_160));  //弹出窗高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setHours(List<Integer> hours) {
        if (hours == null)
            return;
        this.hours.clear();
        this.hours.addAll(hours);
        commonAdapter.notifyDataSetChanged();
    }

    public void setOnCall(OnCall onCall) {
        this.onCall = onCall;
    }

    public interface OnCall {
        void onClick(Integer item);
    }
}
