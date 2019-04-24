package com.konka.renting.landlord.user.withdrawcash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.WithDrawRecordBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;

public class WithdrawRecordFragment extends BaseFragment {

    @BindView(R.id.list_withdraw_record)
    ListView listWithdrawRecord;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    private WithdrawAdapter mAdapter;
    List<WithDrawRecordBean> mData = new ArrayList<>();
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private int page;
    Unbinder unbinder;

    public WithdrawRecordFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static WithdrawRecordFragment newInstance() {
        WithdrawRecordFragment fragment = new WithdrawRecordFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_withdraw_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }


    @Override
    public void init() {
        linTitle.setVisibility(View.GONE);
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

    private void initList() {
        mAdapter = new WithdrawAdapter(getContext());
        listWithdrawRecord.setAdapter(mAdapter);
    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = RetrofitHelper.getInstance().getWithDrawRecord(page)
                .compose(RxUtil.<DataInfo<ListInfo<WithDrawRecordBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<WithDrawRecordBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else
                            mRefresh.finishLoadmore();
                    }

                    @Override
                    public void onNext(DataInfo<ListInfo<WithDrawRecordBean>> listInfoDataInfo) {

                        if (type == REFRESH) {
                            mRefresh.finishRefresh();
                            mData.clear();
                        } else
                            mRefresh.finishLoadmore();
                        if (listInfoDataInfo.success()) {
                            mData.addAll(listInfoDataInfo.data().lists);
                            mAdapter.setData(mData);
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
