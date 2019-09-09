package com.konka.renting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class WarmPopupwindow extends PopupWindow {
    private View mView;
    private TextView tvTitle, tvContext, tvKnow;
    private ImageView imgTitle;

    public WarmPopupwindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_warm, null);    //绑定布局
        tvTitle = mView.findViewById(R.id.pop_warm_tv_please);
        tvContext = mView.findViewById(R.id.pop_warm_tv_context);
        imgTitle = mView.findViewById(R.id.pop_warm_img_title);
        tvKnow = mView.findViewById(R.id.pop_warm_tv_know);
        tvKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dismiss();
                }
        });

    }

    public void setContext(String cont) {
        TextView tv = mView.findViewById(R.id.pop_warn_tv_comtext);
        tv.setText(cont);
    }



    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }
}
