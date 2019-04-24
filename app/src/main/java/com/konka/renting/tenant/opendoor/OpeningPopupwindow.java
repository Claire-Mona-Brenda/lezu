package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;


/**
 * Created by kaite on 2018/3/15.
 */

public class OpeningPopupwindow extends PopupWindow {
    private View mView;
    public OpeningPopupwindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);
        mView=infalter.inflate(R.layout.layout_popup_opening,null);    //绑定布局

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }
}
