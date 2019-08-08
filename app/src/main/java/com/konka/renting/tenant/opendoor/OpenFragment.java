package com.konka.renting.tenant.opendoor;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.ContractBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.PaySeverActivity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.SharedPreferenceUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.ChooseDevicePicker;
import com.konka.renting.widget.KeyPwdPopup;
import com.konka.renting.widget.UploadFilePopupwindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class OpenFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.frame_open_ll)
    LinearLayout linearLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    ImageView tvRight;
    @BindView(R.id.frame_open_refresh)
    SmartRefreshLayout mRefresh;
    @BindView(R.id.fragment_open_img_empty)
    ImageView imgEmpty;
    @BindView(R.id.fragment_open_tv_name)
    TextView tvName;
    @BindView(R.id.fragment_open_img_type)
    ImageView imgType;
    @BindView(R.id.frame_open_btn_open)
    Button btnOpen;
    @BindView(R.id.frame_open_tv_serverTime)
    TextView tvServerTime;
    @BindView(R.id.frame_open_rl_renew)
    RelativeLayout rlRenew;
    @BindView(R.id.fragment_open_rl_open)
    RelativeLayout rlOpen;
    @BindView(R.id.frame_open_tv_renttime)
    TextView mTvRenttime;
    @BindView(R.id.frame_open_ll_renttime)
    LinearLayout llRenttime;
    @BindView(R.id.frame_open_tv_endtime)
    TextView mTvEndtime;
    @BindView(R.id.frame_open_ll_endtime)
    LinearLayout llEndtime;
    @BindView(R.id.fragment_open_rl_rent_long)
    RelativeLayout rlRentLong;
    @BindView(R.id.fragment_open_rl_rent_short)
    RelativeLayout rlRentShort;
    @BindView(R.id.frame_open_tv_long_other_setting)
    TextView tvLongOtherSetting;
    @BindView(R.id.frame_open_tv_short_other_setting)
    TextView tvShortOtherSetting;

    private final int REFRESH = 1;
    private final int LOADMORE = 2;

    private final String KEY_ORDER_ID = "key_open_order_id";
    private final int CAN_OPEN_TIME = 15;

    private int queryTime = 0;
    private int page;
    private String mSelId = "";
    private int current;
    private int canOpenTime = CAN_OPEN_TIME;
    private List<OpenDoorListbean> mData = new ArrayList<>();

    private UploadFilePopupwindow uploadFilePopupwindow;
    private OpenDoorListPopupwindow openDoorListPopupwindow;
    private ShowOpenPwdPopupwindow showOpenPwdPopupwindow;
    private KeyPwdPopup keyPwdPopupwindow;
    private ChooseDevicePicker<OpenDoorListbean> OpenDoorListPicker;

    public static OpenFragment newInstance() {
        OpenFragment fragment = new OpenFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        ViewGroup viewGroup = (ViewGroup) tvTitle.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());

        return view;
    }

    @Override
    public void init() {
        super.init();
        tvRight.setVisibility(View.GONE);
        mSelId = SharedPreferenceUtil.getString(getActivity(), KEY_ORDER_ID);
        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(REFRESH);
            }
        });


        addRxBusSubscribe(UploadFileEvent.class, new Action1<UploadFileEvent>() {
            @Override
            public void call(UploadFileEvent uploadFileEvent) {
                UploadFileActivity.toActivity(getContext(), mData.get(current).getOrder_no());
            }
        });
        addRxBusSubscribe(OpenDeviceEvent.class, new Action1<OpenDeviceEvent>() {
            @Override
            public void call(OpenDeviceEvent openDeviceEvent) {
                mSelId = mData.get(openDeviceEvent.current).getOrder_id();
                SharedPreferenceUtil.setString(getActivity(), KEY_ORDER_ID, mSelId);
                refreshData();
            }
        });
        addRxBusSubscribe(SetPwdEvent.class, new Action1<SetPwdEvent>() {
            @Override
            public void call(SetPwdEvent setPwdEvent) {
//                if (setPwdEvent.getType() == SetPwdEvent.Type.CHECK)
//                    checkPwdAuth(setPwdEvent.getPwd());
            }
        });
        addRxBusSubscribe(TentantOpenDoorEvent.class, new Action1<TentantOpenDoorEvent>() {
            @Override
            public void call(TentantOpenDoorEvent tentantOpenDoorEvent) {
                if (tentantOpenDoorEvent.isUpdata()) {
                    mRefresh.autoRefresh();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(REFRESH);
    }

    /**
     * 获取门锁列表数据
     */
    private void initData(final int refreshType) {
        if (refreshType == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = SecondRetrofitHelper.getInstance().getOpenRoomList()
                .compose(RxUtil.<DataInfo<List<OpenDoorListbean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<OpenDoorListbean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (refreshType == REFRESH)
                            mRefresh.finishRefresh();
                        else
                            mRefresh.finishLoadmore();
                    }

                    @Override
                    public void onNext(DataInfo<List<OpenDoorListbean>> payOrderDataInfo) {
                        if (refreshType == REFRESH) {
                            mRefresh.finishRefresh();
                        } else
                            mRefresh.finishLoadmore();
                        if (payOrderDataInfo.success()) {
                            if (refreshType == REFRESH)
                                mData.clear();
                            mData.addAll(payOrderDataInfo.data());
                            refreshData();
                        } else {
                            showToast(payOrderDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 数据更新
     */
    private void refreshData() {
        if (mData.size() > 0) {
            if (OpenDoorListPicker != null) {
                OpenDoorListPicker.setItems(mData);
            }
            tvRight.setVisibility(View.VISIBLE);
            imgEmpty.setVisibility(View.GONE);
            rlOpen.setVisibility(View.VISIBLE);
            int size = mData.size();
            current = 0;
            for (int i = 0; i < size; i++) {
                OpenDoorListbean bean = mData.get(i);
                if (bean.getOrder_id().equals(mSelId)) {
                    current = i;
                    break;
                }
            }
            OpenDoorListbean listbean = mData.get(current);
            tvName.setText(listbean.getRoom_name());
            imgType.setImageResource(listbean.getStatus() == 2 ? R.mipmap.opendoor_notcheckin_icon : R.mipmap.opendoor_checkin_icon);
            tvServerTime.setText(listbean.getService_time());
            mTvRenttime.setText(listbean.getStart_time());
            mTvEndtime.setText(listbean.getEnd_time());
            if (listbean.getType() == 1) {
                rlRentLong.setVisibility(View.GONE);
                rlRentShort.setVisibility(View.VISIBLE);
            } else if (listbean.getType() == 2) {
                rlRentLong.setVisibility(View.VISIBLE);
                rlRentShort.setVisibility(View.GONE);
            }
        } else {
            tvRight.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.VISIBLE);
            rlOpen.setVisibility(View.GONE);
        }
    }


    private void checkUploadContract() {
        if (mData.size() <= 0)
            return;
        if (mData.get(current).getType() == 1)
            return;
        Subscription subscription = RetrofitHelper.getInstance().getContract(mData.get(current).getOrder_no())
                .compose(RxUtil.<DataInfo<ContractBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ContractBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<ContractBean> dataInfo) {
                        if (dataInfo.success()) {
                            if (dataInfo.data() == null)
                                showUploadPopwindow();
                            else if (dataInfo.data().getStatus().equals("2"))
                                showUploadPopwindow();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void showUploadPopwindow() {
        // 开启 popup 时界面透明
        uploadFilePopupwindow = new UploadFilePopupwindow(getContext());
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        uploadFilePopupwindow.showAtLocation(linearLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        uploadFilePopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    private void showDevicesPopwindow() {
        // 开启 popup 时界面透明
        if (openDoorListPopupwindow == null) {
            openDoorListPopupwindow = new OpenDoorListPopupwindow(getContext());
        }
        openDoorListPopupwindow.setDoorListbeans(mData, current);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openDoorListPopupwindow.showAtLocation(getActivity().findViewById(R.id.main_tenant_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        openDoorListPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 展示切换门锁列表弹窗
     */
    private void chooseDevicesDialog() {
        if (OpenDoorListPicker == null) {
            OpenDoorListPicker = new ChooseDevicePicker<OpenDoorListbean>(getActivity(), mData, new ChooseDevicePicker.ToGetItemStringCall<OpenDoorListbean>() {
                @Override
                public String getItemString(OpenDoorListbean item) {
                    return item.getRoom_name();
                }
            });
            OpenDoorListPicker.setCanLoop(false);//不禁用循环
            OpenDoorListPicker.setTopBackgroundColor(0xFFFFFFFF);
            OpenDoorListPicker.setTopHeight(60);
            OpenDoorListPicker.setTopPadding(20);
            OpenDoorListPicker.setTopLineColor(0x55999999);
            OpenDoorListPicker.setTopLineHeight(1);
            OpenDoorListPicker.setTitleText(getString(R.string.please_choose_device));
            OpenDoorListPicker.setTitleTextColor(0xFF333333);
            OpenDoorListPicker.setTitleTextSize(16);
            OpenDoorListPicker.setCancelVisible(false);
            OpenDoorListPicker.setSubmitTextColor(0xFFFF4707);
            OpenDoorListPicker.setSubmitTextSize(16);
            OpenDoorListPicker.setSelectedTextColor(0xFF333333);
            OpenDoorListPicker.setUnSelectedTextColor(0xFFE5E5E5);
            OpenDoorListPicker.setWheelModeEnable(false);
            LineConfig config = new LineConfig();
            config.setColor(0xFFEFEFEF);//线颜色
            config.setAlpha(120);//线透明度
            config.setVisible(true);
//        config.setRatio(1);//线比率
            OpenDoorListPicker.setLineConfig(config);
            OpenDoorListPicker.setItemWidth(getResources().getDisplayMetrics().widthPixels);
            OpenDoorListPicker.setBackgroundColor(0xFFFFFFFF);
            OpenDoorListPicker.setTextSize(16);
            OpenDoorListPicker.setSelectedIndex(current);
            OpenDoorListPicker.setOnItemPickListener(new OnItemPickListener<OpenDoorListbean>() {
                @Override
                public void onItemPicked(int index, OpenDoorListbean item) {
                    mSelId = item.getOrder_id();
                    SharedPreferenceUtil.setString(getActivity(), KEY_ORDER_ID, mSelId);
                    refreshData();
                }
            });
        }
        OpenDoorListPicker.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_right, R.id.frame_open_btn_open, R.id.frame_open_rl_renew,
            R.id.frame_open_tv_long_other_setting, R.id.frame_open_tv_short_other_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
//                showDevicesPopwindow();
                chooseDevicesDialog();
                break;
            case R.id.frame_open_btn_open://开门
                if (mData.get(current).getStatus() > 2 && !mData.get(current).getDevice_id().equals("")) {
                    openDoor();
                } else if (mData.get(current).getStatus() > 2) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_on_rent));
                }
                break;
            case R.id.frame_open_rl_renew://续交服务费
                OpenDoorListbean listbean = mData.get(current);
                if (listbean.getIs_install()==1){
                    PaySeverActivity.toActivity(getContext(), listbean.getRoom_id(), listbean.getRoom_name(), listbean.getService_time(), 2);
                }else{
                    showToast(getString(R.string.please_talk_pay_install));
                }
                break;
//            case R.id.frame_open_tv_pwd://长租设置门锁开锁密码
//                if (mData.get(current).getStatus() > 2 && !mData.get(current).getDevice_id().equals("")) {
//                    DevicesOpenPasswordActivity.toActivity(getActivity(), mData.get(current).getDevice_id());
//                } else if (mData.get(current).getStatus() > 2) {
//                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
//                } else {
//                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_on_rent));
//                }
//                break;
//            case R.id.frame_open_tv_check_pwd://短租查看门锁开锁密码
//                if (mData.get(current).getStatus() > 2 && !mData.get(current).getDevice_id().equals("")) {
//                    checkOpenPwd();//短租查看密码
//                } else if (mData.get(current).getStatus() > 2) {
//                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
//                } else {
//                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_on_rent));
//                }
//                break;
            case R.id.frame_open_tv_long_other_setting://长租其他功能
                OtherSettingActivity.toActivity(getContext(), 0, mData.get(current));
                break;
            case R.id.frame_open_tv_short_other_setting://短租其他功能
                OtherSettingActivity.toActivity(getContext(), 1, mData.get(current));
                break;
        }
    }


    private void checkPwd() {
        // 开启 popup 时界面透明
        SetPwdPopup reviseNeeknamePopup = new SetPwdPopup(mActivity, SetPwdEvent.Type.CHECK);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        // popupwindow 第一个参数指定popup 显示页面
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        reviseNeeknamePopup.showAtLocation(linearLayout, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
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

    private void checkPwdAuth(String pwd) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().checkPwd("1", pwd)
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
                            openDoor();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 开门
     */
    private void openDoor() {
        btnOpen.setClickable(false);
        AnimationDrawable animation = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_open_door_botton);
        btnOpen.setBackground(animation);
        animation.start();
        OpenDoorListbean listbean = mData.get(current);
        canOpenTime = CAN_OPEN_TIME;
        controlOpenTime();
        Subscription subscription = SecondRetrofitHelper.getInstance().openDoor(listbean.getRoom_id(), listbean.getGateway_id(), listbean.getDevice_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (!dataInfo.success()) {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /*************************************************倒计时处理**************************************************************/
    private void controlOpenTime() {
        if (canOpenTime <= 0) {
            btnOpen.setClickable(true);
            btnOpen.setBackground(getResources().getDrawable(R.drawable.open_button_animation_1));
            return;
        }
        Subscription subscription = Observable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        canOpenTime--;
                        controlOpenTime();
                    }
                });
        addSubscrebe(subscription);
    }

}
