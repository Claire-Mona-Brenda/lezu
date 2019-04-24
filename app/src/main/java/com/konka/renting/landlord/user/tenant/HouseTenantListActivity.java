package com.konka.renting.landlord.user.tenant;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.TenantListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class HouseTenantListActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.activity_house_tenantlist_recycleView)
    RecyclerView mRecycleView;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;

    List<TenantListBean> mListBeans;
    TenantListAdapter mAdapter;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, HouseTenantListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_house_tenantlist;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.tenanter_list);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mListBeans = new ArrayList<>();
        mAdapter = new TenantListAdapter(mListBeans, this);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setmClickListent(new TenantListAdapter.OnItemClickListent() {
            @Override
            public void clickCall(String phone) {
                PhoneUtil.call(phone, getBaseContext());
            }
        });
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        refresh.autoRefresh();
    }

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterList("1")
                .compose(RxUtil.<DataInfo<PageDataBean<TenantListBean>>> rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<TenantListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        e.printStackTrace();
                        refresh.finishRefresh();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<TenantListBean>> dataInfo) {
                        refresh.finishRefresh();
                        dismiss();
                        if (dataInfo.success()) {
                            mListBeans.clear();
                            mListBeans.addAll(dataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
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
