package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GeneratePwdBean;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.DateTimeUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.DateHourPicker;
import com.konka.renting.widget.ShareAppPopup;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.addapp.pickers.picker.DatePicker;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.konka.renting.utils.DateTimeUtil.FORMAT_DATE;
import static com.konka.renting.utils.DateTimeUtil.FORMAT_DATE_TIME_SECOND;

public class SetTimerPwdActivity extends BaseActivity {
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
    @BindView(R.id.activity_set_timer_pwd_tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.activity_set_timer_pwd_ll_start_time)
    LinearLayout mLlStartTime;
    @BindView(R.id.activity_set_timer_pwd_tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.activity_set_timer_pwd_ll_end_time)
    LinearLayout mLlEndTime;
    @BindView(R.id.activity_set_timer_pwd_tv_create)
    TextView mTvCreate;


    final String KEY_DEVICE_ID = "device_id";
    final String KEY_ROOM_ID = "room_id";

    final int QUERY_TIME_MAX = 10;

    int queryTime = QUERY_TIME_MAX;

    String deviceId;
    String room_id;
    String managePwd;
    GeneratePwdBean generatePwdBean;

    private ShareAppPopup shareAppPopup;
    DateHourPicker picker;

    boolean isStart = true;
    String start;
    String end;

    public static void toActivity(Context context, String device_id, String room_id) {
        Intent intent = new Intent(context, SetTimerPwdActivity.class);
        intent.putExtra("device_id", device_id);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_timer_pwd;
    }

    @Override
    public void init() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        room_id = getIntent().getStringExtra(KEY_ROOM_ID);

        getManagePwd();
    }


    @OnClick({R.id.iv_back, R.id.activity_set_timer_pwd_ll_start_time, R.id.activity_set_timer_pwd_ll_end_time, R.id.activity_set_timer_pwd_tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_set_timer_pwd_ll_start_time:
                if (!TextUtils.isEmpty(managePwd)) {
                    isStart = true;
                    onYearMonthDayPicker();
                } else {
                    showToast(R.string.please_set_manage_pwd);
                }
                break;
            case R.id.activity_set_timer_pwd_ll_end_time:
                if (!TextUtils.isEmpty(managePwd)) {
                    isStart = false;
                    onYearMonthDayPicker();
                } else {
                    showToast(R.string.please_set_manage_pwd);
                }
                break;
            case R.id.activity_set_timer_pwd_tv_create:
                if (TextUtils.isEmpty(managePwd)) {
                    showToast(R.string.please_set_manage_pwd);
                } else if (TextUtils.isEmpty(start)) {
                    showToast(R.string.timer_start_time);
                } else if (TextUtils.isEmpty(end)) {
                    showToast(R.string.timer_end_time);
                } else if(DateTimeUtil.stringToDate(end, FORMAT_DATE_TIME_SECOND).getTime()<new Date().getTime()){
                    showToast(R.string.timer_end_no_to_more_now_time);
                }else if (DateTimeUtil.stringToDate(start, FORMAT_DATE_TIME_SECOND).getTime() >= DateTimeUtil.stringToDate(end, FORMAT_DATE_TIME_SECOND).getTime()) {
                    showToast(R.string.timer_start_no_to_more_end_time);
                } else {
                    addGeneratePassword();
                }
                break;
        }
    }

    /*************************************************计时处理*****************************************************************/


    private void queryPwdTimer(long delay, final String password_id) {
        if (queryTime <= 0) {
            dismiss();
            showToast(R.string.open_tips_get_pwd_time_fail);
            return;
        }
        queryTime--;
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

    /***************************************************接口************************************************/
    private void getManagePwd() {
        showLoadingDialog();
        mProgressDialog.setCanceledOnTouchOutside(false);
        Subscription subscription = SecondRetrofitHelper.getInstance().lockPwd(room_id, "0")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                dismiss();
                                managePwd = info.data().getPassword();
                            } else {
                                queryPwdTimer(5, info.data().getId());
                            }
                        } else {
                            dismiss();
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void queryPasswordResult(final String password_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo<PwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        queryPwdTimer(1, password_id);
                    }

                    @Override
                    public void onNext(DataInfo<PwdBean> dataInfo) {
                        if (dataInfo.success()) {
                            dismiss();
                            managePwd = dataInfo.data().getPassword();
                        } else {
                            queryPwdTimer(1, password_id);
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void addGeneratePassword() {
        Subscription subscription = SecondRetrofitHelper.getInstance().addGeneratePassword("2", managePwd, deviceId, "", start, end)
                .compose(RxUtil.<DataInfo<GeneratePwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<GeneratePwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<GeneratePwdBean> info) {
                        dismiss();
                        if (info.success()) {
                            generatePwdBean = info.data();
                            PwdResultActivity.toActivity(mActivity, generatePwdBean.getPassword(), generatePwdBean.getStart_time(), generatePwdBean.getEnd_time(), "", "", true);
                            finish();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**************************************************************************************************/
    public void onYearMonthDayPicker() {
        if (picker == null) {
            String today = DateTimeUtil.getFormatToday(FORMAT_DATE_TIME_SECOND);
            int y = Integer.valueOf(today.split(" ")[0].split("-")[0]);
            int m = Integer.valueOf(today.split(" ")[0].split("-")[1]);
            int d = Integer.valueOf(today.split(" ")[0].split("-")[2]);
            int h = Integer.valueOf(today.split(" ")[1].split(":")[0]);
            picker = new DateHourPicker(this);
            picker.setCanLoop(false);
            picker.setWheelModeEnable(true);
            picker.setCanLinkage(true);
            picker.setTopPadding(15);
            picker.setRangeStart(y, m, d);
            picker.setRangeEnd(y + 2, 6, 1);
            picker.setSelectedItem(y, m, d, 0);
            picker.setWeightEnable(true);
            picker.setSubmitTextColor(0xFF309BEA);//顶部确定按钮文字颜色
            picker.setLineColor(Color.WHITE);
            picker.setSelectedTextColor(Color.BLACK);
            picker.setOnDatePickListener(new DateHourPicker.OnYearMonthDayHourPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day, String hour) {
                    String time = year + "-" + month + "-" + day + " " + hour + ":00:00";
                    if (isStart) {
                        start = time;
                        mTvStartTime.setText(time);
                        mTvStartTime.setTextColor(getResources().getColor(R.color.text_black));
                    } else {
                        end = time;
                        mTvEndTime.setText(time);
                        mTvEndTime.setTextColor(getResources().getColor(R.color.text_black));
                    }
                }
            });
        }

        picker.show();
    }
}
