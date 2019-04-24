package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;


public class CustompopupWindow extends PopupWindow implements View.OnClickListener{
    private TextView btnPhoto,btnSelect,btnCancel;    //popup控件
    private View mview;   //视图
    private OnPopItemClickListener mlistener;  // 定义item点击事件

    public CustompopupWindow(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);
        mview=infalter.inflate(R.layout.photo_popup_select,null);    //绑定布局
        //获取控件
        btnPhoto= (TextView) mview.findViewById(R.id.id_btn_take_photo);
        btnSelect= (TextView) mview.findViewById(R.id.id_btn_select);
        btnCancel= (TextView) mview.findViewById(R.id.id_btn_cancel);

        btnPhoto.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void setPopupWindow() {
        this.setContentView(mview); //设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
        mview.setOnTouchListener(new View.OnTouchListener() {   //如果触摸位置在窗口外面则销毁
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
        });
    }

    // popup item点击事件
    public void setPopItemClickListener(OnPopItemClickListener listener){
        this.mlistener=listener;
    }
    /**
     * 定义接口， popup内部控件点击事件
     */
    public interface OnPopItemClickListener{
        void setPopItemClick(View v);
    }

    //实现内部控件接口
    @Override
    public void onClick(View v) {
        if (mlistener!=null){
            mlistener.setPopItemClick(v);
        }
    }
}
