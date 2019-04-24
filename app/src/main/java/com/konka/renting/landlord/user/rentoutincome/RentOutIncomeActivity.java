package com.konka.renting.landlord.user.rentoutincome;

import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class RentOutIncomeActivity extends BaseActivity {
    @BindView(R.id.list_rentout_income)
    ListView mListRentoutIncome;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    List<RentOutIncomeBean> mData = new ArrayList<>();
    private int page;
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private CommonAdapter<RentOutIncomeBean> mAdapter;


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, RentOutIncomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rent_out_income;
    }

    @Override
    public void init() {
        setTitleText(R.string.rent_out_income_title);
        initList();
        mRefresh.autoRefresh();
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
    }

    private void initList() {
        mAdapter = new CommonAdapter<RentOutIncomeBean>(this, mData, R.layout.item_rent_out_income) {
            @Override
            public void convert(ViewHolder viewHolder, RentOutIncomeBean rentOutIncomeBean, int i) {
                viewHolder.setText(R.id.tv_item_rentout_remark, rentOutIncomeBean.remark);
                viewHolder.setText(R.id.tv_item_rentout_room, rentOutIncomeBean.room);
                viewHolder.setVisible(R.id.tv_item_rentout_room,!rentOutIncomeBean.room.equals(""));
                viewHolder.setText(R.id.tv_rent_out_income, rentOutIncomeBean.type.equals("0") ? "+" + rentOutIncomeBean.amount : "-" + rentOutIncomeBean.amount);
                viewHolder.setText(R.id.tv_item_rentout_time, rentOutIncomeBean.time);
                viewHolder.setText(R.id.tv_rent_out_income_balance, rentOutIncomeBean.balance);
                notifyDataSetChanged();
            }
        };
        mListRentoutIncome.setAdapter(mAdapter);
    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = RetrofitHelper.getInstance().getRentOutIncome(page)
                .compose(RxUtil.<DataInfo<ListInfo<RentOutIncomeBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<RentOutIncomeBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else
                            mRefresh.finishLoadmore();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<ListInfo<RentOutIncomeBean>> listInfoDataInfo) {
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else
                            mRefresh.finishLoadmore();
                        if (listInfoDataInfo.success()) {
                            mRefresh.finishRefresh();
                            mRefresh.finishLoadmore();
                            mData.addAll(listInfoDataInfo.data().lists);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            showToast(listInfoDataInfo.msg());
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
