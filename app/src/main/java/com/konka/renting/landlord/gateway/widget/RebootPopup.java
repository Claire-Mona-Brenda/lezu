package com.konka.renting.landlord.gateway.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.event.RebootEvent;
import com.konka.renting.utils.RxBus;


/**
 * Created by Administrator on 2018/1/1.
 */

public class RebootPopup extends PopupWindow {
    private View mView;
    String id;
    public RebootPopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter=LayoutInflater.from(context);
        mView=infalter.inflate(R.layout.layout_reboot,null);    //绑定布局
        TextView tvCancel = mView.findViewById(R.id.tv_no_connect);
        TextView tvDial = mView.findViewById(R.id.dial);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getDefault().post(new RebootEvent(id));
                dismiss();
            }
        });
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setId(String id) {
        this.id = id;
    }
}
