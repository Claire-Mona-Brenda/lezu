package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.widget.PasswordView;

public class WithdrawcashPwdPopup extends PopupWindow {
    private View mView;
    public TextView tvTitle;
    public TextView tvForget;
    public PasswordView passwordView;

    public WithdrawcashPwdPopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_withdrawcash_pwd, null);    //绑定布局
        tvTitle = mView.findViewById(R.id.pop_withdrawcash_pwd_tv_title);
        passwordView = mView.findViewById(R.id.pop_withdrawcash_pwd_pv_pwd);
        tvForget = mView.findViewById(R.id.pop_withdrawcash_pwd_tv_forget);

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        //防止虚拟软键盘被弹出菜单遮住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setPasswordViewFouse(){
        if (passwordView != null) {
            passwordView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    passwordView.showInput();
                }
            }, 500);

        }
    }

    public void hidePasswordViewFouse(){
        if (passwordView != null) {
            passwordView.hideInput();

        }
    }

    public void setListent(PasswordView.PasswordListener listent) {
        if (passwordView != null)
            passwordView.setPasswordListener(listent);
    }

    public void setTvForgetEnable(boolean is) {
        if (tvForget != null) {
            tvForget.setEnabled(is);
            tvForget.setVisibility(is ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setForgetListent(View.OnClickListener  onClickListener) {
        if (tvForget != null)
            tvForget.setOnClickListener(onClickListener);
    }

    @Override
    public void dismiss() {
        hidePasswordViewFouse();
        super.dismiss();
    }
}
