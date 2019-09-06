package com.konka.renting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class CommonInputPopupWindow extends PopupWindow {
    private View mView;
    TextView tvTitle, btnLeft, btnRight;
    EditText edtContent;

    public CommonInputPopupWindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_input, null);    //绑定布局
        tvTitle = mView.findViewById(R.id.tv_title);
        edtContent = mView.findViewById(R.id.edt_content);
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

    public void setTvTitle(String title) {
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    public EditText getEdtContent() {
        return edtContent;
    }

    public void setBtnLeftOnClickListener(View.OnClickListener onClickListener) {
        if (btnLeft != null) {
            btnLeft.setOnClickListener(onClickListener);
        }
    }

    public void setBtnRightOnClickListener(View.OnClickListener onClickListener) {
        if (btnRight != null) {
            btnRight.setOnClickListener(onClickListener);
        }
    }
}
