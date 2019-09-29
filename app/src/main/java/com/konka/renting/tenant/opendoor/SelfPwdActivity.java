package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.DevicesOpenPasswordBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SelfPwdActivity extends BaseActivity {
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
    @BindView(R.id.activity_self_pwd_img_add)
    ImageView mImgAdd;
    @BindView(R.id.activity_self_pwd_tv_tips)
    TextView mTvTips;
    @BindView(R.id.activity_self_pwd_rv)
    RecyclerView mRv;

    final String KEY_DEVICE_ID = "device_id";
    final String KEY_ROOM_ID = "room_id";
    final int QUERY_TIME_MAX = 10;

    int queryTime = QUERY_TIME_MAX;

    String deviceId;
    String room_id;

    List<DevicesOpenPasswordBean> mData = new ArrayList<>();
    CommonAdapter<DevicesOpenPasswordBean> adapter;
    CommonPopupWindow commonPopupWindow;

    public static void toActivity(Context context, String device_id, String room_id) {
        Intent intent = new Intent(context, SelfPwdActivity.class);
        intent.putExtra("device_id", device_id);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_self_pwd;
    }

    @Override
    public void init() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        room_id = getIntent().getStringExtra(KEY_ROOM_ID);

        adapter=new CommonAdapter<DevicesOpenPasswordBean>(this, mData, R.layout.adapter_device_pwd_item) {
            @Override
            public void convert(ViewHolder viewHolder, DevicesOpenPasswordBean bean) {
                SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = "";
                if (bean.getEnd_time().equals("0"))
                    time = getString(R.string.forever);
                else {
                    time = bean.getEnd_time();
//                    try {
//                        Date beginTime = CurrentTime.parse(bean.getStart_time());
//                        Date endTime = CurrentTime.parse(bean.getEnd_time());
//                        float days = (float) (endTime.getTime() - beginTime.getTime()) / (24 * 60 * 60 * 1000);
//                        time = days < 1 ? (endTime.getTime() - beginTime.getTime()) / (60 * 60 * 1000) + "小时" : (int) days + "天";
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                }
                viewHolder.setText(R.id.adapter_device_item_tv_pwd, bean.getPassword());
                viewHolder.setText(R.id.item_device_pwd_tv_time, time);
                viewHolder.setOnClickListener(R.id.item_device_pwd_tv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDelPwdPopup(bean);
                    }
                });
            }
        };

        addRxBusSubscribe(DeviceOpenPwdListEvent.class, new Action1<DeviceOpenPwdListEvent>() {
            @Override
            public void call(DeviceOpenPwdListEvent deviceOpenPwdListEvent) {
                refresh();
            }
        });

    }


    @OnClick({R.id.iv_back, R.id.activity_self_pwd_img_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_self_pwd_img_add:
                if (mData.size() < 7) {
                    AddDevicesOpenPwdActivity.toActivity(this, deviceId, room_id);
                } else {
                    showToast(R.string.warm_no_to_add_pwd_more);
                }
                break;
        }
    }

    private void queryPwdTimer(long delay, DevicesOpenPasswordBean bean) {
        if (queryTime <= 0) {
            dismiss();
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
                        queryPasswordResult(bean);
                    }
                });
        addSubscrebe(subscription);
    }


    /************************************************接口*************************************************/
    private void refresh() {
        Subscription subscription = SecondRetrofitHelper.getInstance().passwordList(deviceId)
                .compose(RxUtil.<DataInfo<List<DevicesOpenPasswordBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<DevicesOpenPasswordBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<List<DevicesOpenPasswordBean>> payOrderDataInfo) {
                        if (payOrderDataInfo.success()) {
                            mData.clear();
                            if (payOrderDataInfo.data() != null)
                                mData.addAll(payOrderDataInfo.data());
                            if (mData.size() > 0) {
                                mTvTips.setVisibility(View.GONE);
                                mRv.setVisibility(View.VISIBLE);
                            } else {
                                mTvTips.setVisibility(View.VISIBLE);
                                mRv.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showToast(payOrderDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void delDevicePassword(DevicesOpenPasswordBean bean) {
        showLoadingDialog();
        mProgressDialog.setCanceledOnTouchOutside(false);
        Subscription subscription = SecondRetrofitHelper.getInstance().deletePassword(deviceId, bean.getId())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo payOrderDataInfo) {
                        if (payOrderDataInfo.success()) {
                            queryTime = QUERY_TIME_MAX;
                            queryPwdTimer(5, bean);
                        } else {
                            dismiss();
                            showToast(payOrderDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);

    }

    private void queryPasswordResult(DevicesOpenPasswordBean bean) {
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(bean.getId())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        queryPwdTimer(1, bean);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            queryPwdTimer(1, bean);
                        } else {
                            dismiss();
                            mData.remove(bean);
                            if (mData.size() <= 0) {
                                mTvTips.setVisibility(View.VISIBLE);
                                mRv.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            refresh();
                        }

                    }
                });
        addSubscrebe(subscription);
    }


/******************************************************************************************************************/


    /**
     * 删除密码
     */
    private void showDelPwdPopup(DevicesOpenPasswordBean bean) {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.open_tips_del_pwd))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPopupWindow.dismiss();
                        delDevicePassword(bean);
                    }
                })
                .create();
        showPopup(commonPopupWindow);

    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) linTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
