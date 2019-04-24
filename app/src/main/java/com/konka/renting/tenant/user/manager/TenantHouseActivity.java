package com.konka.renting.tenant.user.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.payrent.view.PayHouseAdapter2;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

/**
 * Created by kaite on 2018/4/24.
 */

public class TenantHouseActivity extends BaseActivity {
    PayHouseAdapter2 payHouseAdapter1;
    List<RenterOrderListBean> list1 = new ArrayList<>();
    @BindView(R.id.listView1)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private int page;
    private int REFRESH = 1;
    private int LOADMORE = 2;

    public static void toActivity(Context context) {

        Intent intent = new Intent(context, TenantHouseActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tenant_house;
    }

    @Override
    public void init() {
        setTitleText(R.string.rent_manager);
        payHouseAdapter1 = new PayHouseAdapter2(this, list1);
        recyclerView.setAdapter(payHouseAdapter1);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(REFRESH);
            }
        });
        mRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(LOADMORE);
            }
        });
        mRefresh.autoRefresh();
    }

    public void getData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Observable<DataInfo<PayOrder>> observable = null;
        observable = RetrofitHelper.getInstance().getRenterRoomList("1", page);

        Subscription subscription = (observable.compose(RxUtil.<DataInfo<PayOrder>>rxSchedulerHelper()).subscribe(new CommonSubscriber<DataInfo<PayOrder>>() {
            @Override
            public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                Log.d("jia", "");
                e.printStackTrace();
                if (type == REFRESH)
                    mRefresh.finishRefresh();
                else
                    mRefresh.finishLoadmore();
            }

            @Override
            public void onNext(DataInfo<PayOrder> homeInfoDataInfo) {
//                        dismiss();

                if (type == REFRESH) {
                    list1.clear();
                    mRefresh.finishRefresh();
                } else
                    mRefresh.finishLoadmore();
                if (homeInfoDataInfo.success()) {
                    homeInfoDataInfo.data();
                    bindData(homeInfoDataInfo.data().lists);
                } else {
//                            showToast(homeInfoDataInfo.msg());
                }
            }
        }));
        addSubscrebe(subscription);

    }

    public void bindData(List<PayOrder> list) {
//        list1.addAll(list);
        payHouseAdapter1.notifyDataSetChanged();


    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
