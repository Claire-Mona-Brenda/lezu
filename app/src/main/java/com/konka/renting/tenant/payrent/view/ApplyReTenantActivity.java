package com.konka.renting.tenant.payrent.view;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class ApplyReTenantActivity extends BaseActivity implements OnDateSetListener {

    @BindView(R.id.ll_checkin_time)
    LinearLayout mLlCheckinTime;
    @BindView(R.id.tv_chenckin_time)
    TextView mTvChenckinTime;
    @BindView(R.id.ll_checkout_time)
    LinearLayout mLlcheckoutTime;
    @BindView(R.id.tv_checkout_time)
    TextView mTvCheckoutTime;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.activity_apply_tenant_edit_phone)
    EditText editPhone;
    @BindView(R.id.activity_apply_tenant_tv_getSecurityCode)
    TextView tvGetSecurityCode;
    @BindView(R.id.activity_apply_tenant_edit_securityCode)
    EditText editSecurityCode;
    @BindView(R.id.activity_apply_tenant_edit_mark)
    EditText editMark;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    private TimePickerDialog mDialogAll;        // 日期选择器控件
    private String time = null;
    private String order_id;
    String member_phone;
    private CountDownTime mTime;
    String start_time;
    Date start;
    long startMin;

    public static void toActivity(Context context, String order_id,String member_phone,String start_time) {

        Intent intent = new Intent(context, ApplyReTenantActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("member_phone", member_phone);
        intent.putExtra("start_time", start_time);
        context.startActivity(intent);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_apply_re_tenant;
    }

    @Override
    public void init() {

        setTitleText("申请续租");
        order_id = getIntent().getStringExtra("order_id");
        member_phone=getIntent().getStringExtra("member_phone");
        start_time=getIntent().getStringExtra("start_time");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            start = sdf.parse(start_time);
        } catch (Exception e) {
        }
        startMin=start.getTime()+ (long) (24 * 60 * 60 * 1000);
        mTvChenckinTime.setText(sdf.format(start));
        mTime = new CountDownTime(59000, 1000);
        editPhone.setEnabled(false);
        editPhone.setText(member_phone);
    }

    @OnClick({R.id.iv_back,  R.id.ll_checkout_time, R.id.btn_commit,R.id.activity_apply_tenant_tv_getSecurityCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
//            case R.id.tv_chenckin_time:
//                time = "入住时间";
//                showDatePicker();
//                break;
            case R.id.ll_checkout_time:
                time = "";
                showDatePicker();
                break;
            case R.id.activity_apply_tenant_tv_getSecurityCode://获取验证码
                mTime.start();
                yzm();
                break;
            case R.id.btn_commit:
                if (mTvChenckinTime.getText().equals("")) {
                    showToast("请选择入住时间");
                } else if (mTvCheckoutTime.getText().equals("")) {
                    showToast("请选择退房时间");
                } else if(editSecurityCode.equals("")){
                    showToast(R.string.regist_verfication_num_hint);
                }else {
                    reNew();
                }
                break;
        }
    }

    //展示时间选择器
    private void showDatePicker() {
        long tenYears = 100L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId(time)
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(false)
                .setMinMillseconds(startMin)
                .setMaxMillseconds(startMin + tenYears)
                .setCurrentMillseconds(startMin)
                .setThemeColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(19)
                .build();
        mDialogAll.show(getSupportFragmentManager(), "YEAR_MONTH_DAY");
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        if (time.equals("入住时间")) {
            mTvChenckinTime.setText(getDateToString(millseconds));
        } else {
            mTvCheckoutTime.setText(getDateToString(millseconds));
        }
    }

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");



    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void reNew() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().relet(order_id,
                editSecurityCode.getText().toString(),
                mTvChenckinTime.getText().toString(),
                mTvCheckoutTime.getText().toString(),
                editMark.getText().toString())
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
                        if (dataInfo.success()) {
                            showToast(dataInfo.msg());
                            RxBus.getDefault().post(new RenewEvent());
                            finish();
                        } else
                            showToast(dataInfo.msg());
                    }
                });
        addSubscrebe(subscription);
    }

    public void yzm(){
        String tel=editPhone.getText().toString();
        if (tel.equals("")){
            showToast(R.string.login_phone_hint);
            return;
        }

        Subscription subscription = (SecondRetrofitHelper.getInstance().getVerify(tel,"1")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
            @Override
            public void onNext(DataInfo homeInfoDataInfo) {

                if (homeInfoDataInfo.success()) {
                    ShowToastUtil.showSuccessToast(ApplyReTenantActivity.this,homeInfoDataInfo.msg());
                } else {
                    ShowToastUtil.showWarningToast(ApplyReTenantActivity.this,homeInfoDataInfo.msg());
                }
            }
        }));
        addSubscrebe(subscription);
    }
    class CountDownTime extends CountDownTimer {

        //构造函数  第一个参数代表总的计时时长  第二个参数代表计时间隔  单位都是毫秒
        public CountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) { //每计时一次回调一次该方法
            TextView textView= findViewById(R.id.activity_apply_tenant_tv_getSecurityCode);
            textView .setClickable(false);
            textView.setText(l/1000 +"s" );
        }

        @Override
        public void onFinish() { //计时结束回调该方法
            TextView textView= findViewById(R.id.activity_apply_tenant_tv_getSecurityCode);
            textView.setClickable(true);
            textView.setText("获取验证码");
        }
    }
}
