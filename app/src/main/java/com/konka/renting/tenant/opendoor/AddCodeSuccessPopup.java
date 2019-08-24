package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.konka.renting.R;
import com.konka.renting.utils.RxBus;

public class AddCodeSuccessPopup extends PopupWindow {

    private View mView;
    private TextView mTvName, mTvSure;


    public AddCodeSuccessPopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_add_code_success, null);    //绑定布局
        mTvName = mView.findViewById(R.id.pop_add_code_tv_name);
        mTvSure = mView.findViewById(R.id.pop_add_code_tv_sure);

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setmTvName(String name) {
        if (mTvName != null)
            mTvName.setText(name);
    }

    public void setmTvSure(View.OnClickListener onClickListener) {
        if (mTvSure != null)
            mTvSure.setOnClickListener(onClickListener);
    }
}
