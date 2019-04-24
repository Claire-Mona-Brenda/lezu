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
import com.konka.renting.tenant.opendoor.OpenDoorServiceEvent;
import com.konka.renting.tenant.opendoor.UploadFileEvent;
import com.konka.renting.utils.RxBus;

public class UploadFilePopupwindow extends PopupWindow {

    private View mView;
    public UploadFilePopupwindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);
        mView=infalter.inflate(R.layout.popup_upload_file,null);    //绑定布局
        Button btn=mView.findViewById(R.id.pop_upload_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getDefault().post(new UploadFileEvent(11));
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
}
