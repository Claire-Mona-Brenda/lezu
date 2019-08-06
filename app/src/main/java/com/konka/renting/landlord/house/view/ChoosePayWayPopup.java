package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;

public class ChoosePayWayPopup extends PopupWindow implements View.OnClickListener {
    private View mView;
    public LinearLayout llAliPay, llWxPay, llQianBao;
    OnCall onCall;

    public ChoosePayWayPopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_choose_pay_way, null);    //绑定布局
        llAliPay = mView.findViewById(R.id.pop_choose_pay_ll_alipay);
        llWxPay = mView.findViewById(R.id.pop_choose_pay_ll_wxpay);
        llQianBao = mView.findViewById(R.id.pop_choose_pay_ll_qianbao);

        llAliPay.setOnClickListener(this);
        llWxPay.setOnClickListener(this);
        llQianBao.setOnClickListener(this);
    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public OnCall getOnCall() {
        return onCall;
    }

    public void setOnCall(OnCall onCall) {
        this.onCall = onCall;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_choose_pay_ll_alipay://支付宝支付
                if (onCall != null) {
                    onCall.aliPay();
                }
                break;
            case R.id.pop_choose_pay_ll_wxpay://微信支付
                if (onCall != null) {
                    onCall.wxPay();
                }
                break;
            case R.id.pop_choose_pay_ll_qianbao://钱包支付
                if (onCall != null) {
                    onCall.qianBaoPay();
                }
                break;
        }
    }

    public interface OnCall {

        void aliPay();

        void wxPay();

        void qianBaoPay();
    }
}
