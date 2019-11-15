package com.konka.renting.landlord.house;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseOrderInfoBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.event.CreateOrderEvent;
import com.konka.renting.event.DelHouseEvent;
import com.konka.renting.event.HousePublishEvent;
import com.konka.renting.event.LandlordHouseListEvent;
import com.konka.renting.event.PublicCancelEvent;
import com.konka.renting.event.RefreshDeviceEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.activity.AddHouseAddressActivity;
import com.konka.renting.landlord.house.activity.DevListActivity;
import com.konka.renting.landlord.house.activity.HouseInfoActivity2;
import com.konka.renting.landlord.house.activity.PayAllMoneyActivity;
import com.konka.renting.landlord.house.view.HouseAdapter;
import com.konka.renting.landlord.house.view.HousePublishActivity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.tenant.opendoor.OpeningPopupwindow;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

public class HouseFragment2 extends BaseFragment {
    @BindView(R.id.text_title)
    TextView tvTitle;
    @BindView(R.id.button_add)
    TextView buttonAdd;
    @BindView(R.id.title_bg)
    RelativeLayout titleBg;
    @BindView(R.id.add_house)
    Button addHouse;
    @BindView(R.id.fragment_house_ll_empty)
    LinearLayout mLlEmpty;
    @BindView(R.id.fragment_house_rv_list)
    RecyclerView mRvList;
    @BindView(R.id.fragment_house_refresh)
    SmartRefreshLayout mRefresh;
    @BindView(R.id.fragment_house_rl_house)
    RelativeLayout mRlHouse;

    Unbinder unbinder;
    List<HouseOrderInfoBean> dataList = new ArrayList<>();
    int offset = 1;
    CommonAdapter<HouseOrderInfoBean> commonAdapter;
    CommonPopupWindow commonPopupWindow;
    private OpeningPopupwindow openingPopupwindow;

    public static HouseFragment2 newInstance() {
        Bundle args = new Bundle();
        HouseFragment2 fragment = new HouseFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_house, container, false);
        unbinder = ButterKnife.bind(this, v);
        mRlHouse.setPadding(mRlHouse.getPaddingLeft(), mRlHouse.getPaddingTop() + UIUtils.getStatusHeight(), mRlHouse.getPaddingRight(), mRlHouse.getPaddingBottom());

