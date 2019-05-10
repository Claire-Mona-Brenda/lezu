package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.DeviceInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.event.RefreshDeviceEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.opendoor.OpeningFailerPopupwindow;
import com.konka.renting.tenant.opendoor.OpeningPopupwindow;
import com.konka.renting.tenant.opendoor.OpeningSuccessPopupwindow;
import com.konka.renting.utils.CircleTransform;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class DeviceListActivity extends BaseActivity {


    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    RefreshLayout mRefreshLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    private List<DeviceInfo> mDeviceInfos;

    //第几页
    private int page=1;

    private CommonAdapter<DeviceInfo> mDeviceInfoCommonAdapter;

    String mRoomId;
    int status;//	房屋状态 1未缴纳安装费 2待安装认证 3待发布 4已发布 5已出租

    private OpeningPopupwindow openingPopupwindow;

    private OpeningSuccessPopupwindow openSuccess;
    private OpeningFailerPopupwindow openingFailerPopupwindow;
//    private RoomInfo mRoomInfo;

    public static void toActivity(Context context, String room_id, int status) {
        Intent intent = new Intent(context, DeviceListActivity.class);
//        intent.putExtra("roomInfo", roomInfo);
        intent.putExtra("room_id", room_id);
        intent.putExtra("status", status);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_list;
    }

    @Override
    public void init() {
        setTitleText(R.string.device_list_title);
        ivRight.setImageResource(R.mipmap.device_icon_add);
        ivRight.setVisibility(View.VISIBLE);
        mRoomId = getIntent().getStringExtra("room_id");
        status = getIntent().getIntExtra("status", 0);


        mDeviceInfos = new ArrayList<>();
        // getUserInfo();
        mDeviceInfoCommonAdapter = new CommonAdapter<DeviceInfo>(this, mDeviceInfos, R.layout.item_device) {
            @Override
            public void convert(ViewHolder viewHolder, final DeviceInfo deviceInfo, final int i) {
                viewHolder.setText(R.id.tv_name, deviceInfo.name);
                viewHolder.setText(R.id.tv_device_name, deviceInfo.type_name);
                viewHolder.setText(R.id.tv_time, deviceInfo.time);
                viewHolder.setSelected(R.id.tv_status, !deviceInfo.status());

//                viewHolder.setVisible(R.id.tv_open_door, status!=5&&deviceInfo.type_name.equals("门锁"));
                viewHolder.setVisible(R.id.tv_open_door, false);
                viewHolder.setOnClickListener(R.id.tv_open_door, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDoorFUnction(mRoomId, deviceInfo.id, deviceInfo.gateway.getId());
                    }
                });

//                viewHolder.setText(R.id.tv_status, deviceInfo.statusText());

//                viewHolder.setText(R.id.tv_location, deviceInfo.device_address);
                ImageView imageView = viewHolder.getView(R.id.iv_icon);

                final SwipeMenuLayout swipeMenuLayout = viewHolder.getView(R.id.swipemenulayout);
                swipeMenuLayout.smoothClose();
//                if (status < 3)
//                    swipeMenuLayout.setSwipeEnable(true);
//                else
                    swipeMenuLayout.setSwipeEnable(false);
                viewHolder.setOnClickListener(R.id.btn_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDevice(i);
                    }
                });
                if (!TextUtils.isEmpty(deviceInfo.image))
                    Picasso.get().load(deviceInfo.image).transform(new CircleTransform()).into(imageView);
            }
        };

        addRxBusSubscribe(RefreshDeviceEvent.class, new Action1<RefreshDeviceEvent>() {
            @Override
            public void call(RefreshDeviceEvent refreshDeviceEvent) {
                mRefreshLayout.autoRefresh();
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshlayout) {
                refresh(true);
            }
        });
//        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                refresh(false);
//            }
//        });
        mRefreshLayout.setEnableLoadmore(false);

        mListview.setAdapter(mDeviceInfoCommonAdapter);
        mRefreshLayout.autoRefresh();

    }

    private void openDoorFUnction(String room_id, String gateway_id, String device_id) {

        showOpening();
        Subscription subscription = SecondRetrofitHelper.getInstance().openDoor(room_id, gateway_id, device_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openingPopupwindow.dismiss();
//                                showFailer();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openingPopupwindow.dismiss();
//                                    showFailer();
                            }
                        }, 5000);
                    }
                });
        addSubscrebe(subscription);
    }

    private void showSuccess() {
        // 开启 popup 时界面透明
        openSuccess = new OpeningSuccessPopupwindow(this);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openSuccess.showAtLocation(mActivity.findViewById(R.id.layout_device), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        openSuccess.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

    private void showFailer() {
        // 开启 popup 时界面透明
        openingFailerPopupwindow = new OpeningFailerPopupwindow(this);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openingFailerPopupwindow.showAtLocation(mActivity.findViewById(R.id.layout_device), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        openingFailerPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

    private void showOpening() {
        // 开启 popup 时界面透明
        openingPopupwindow = new OpeningPopupwindow(this);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openingPopupwindow.showAtLocation(mActivity.findViewById(R.id.layout_device), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        openingPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

    private void deleteDevice(final int position) {
        showLoadingDialog();

        Subscription subscription = RetrofitHelper.getInstance().deleteDevice(mDeviceInfos.get(position).id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        dismiss();
                        if (info.success()) {
                            doSuccess();
                            mDeviceInfos.remove(position);
                            mDeviceInfoCommonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void refresh(final boolean isRefresh) {

        Subscription subscription = SecondRetrofitHelper.getInstance().getDeviceList(mRoomId,page+"")
                .compose(RxUtil.<DataInfo<PageDataBean<DeviceInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<DeviceInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                        processResult(false, isRefresh);
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<DeviceInfo>> listDataInfo) {
                        processResult(isRefresh, listDataInfo.success());
                        if (listDataInfo.success()) {
                            mDeviceInfos.clear();
                            mDeviceInfos.addAll(listDataInfo.data().getList());
                            mDeviceInfoCommonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(listDataInfo.msg());
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    private void processResult(boolean success, boolean isRefresh) {
//        if (isRefresh) {
//            mRefreshLayout.setEnableLoadmore(true);
//            mRefreshLayout.finishRefresh(1000, success);
//        } else {
//            mRefreshLayout.finishLoadmore(1000, success);
//        }

        mRefreshLayout.finishRefresh(200, success);
    }

    private void processResult(boolean isRefresh, DataInfo<ListInfo<DeviceInfo>> dataInfo) {

//        processResult(dataInfo.success(), isRefresh);
//
//        if (isRefresh)
//            mDeviceInfos.clear();
//
//        if (dataInfo.data().size < KangJiaApplication.DEFAULT_LOAD_SIZE) {
//            mRefreshLayout.setEnableLoadmore(false);
//        }

    }


    @OnClick({R.id.iv_back, R.id.iv_right, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                ChoiceMachineActivity.toActivity(this, mRoomId,status);
//                GatewayListActivity.toActivity(mActivity, mRoomId);
                break;
            case R.id.btn_add:
                ChoiceMachineActivity.toActivity(this, mRoomId,status);
//                GatewayListActivity.toActivity(mActivity, mRoomId);
                break;
        }
    }

}
