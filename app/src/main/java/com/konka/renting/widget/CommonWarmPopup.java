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

public class CommonWarmPopup extends PopupWindow {
    private View mView;
    public TextView tvTitle, tvContent, tvWarm, btnLeft, btnRight;

    public CommonWarmPopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_common_warm, null);    //绑定布局
        tvTitle = mView.findViewById(R.id.tv_title);
        tvContent = mView.findViewById(R.id.tv_content);
        tvWarm = mView.findViewById(R.id.tv_content_warm);
        btnLeft = mView.findViewById(R.id.tv_left);
        btnRight = mView.findViewById(R.id.tv_right);
        btnLeft.setOnClickListener(new View.OnClickListener() {
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

    public static class Builder {
        Context context;
        String title, content, warm, leftBtnString, rightBtnString, colorCotent, colorWarm;
        View.OnClickListener leftBtnClickListener, rightBtnClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        public CommonWarmPopup.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public CommonWarmPopup.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public CommonWarmPopup.Builder setContentTextColor(String color) {
            this.colorCotent = color;
            return this;
        }

        public CommonWarmPopup.Builder setWarmContent(String content) {
            this.warm = content;
            return this;
        }

        public CommonWarmPopup.Builder setWarmContentTextColor(String color) {
            this.colorWarm = color;
            return this;
        }

        public CommonWarmPopup.Builder setLeftBtnString(String leftString) {
            this.leftBtnString = leftString;
            return this;
        }

        public CommonWarmPopup.Builder setRightBtnString(String rightString) {
            this.rightBtnString = rightString;
            return this;
        }

        public CommonWarmPopup.Builder setLeftBtnClickListener(View.OnClickListener leftBtnClickListener) {
            this.leftBtnClickListener = leftBtnClickListener;
            return this;
        }

        public CommonWarmPopup.Builder setRightBtnClickListener(View.OnClickListener rightBtnClickListener) {
            this.rightBtnClickListener = rightBtnClickListener;
            return this;
        }

        public CommonWarmPopup create() {
            CommonWarmPopup popupWindow = new CommonWarmPopup(context);
            if (title != null) popupWindow.tvTitle.setText(title);
            if (content != null) popupWindow.tvContent.setText(content);
            if (colorCotent != null)
                popupWindow.tvContent.setTextColor(Color.parseColor(colorCotent));
            if (warm != null) popupWindow.tvWarm.setText(warm);
            if (colorWarm != null)
                popupWindow.tvWarm.setTextColor(Color.parseColor(colorWarm));
            if (leftBtnString != null) popupWindow.btnLeft.setText(leftBtnString);
            if (rightBtnString != null) popupWindow.btnRight.setText(rightBtnString);
            if (leftBtnClickListener != null)
                popupWindow.btnLeft.setOnClickListener(leftBtnClickListener);
            if (rightBtnClickListener != null)
                popupWindow.btnRight.setOnClickListener(rightBtnClickListener);
            return popupWindow;
        }
    }
}
