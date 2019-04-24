package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.utils.RxBus;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

import java.util.List;

public class OpenDoorListPopupwindow extends PopupWindow implements View.OnClickListener {
    ImageView imgClose;
    ListView listView;
    Button btnConfirm;

    private View mView;
    Context mContext;
    List<OpenDoorListbean> doorListbeans;
    private CommonAdapter<OpenDoorListbean> mAdapter;
    private int current;


    public OpenDoorListPopupwindow(Context context) {
        super(context);
        mContext = context;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    public List<OpenDoorListbean> getDoorListbeans() {
        return doorListbeans;
    }

    public void setDoorListbeans(List<OpenDoorListbean> doorListbeans, int current) {
        this.doorListbeans = doorListbeans;
        this.current = current;
        if (mAdapter == null)
            initData();
        else
            mAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_opendoorlist, null);    //绑定布局
        listView = mView.findViewById(R.id.pop_opendoorlist_lv_list);
        btnConfirm = mView.findViewById(R.id.pop_opendoorlist_btn_confirm);
        btnConfirm.setOnClickListener(this);

    }

    private void initData() {
        mAdapter = new CommonAdapter<OpenDoorListbean>(mContext, doorListbeans, R.layout.pop_openlist_item) {
            @Override
            public void convert(ViewHolder viewHolder, OpenDoorListbean openDoorListbean, final int i) {
                viewHolder.setText(R.id.pop_list_tv_item, openDoorListbean.getRoom_name());
                viewHolder.setSelected(R.id.pop_list_tv_item, current == i ? true : false);
                viewHolder.setOnClickListener(R.id.pop_list_tv_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        current = i;
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        listView.setAdapter(mAdapter);

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_opendoorlist_btn_confirm:
                RxBus.getDefault().post(new OpenDeviceEvent(current));
                dismiss();
                break;
        }

    }
}
