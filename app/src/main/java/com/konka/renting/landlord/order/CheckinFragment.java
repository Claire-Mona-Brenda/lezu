package com.konka.renting.landlord.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.event.LandlordOrderCheckinEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
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

/**
 * Created by kaite on 2018/3/16.
 */

public class CheckinFragment extends BaseFragment {
    @BindView(R.id.list_checkin)
    ListView mListCheckin;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    Unbinder unbinder;
    private List<RenterOrderListBean> mData = new ArrayList<>();
    private int page;
    private int status = 2;
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private CommonAdapter<RenterOrderListBean> mAdapter;

    public static CheckinFragment newInstance() {

        Bundle args = new Bundle();

        CheckinFragment fragment = new CheckinFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CheckinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkin, container, false);

        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        initData(REFRESH);
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mRefresh.autoRefresh();
        }
    }

    private void initList() {
        mAdapter = new CommonAdapter<RenterOrderListBean>(getContext(), mData, R.layout.item_list_checkin) {
            @Override
            public void convert(ViewHolder viewHolder, final RenterOrderListBean s, int i) {
                String unit = s.getRent_type() == 1 ? "/天" : "/月";
                viewHolder.setText(R.id.tv_money, "¥ " + (int) Float.parseFloat(s.getHousing_price()) + unit);
                viewHolder.setText(R.id.tv_order_number, s.getOrder_no());
                ImageView ivPic = viewHolder.getView(R.id.icon_room);
                if (!TextUtils.isEmpty(s.getThumb_image()))
                    Picasso.get().load(s.getThumb_image()).into(ivPic);
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).into(ivPic);
                viewHolder.setText(R.id.tv_address, s.getRoom_name());
                viewHolder.setText(R.id.tv_status, getStringStatus(s.getStatus()));
                viewHolder.setText(R.id.tv_start_time, s.getStart_time());
                viewHolder.setText(R.id.tv_end_time, s.getEnd_time());

                TextView tvShort = viewHolder.getView(R.id.tv_short);
                TextView tvLong = viewHolder.getView(R.id.tv_long);
                if (s.getRent_type() == 1) {
                    tvShort.setVisibility(View.VISIBLE);
                    tvLong.setVisibility(View.GONE);
                } else {
                    tvLong.setVisibility(View.VISIBLE);
                    tvShort.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        };

        mListCheckin.setAdapter(mAdapter);
        mListCheckin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderDetailActivity.toActivity(getContext(), mData.get(i).getOrder_id(), mData.get(i).getRent_type() + "", mData.get(i).getOrder_no(), mData.get(i).getStatus() + "");
            }
        });
        addRxBusSubscribe(ChangeEvent.class, new Action1<ChangeEvent>() {
            @Override
            public void call(ChangeEvent changeEvent) {
                mRefresh.autoRefresh();
            }
        });
    }


    @Override
    public void init() {
        super.init();
        initList();
        mRefresh.setReboundDuration(100);
        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(REFRESH);
            }
        });
        mRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                initData(LOADMORE);
            }
        });
        addRxBusSubscribe(UpdateRentingEvent.class, new Action1<UpdateRentingEvent>() {
            @Override
            public void call(UpdateRentingEvent updateRentingEvent) {
                mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(RenewEvent.class, new Action1<RenewEvent>() {
            @Override
            public void call(RenewEvent renewEvent) {
                mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(LandlordOrderCheckinEvent.class, new Action1<LandlordOrderCheckinEvent>() {
            @Override
            public void call(LandlordOrderCheckinEvent event) {
                if (event.isUpdata()) {
                    mRefresh.autoRefresh();
                }
            }
        });

    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordOrderList(status + "", page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterOrderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterOrderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else {
                            mRefresh.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterOrderListBean>> listInfoDataInfo) {
                        if (type == REFRESH) {
                            mRefresh.finishRefresh();
                        } else {
                            mRefresh.finishLoadmore();
                        }
                        if (listInfoDataInfo.success()) {
                            if (page == 1) {
                                mData.clear();
                            } else if (listInfoDataInfo.data().getTotalPage() < listInfoDataInfo.data().getPage()) {
                                page--;
                            }
                            mRefresh.setEnableLoadmore(page<listInfoDataInfo.data().getTotalPage());
                            mData.addAll(listInfoDataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (page > 1)
                                page--;
                            showToast(listInfoDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private String getStringStatus(int status) {
        String s = "";
        switch (status) {
            case 1:
                s = getString(R.string.order_status_1);
                break;
            case 2:
                s = getString(R.string.order_status_2);
                break;
            case 3:
                s = getString(R.string.order_status_3);
                break;
            case 4:
                s = getString(R.string.order_status_4);
                break;
            case 5:
                s = getString(R.string.order_status_5);
                break;
            case 6:
                s = getString(R.string.order_status_6);
                break;
            case 7:
                s = getString(R.string.order_status_7);
                break;
        }
        return s;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRefresh.finishRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
