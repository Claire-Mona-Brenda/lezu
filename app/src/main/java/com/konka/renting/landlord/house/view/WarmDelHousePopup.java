package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class WarmDelHousePopup extends PopupWindow {
    private View mView;
    public TextView tvContent, tvWarm, btnSure;

    public WarmDelHousePopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_del_warm, null);    //绑定布局
        tvContent = mView.findViewById(R.id.tv_content);
        tvWarm = mView.findViewById(R.id.tv_content_warm);
        btnSure = mView.findViewById(R.id.tv_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void setBtnOnclick(View.OnClickListener btnOnclick) {
        if (btnSure != null) {
            btnSure.setOnClickListener(btnOnclick);
        }
    }

    public void setTvContent(String content) {
        if (tvContent != null)
            tvContent.setText(content);
    }
}
