package com.konka.renting.tenant.opendoor;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.event.AddCodeEvent;
import com.konka.renting.event.AddShareRentEvent;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.GatewaySettingActivity;
import com.konka.renting.landlord.gateway.ManagePwdActivity;
import com.konka.renting.landlord.house.PaySeverActivity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.tenant.opendoor.adapter.OpenItemAdapter;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.SharedPreferenceUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.KeyPwdPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class OpenNewFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.fragment_open_new_img_msg)
    ImageView mImgMsg;
    @BindView(R.id.fragment_open_new_img_add_order)
    ImageView mImgAddOrder;
    @BindView(R.id.fragment_open_new_rl_top_bar)
    RelativeLayout mRlTopBar;
    @BindView(R.id.fragment_open_new_tv_item_circle_left)
    TextView mTvItemCircleLeft;
    @BindView(R.id.fragment_open_new_tv_item_circle_concent)
    TextView mTvItemCircleConcent;
    @BindView(R.id.fragment_open_new_tv_item_circle_right)
    TextView mTvItemCircleRight;
    @BindView(R.id.fragment_open_new_tv_input_code)
    TextView mTvInputCode;
    @BindView(R.id.fragment_open_new_rl_empty)
    RelativeLayout mRlEmpty;
    @BindView(R.id.fragment_open_new_view_page_item)
    ViewPager mViewPageItem;
    @BindView(R.id.fragment_open_new_tv_server_pay)
    TextView mTvServerPay;
    @BindView(R.id.fragment_open_new_tv_server_end_time)
    TextView mTvServerEndTime;
    @BindView(R.id.fragment_open_new_img_server_end_time_tips)
    ImageView mImgServerEndTimeTips;
    @BindView(R.id.fragment_open_new_srl_refresh)
    SwipeRefreshLayout mSrlRefresh;
    @BindView(R.id.fragment_open_new_rl_item_circle)
    LinearLayout mRlItemCircle;
    @BindView(R.id.fragment_open_new_ll_server_end_time)
    LinearLayout mLlServerEndTime;

    private final String KEY_ORDER_ID = "key_open_order_id";
    final int QUERY_TIME_MAX = 10;

    private String mSelId = "";
    private int current = 0;
    private List<OpenDoorListbean> mData = new ArrayList<>();
    OpenItemAdapter openItemAdapter;

    CommonPopupWindow popupWindow;
    OpenRenewPopup openRenewPopup;
    private KeyPwdPopup keyPwdPopupwindow;//查看钥匙锁密码
    private int queryKeyPwdTime = QUERY_TIME_MAX;//查询钥匙孔密码倒计时

    public static OpenNewFragment newInstance() {
        OpenNewFragment fragment = new OpenNewFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_new, container, false);
        unbinder = ButterKnife.bind(this, view);

        ViewGroup.LayoutParams lp = mRlTopBar.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        mRlTopBar.setLayoutParams(lp);
        mRlTopBar.setPadding(mRlTopBar.getPaddingLeft(), mRlTopBar.getPaddingTop() + UIUtils.getStatusHeight(), mRlTopBar.getPaddingRight(), mRlTopBar.getPaddingBottom());

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mData.size() == 0 && unbinder != null) {
            mSrlRefresh.setRefreshing(true);
            initData();
        }
    }

    @Override
    public void init() {
        super.init();

        mSelId = SharedPreferenceUtil.getString(getActivity(), KEY_ORDER_ID);
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        openItemAdapter = new OpenItemAdapter(mActivity, mData);
        openItemAdapter.setOnCall(new OpenItemAdapter.OnCall() {
            @Override
            public void onClickOpen(int position) {//开门操作
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }

                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (TextUtils.isEmpty(openDoorListbean.getDevice_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else if (openDoorListbean.getStatus() <= 2) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_on_rent));
                } else if (openDoorListbean.getIs_install() == 0) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.please_talk_pay_install));
                } else {
                    openDoor(position);
                }
            }

            @Override
            public void onClickHistoryMore(int position) {//开门记录
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else {
                    OpenHistoryActivity.toActivity(mActivity, openDoorListbean.getRoom_id());
                }
            }

            @Override
            public void onClickOpenPwd(int position) {//开锁密码
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (TextUtils.isEmpty(openDoorListbean.getDevice_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else if (openDoorListbean.getStatus() <= 2) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_on_rent));
                } else {
//                    DevicesOpenPasswordActivity.toActivity(mActivity, openDoorListbean.getDevice_id(), openDoorListbean.getRoom_id());
                    OpenPwdWayActivity.toActivity(mActivity, openDoorListbean.getDevice_id(), openDoorListbean.getRoom_id(),openDoorListbean.getIs_generate_password());
                }
            }

            @Override
            public void onClickGateway(int position) {//网关设置
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (TextUtils.isEmpty(openDoorListbean.getDevice_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else if (openDoorListbean.getStatus() <= 2) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_on_rent));
                } else if (TextUtils.isEmpty(openDoorListbean.getGateway_id())) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_gateway));
                } else {
                    GatewaySettingActivity.toActivity(mActivity, openDoorListbean.getRoom_id(), openDoorListbean.getGateway_id(), openDoorListbean.getGateway_version(), GatewaySettingActivity.TYPE_TENANT);
                }
            }

            @Override
            public void onClickSync(int position) {//同步服务费
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (TextUtils.isEmpty(openDoorListbean.getDevice_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else if (openDoorListbean.getStatus() <= 2) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_on_rent));
                } else {
                    showSyncSeverPopup();
                }
            }

            @Override
            public void onClickKeyPwd(int position) {//钥匙孔密码
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if ( !openDoorListbean.getDevice_id().equals("")) {
                    if (queryKeyPwdTime <= 0 || queryKeyPwdTime >= 10) {
                        showKeyPwdPopup(openDoorListbean.getRoom_id());
                        keyPwdPopupwindow.setPwd(getString(R.string.tips_loading));
                        getKeyPwd(openDoorListbean.getRoom_id());
                    } else {
                        showKeyPwdPopup(openDoorListbean.getRoom_id());
                    }

                } else if (openDoorListbean.getDevice_id().equals("")) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_device));
                } else {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_on_rent));
                }
            }

            @Override
            public void onClickAddUser(int position) {//添加使用者
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (TextUtils.isEmpty(openDoorListbean.getDevice_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else if (openDoorListbean.getIs_share_rent() == 1){
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.tips_share_rent_no_authority));
                }else if (openDoorListbean.getIs_rent() == 1) {
                    ShareRentListActivity.toActivity(mActivity, openDoorListbean.getOrder_id());
                } else {
                    AddRentPeopleActivity.toActivity(mActivity, openDoorListbean.getOrder_id(), true);
                }
            }

            @Override
            public void onClickManager(int position) {//管理员密码
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean openDoorListbean = mData.get(position);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (TextUtils.isEmpty(openDoorListbean.getDevice_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_device));
                } else if (openDoorListbean.getStatus() <= 2) {
                    ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_on_rent));
                }  else if (openDoorListbean.getIs_share_rent() == 1){
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.tips_share_rent_no_authority));
                }else {
                    ManagePwdActivity.toActivity(mActivity, openDoorListbean.getRoom_id());
                }
            }
        });
        mViewPageItem.setAdapter(openItemAdapter);

        mViewPageItem.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSrlRefresh.setEnabled(true);
                current = position;
                mSelId = TextUtils.isEmpty(mData.get(position).getOrder_id()) ? "" : mData.get(position).getOrder_id();
                SharedPreferenceUtil.setString(getActivity(), KEY_ORDER_ID, mSelId);
                refreshData();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mData.size()==1){
                    mSrlRefresh.setEnabled(true);
                    return;
                }
                if (state == 1) {
                    mSrlRefresh.setEnabled(false);
                } else if (state == 2) {
                    mSrlRefresh.setEnabled(true);
                }
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
                    initData();
                }
            }
        });
        addRxBusSubscribe(AddShareRentEvent.class, new Action1<AddShareRentEvent>() {
            @Override
            public void call(AddShareRentEvent addShareRentEvent) {
                int size = mData.size();
                for (int i = 0; i < size; i++) {
                    if (mData.get(i).getOrder_id().equals(addShareRentEvent.getOrder_id())) {
                        mData.get(i).setIs_rent(addShareRentEvent.getHave());
                        openItemAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        addRxBusSubscribe(AddCodeEvent.class, new Action1<AddCodeEvent>() {
            @Override
            public void call(AddCodeEvent addCodeEvent) {
                AddCodeActivity.toActivity(mActivity);
            }
        });
        addRxBusSubscribe(PayRentRefreshEvent.class, new Action1<PayRentRefreshEvent>() {
            @Override
            public void call(PayRentRefreshEvent payRentRefreshEvent) {
                initData();
            }
        });
    }

    @OnClick({R.id.fragment_open_new_img_msg, R.id.fragment_open_new_tv_server_pay, R.id.fragment_open_new_tv_input_code, R.id.fragment_open_new_img_add_order, R.id.fragment_open_new_img_server_end_time_tips})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_open_new_img_msg://消息
                break;
            case R.id.fragment_open_new_tv_input_code://输入激活码
            case R.id.fragment_open_new_img_add_order://输入激活码
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                AddCodeActivity.toActivity(mActivity);
                break;
            case R.id.fragment_open_new_tv_server_pay://续交服务费
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                    return;
                }
                OpenDoorListbean listbean = mData.get(current);
                if (TextUtils.isEmpty(listbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else if (listbean.getIs_install() == 1) {
                    PaySeverActivity.toActivity(getContext(), listbean.getRoom_id(), listbean.getRoom_name(), listbean.getService_time(), 2);
                } else {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.please_talk_pay_install));
                }
                break;
            case R.id.fragment_open_new_img_server_end_time_tips:
                OpenDoorListbean openDoorListbean = mData.get(current);
                if (TextUtils.isEmpty(openDoorListbean.getOrder_id())) {
                    ShowToastUtil.showNormalToast(getContext(), getString(R.string.warm_open_no_order));
                } else {
                    String time;
                    if (openDoorListbean.getOrder_id() != null) {
                        time = openDoorListbean.getService_time().equals("0") ? (openDoorListbean.getIs_install() == 0 ? "- - -" + getString(R.string.end_time) : getString(R.string.house_sever_end_time_end)) : (openDoorListbean.getService_time() + getString(R.string.end_time));
                    } else {
                        time = "- - -" + getString(R.string.end_time);
                    }
                    showRenewPopup(mImgServerEndTimeTips, time);

                }
                break;
        }
    }

    /**
     * 数据更新
     */
    private void refreshData() {
        int size = mData.size();
        current = 0;
        for (int i = 0; i < size; i++) {
            OpenDoorListbean bean = mData.get(i);
            if (bean.getOrder_id() != null && bean.getOrder_id().equals(mSelId)) {
                current = i;
                break;
            }
        }
        if (mData.size() > 0)
            mViewPageItem.setCurrentItem(current);
        if(mData.size()==1){
            mSrlRefresh.setEnabled(true);
        }
        OpenDoorListbean listbean = mData.get(current);
        if (listbean.getOrder_id() != null) {
            mTvServerEndTime.setText(listbean.getService_time().equals("0") ? (listbean.getIs_install() == 0 ? getString(R.string.house_sever_end_time_emty) : getString(R.string.house_sever_end_time_end)) : (listbean.getService_time() + getString(R.string.end_time)));

        } else {
            mTvServerEndTime.setText("- - -" + getString(R.string.end_time));
        }
        mTvItemCircleConcent.setText(size <= 0 ? "" : current + 1 + "");
        mTvItemCircleLeft.setText(current == 0 ? "" : current + "");
        mTvItemCircleRight.setText(current >= size - 1 ? "" : current + 2 + "");
    }

    /********************************************接口*************************************************/
    /**
     * 获取门锁列表数据
     */
    private void initData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getOpenRoomList()
                .compose(RxUtil.<DataInfo<List<OpenDoorListbean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<OpenDoorListbean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mSrlRefresh.setRefreshing(false);

                    }

                    @Override
                    public void onNext(DataInfo<List<OpenDoorListbean>> payOrderDataInfo) {
                        mSrlRefresh.setRefreshing(false);
                        if (payOrderDataInfo.success()) {
                            mData.clear();
                            mData.addAll(payOrderDataInfo.data());
                            if (mData.size() <= 0) {
                                OpenDoorListbean bean = new OpenDoorListbean();
                                mData.add(bean);
                                mViewPageItem.setVisibility(View.GONE);
                                mRlEmpty.setVisibility(View.VISIBLE);
                                mRlItemCircle.setVisibility(View.INVISIBLE);
                                mLlServerEndTime.setVisibility(View.GONE);
                                mTvServerPay.setVisibility(View.GONE);
                            } else {
                                mViewPageItem.setVisibility(View.VISIBLE);
                                mRlEmpty.setVisibility(View.GONE);
                                mRlItemCircle.setVisibility(View.VISIBLE);
                                mLlServerEndTime.setVisibility(View.VISIBLE);
                                mTvServerPay.setVisibility(View.VISIBLE);
                            }
                            openItemAdapter.notifyDataSetChanged();
                            mViewPageItem.setAdapter(openItemAdapter);
                            refreshData();
                        } else {
                            showToast(payOrderDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 开门
     */
    private void openDoor(int position) {
        openItemAdapter.startOpenAnimation(position);
        OpenDoorListbean listbean = mData.get(position);
        Subscription subscription = SecondRetrofitHelper.getInstance().openDoor(listbean.getRoom_id(), listbean.getGateway_id(), listbean.getDevice_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        openItemAdapter.cancelTimer(position);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (!dataInfo.success()) {
                            showToast(dataInfo.msg());
                            openItemAdapter.cancelTimer(position);
                        } else {
                            openItemAdapter.startTimer(position);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 同步服务费
     */
    private void syncServiceExpire() {
        showLoadingDialog();
        OpenDoorListbean openDoorListbean = mData.get(current);
        Subscription subscription = SecondRetrofitHelper.getInstance().sync_service_expire(openDoorListbean.getRoom_id(), openDoorListbean.getDevice_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            ShowToastUtil.showNormalToast(getContext(), getString(R.string.please_info_lock_setting_sync_device));
                        } else {
                            ShowToastUtil.showNormalToast(getContext(), "服务费同步：" + dataInfo.msg());
                        }
                        dismiss();
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取钥匙孔密码
     * */
    private void getKeyPwd(String room_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().lockPwd(room_id, "9")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                queryKeyPwdTime = 0;
                                keyPwdPopupwindow.setPwd(info.data().getPassword());
                            } else {
                                queryPwdTimer(5, info.data().getId(),room_id);
                            }
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 密码刷新
     * */
    private void passwordRefresh(String room_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().passwordRefresh(room_id, "9")
                .compose(RxUtil.<DataInfo<QueryPwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<QueryPwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<QueryPwdBean> info) {
                        if (info.success()) {
                            if (TextUtils.isEmpty(info.data().getId())) {
                                queryKeyPwdTime = 0;
                                keyPwdPopupwindow.setPwd(info.data().getPassword());
                            } else {
                                queryKeyPwdTime = QUERY_TIME_MAX;
                                queryPwdTimer(5, info.data().getId(),room_id);
                            }
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 密码查询
     * */
    private void queryPasswordResult(final String password_id,String room_id) {
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        queryPwdTimer(1, password_id,room_id);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            getKeyPwd(room_id);
                        } else {
                            queryPwdTimer(1, password_id,room_id);
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /****************************************************弹窗**********************************************/
    /**
     * 展示钥匙孔密码
     */
    private void showKeyPwdPopup(String room_id) {
        if (keyPwdPopupwindow == null) {
            keyPwdPopupwindow = new KeyPwdPopup(mActivity);
            keyPwdPopupwindow.setOnClickListen(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (queryKeyPwdTime <= 0 || queryKeyPwdTime > 10) {
                        keyPwdPopupwindow.dismiss();
                        showPasswordRefreshPopup(room_id);
                    }
                }
            });
        }
        showPopup(keyPwdPopupwindow);
    }

    /**
     * 是否重置密码
     */
    private void showPasswordRefreshPopup(String room_id) {
        popupWindow = new CommonPopupWindow.Builder(mActivity)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_refresh_pwd))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.setOnDismissListener(null);
                        popupWindow.dismiss();
                        keyPwdPopupwindow.setPwd(getString(R.string.tips_loading));
                        showKeyPwdPopup(room_id);
                        passwordRefresh(room_id);
                    }
                })
                .create();
        showPopup(popupWindow);

    }

    /**
     * 是否同步服务费时间
     */
    private void showSyncSeverPopup() {
        popupWindow = new CommonPopupWindow.Builder(mActivity)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.tips_info_sync_device_content))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        syncServiceExpire();
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.8f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) mSrlRefresh.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 服务费到期提示
     */
    private void showRenewPopup(View imgDoubt, String endTime) {
        if (openRenewPopup == null)
            openRenewPopup = new OpenRenewPopup(mActivity);
        openRenewPopup.setTime(String.format(getString(R.string.door_open_popup_end_time), endTime));
        openRenewPopup.showAsDropDown(imgDoubt, -getResources().getDimensionPixelSize(R.dimen.dp_125), 0);

    }
    /*************************************************计时处理*****************************************************************/


    private void queryPwdTimer(long delay, final String password_id,String room_id) {
        if (queryKeyPwdTime <= 0) {
            return;
        }
        queryKeyPwdTime--;
        Subscription subscription = Observable.timer(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        queryPasswordResult(password_id,room_id);
                    }
                });
        addSubscrebe(subscription);
    }
}
