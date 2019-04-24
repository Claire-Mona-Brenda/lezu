package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import com.konka.renting.R;

public class HowToSettingPopup extends PopupWindow {
    private View mView;
    VideoView videoView;
    TextView tvTips;
    Context mContext;

    public HowToSettingPopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        this.mContext=context;
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.popup_how_setting, null);    //绑定布局
        videoView = mView.findViewById(R.id.pop_vv_video);
        tvTips = mView.findViewById(R.id.pop_tv_tips);

        //设置循环播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
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

    public void chooseItem(int position){
        String uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k52_add_fingerprint;
        switch (position){
            case 0://录入指纹
                tvTips.setText(R.string.how_to_setting_fingerprint);
                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k52_add_fingerprint;
                break;
            case 1://添加IC卡
                tvTips.setText(R.string.how_to_setting_ic_card);
                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k52_add_ic;
                break;
            case 2://添加遥控器
                tvTips.setText(R.string.how_to_setting_remote);
                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k52_add_remote;
                break;
        }
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
    }

    public void onDestroy(){
        videoView.stopPlayback();
        videoView.setOnCompletionListener(null);
        videoView.setOnPreparedListener(null);
        videoView = null;
    }
}
