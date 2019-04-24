package com.konka.renting.landlord.house.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.konka.renting.R;

public class PublicShortRentPopupwindow extends PopupWindow {
    private View mView;
    private PublicShortRentPopupwindow.ConfirmCallback confirmCallback;
    private PublicShortRentPopupwindow.CancelCallback cancelCallback;

    private boolean isYear=true;

    public PublicShortRentPopupwindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);
        mView=infalter.inflate(R.layout.popup_public_short_rent,null);    //绑定布局
        RadioGroup radioGroup=mView.findViewById(R.id.pop_rg_yearmoon);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.pop_rg_yearmoon_y:
                        isYear=true;
                        break;
                    case R.id.pop_rg_yearmoon_m:
                        isYear=false;
                        break;
                }
            }
        });
        Button okButton=mView.findViewById(R.id.pop_btn_confirm);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmCallback!=null)
                    confirmCallback.onClick();
            }
        });
        Button cancel=mView.findViewById(R.id.pop_btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelCallback!=null)
                    cancelCallback.onClick();
            }
        });

    }

    public void setText(String cont){
        TextView tv=mView.findViewById(R.id.pop_tv_comtext);
        tv.setText(cont);
    }

    public boolean isYear() {
        return isYear;
    }

    public void setConfirmCallback(PublicShortRentPopupwindow.ConfirmCallback confirmCallback) {
        this.confirmCallback = confirmCallback;
    }

    public void setCancelCallback(PublicShortRentPopupwindow.CancelCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public interface ConfirmCallback{
        void onClick();
    }
    public interface CancelCallback{
        void onClick();
    }
}
