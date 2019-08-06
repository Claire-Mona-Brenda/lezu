package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.bean.ServerDeviceInfo;
import com.konka.renting.event.BindDevSuccessEvent;
import com.konka.renting.event.RefreshDeviceEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.activity.BindDevSuccessActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class GatewayConnectionActivity extends BaseActivity {

    @BindView(R.id.tv_msg)
    TextView mTvMsg;
//    @BindView(R.id.circleView)
//    CircleProgressView mCircleView;
//    @BindView(R.id.tv_time)
//    TextView mTvTime;

    public static final int MAX = 600;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.activity_gate_conn_img_gateway)
    ImageView imgGateway;
    @BindView(R.id.activity_gate_conn_pb_progress)
    ProgressBar pbProgress;
    @BindView(R.id.activity_gate_conn_tv_control)
    TextView tvControl;

    private int mValue = MAX;

    private MachineInfo mMachineInfo;
    private GatewayInfo mGatewayInfo;

    private String mRoomId;
    boolean is_Install;
    boolean isFirst;

    public static void toActivity(Context context, GatewayInfo gatewayInfo, MachineInfo machineInfo, String room_id, boolean is_Install, boolean isFirst) {
        Intent intent = new Intent(context, GatewayConnectionActivity.class);
        intent.putExtra(MachineInfo.class.getSimpleName(), machineInfo);
        intent.putExtra(GatewayInfo.class.getSimpleName(), gatewayInfo);
        intent.putExtra("room_id", room_id);
        intent.putExtra("is_Install", is_Install);
        intent.putExtra("isFirst", isFirst);
        context.startActivity(intent);
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            mTvTime.setText(String.format("%dS", mValue));
            if (mValue == 0) {

                BindGatewayFaildedActivity.toActivity(mActivity);
                finish();
            } else {
                mHanlder.sendMessageDelayed(this.obtainMessage(0), 1000);
                mValue--;
                pbProgress.setProgress(mValue);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_gateway_connection;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHanlder.removeMessages(0);
    }

    @Override
    public void init() {
        setTitleText("");

        mMachineInfo = getIntent().getParcelableExtra(MachineInfo.class.getSimpleName());
        mGatewayInfo = getIntent().getParcelableExtra(GatewayInfo.class.getSimpleName());
        mRoomId = getIntent().getStringExtra("room_id");
        is_Install = getIntent().getBooleanExtra("is_Install", false);
        isFirst = getIntent().getBooleanExtra("isFirst", false);

        imgGateway.setImageResource(Integer.valueOf(mGatewayInfo.getGateway_version()) > 3 ? R.mipmap.wangguan_2_116px_png : R.mipmap.wangguan_1_116px_png);
        tvControl.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        pbProgress.setMax(MAX);
        pbProgress.setProgress(MAX);

//        mTvMsg.setText(Html.fromHtml("点击设置键<br /><font color='#ff0000'>听到滴声后松手</font>等待配对"));

//        mCircleView.setUnit("S");
//        mCircleView.setUnitVisible(false);
//        mCircleView.setUnitSize(UIUtils.px2dip(20));
//        mCircleView.setTextMode(TextMode.TEXT);
//        mCircleView.setText("");
//        mCircleView.setSeekModeEnabled(false);
//        mCircleView.setValueInterpolator(new LinearInterpolator());
//        startAnim();
        goBandingMode();
    }

    @OnClick({R.id.iv_back, R.id.activity_gate_conn_tv_control})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_gate_conn_tv_control:
                HowBindingActivity.toActivity(this);
                break;
        }
    }

    private void startAnim() {
//        if (mCircleView == null)
//            return;
//
//        mCircleView.setValue(0);
//        mCircleView.setMaxValue(MAX);
//        mCircleView.setValueAnimated(0, MAX, MAX * 1000);

        mHanlder.sendEmptyMessage(0);
    }

    //step 1
    private void goBandingMode() {
//        Subscription subscription = RetrofitHelper.getInstance().goBandingMode(mGatewayInfo.getId())
        Subscription subscription = SecondRetrofitHelper.getInstance().gatewayBandMode(mGatewayInfo.getId())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        BindGatewayFaildedActivity.toActivity(mActivity);
                        finish();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            mTvMsg.setText(R.string.tips_gateway_binding);
                            startAnim();
                            checkBandingStatus();
                        } else {
                            showToast(dataInfo.msg());
                            BindGatewayFaildedActivity.toActivity(mActivity);
                            finish();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //step 2
    private void checkBandingStatus() {
//        Subscription subscription = RetrofitHelper.getInstance().checkBandingStatus(mGatewayInfo.getId())
        Subscription subscription = SecondRetrofitHelper.getInstance().bindResult(mGatewayInfo.getId())
                .compose(RxUtil.<DataInfo<ServerDeviceInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ServerDeviceInfo>>() {
                    @Override
                    public void onError(Throwable e) {
//                        doFailed();
//                        BindGatewayFaildedActivity.toActivity(mActivity);
//                        finish();
                        continueCheck();
                    }

                    @Override
                    public void onNext(DataInfo<ServerDeviceInfo> dataInfo) {
                        if (dataInfo.success()) {
                            addDevice(dataInfo.data().device_no);
                        } else if (dataInfo.code() == -1001) {
                            showToast(dataInfo.msg());
                            finish();
                        } else {
//                            showToast(dataInfo.msg());
//                            BindGatewayFaildedActivity.toActivity(mActivity);
//                            finish();
                            continueCheck();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void continueCheck() {
        if (mValue == 0) {
            return;
        }
        Subscription subscription = Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        checkBandingStatus();
                    }
                });

        addSubscrebe(subscription);
    }

    //step 3
    private void addDevice(final String server_device_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance()
                .addDevice(
                        mRoomId,
                        server_device_id,
                        mMachineInfo.id,
                        mMachineInfo.name)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        BindGatewayFaildedActivity.toActivity(mActivity);
                        finish();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new RefreshDeviceEvent());
                            if (isFirst)
                                RxBus.getDefault().post(new BindDevSuccessEvent());
                            BindDevSuccessActivity.toActivity(mActivity, mRoomId, is_Install);
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
