package com.konka.renting.landlord.gateway.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import com.konka.renting.R;

public class HowBindPopup extends PopupWindow {
    private View mView;
    VideoView videoView;
    TextView tvName, tvTitle1, tvTitle2, tvStep1, tvStep2, tvStep3, tvStep4, tvStep5, tvStep6;
    MediaController media;
    Context mContext;

    public HowBindPopup(Context context) {
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
        mView = infalter.inflate(R.layout.popup_how_bind, null);    //绑定布局
        videoView = mView.findViewById(R.id.pop_vv_video);
        tvName = mView.findViewById(R.id.pop_tv_name);
        tvTitle1 = mView.findViewById(R.id.pop_tv_title_1);
        tvTitle2 = mView.findViewById(R.id.pop_tv_title_2);
        tvStep1 = mView.findViewById(R.id.pop_tv_step_1);
        tvStep2 = mView.findViewById(R.id.pop_tv_step_2);
        tvStep3 = mView.findViewById(R.id.pop_tv_step_3);
        tvStep4 = mView.findViewById(R.id.pop_tv_step_4);
        tvStep5 = mView.findViewById(R.id.pop_tv_step_5);
        tvStep6 = mView.findViewById(R.id.pop_tv_step_6);

//        media=new MediaController(context);
//        media.setAnchorView(videoView);
//        videoView.setMediaController(media);
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

    public void stop(){
        if (videoView.isPlaying())
            videoView.stopPlayback();
    }

    public void chooseItem(int position){
        String uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k44;
        switch (position){
            case 0://K44
                tvName.setText(R.string.how_bind_device_name_1);
                tvTitle1.setText(R.string.how_bind_title_step);
                tvStep1.setText(R.string.how_bind_device_1_step_1);
                tvStep2.setText(R.string.how_bind_device_1_step_2);

                tvTitle1.setVisibility(View.VISIBLE);
                tvStep1.setVisibility(View.VISIBLE);
                tvStep2.setVisibility(View.VISIBLE);
                tvStep3.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvStep4.setVisibility(View.GONE);
                tvStep5.setVisibility(View.GONE);
                tvStep6.setVisibility(View.GONE);

                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k44;
                break;
            case 1://k52
                tvName.setText(R.string.how_bind_device_name_2);
                tvTitle1.setText(R.string.how_bind_title_step);
                tvStep1.setText(R.string.how_bind_device_2_step_1);
                tvStep2.setText(R.string.how_bind_device_2_step_2);

                tvTitle1.setVisibility(View.VISIBLE);
                tvStep1.setVisibility(View.VISIBLE);
                tvStep2.setVisibility(View.VISIBLE);
                tvStep3.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvStep4.setVisibility(View.GONE);
                tvStep5.setVisibility(View.GONE);
                tvStep6.setVisibility(View.GONE);

                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k52;
                break;
            case 2://K47
                tvName.setText(R.string.how_bind_device_name_3);
                tvTitle1.setText(R.string.how_bind_title_lock_main);
                tvStep1.setText(R.string.how_bind_device_3_step_1);
                tvTitle2.setText(R.string.how_bind_title_lock_second);
                tvStep4.setText(R.string.how_bind_device_3_step_2);
                tvStep5.setText(R.string.how_bind_device_3_step_3);


                tvTitle1.setVisibility(View.VISIBLE);
                tvStep1.setVisibility(View.VISIBLE);
                tvStep2.setVisibility(View.GONE);
                tvStep3.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.VISIBLE);
                tvStep4.setVisibility(View.VISIBLE);
                tvStep5.setVisibility(View.VISIBLE);
                tvStep6.setVisibility(View.GONE);

                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.k47;
                break;
            case 3://智能保险箱
                tvName.setText(R.string.how_bind_device_name_4);
                tvTitle1.setText(R.string.how_bind_title_lock_main);
                tvStep1.setText(R.string.how_bind_device_4_step_1);
                tvTitle2.setText(R.string.how_bind_title_lock_second);
                tvStep4.setText(R.string.how_bind_device_4_step_2);
                tvStep5.setText(R.string.how_bind_device_4_step_3);

                tvTitle1.setVisibility(View.VISIBLE);
                tvStep1.setVisibility(View.VISIBLE);
                tvStep2.setVisibility(View.GONE);
                tvStep3.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.VISIBLE);
                tvStep4.setVisibility(View.VISIBLE);
                tvStep5.setVisibility(View.VISIBLE);
                tvStep6.setVisibility(View.GONE);

                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.safebox;
                break;
            case 4://空气质量探测器
                tvName.setText(R.string.how_bind_device_name_5);
                tvTitle1.setText(R.string.how_bind_title_step);
                tvStep1.setText(R.string.how_bind_device_5_step_1);
                tvStep2.setText(R.string.how_bind_device_5_step_2);

                tvTitle1.setVisibility(View.VISIBLE);
                tvStep1.setVisibility(View.VISIBLE);
                tvStep2.setVisibility(View.VISIBLE);
                tvStep3.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvStep4.setVisibility(View.GONE);
                tvStep5.setVisibility(View.GONE);
                tvStep6.setVisibility(View.GONE);

                uri="android.resource://"+mContext.getPackageName()+"/"+R.raw.pm25;
                break;
        }
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
    }

    public void onDestroy(){
        videoView.stopPlayback();
        videoView.setOnCompletionListener(null);
        videoView.setOnPreparedListener(null);
//        media.removeAllViews();
        videoView = null;
        media=null;

    }
}
