package com.konka.renting.landlord.user.bill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.BillBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.http.RetrofitHelper;
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

/**
 * Created by kaite on 2018/3/14.
 */

public class NonPayFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.list_non_pay)
    ListView mListNonPay;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private List<BillBean> mData = new ArrayList<>();
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private int page;
    private CommonAdapter<BillBean> mAdapter;

    public NonPayFragment() {
    }

    public static NonPayFragment newInstance() {
        NonPayFragment nonPayFragment = new NonPayFragment();
        return nonPayFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_non_pay, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void init() {
        super.init();
        initList();
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
        mRefresh.autoRefresh();
    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = RetrofitHelper.getInstance().getBill(0, page)
                .compose(RxUtil.<DataInfo<ListInfo<BillBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<BillBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else
                            mRefresh.finishLoadmore();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<ListInfo<BillBean>> listInfoDataInfo) {
                        if (type == REFRESH) {
                            mRefresh.finishRefresh();
                            mData.clear();
                        } else
                            mRefresh.finishLoadmore();
                        if (listInfoDataInfo.success()){
                            mData.addAll(listInfoDataInfo.data().lists);
                            mAdapter.notifyDataSetChanged();
                        }else {
                            showToast(listInfoDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void initList() {
        mAdapter = new CommonAdapter<BillBean>(getContext(), mData, R.layout.item_list_non_pay) {
            @Override
            public void convert(ViewHolder viewHolder, final BillBean s, int i) {
                viewHolder.setText(R.id.tv_money, "￥" + s.getReal_amount());
                viewHolder.setText(R.id.tv_order_number, s.getOrder_no());
                viewHolder.setText(R.id.tv_status, s.getStatus());
                viewHolder.setText(R.id.tv_address, s.getRoomInfo().getRoom_name());
                viewHolder.setText(R.id.tv_time, s.getStart_time() + "-" + s.getEnd_time());
                ImageView ivPhoto = viewHolder.getView(R.id.icon_room);
                Picasso.get().load(s.getRoomInfo().getImage()).into(ivPhoto);
                Button btnCollection = viewHolder.getView(R.id.btn_collection);
                btnCollection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        collection(s.getOrder_no());
                    }
                });
                notifyDataSetChanged();
            }
        };
        mListNonPay.setAdapter(mAdapter);
    }

    private void collection(String order_no) {

        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().reminderRent(order_no)
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
                        if (dataInfo.success()){
                            showToast(dataInfo.msg());
                        }else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
