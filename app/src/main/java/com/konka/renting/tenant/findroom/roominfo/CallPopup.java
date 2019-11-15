package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class CallPopup extends PopupWindow {
    private Context mContext;
    private View mView;
    public TextView tvNumber, btnCancel, btnSure;

    public CallPopup(Context context) {
        super(context);
        mContext = context;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_call, null);    //绑定布局
        tvNumber = mView.findViewById(R.id.pop_call_tv_number);
        btnCancel = mView.findViewById(R.id.pop_call_tv_cancel);
        btnSure = mView.findViewById(R.id.pop_call_tv_sure);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setPhone(String phone) {
        if (tvNumber != null) {
            tvNumber.setText(phone);
        }
    }

    public void setBtnColor(Drawable drawable){
        if (btnSure != null) {
            btnSure.setBackground(drawable);
        }
    }

    public void setOnClickListen(View.OnClickListener onClickListen) {
        if (btnSure != null) {
            btnSure.setOnClickListener(onClickListen);
        }
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.dp_310));  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画


    }
}
