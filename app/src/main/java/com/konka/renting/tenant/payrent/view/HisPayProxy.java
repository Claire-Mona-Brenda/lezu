package com.konka.renting.tenant.payrent.view;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.payrent.BasePresenter;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by ATI on 2018/4/1.
 */

public class HisPayProxy extends BasePresenter {
    RecyclerView recyclerView;
    RefreshLayout refresh;
    PayHistoryAdapter payHistoryAdapter;
    List<PayOrder>list=new ArrayList<>();
    int p=1;
    public void init(View view,final String no){
        payHistoryAdapter=new PayHistoryAdapter(view.getContext(),list);
        recyclerView = view.findViewById(R.id.listView1);
        refresh = view.findViewById(R.id.refresh);
        LinearLayoutManager lm = new LinearLayoutManager(view.getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(payHistoryAdapter);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                list.clear();
                p=1;
                getData(no);
                refreshlayout.finishRefresh();
            }
        });
        refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                p++;
                getData(no);
                refreshlayout.finishLoadmore();

            }
        });
        refresh.autoRefresh();
    }
    public void getData(String no) {
        rx.Observable<DataInfo<PayOrder>> observable = null;
        observable = RetrofitHelper.getInstance().getHistoryOrderList(no,p);

        Subscription subscription = (observable.compose(RxUtil.<DataInfo<PayOrder>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PayOrder>>() {
            @Override
            public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                Log.d("jia", "");
                e.printStackTrace();
            }

            @Override
            public void onNext(DataInfo<PayOrder> homeInfoDataInfo) {
//                        dismiss();

                if (homeInfoDataInfo.success()) {
//                    homeInfoDataInfo.data();
                    bindData(homeInfoDataInfo.data().lists);
                } else {
//                            showToast(homeInfoDataInfo.msg());
                }
            }
        }));
        addSubscrebe(subscription);

    }
    public void bindData(List<PayOrder>data){
        list.addAll(data);
        payHistoryAdapter.notifyDataSetChanged();

    }
}
