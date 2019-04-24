package com.konka.renting.landlord.gateway.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.konka.renting.R;

import rx.subscriptions.CompositeSubscription;

public class GatewaySettingPopup extends PopupWindow implements View.OnClickListener {
    public static final String CLICK_DELETE = "delete";

    Context mContext;
    private View mView;
    private RelativeLayout rlDel;//删除网关

    protected CompositeSubscription mCompositeSubscription;
    private OnClickItemListent onClickItemListent;

    public GatewaySettingPopup(Context context) {
        super(context);
        this.mContext = context;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    private void init(Context context) {
        mView = View.inflate(context, R.layout.popup_gateway_setting, null);
        rlDel = mView.findViewById(R.id.popup_rl_del_gateway);
        rlDel.setOnClickListener(this);

    }

    public void initStatus(int status) {
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setOnClickItemListent(OnClickItemListent onClickItemListent) {
        this.onClickItemListent = onClickItemListent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_rl_del_gateway://删除网关
                if (onClickItemListent != null)
                    onClickItemListent.onClick(CLICK_DELETE);
                this.setOnDismissListener(null);
                this.dismiss();
                break;
        }
    }

    public interface OnClickItemListent {
        public void onClick(String type);
    }
}
