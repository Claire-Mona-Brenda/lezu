package com.konka.renting.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.AppConfigBean;
import com.konka.renting.bean.CheckVersionBean;
import com.konka.renting.utils.RxBus;


/**
 * Created by kaite on 2018/3/15.
 */

public class UpdateAppPopupwindow extends PopupWindow {
    private View mView;

    public UpdateAppPopupwindow(Context context, AppConfigBean.VersionBean mData) {
        super(context);
        init(context, mData);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context, final AppConfigBean.VersionBean mData) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_update_app, null);    //绑定布局
        TextView mTvNewVersion = mView.findViewById(R.id.tv_version_num);
        TextView tvContent = mView.findViewById(R.id.tv_version_content);
        Button mBtnTry = mView.findViewById(R.id.btn_try);
        ImageView imgClose = mView.findViewById(R.id.img_close);
        mTvNewVersion.setText("· " + mData.getVersion());
        tvContent.setText(Html.fromHtml(mData.getDescribe()));
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().post(new UpdateEvent(mData.getUrl()));
                if (mData.getIs_forced() != 1)
                    dismiss();
            }
        });
        if (mData.getIs_forced() == 1) {
            imgClose.setVisibility(View.GONE);
        }
        //tvContent.setText(mData.content);
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(false);  //弹出窗可触摸
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }
}
