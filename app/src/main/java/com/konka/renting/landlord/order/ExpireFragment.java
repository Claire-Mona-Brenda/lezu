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
import com.konka.renting.event.LandlordOrderExpireEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
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

public class ExpireFragment extends BaseFragment {
    @BindView(R.id.list_expire)
    ListView mListExpire;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    Unbinder unbinder;
    private List<RenterOrderListBean> mData = new ArrayList<>();
    private int page;
    private int status = 4;
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private CommonAdapter<RenterOrderListBean> mAdapter;

    public static ExpireFragment newInstance() {

        Bundle args = new Bundle();

        ExpireFragment fragment = new ExpireFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpireFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_expire, container, false);

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
        addRxBusSubscribe(LandlordOrderExpireEvent.class, new Action1<LandlordOrderExpireEvent>() {
            @Override
            public void call(LandlordOrderExpireEvent event) {
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
                        } else
                            mRefresh.finishLoadmore();
                        if (listInfoDataInfo.success()) {
                            if (page == 1) {
                                mData.clear();
                            } else if (listInfoDataInfo.data().getTotalPage() < listInfoDataInfo.data().getPage()) {
                                page--;
                            }
                            mData.addAll(listInfoDataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                            mRefresh.setEnableLoadmore(page < listInfoDataInfo.data().getTotalPage());
                        } else {
                            if (page > 1)
                                page--;
                            showToast(listInfoDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void initList() {
        mAdapter = new CommonAdapter<RenterOrderListBean>(getContext(), mData, R.layout.item_list_expire) {
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
        mListExpire.setAdapter(mAdapter);
        mListExpire.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderDetailActivity.toActivity(getContext(), mData.get(i).getOrder_id(), mData.get(i).getRent_type() + "", mData.get(i).getOrder_no(), mData.get(i).getStatus() + "");
            }
        });
    }

    public void renew(String ordernum) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().reNew(ordernum)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            mRefresh.autoRefresh();
                            RxBus.getDefault().post(new RenewEvent());
                        } else {
                            showToast(dataInfo.msg());
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
