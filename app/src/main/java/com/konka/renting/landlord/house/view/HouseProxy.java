package com.konka.renting.landlord.house.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseOrderInfoBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.event.LandlordHouseListEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.HouseAddActivity;
import com.konka.renting.landlord.house.HouseContract;
import com.konka.renting.landlord.house.HouseInfoActivity;
import com.konka.renting.landlord.house.IHouseRefresh;
import com.konka.renting.landlord.house.activity.AddHouseAddressActivity;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.tenant.opendoor.OpeningFailerPopupwindow;
import com.konka.renting.tenant.opendoor.OpeningPopupwindow;
import com.konka.renting.tenant.opendoor.OpeningSuccessPopupwindow;
import com.konka.renting.tenant.opendoor.SetPwdEvent;
import com.konka.renting.tenant.opendoor.SetPwdPopup;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class HouseProxy implements HouseContract.IMissionView, HouseAdapter.MissionitemClick, HouseContract.IMissionData, OnClickListener, OnLoadmoreListener, OnRefreshListener, IHouseRefresh {
    HouseAdapter missionadapter;
    List<HouseOrderInfoBean> list = new ArrayList<>();
    String taskinfonum;
    RecyclerView recyclerview;
    HouseContract.IMissionView imissionview;
    String level2num;
    RelativeLayout re;
    String cnum;
    String collectywbfpxtm;
    int index;
    Class cur_cls = HouseProxy.class;
    Context context;
    Button addHouse;
    TextView button_add;
    SmartRefreshLayout refresh;
    private CompositeSubscription mCompositeSubscription;
    LinearLayout gop;
    private ProgressDialog mProgressDialog;
    private OpeningFailerPopupwindow openingFailerPopupwindow;
    private String mGatewayId;
    private String mDeviceId;
    private OpeningPopupwindow openingPopupwindow;
    private OpeningSuccessPopupwindow openSuccess;
    private Activity mActivity;

    int offset = 1;

    public void init(View view) {
        // TODO Auto-generated method stub
        context = view.getContext();
        refresh = view.findViewById(R.id.refresh);
        gop = view.findViewById(R.id.gop);
        re = (RelativeLayout) view.findViewById(R.id.title_bg);
        addHouse = (Button) view.findViewById(R.id.add_house);
        button_add = view.findViewById(R.id.button_add);
        recyclerview = (RecyclerView) view.findViewById(R.id.listView1);
        LinearLayoutManager lm = new LinearLayoutManager(view.getContext());
        lm.setOrientation(RecyclerView.VERTICAL);
        recyclerview.setLayoutManager(lm);
        context = recyclerview.getContext();
        refresh.setReboundDuration(100);
        refresh.requestFocus(100);
        refresh.setOnLoadmoreListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setEnableLoadmore(true);
        missionadapter = new HouseAdapter(context, list);
        missionadapter.setiHouseRefresh(HouseProxy.this);
        missionadapter.setItemClickListener(HouseProxy.this);
        recyclerview.setAdapter(missionadapter);
        addHouse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                FaceDectacUtil.faceDectac(context, FaceDectacUtil.FACETYPE.LANDLORD, new FaceDectacUtil.StatusSuccess() {
//                    @Override
//                    public void success() {
//                        Intent intent = new Intent(context, HouseAddActivity.class);
//                        context.startActivity(intent);
//                    }
//                });
                if (LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
//                    Intent intent = new Intent(context, HouseAddActivity.class);
//                    context.startActivity(intent);
                    AddHouseAddressActivity.toActivity(mActivity);
                } else {
                    NewFaceDectectActivity.toActivity(context, 1);
                }

            }
        });
        button_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
//                    Intent intent = new Intent(context, HouseAddActivity.class);
//                    context.startActivity(intent);
                    AddHouseAddressActivity.toActivity(mActivity);
                } else {
                    NewFaceDectectActivity.toActivity(context, 1);
                }


            }
        });


