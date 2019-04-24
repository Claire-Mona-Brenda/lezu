package com.konka.renting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class WarningPopupwindow extends PopupWindow {
    private View mView;
    private ConfirmCallback confirmCallback;
    private CancelCallback cancelCallback;

    public WarningPopupwindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);
        mView=infalter.inflate(R.layout.popup_warn,null);    //绑定布局
        Button okButton=mView.findViewById(R.id.pop_warn_btn_confirm);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmCallback!=null)
                    confirmCallback.onClick();
            }
        });
        Button cancel=mView.findViewById(R.id.pop_warn_btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelCallback!=null)
                    cancelCallback.onClick();
            }
        });

    }

    public void setContext(String cont){
        TextView tv=mView.findViewById(R.id.pop_warn_tv_comtext);
        tv.setText(cont);
    }

    public void setConfirmCallback(ConfirmCallback confirmCallback) {
        this.confirmCallback = confirmCallback;
    }

    public void setCancelCallback(CancelCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public interface ConfirmCallback{
        public void onClick();
    }
    public interface CancelCallback{
        public void onClick();
    }
}
