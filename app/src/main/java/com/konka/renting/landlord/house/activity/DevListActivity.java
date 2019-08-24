package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.DeviceInfo;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.event.BindDevSuccessEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.BindGatewayActivity;
import com.konka.renting.landlord.gateway.DeviceListActivity;
import com.konka.renting.landlord.gateway.GatewayConnectionActivity;
import com.konka.renting.landlord.gateway.GatewayListActivity;
import com.konka.renting.landlord.gateway.GatewaySettingActivity;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class DevListActivity extends BaseActivity {
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
    @BindView(R.id.activity_dev_list_btn_add_gateway)
    Button mBtnAddGateway;
    @BindView(R.id.activity_dev_list_rl_add_gateway)
    RelativeLayout mRlAddGateway;
    @BindView(R.id.activity_dev_list_tv_gateway_pic)
    ImageView mImgGatewayPic;
    @BindView(R.id.activity_dev_list_tv_gateway_name)
    TextView mTvGatewayName;
    @BindView(R.id.activity_dev_list_tv_gateway_type)
    TextView mTvGatewayType;
    @BindView(R.id.activity_dev_list_btn_gateway_manage)
    Button mBtnGatewayManage;
    @BindView(R.id.activity_dev_list_rl_gateway)
    RelativeLayout mRlGateway;
    @BindView(R.id.activity_dev_list_btn_add_dev)
    Button mBtnAddDev;
    @BindView(R.id.activity_dev_list_rl_add_dev)
    RelativeLayout mRlAddDev;
    @BindView(R.id.activity_dev_list_tv_dev_pic)
    ImageView mImgDevPic;
    @BindView(R.id.activity_dev_list_tv_dev_name)
    TextView mTvDevName;
    @BindView(R.id.activity_dev_list_tv_dev_type)
    TextView mTvDevType;
    @BindView(R.id.activity_dev_list_tv_dev_time)
    TextView mTvDevTime;
    @BindView(R.id.activity_dev_list_rl_dev)
    RelativeLayout mRlDev;

    String mRoomId;
    int status;// 0默认 1未入住 2已入住
    DeviceInfo mDev;
    GatewayInfo mGateway;
    boolean is_Install;
    boolean isFirst;

    public static void toActivity(Context context, String room_id, int status, boolean is_Install, boolean isFirst) {
        Intent intent = new Intent(context, DevListActivity.class);
//        intent.putExtra("roomInfo", roomInfo);
        intent.putExtra("room_id", room_id);
        intent.putExtra("status", status);
        intent.putExtra("is_Install", is_Install);
        intent.putExtra("isFirst", isFirst);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dev_list;
    }

    @Override
    public void init() {
        setTitleText(R.string.bind_dev);

        mRoomId = getIntent().getStringExtra("room_id");
        status = getIntent().getIntExtra("status", 0);

        is_Install = getIntent().getBooleanExtra("is_Install", false);
        isFirst = getIntent().getBooleanExtra("isFirst", false);

        addRxBusSubscribe(BindDevSuccessEvent.class, new Action1<BindDevSuccessEvent>() {
            @Override
            public void call(BindDevSuccessEvent bindDevSuccessEvent) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoadingDialog();
        getGatewayList();
        getDevData();
    }

    @OnClick({R.id.iv_back, R.id.activity_dev_list_btn_add_gateway, R.id.activity_dev_list_btn_gateway_manage, R.id.activity_dev_list_btn_add_dev, R.id.activity_dev_list_rl_dev})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_dev_list_btn_add_gateway://添加网关
                BindGatewayActivity.toActivity(this, mRoomId);
                break;
            case R.id.activity_dev_list_btn_gateway_manage://网关管理
                if (mGateway != null) {
                    GatewaySettingActivity.toActivity(this, mRoomId, mGateway.getId(), mGateway.getGateway_version(), GatewaySettingActivity.TYPE_LANDLORD);
                }
                break;
            case R.id.activity_dev_list_btn_add_dev://添加设备
                if (mGateway == null) {
                    showToast(R.string.please_add_gateway);
                } else {
                    MachineInfo mMachineInfo = new MachineInfo();
                    mMachineInfo.id = "";
                    mMachineInfo.name = "";
                    GatewayConnectionActivity.toActivity(mActivity, mGateway, mMachineInfo, mRoomId, is_Install, isFirst);
                }
                break;
            case R.id.activity_dev_list_rl_dev:
                break;
        }
    }

    /**************************************************接口***************************************************/
    private void getDevData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getDeviceList(mRoomId, "1")
                .compose(RxUtil.<DataInfo<PageDataBean<DeviceInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<DeviceInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<DeviceInfo>> listDataInfo) {
                        dismiss();
                        if (listDataInfo.success()) {
                            if (listDataInfo.data().getList().size() > 0) {
                                mDev = (DeviceInfo) listDataInfo.data().getList().get(0);
                                mRlAddDev.setVisibility(View.GONE);
                                mRlDev.setVisibility(View.VISIBLE);
                                mTvDevTime.setText(mDev.time);
                                mTvDevName.setText(mDev.name);
                                mBtnGatewayManage.setVisibility(status ==2 ? View.GONE : View.VISIBLE);
                            } else {
                                mDev = null;
                                mRlAddDev.setVisibility(View.VISIBLE);
                                mRlDev.setVisibility(View.GONE);
                                mBtnGatewayManage.setVisibility( View.VISIBLE);
                            }
                        } else {
                            showToast(listDataInfo.msg());
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    private void getGatewayList() {
        Subscription subscription = SecondRetrofitHelper.getInstance().gatewayList(mRoomId)
                .compose(RxUtil.<DataInfo<List<GatewayInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<GatewayInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<List<GatewayInfo>> listInfoDataInfo) {
                        if (listInfoDataInfo.success()) {
                            if (listInfoDataInfo.data().size() > 0) {
                                mGateway = listInfoDataInfo.data().get(0);
                                mRlAddGateway.setVisibility(View.GONE);
                                mRlGateway.setVisibility(View.VISIBLE);
                                switch (mGateway.getNetwork()) {
                                    case 1:
                                        mTvGatewayType.setText(R.string.gateway_conn_2G);
                                        break;
                                    case 2:
                                        mTvGatewayType.setText(R.string.gateway_conn_WiFi);
                                        break;
                                    case 3:
                                        mTvGatewayType.setText(R.string.gateway_conn_Ethernet);
                                        break;
                                }
                                int gateway_version;
                                try {
                                    gateway_version = Integer.valueOf(mGateway.getGateway_version());
                                } catch (Exception e) {
                                    gateway_version = 2;
                                }
                                mImgGatewayPic.setImageResource(gateway_version < 4 ? R.mipmap.wangguan_1_72px_png : R.mipmap.wangguan_2_72px_png);
                                mTvGatewayName.setText(mGateway.getGateway_name());
                                mBtnGatewayManage.setVisibility(status ==2 && mDev != null ? View.GONE : View.VISIBLE);
                            } else {
                                mGateway = null;
                                mRlAddGateway.setVisibility(View.VISIBLE);
                                mRlGateway.setVisibility(View.GONE);
                            }
                        } else {
                            showToast(listInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