//        RxBus.getDefault().toDefaultObservable(SetPwdEvent.class, new Action1<SetPwdEvent>() {
//            @Override
//            public void call(SetPwdEvent setPwdEvent) {
//                if (setPwdEvent.getType() == SetPwdEvent.Type.CHECK)
//                    checkPwdAuth(setPwdEvent.getPwd());
//            }
//        });
//        getRefreshData();

                RxBus.getDefault().toDefaultObservable(LandlordHouseListEvent.class, new Action1<LandlordHouseListEvent>() {
            @Override
            public void call(LandlordHouseListEvent event) {
                if (event.isUpdata())
                    refresh.autoRefresh();
            }
        });
        getRoomList();

    }

    private void getRoomList() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().getRoomList2(offset + "")
                .compose(RxUtil.<DataInfo<PageDataBean<HouseOrderInfoBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<HouseOrderInfoBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (offset>1){
                            offset--;
                        }
                        if (refresh.isRefreshing())
                            refresh.finishRefresh();
                        if (refresh.isLoading())
                            refresh.finishLoadmore();
                    }


                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<PageDataBean<HouseOrderInfoBean>> dataInfo) {
                        if (refresh.isRefreshing())
                            refresh.finishRefresh();
                        if (refresh.isLoading())
                            refresh.finishLoadmore();
                        if (dataInfo.success()) {
                            if (offset == 1) {
                                list.clear();
                            }else if (dataInfo.data().getTotalPage()<dataInfo.data().getPage()){
                                offset--;
                            }
                            list.addAll(dataInfo.data().getList());
                            missionadapter.notifyDataSetChanged();
                            if (missionadapter.getItemCount() < 1) {
                                gop.setVisibility(View.VISIBLE);
                                button_add.setVisibility(View.GONE);
                            } else {
                                gop.setVisibility(View.GONE);
                                button_add.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (offset>1){
                                offset--;
                            }
                            ShowToastUtil.showNormalToast(context,dataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void setDataAdapter(List<MissionEnity> list) {
        // TODO Auto-generated method stub
//        this.list = list;

    }


    @Override
    public void missionItemClick(int position) {
        // TODO Auto-generated method stub\
        index = position;
        HouseInfoActivity.toActivity(context, list.get(index).getRoom_id() + "");

    }

    @Override
    public void share(String roomid) {

//        sharePwd(roomid);
    }

    @Override
    public void openDoor(String room_id, String gatewayId, String deviceId) {

        this.mGatewayId = gatewayId;
        this.mDeviceId = deviceId;
        openDoorFUnction(room_id, gatewayId, deviceId);
    }

    @Override
    public void cancelPublic(String roomid) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().cancelPublishHouse(roomid)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            refresh.autoRefresh();
                        } else {
                            ShowToastUtil.showWarningToast(context, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void checkPwd() {
        // 开启 popup 时界面透明
        SetPwdPopup reviseNeeknamePopup = new SetPwdPopup(context, SetPwdEvent.Type.CHECK);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        // popupwindow 第一个参数指定popup 显示页面
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        reviseNeeknamePopup.showAtLocation(mActivity.findViewById(R.id.layout_opendoor), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        reviseNeeknamePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
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
                        }, 1000);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    openingPopupwindow.dismiss();
//                                    showFailer();
                                }
                            }, 1000);
                        } else {
                            openingPopupwindow.dismiss();
                            ShowToastUtil.showWarningToast(context, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void showOpening() {
        // 开启 popup 时界面透明
        openingPopupwindow = new OpeningPopupwindow(context);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openingPopupwindow.showAtLocation(mActivity.findViewById(R.id.layout_opendoor), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
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

    private void showSuccess() {
        // 开启 popup 时界面透明
        openSuccess = new OpeningSuccessPopupwindow(context);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openSuccess.showAtLocation(mActivity.findViewById(R.id.layout_opendoor), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
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
        openingFailerPopupwindow = new OpeningFailerPopupwindow(context);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openingFailerPopupwindow.showAtLocation(mActivity.findViewById(R.id.layout_opendoor), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
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


    @Override
    public List<MissionEnity> getMissionData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean saveData(Map<String, String> map) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
    }

    public void refreshDataSet() {

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
            offset++;
            getRoomList();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
//        getRefreshData();
        offset=1;
        getRoomList();
    }

    @Override
    public void houseRefresh(Object o) {
        offset=1;
        getRoomList();
    }

    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    public void dismiss() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
