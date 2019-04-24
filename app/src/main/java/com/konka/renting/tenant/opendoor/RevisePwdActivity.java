package com.konka.renting.tenant.opendoor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class RevisePwdActivity extends BaseActivity {

    @BindView(R.id.et_old_pwd)
    EditText mEtOldPwd;
    @BindView(R.id.et_new_pwd)
    EditText mEtNewPwd;
    @BindView(R.id.m_get_code)
    TextView mGetCode;
    private String is_text = "0";
    private ValueAnimator valueAnimator;
    public static final int SMS_CODE_VALID_SECOND = 60;
    private String mCurrentUser;
    private int type;
    private String mobile;

    public static void toActivity(Context context, String type) {
        Intent intent = new Intent(context, RevisePwdActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_revise_pwd;
    }

    @Override
    public void init() {

        setTitleText(R.string.revise_lock_pwd);
        setRightText(R.string.common_save);
        type = Integer.parseInt(getIntent().getStringExtra("type"));
        mobile = LoginUserBean.getInstance().getMobile();
        //mCurrentUser = UserBean.getInstance().getMobile();
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.m_get_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                String verfy = mEtOldPwd.getText().toString();
                String newPwd = mEtNewPwd.getText().toString();
                updatePwd(verfy, newPwd);
                break;
            case R.id.m_get_code:
                getCode();
                break;
        }
    }

    //获取文本验证码
    private void getCode() {

        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().sendVerifyCode(type, mobile, 5)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success())
                            valueAnimator = countTimer(mGetCode);
                        else
                            showToast(dataInfo.msg());

                    }
                });
        addSubscrebe(subscription);
    }

    private void updatePwd(String verfy, String newPwd) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().updatePwd(getIntent().getStringExtra("type"), newPwd,verfy)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        showToast(dataInfo.msg());
                        if (dataInfo.success())
                            finish();
                        else
                            showToast(dataInfo.msg());
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 验证码获取成功后 开启倒计时
     *
     * @param sendCodeBtn
     * @return
     */
    public static ValueAnimator countTimer(final TextView sendCodeBtn) {
        sendCodeBtn.setEnabled(false);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(SMS_CODE_VALID_SECOND, 0).setDuration(SMS_CODE_VALID_SECOND * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sendCodeBtn.setText(animation.getAnimatedValue() + "s");
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                sendCodeBtn.setText("重新获取");
                sendCodeBtn.setEnabled(true);
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        return valueAnimator;
    }

    /**
     * 停止计时
     */
    private void stopTimeCounter() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            mGetCode.setText("重新获取 停止");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimeCounter();
    }

}
