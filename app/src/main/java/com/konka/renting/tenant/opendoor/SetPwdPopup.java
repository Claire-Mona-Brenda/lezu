package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.konka.renting.R;
import com.konka.renting.utils.RxBus;

/**
 * Created by Administrator on 2018/1/1.
 */

public class SetPwdPopup extends PopupWindow implements View.OnClickListener {
    private View mView;
    private TextView mTvCancel, mTvConfirm;
    private GridPasswordView mEtNeekname;
    private String noEmpty;

    private SetPwdEvent.Type mType;

    public SetPwdPopup(Context context, SetPwdEvent.Type type) {
        super(context);
        mType = type;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter = LayoutInflater.from(context);
        mView = infalter.inflate(R.layout.layout_set_pwd, null);    //绑定布局
        mTvCancel = mView.findViewById(R.id.tv_cancel);
        mEtNeekname = mView.findViewById(R.id.pswView);
        mTvCancel.setOnClickListener(this);
        noEmpty = context.getString(R.string.pwd_no_empty);
        mEtNeekname.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                if (!TextUtils.isEmpty(mEtNeekname.getPassWord().toString())) {
                    RxBus.getDefault().post(new SetPwdEvent(mEtNeekname.getPassWord().toString(), false, mType));
                    dismiss();
                } else {
                    RxBus.getDefault().post(new SetPwdEvent(noEmpty, true, mType));
                }
            }
        });

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                RxBus.getDefault().post(new CancelSetEvent(mType));
                dismiss();
                break;

        }
    }
}
