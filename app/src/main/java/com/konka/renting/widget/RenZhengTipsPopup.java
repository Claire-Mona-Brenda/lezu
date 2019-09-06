package com.konka.renting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class RenZhengTipsPopup extends PopupWindow {
    private View mView;
    public TextView btnToRenZheng;
    Context mContext;
    OnToClickCall onToClickCall;

    public RenZhengTipsPopup(Context context) {
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
        mView = infalter.inflate(R.layout.popup_renzheng_tips, null);    //绑定布局
        btnToRenZheng = mView.findViewById(R.id.pop_renzheng_tv_to_renzheng);
        btnToRenZheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onToClickCall != null)
                    onToClickCall.onClick();
                dismiss();
            }
        });
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.dp_284));  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setOnToClickCall(OnToClickCall onToClickCall) {
        this.onToClickCall = onToClickCall;
    }

    public interface OnToClickCall {
        void onClick();
    }
}
