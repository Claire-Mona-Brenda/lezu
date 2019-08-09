package com.konka.renting.landlord.house.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.HouseConfigBean;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class CheckHouseConfigPopup extends PopupWindow {
    private Context mContext;
    private ArrayList<HouseConfigBean> configList;
    private CommonAdapter<HouseConfigBean> configAdapter;
    private View mView;
    private TextView mTvCancel, mTvSure;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView mRecyclerView;
    private OnCallBack mCallBack;

    public CheckHouseConfigPopup(Context context) {
        super(context);
        this.mContext = context;
        this.configList = new ArrayList<>();
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_check_config, null);    //绑定布局
        mTvCancel = mView.findViewById(R.id.pop_check_config_tv_cancel);
        mTvSure = mView.findViewById(R.id.pop_check_config_tv_sure);
        smartRefreshLayout = mView.findViewById(R.id.pop_check_config_srl_config);
        mRecyclerView = mView.findViewById(R.id.pop_check_config_rv_config);

        configAdapter = new CommonAdapter<HouseConfigBean>(mContext, configList, R.layout.adapter_check_house_config) {
            @Override
            public void convert(ViewHolder viewHolder, HouseConfigBean bean) {
                viewHolder.setText(R.id.adapter_check_config_cb_config, bean.getName());
                CheckBox box = viewHolder.getView(R.id.adapter_check_config_cb_config);
                box.setOnCheckedChangeListener(null);
                box.setChecked(bean.getStatus() == 1);
                box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bean.setStatus(isChecked ? 1 : 0);
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        box.setChecked(!box.isChecked());
                    }
                });
            }
        };

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mRecyclerView.setAdapter(configAdapter);

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.sure(configList);
                }
                dismiss();
            }
        });

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (mCallBack != null) {
                    mCallBack.onRefresh();
                } else {
                    smartRefreshLayout.finishRefresh();
                }
            }
        });

        smartRefreshLayout.setEnableLoadmore(false);
    }

    public void setData(List<HouseConfigBean> list) {
        configList.clear();
        configList.addAll(list);
        configAdapter.notifyDataSetChanged();
    }

    public void onRefresh(List<HouseConfigBean> list) {
        if (list != null) {
            configList.clear();
            configList.addAll(list);
            int size = configList.size();
            for (int i = 0; i < size; i++) {
                configList.get(i).setStatus(0);
            }
        }

        configAdapter.notifyDataSetChanged();
        smartRefreshLayout.finishRefresh();
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);  //弹出窗宽度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }


    public void setmCallBack(OnCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface OnCallBack {
        void onRefresh();

        void sure(ArrayList<HouseConfigBean> list);
    }
}
