package com.konka.renting.landlord.house;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.konka.renting.event.HousePublishEvent;
import com.konka.renting.event.LandlordHouseListEvent;
import com.konka.renting.event.PublicCancelEvent;
import com.konka.renting.event.RefreshDeviceEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.activity.AddHouseAddressActivity;
import com.konka.renting.landlord.house.activity.HouseInfoActivity2;
import com.konka.renting.landlord.house.activity.PayAllMoneyActivity;
import com.konka.renting.landlord.house.view.HouseAdapter;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
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
        RxBus.getDefault().toDefaultObservable(LandlordHouseListEvent.class, new Action1<LandlordHouseListEvent>() {
            @Override
            public void call(LandlordHouseListEvent event) {
                if (event.isUpdata())
                    mRefresh.autoRefresh();
            }
        });
        RxBus.getDefault().toDefaultObservable(RefreshDeviceEvent.class, new Action1<RefreshDeviceEvent>() {
            @Override
            public void call(RefreshDeviceEvent refreshDeviceEvent) {
                mRefresh.autoRefresh();
            }
        });
        RxBus.getDefault().toDefaultObservable(UpdateEvent.class, new Action1<UpdateEvent>() {
            @Override
            public void call(UpdateEvent event) {
                mRefresh.autoRefresh();
            }
        });
        RxBus.getDefault().toDefaultObservable(PublicCancelEvent.class, new Action1<PublicCancelEvent>() {
            @Override
            public void call(PublicCancelEvent event) {
                mRefresh.autoRefresh();
            }
        });
        RxBus.getDefault().toDefaultObservable(HousePublishEvent.class, new Action1<HousePublishEvent>() {
            @Override
            public void call(HousePublishEvent event) {
                mRefresh.autoRefresh();
            }
        });
        getRoomList();
    }

    private void initAdapter() {
        commonAdapter = new CommonAdapter<HouseOrderInfoBean>(getActivity(), dataList, R.layout.adapter_house_fragment) {
            @Override
            public void convert(ViewHolder viewHolder, HouseOrderInfoBean houseOrderInfoBean) {
                viewHolder.setText(R.id.tv_name, houseOrderInfoBean.getRoom_name());
                viewHolder.setText(R.id.adapter_house_tv_endtime, houseOrderInfoBean.getService_date().equals("0") ? (houseOrderInfoBean.getIs_install() == 0 ? getString(R.string.house_sever_end_time_emty) : getString(R.string.house_sever_end_time_end)) : houseOrderInfoBean.getService_date());
                viewHolder.setVisible(R.id.adapter_house_ll_sever_time, !TextUtils.isEmpty(houseOrderInfoBean.getDevice_id()));
                viewHolder.setVisible(R.id.adapter_house_ll_rent_money, houseOrderInfoBean.getRoom_status() > 3);
                viewHolder.setText(R.id.adapter_house_tv_rent_money, houseOrderInfoBean.getHousing_price());
                viewHolder.setText(R.id.adapter_house_tv_rent_money_unit, getString(houseOrderInfoBean.getType() == 1 ? R.string.public_house_pay_unit_day : R.string.public_house_pay_unit_mon));

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

                viewHolder.setOnClickListener(R.id.adapter_house_tv_pay, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (houseOrderInfoBean.getIs_install() == 0) {
                            PayAllMoneyActivity.toActivity(getActivity(), houseOrderInfoBean.getRoom_id() + "");
                        } else {
                            PaySeverActivity.toActivity(getActivity(), houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getAddress(), houseOrderInfoBean.getService_date(), 1);
                        }
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

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

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }
}
