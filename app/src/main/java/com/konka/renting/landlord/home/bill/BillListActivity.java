package com.konka.renting.landlord.home.bill;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.konka.renting.utils.DateTimeUtil;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

import static com.konka.renting.utils.DateTimeUtil.FORMAT_DATE;

//public class BillListActivity extends BaseTabListActivity {
public class BillListActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.activity_bill_list_recycle)
    RecyclerView mRecycle;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;

    List<BillListBean> listBeans = new ArrayList<>();
    Set<String> dateSet = new HashSet<>();
    HashMap<String,String> dateMap = new HashMap<>();
    BillListRecycleAdapter recycleAdapter;
    int page = 1;

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
        recycleAdapter = new BillListRecycleAdapter(this, listBeans, dateSet,dateMap);
        recycleAdapter.setmOnItemClickListen(new BillListRecycleAdapter.OnItemClickListen() {
            @Override
            public void onClick(int position) {
                BillInfoActivity.toActivity(BillListActivity.this, listBeans.get(position).getId());
            }

            @Override
            public void onMonClick(int position) {
                String[] time=listBeans.get(position).getCreate_time().split("-");
                String year="";
                String month="";
                if (time!=null&&time.length>=2){
                    year=time[0];
                    month=time[1];
                }
                BillMonInfoActivity.toActivity(BillListActivity.this,year,month );
            }
        });
        mRecycle.setAdapter(recycleAdapter);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }
        });
        refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });
        refresh.setEnableLoadmore(false);
        refresh.autoRefresh();
    }

    private void getData(boolean is) {
        if (is) {
            page = 1;
        } else {
            page++;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getAccountBillList2(page + "", "0", "", "")
                .compose(RxUtil.<DataInfo<PageDataBean<BillListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<BillListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (is) {
                            refresh.finishRefresh();
                        } else {
                            refresh.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<BillListBean>> dataInfo) {
                        if (dataInfo.success()) {
                            if (is) {
                                dateSet.clear();
                                dateMap.clear();
                                listBeans.clear();
                            }
                            listBeans.addAll(dataInfo.data().getList());
                            recycleAdapter.notifyDataSetChanged();
                            refresh.setEnableLoadmore(dataInfo.data().getPage() < dataInfo.data().getTotalPage());
                        } else {
                            showToast(dataInfo.msg());
                            if (!is) {
                                page--;
                            }
                        }
                        if (is) {
                            refresh.finishRefresh();
                        } else {
                            refresh.finishLoadmore();
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
