package com.konka.renting.tenant.main.Map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.GroupRoomListBean;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EstateListPopup extends PopupWindow {
    Context mContext;
    private View mView;
    TextView tvTitle, tvAddress;
    SmartRefreshLayout srlEstates;
    RecyclerView rvEstates;

    List<GroupRoomListBean> roomListBeans;
    CommonAdapter<GroupRoomListBean> commonAdapter;

    public EstateListPopup(Context context) {
        super(context);
        mContext = context;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_estates_list_map, null);    //绑定布局
        tvTitle = mView.findViewById(R.id.pop_estates_list_map_tv_title);
        tvAddress = mView.findViewById(R.id.pop_estates_list_map_tv_address);
        srlEstates = mView.findViewById(R.id.pop_estates_list_map_srl_estate);
        rvEstates = mView.findViewById(R.id.pop_estates_list_map_rv_estate);

        roomListBeans = new ArrayList<>();
        initAdapter();

    }

    private void initAdapter() {
        commonAdapter = new CommonAdapter<GroupRoomListBean>(mContext, roomListBeans, R.layout.adpter_map_search_room) {
            @Override
            public void convert(ViewHolder viewHolder, GroupRoomListBean bean) {
                String room_type;
                if (bean.getRoom_type().contains("_")) {
                    String[] t = bean.getRoom_type().split("_");
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = bean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + "|");
                spannableStringBuilder.append(bean.getMeasure_area() + mContext.getString(R.string.unit_m2));
                spannableStringBuilder.append("|" + bean.getFloor() + "/" + bean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_map_search_room_tv_info, spannableStringBuilder);

                viewHolder.setText(R.id.adapter_map_search_room_tv_name, bean.getRoom_name());
                viewHolder.setText(R.id.adapter_map_search_room_tv_price, Float.valueOf(bean.getHousing_price()).intValue() + "");
                viewHolder.setText(R.id.adapter_map_search_room_tv_price_unit, mContext.getString(bean.getType() == 1 ? R.string.public_house_pay_unit_day : R.string.public_house_pay_unit_mon));

                ImageView picView = viewHolder.getView(R.id.adapter_map_search_room_iv_icon);
                if (!TextUtils.isEmpty(bean.getThumb_image())) {
                    Picasso.get().load(bean.getThumb_image()).error(R.mipmap.fangchan_jiazai).into(picView);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RoomInfoActivity.toActivity(mContext, bean.getRoom_id() + "");
                    }
                });
            }
        };

        rvEstates.setLayoutManager(new LinearLayoutManager(mContext));
        rvEstates.setAdapter(commonAdapter);

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);  //弹出窗宽度
        this.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dp_420)); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDate(List<GroupRoomListBean> list) {
        if (rvEstates == null)
            return;
        roomListBeans.clear();
        roomListBeans.addAll(list);
        commonAdapter.notifyDataSetChanged();
        if (roomListBeans.size() > 0) {
            String address = TextUtils.isEmpty(roomListBeans.get(0).getBusiness()) ? "" : "·" + roomListBeans.get(0).getBusiness();
            tvAddress.setText(roomListBeans.get(0).getArea() + address);
        } else {
            tvAddress.setText("");
        }
    }

    public void setEnableRefresh(boolean is) {
        if (srlEstates != null)
            srlEstates.setEnableRefresh(is);
    }

    public void setEnableLoadmore(boolean is) {
        if (srlEstates != null)
            srlEstates.setEnableLoadmore(is);
    }

    public void finishRefresh() {
        if (srlEstates != null)
            srlEstates.finishRefresh();
    }

    public void finishLoadmore() {
        if (srlEstates != null)
            srlEstates.finishLoadmore();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        if (srlEstates != null)
            srlEstates.setOnRefreshListener(listener);
    }

    public void setOnLoadmoreListener(OnLoadmoreListener listener) {
        if (srlEstates != null)
            srlEstates.setOnLoadmoreListener(listener);
    }
}
