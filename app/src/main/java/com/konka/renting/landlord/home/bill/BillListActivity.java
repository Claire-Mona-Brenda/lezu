package com.konka.renting.landlord.home.bill;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.BillListBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.user.bill.BillInfoActivity;
import com.konka.renting.landlord.user.bill.BillListRecycleAdapter;
import com.konka.renting.landlord.user.bill.BillMonInfoActivity;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

//public class BillListActivity extends BaseTabListActivity {
public class BillListActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.activity_bill_list_recycle)
    RecyclerView mRecycle;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;

    List<BillListBean> listBeans=new ArrayList<>();
    Set<String> dateSet=new HashSet<>();
    BillListRecycleAdapter recycleAdapter;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, BillListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_billlist;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.bill_title);

        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new BillListRecycleAdapter(this, listBeans,dateSet);
        recycleAdapter.setmOnItemClickListen(new BillListRecycleAdapter.OnItemClickListen() {
            @Override
            public void onClick(int position) {
                BillInfoActivity.toActivity(BillListActivity.this,listBeans.get(position).getId());
            }

            @Override
            public void onMonClick(int position) {
                BillMonInfoActivity.toActivity(BillListActivity.this,listBeans.get(position).getId());
            }
        });
        mRecycle.setAdapter(recycleAdapter);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        refresh.autoRefresh();
    }

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getAccountBillList("1")
                .compose(RxUtil.<DataInfo<PageDataBean<BillListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<BillListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        e.printStackTrace();
                        refresh.finishRefresh();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<BillListBean>> dataInfo) {
                        refresh.finishRefresh();
                        dismiss();
                        if (dataInfo.success()) {
                            dateSet.clear();
                            listBeans.clear();
                            listBeans.addAll(dataInfo.data().getList());
                            recycleAdapter.notifyDataSetChanged();
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void getDate(String date){

    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
