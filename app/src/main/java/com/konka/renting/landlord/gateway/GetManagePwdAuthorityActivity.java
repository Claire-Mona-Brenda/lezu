package com.konka.renting.landlord.gateway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseDetailsInfoBean;
import com.konka.renting.bean.RoomUserPhoneBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.OpenManageActivity;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonSurePopupWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class GetManagePwdAuthorityActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.activity_get_manage_pwd_tv_phone)
    TextView tvPhone;
    @BindView(R.id.activity_get_manage_pwd_tv_send_msg)
    TextView tvSendMsg;
    @BindView(R.id.activity_get_manage_pwd_edt_mail)
    EditText edtMail;
    @BindView(R.id.activity_get_manage_pwd_btn_sure)
    Button btnSure;

    final int SMS_CODE_VALID_SECOND = 60;

    private CommonSurePopupWindow commonTipsPopup;
    String room_id;
    String mobile;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, GetManagePwdAuthorityActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_get_manage_authority;
    }

    @Override
    public void init() {
        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(true, this);
        UIUtils.setDarkStatusIcon(this, true);

        room_id = getIntent().getStringExtra("room_id");
        getPhoneMobile();
    }


    @OnClick({R.id.ll_title, R.id.activity_get_manage_pwd_tv_send_msg, R.id.activity_get_manage_pwd_btn_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_title:
                finish();
                break;
            case R.id.activity_get_manage_pwd_tv_send_msg:
                if (TextUtils.isEmpty(mobile)) {
                    getPhoneMobile();
                    return;
                }else{
                    sendCode();
                }
                break;
            case R.id.activity_get_manage_pwd_btn_sure:
                String code =edtMail.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    getPhoneMobile();
                    return;
                }else if (TextUtils.isEmpty(code)){
                    showToast(R.string.warm_please_verfication_no_empty);
                }else{
                    getManagePwd(code);
                }
                break;
        }
    }

    private void enableInit(boolean enable){
        tvSendMsg.setEnabled(enable);
        btnSure.setEnabled(enable);
        edtMail.setEnabled(enable);
    }

    /************************************************接口********************************************************************/

    private void getPhoneMobile() {
        enableInit(false);
        Subscription subscription = SecondRetrofitHelper.getInstance().room_used_phone(room_id)
                .compose(RxUtil.<DataInfo<RoomUserPhoneBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomUserPhoneBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        enableInit(true);
                    }

                    @Override
                    public void onNext(DataInfo<RoomUserPhoneBean> dataInfo) {
                        enableInit(true);
                        if (dataInfo.success()) {
                            mobile = dataInfo.data().getPhone();
                            if (!TextUtils.isEmpty(mobile))
                                tvPhone.setText(mobile);
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void sendCode() {

        Subscription subscription = SecondRetrofitHelper.getInstance().getManageCode(room_id, mobile)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            countTimer();
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void getManagePwd(String code) {

        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getManagePwd(room_id, mobile, code)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            showTips(getString(R.string.get_manage_pwd_success_tips));
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /*******************************************倒计时处理*****************************************************************/
    /**
     * 验证码获取成功后 开启倒计时
     */
    public ValueAnimator countTimer() {
        tvSendMsg.setEnabled(false);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(SMS_CODE_VALID_SECOND, 0).setDuration(SMS_CODE_VALID_SECOND * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tvSendMsg.setText(animation.getAnimatedValue() + "s");
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tvSendMsg.setText("重新获取");
                tvSendMsg.setEnabled(true);
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        return valueAnimator;
    }

    /******************************************************弹窗*******************************************************************/
    /**
     * 提示
     */
    private void showTips(String content) {
        commonTipsPopup = new CommonSurePopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(content)
                .setBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonTipsPopup.dismiss();
                        finish();
                    }
                })
                .create();
        showPopup(commonTipsPopup);

    }
    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.8f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) llTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }
}
