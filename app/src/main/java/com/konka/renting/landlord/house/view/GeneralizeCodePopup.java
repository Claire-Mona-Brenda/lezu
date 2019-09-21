package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.konka.renting.R;

public class GeneralizeCodePopup extends PopupWindow {
    private View mView;
    private EditText edtCode;
    private Button btnSure;
    private OnCall onCall;

    public GeneralizeCodePopup(Context context) {
        super(context);
        init(context);   //布局
        setPopupWindow(context);   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.pop_generalize_code, null);    //绑定布局
        edtCode = mView.findViewById(R.id.pop_generalize_edt_code);
        btnSure = mView.findViewById(R.id.pop_generalize_btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCall != null) {
                    onCall.onClick(edtCode.getText().toString());
                }
            }
        });

    }

    private void setPopupWindow(Context context) {
        this.setContentView(mView); //设置View
        this.setWidth(context.getResources().getDimensionPixelSize(R.dimen.dp_330));  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    public void setOnCall(OnCall onCall) {
        this.onCall = onCall;
    }

    public interface OnCall {
        void onClick(String code);
    }
}