        init();
        return v;
    }

    @Override
    public void init() {
        super.init();
        initAdapter();
        mRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offset++;
                getRoomList();
            }
        });
        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                offset = 1;
                getRoomList();
            }
        });
        mRefresh.setEnableLoadmore(false);
        addRxBusSubscribe(LandlordHouseListEvent.class, new Action1<LandlordHouseListEvent>() {
            @Override
            public void call(LandlordHouseListEvent event) {
                if (event.isUpdata())
                    mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(RefreshDeviceEvent.class, new Action1<RefreshDeviceEvent>() {
            @Override
            public void call(RefreshDeviceEvent refreshDeviceEvent) {
                mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(UpdateEvent.class, new Action1<UpdateEvent>() {
            @Override
            public void call(UpdateEvent event) {
                mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(DelHouseEvent.class, new Action1<DelHouseEvent>() {
            @Override
            public void call(DelHouseEvent delHouseEvent) {
                if (TextUtils.isEmpty(delHouseEvent.getRoom_id())) {
                    mRefresh.autoRefresh();
                } else {
                    int size = dataList.size();
                    for (int i = 0; i < size; i++) {
                        if (delHouseEvent.getRoom_id().equals(dataList.get(i).getRoom_id() + "")) {
                            dataList.remove(i);
                            commonAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
        addRxBusSubscribe(CreateOrderEvent.class, new Action1<CreateOrderEvent>() {
            @Override
            public void call(CreateOrderEvent createOrderEvent) {
                mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(PublicCancelEvent.class, new Action1<PublicCancelEvent>() {
            @Override
            public void call(PublicCancelEvent event) {
                int size = dataList.size();
                for (int i = 0; i < size; i++) {
                    if (event.getRoom_id().equals(dataList.get(i).getRoom_id() + "")) {
                        dataList.get(i).setIs_pub(0);
                        commonAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        addRxBusSubscribe(HousePublishEvent.class, new Action1<HousePublishEvent>() {
            @Override
            public void call(HousePublishEvent event) {
                int size = dataList.size();
                for (int i = 0; i < size; i++) {
                    HouseOrderInfoBean bean = dataList.get(i);
                    if (event.getRoom_id().equals(bean.getRoom_id() + "")) {
                        bean.setIs_pub(1);
                        bean.setType(event.getType());
                        bean.setHousing_price(event.getPrice());
                        commonAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        getRoomList();
    }

    private void initAdapter() {
        commonAdapter = new CommonAdapter<HouseOrderInfoBean>(getActivity(), dataList, R.layout.adapter_house_fragment) {
            @Override
            public void convert(ViewHolder viewHolder, HouseOrderInfoBean houseOrderInfoBean) {
                viewHolder.setText(R.id.adapter_houselist_tv_id, houseOrderInfoBean.getRoom_no());
                viewHolder.setText(R.id.tv_name, houseOrderInfoBean.getRoom_name());
                viewHolder.setText(R.id.adapter_house_tv_endtime, houseOrderInfoBean.getService_date().equals("0") ? (houseOrderInfoBean.getIs_install() == 0 ? getString(R.string.house_sever_end_time_emty) : getString(R.string.house_sever_end_time_end)) : houseOrderInfoBean.getService_date());


                if (houseOrderInfoBean.getIs_pub() == 0) {//未发布
                    viewHolder.setText(R.id.status, getString(R.string.house_status_type_3));
                    viewHolder.setText(R.id.start_end, getString(R.string.start_to_rent));
                    viewHolder.setText(R.id.adapter_houselist_tv_rent_type, "");
                    viewHolder.setVisible(R.id.adapter_house_ll_rent_money, false);
                } else {//已发布
                    viewHolder.setText(R.id.status, getString(R.string.house_status_type_public));
                    viewHolder.setVisible(R.id.adapter_house_ll_rent_money, true);

                    String unit = houseOrderInfoBean.getType() == 1 ? getString(R.string.public_house_pay_unit_day) : getString(R.string.public_house_pay_unit_mon);
                    String price = houseOrderInfoBean.getHousing_price();
                    if (!TextUtils.isEmpty(price)) {
                        float priceF = Float.valueOf(houseOrderInfoBean.getHousing_price());
                        int priceI = (int) priceF;
                        if (priceF <= 0) {
                            price = "";
                            unit = getString(R.string.negotiable);
                        } else if (priceF > priceI) {
                            price = priceF + "";
                        } else {
                            price = priceI + "";
                        }
                    } else {
                        price = "";
                    }
                    viewHolder.setText(R.id.adapter_house_tv_rent_money, price);
                    viewHolder.setText(R.id.adapter_house_tv_rent_money_unit, unit);
                    viewHolder.setText(R.id.start_end, TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())?getString(R.string.end_to_rent):getString(R.string.switch_push_rent));
                    if (houseOrderInfoBean.getType() == 1) {//短租
                        viewHolder.setText(R.id.adapter_houselist_tv_rent_type, "【" + getString(R.string.short_rent) + "】");
                        viewHolder.setTextColorRes(R.id.adapter_houselist_tv_rent_type, R.color.color_short);
                        viewHolder.setTextColorRes(R.id.adapter_house_tv_rent_money, R.color.text_green);
                        viewHolder.setTextColorRes(R.id.adapter_house_tv_rent_money_unit, R.color.text_green);
                    } else {//长租
                        viewHolder.setText(R.id.adapter_houselist_tv_rent_type, "【" + getString(R.string.long_rent) + "】");
                        viewHolder.setTextColorRes(R.id.adapter_houselist_tv_rent_type, R.color.color_long);
                        viewHolder.setTextColorRes(R.id.adapter_house_tv_rent_money, R.color.text_ren);
                        viewHolder.setTextColorRes(R.id.adapter_house_tv_rent_money_unit, R.color.text_ren);
                    }

                }

                String room_type;
                if (houseOrderInfoBean.getRoom_type().contains("_")) {
                    String[] t = houseOrderInfoBean.getRoom_type().split("_");
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = houseOrderInfoBean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + "/");
                spannableStringBuilder.append(getArea(houseOrderInfoBean.getMeasure_area()));
                spannableStringBuilder.append("/" + houseOrderInfoBean.getFloor() + "楼");
                viewHolder.setText(R.id.adapter_house_tv_info, spannableStringBuilder);

                ImageView ipc = viewHolder.getView(R.id.img_house);
                if (CacheUtils.checkFileExist(houseOrderInfoBean.getThumb_image())) {
                    Picasso.get().load(CacheUtils.getFile(houseOrderInfoBean.getThumb_image())).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(ipc);
                } else if (!TextUtils.isEmpty(houseOrderInfoBean.getThumb_image())) {
                    CacheUtils.saveFile(houseOrderInfoBean.getThumb_image(), getContext());
                    Picasso.get().load(houseOrderInfoBean.getThumb_image()).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(ipc);
                } else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai).into(ipc);

                viewHolder.setVisible(R.id.ll_create_order, !TextUtils.isEmpty(houseOrderInfoBean.getDevice_id()) && houseOrderInfoBean.getIs_install() == 1);
                viewHolder.setVisible(R.id.ll_open, !TextUtils.isEmpty(houseOrderInfoBean.getDevice_id()) && houseOrderInfoBean.getStatus() <= 1);
                viewHolder.setVisible(R.id.ll_bind, TextUtils.isEmpty(houseOrderInfoBean.getDevice_id()));
                viewHolder.setVisible(R.id.ll_install, !TextUtils.isEmpty(houseOrderInfoBean.getDevice_id()) && houseOrderInfoBean.getIs_install() == 0);
                viewHolder.setVisible(R.id.adapter_houselist_ll_sever, !TextUtils.isEmpty(houseOrderInfoBean.getDevice_id()) && houseOrderInfoBean.getIs_install() == 1);
                viewHolder.setVisible(R.id.ll_start_end, false);

                viewHolder.setOnClickListener(R.id.ll_create_order, new View.OnClickListener() {//生成订单
                    @Override
                    public void onClick(View v) {
                        CreateOrderActivity.toActivity(mActivity, houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getRoom_name());
                    }
                });
                viewHolder.setOnClickListener(R.id.ll_bind, new View.OnClickListener() {//绑定设备
                    @Override
                    public void onClick(View v) {
                        DevListActivity.toActivity(mActivity, houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getStatus(), houseOrderInfoBean.getIs_install() == 0, false);
                    }
                });
                viewHolder.setOnClickListener(R.id.ll_install, new View.OnClickListener() {//安装付费
                    @Override
                    public void onClick(View v) {
                        PayAllMoneyActivity.toActivity(getActivity(), houseOrderInfoBean.getRoom_id() + "");
                    }
                });
                viewHolder.setOnClickListener(R.id.adapter_houselist_ll_sever, new View.OnClickListener() {//缴纳服务费
                    @Override
                    public void onClick(View v) {
                        PaySeverActivity.toActivity(getActivity(), houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getRoom_name(), houseOrderInfoBean.getService_date(), 1);
                    }
                });
                viewHolder.setOnClickListener(R.id.ll_open, new View.OnClickListener() {//开门
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())) {
                            showOpen(houseOrderInfoBean);
                        } else {
                            ShowToastUtil.showNormalToast(mActivity, getString(R.string.warm_open_no_device));
                        }
                    }
                });
                viewHolder.setOnClickListener(R.id.ll_start_end, new View.OnClickListener() {//发布操作
                    @Override
                    public void onClick(View v) {
                        if (houseOrderInfoBean.getIs_pub() == 1&&TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())) {
                            cancelPublic(houseOrderInfoBean.getRoom_id() + "");
                        } else {
                            HousePublishActivity.toActivity(mActivity, houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getType() != 1);
                        }
                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {//查看详情

                    @Override
                    public void onClick(View arg0) {
                        HouseInfoActivity2.toActivity(getActivity(), houseOrderInfoBean.getRoom_id() + "");
                    }
                });
            }

        };
        mRvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvList.setAdapter(commonAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.button_add, R.id.add_house})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_add:
            case R.id.add_house:
                if (LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    AddHouseAddressActivity.toActivity(mActivity);
                } else {
                    NewFaceDectectActivity.toActivity(getActivity(), 1);
                }
                break;
        }
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }


    /********************************************接口*******************************************************/
    private void getRoomList() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().getRoomList2(offset + "")
                .compose(RxUtil.<DataInfo<PageDataBean<HouseOrderInfoBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<HouseOrderInfoBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (offset > 1) {
                            offset--;
                        }
                        if (mRefresh.isRefreshing())
                            mRefresh.finishRefresh();
                        if (mRefresh.isLoading())
                            mRefresh.finishLoadmore();
                    }


                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<PageDataBean<HouseOrderInfoBean>> dataInfo) {
                        if (mRefresh.isRefreshing())
                            mRefresh.finishRefresh();
                        if (mRefresh.isLoading())
                            mRefresh.finishLoadmore();
                        if (dataInfo.success()) {
                            if (offset == 1) {
                                dataList.clear();
                            } else if (dataInfo.data().getTotalPage() < dataInfo.data().getPage()) {
                                offset--;
                            }
                            mRefresh.setEnableLoadmore(dataInfo.data().getTotalPage() > dataInfo.data().getPage());
                            dataList.addAll(dataInfo.data().getList());
                            commonAdapter.notifyDataSetChanged();
                            if (dataList.size() < 1) {
                                mLlEmpty.setVisibility(View.VISIBLE);
                                buttonAdd.setVisibility(View.GONE);
                            } else {
                                mLlEmpty.setVisibility(View.GONE);
                                buttonAdd.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (offset > 1) {
                                offset--;
                            }
                            ShowToastUtil.showNormalToast(getActivity(), dataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    private void openDoor(String room_id, String gateway_id, String device_id) {
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
                            ShowToastUtil.showWarningToast(mActivity, dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public void cancelPublic(String room_id) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().cancelPublishHouse2(room_id)
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
                            RxBus.getDefault().post(new PublicCancelEvent(room_id));

                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    /****************************************************弹窗**************************************************/
    private void showOpen(final HouseOrderInfoBean bean) {
        commonPopupWindow = new CommonPopupWindow.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.tips))
                .setContent(mActivity.getString(R.string.warm_open_door))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDoor(bean.getRoom_id() + "", bean.getGateway_id(), bean.getDevice_id());
                        commonPopupWindow.setOnDismissListener(null);
                        commonPopupWindow.dismiss();
                    }
                })
                .create();
        showPopup(commonPopupWindow);
    }

    private void showOpening() {
        // 开启 popup 时界面透明
        openingPopupwindow = new OpeningPopupwindow(mActivity);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        openingPopupwindow.showAtLocation((View) titleBg.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
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

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) titleBg.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
}
