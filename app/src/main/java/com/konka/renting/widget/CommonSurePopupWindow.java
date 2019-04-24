package com.konka.renting.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class CommonSurePopupWindow extends PopupWindow {
    private View mView;
    public TextView tvTitle, tvContent, btn;

    public CommonSurePopupWindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_common_sure, null);    //绑定布局
        tvTitle = mView.findViewById(R.id.tv_title);
        tvContent = mView.findViewById(R.id.tv_content);
        btn = mView.findViewById(R.id.tv_btn);
        btn.setOnClickListener(new View.OnClickListener() {
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
        this.setFocusable(false);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画


    }

    public static class Builder {
        Context context;
        String title, content, btnString, colorCotent, colorBtn;
        View.OnClickListener btnClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setContentTextColor(String color) {
            this.colorCotent = color;
            return this;
        }

        public Builder setBtnTextColor(String color) {
            this.colorBtn = color;
            return this;
        }

        public Builder setBtnString(String string) {
            this.btnString = string;
            return this;
        }

        public Builder setBtnClickListener(View.OnClickListener btnClickListener) {
            this.btnClickListener = btnClickListener;
            return this;
        }

        public CommonSurePopupWindow create() {
            CommonSurePopupWindow popupWindow = new CommonSurePopupWindow(context);
            if (title != null) popupWindow.tvTitle.setText(title);
            if (content != null) popupWindow.tvContent.setText(content);
            if (colorCotent != null)
                popupWindow.tvContent.setTextColor(Color.parseColor(colorCotent));
            if (colorBtn != null) popupWindow.btn.setTextColor(Color.parseColor(colorBtn));
            if (btnString != null) popupWindow.btn.setText(btnString);
            if (btnClickListener != null)
                popupWindow.btn.setOnClickListener(btnClickListener);
            return popupWindow;
        }
    }
}
