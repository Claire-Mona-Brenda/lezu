package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.widget.ListView;


import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
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
import butterknife.OnClick;
import rx.Subscription;

public class WithdrawRecordActivity extends BaseActivity {

    @BindView(R.id.list_withdraw_record)
    ListView listWithdrawRecord;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private WithdrawAdapter mAdapter;
    List<WithDrawRecordBean> mData = new ArrayList<>();
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private int page;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, WithdrawRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw_record;
    }

    @Override
    public void init() {
        setTitleText(R.string.withdraw_record);
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
        mAdapter = new WithdrawAdapter(this);
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
                        if (listInfoDataInfo.success()){
                            mData.addAll(listInfoDataInfo.data().lists);
                            mAdapter.setData(mData);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
