package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class AddDevicesOpenPwdActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.pswView)
    GridPasswordView pswView;
    @BindView(R.id.activity_addDevicePwd_rb_days)
    RadioButton rbDays;
    @BindView(R.id.activity_addDevicePwd_rb_hours)
    RadioButton rbHours;
    @BindView(R.id.activity_addDevicePwd_rb_forever)
    RadioButton rbForever;
    @BindView(R.id.activity_addDevicePwd_edt_hours)
    EditText edtHours;
    @BindView(R.id.activity_addDevicePwd_edt_days)
    EditText edtDays;
    @BindView(R.id.tv_pwd_1)
    TextView tvPwd1;
    @BindView(R.id.tv_pwd_2)
    TextView tvPwd2;
    @BindView(R.id.tv_pwd_3)
    TextView tvPwd3;
    @BindView(R.id.tv_pwd_4)
    TextView tvPwd4;
    @BindView(R.id.tv_pwd_5)
    TextView tvPwd5;
    @BindView(R.id.tv_pwd_6)
    TextView tvPwd6;
    @BindView(R.id.ed_input)
    EditText edInput;

    final String KEY_DEVICE_ID = "device_id";
    final int QUERY_TIME_MAX = 10;

    String deviceId;
    String room_id;
    String[] pwds = new String[6];

    int queryTime = QUERY_TIME_MAX;


    public static void toActivity(Context context, String device_id, String room_id) {
        Intent intent = new Intent(context, AddDevicesOpenPwdActivity.class);
        intent.putExtra("device_id", device_id);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_pwd_add;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_set_pwd);
        tvRight.setText(R.string.confirm);
        tvRight.setVisibility(View.VISIBLE);

        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        room_id = getIntent().getStringExtra("room_id");

        edInput.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

            }
        });
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = s.toString();
                for (int i = 0; i < 6; i++) {
                    if (i < pwd.length())
                        pwds[i] = pwd.charAt(i) + "";
                    else
                        pwds[i] = null;
                }
                tvPwd1.setText(pwds[0]);
                tvPwd2.setText(pwds[1]);
                tvPwd3.setText(pwds[2]);
                tvPwd4.setText(pwds[3]);
                tvPwd5.setText(pwds[4]);
                tvPwd6.setText(pwds[5]);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                check();
                break;
        }
    }

    private void check() {
        String pwd = "";
        for (int i = 0; i < 6; i++) {
            if (pwds[i] != null)
                pwd += pwds[i];
        }
        if (pwd.length() < 6) {
            showToast(R.string.please_input_pwd);
            return;
        }
        if (rbHours.isChecked()) {
            String hours = edtHours.getText().toString();
            if (hours.equals("")) {
                showToast(R.string.please_input_hours);
                return;
            } else if (hours.equals("0")) {
                showToast(R.string.please_input_hours_no_0);
                return;
            } else
                addPwd(pwd, Integer.valueOf(hours));
        } else if (rbDays.isChecked()) {
            String days = edtDays.getText().toString();
            if (days.equals("")) {
                showToast(R.string.please_input_days);
                return;
            } else if (days.equals("0")) {
                showToast(R.string.please_input_days_no_0);
                return;
            } else
                addPwd(pwd, Integer.valueOf(days) * 24);
        } else {
            addPwd(pwd, 0);
        }
    }

    private void addPwd(String password, int hours) {
        tvRight.setEnabled(false);
        queryTime = QUERY_TIME_MAX;
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().setPassword(room_id, deviceId, password, hours + "", "2", password)
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        tvRight.setEnabled(true);
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> dataInfo) {
                        if (dataInfo.success()) {
                            queryPwdTimer(5, dataInfo.data().getId());
                        } else {
                            dismiss();
                            showToast(dataInfo.msg());
                            tvRight.setEnabled(true);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void queryPasswordResult(final String password_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        queryPwdTimer(1, password_id);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            tvRight.setEnabled(true);
                            dismiss();
                            showToast(R.string.add_success);
                            RxBus.getDefault().post(new DeviceOpenPwdListEvent());
                            finish();
                        } else
                            queryPwdTimer(1, password_id);
                    }
                });
        addSubscrebe(subscription);
    }

    private void queryPwdTimer(long delay, final String password_id) {
        if (queryTime <= 0) {
            tvRight.setEnabled(true);
            return;
        }
        Subscription subscription = Observable.timer(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        queryPasswordResult(password_id);
                    }
                });
        addSubscrebe(subscription);
    }


    /**
     * 将密码转换成" "显示
     */
    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        public char charAt(int index) {
            //这里返回的char，就是密码的样式，注意，是char类型的
            return ' ';
        }

        public int length() {
            return mSource.length();
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }

}
