package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

/**
 * Created by kaite on 2018/5/15.
 */

public class ShowPwdPopup extends PopupWindow {

    private View mview;

    private String pwd;

    public ShowPwdPopup(Context context, String pwd) {
        super(context);
        this.pwd = pwd;
        init(context);
        setPopupWindow();
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);
        //绑定布局
        mview = infalter.inflate(R.layout.popup_show_pwd,null);
        TextView tvPwd = mview.findViewById(R.id.tv_pwd);
        tvPwd.setText(pwd);
        mview.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    private void setPopupWindow() {
        this.setContentView(mview); //设置View
        this.setWidth(700);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
        /*mview.setOnTouchListener(new View.OnTouchListener() {   //如果触摸位置在窗口外面则销毁
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height=mview.findViewById(R.id.id_pop_layout).getTop();
                int y= (int) event.getY();
                if (event.getAction()== MotionEvent.ACTION_UP){
                    if (y<height){
                        dismiss();   //销毁
                    }
                }
                return true;
            }
        });*/
    }
}
