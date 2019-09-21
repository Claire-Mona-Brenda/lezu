package com.konka.renting.tenant.order;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.event.LandlordOrderApplyEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.order.ConfirmEvent;
import com.konka.renting.utils.RxUtil;
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
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

public class UnderwayTFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_upderway_rv_recycler)
    RecyclerView mRvRecycler;
    @BindView(R.id.fragment_upderway_srl_refresh)
    SmartRefreshLayout mSrlRefresh;

    private List<RenterOrderListBean> mData = new ArrayList<>();
    private int page;
    CommonAdapter<RenterOrderListBean> commonAdapter;


    public static UnderwayTFragment newInstance() {
        Bundle args = new Bundle();
        UnderwayTFragment fragment = new UnderwayTFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upderway, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void init() {
        super.init();
        initList();
        mSrlRefresh.setReboundDuration(100);
        mSrlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(true);
            }
        });
        mSrlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                initData(false);
            }
        });
        addRxBusSubscribe(ConfirmEvent.class, new Action1<ConfirmEvent>() {
            @Override
            public void call(ConfirmEvent confirmEvent) {
                mSrlRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(PayRentRefreshEvent.class, new Action1<PayRentRefreshEvent>() {
            @Override
            public void call(PayRentRefreshEvent locationRefreshEvent) {
                mSrlRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(LandlordOrderApplyEvent.class, new Action1<LandlordOrderApplyEvent>() {
            @Override
            public void call(LandlordOrderApplyEvent landlordOrderApplyEvent) {
                if (landlordOrderApplyEvent.isUpdata()) {
                    mSrlRefresh.autoRefresh();
                }
            }
        });

        mSrlRefresh.autoRefresh();
    }


    private void initList() {
        commonAdapter = new CommonAdapter<RenterOrderListBean>(getContext(), mData, R.layout.adapter_order_t) {
            @Override
            public void convert(ViewHolder viewHolder, RenterOrderListBean listBean) {
                if (!TextUtils.isEmpty(listBean.getHousing_price()) && listBean.getRent_type() != 0) {
                    String unit = listBean.getRent_type() == 1 ? "/天" : "/月";
                    String price=listBean.getHousing_price();
                    if (!TextUtils.isEmpty(price)){
                        float priceF = Float.valueOf(listBean.getHousing_price());
                        int priceI = (int) priceF;
                        if (priceF>priceI){
                            price= priceF+"";
                        }else{
                            price= priceI+"";
                        }
                    }else{
                        price="";
                    }
                    viewHolder.setText(R.id.adapter_tv_room_price, "¥ " + price + unit);
                    viewHolder.setVisible(R.id.adapter_tv_room_price, true);
                } else {
                    viewHolder.setVisible(R.id.adapter_tv_room_price, false);
                }

                ImageView ivPic = viewHolder.getView(R.id.adapter_icon_room);
                if (!TextUtils.isEmpty(listBean.getThumb_image()))
                    Picasso.get().load(listBean.getThumb_image()).into(ivPic);
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).into(ivPic);
                viewHolder.setText(R.id.adapter_tv_room_name, listBean.getRoom_name());
                viewHolder.setText(R.id.adapter_tv_start_time, listBean.getStart_time());
                viewHolder.setText(R.id.adapter_tv_end_time, listBean.getEnd_time());

                String room_type;
                if (listBean.getRoom_type().contains("_")) {
                    String[] t = listBean.getRoom_type().split("_");
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = listBean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + "|");
                spannableStringBuilder.append(getArea(listBean.getMeasure_area()));
                spannableStringBuilder.append("|" + listBean.getFloor() + "/" + listBean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_tv_room_info, spannableStringBuilder);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderInfoTActivity.toActivity(getActivity(), listBean.getOrder_id(),0);
                    }
                });
            }
        };
        mRvRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvRecycler.setAdapter(commonAdapter);
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }

    private void removeBean(String order_id) {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (mData.get(i).getOrder_id().equals(order_id)) {
                mData.remove(i);
                commonAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /*******************************************************接口********************************************/
    private void initData(boolean isRefresh) {
        if (isRefresh)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = SecondRetrofitHelper.getInstance().getOrderList2("1", page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterOrderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterOrderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        if (isRefresh)
                            mSrlRefresh.finishRefresh();
                        else {
                            mSrlRefresh.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterOrderListBean>> listInfoDataInfo) {
                        if (isRefresh) {
                            mSrlRefresh.finishRefresh();
                        } else {
                            mSrlRefresh.finishLoadmore();
                        }
                        if (listInfoDataInfo.success()) {
                            if (page == 1) {
                                mData.clear();
                            } else if (listInfoDataInfo.data().getTotalPage() < listInfoDataInfo.data().getPage()) {
                                page--;
                            }
                            mSrlRefresh.setEnableLoadmore(page < listInfoDataInfo.data().getTotalPage());
                            mData.addAll(listInfoDataInfo.data().getList());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            if (page > 1)
                                page--;
                            showToast(listInfoDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

}
