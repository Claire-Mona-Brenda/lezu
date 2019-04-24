package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.event.ToGetPwdTimeEvent;
import com.konka.renting.utils.RxBus;

public class ShowOpenPwdPopupwindow extends PopupWindow {
    private View mView;
    Context mContext;
    LinearLayout llPwd;
    TextView tvPwd1, tvPwd2, tvPwd3, tvPwd4, tvPwd5, tvPwd6, tvPwdTime, tvFail;
    ImageView imgClose;

    public ShowOpenPwdPopupwindow(Context context) {
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
        mView = infalter.inflate(R.layout.popup_show_open_pwd, null);    //绑定布局
        llPwd=mView.findViewById(R.id.pop_ll_pwd);
        tvPwd1 = mView.findViewById(R.id.pop_tv_pwd_1);
        tvPwd2 = mView.findViewById(R.id.pop_tv_pwd_2);
        tvPwd3 = mView.findViewById(R.id.pop_tv_pwd_3);
        tvPwd4 = mView.findViewById(R.id.pop_tv_pwd_4);
        tvPwd5 = mView.findViewById(R.id.pop_tv_pwd_5);
        tvPwd6 = mView.findViewById(R.id.pop_tv_pwd_6);
        tvFail = mView.findViewById(R.id.pop_tv_fail);
        tvPwdTime = mView.findViewById(R.id.pop_tv_pwd_time);
        imgClose = mView.findViewById(R.id.pop_img_close);
        tvPwdTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().post(new ToGetPwdTimeEvent());
                dismiss();
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            llPwd.setVisibility(View.GONE);
            tvFail.setVisibility(View.VISIBLE);
        }else {
            llPwd.setVisibility(View.VISIBLE);
            tvFail.setVisibility(View.GONE);

            tvPwd1.setText(pwd.charAt(0) + "");
            tvPwd2.setText(pwd.charAt(1) + "");
            tvPwd3.setText(pwd.charAt(2) + "");
            tvPwd4.setText(pwd.charAt(3) + "");
            tvPwd5.setText(pwd.charAt(4) + "");
            tvPwd6.setText(pwd.charAt(5) + "");
        }
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }
}
