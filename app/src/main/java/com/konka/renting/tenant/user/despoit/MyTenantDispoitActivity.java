package com.konka.renting.tenant.user.despoit;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.TenantDespoitlistBean;
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
import butterknife.OnClick;
import rx.Subscription;

public class MyTenantDispoitActivity extends BaseActivity {

    @BindView(R.id.list_checkin)
    ListView mListCheckin;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private List<TenantDespoitlistBean> mData = new ArrayList<>();
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private int page;
    private CommonAdapter<TenantDespoitlistBean> mAdapter;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MyTenantDispoitActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_tenant_dispoit;
    }

    @Override
    public void init() {
        setTitleText(R.string.tenant_my_despoit);
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
        mAdapter = new CommonAdapter<TenantDespoitlistBean>(this, mData, R.layout.item_list_my_despoit) {
            @Override
            public void convert(ViewHolder viewHolder, TenantDespoitlistBean s, int i) {
                viewHolder.setText(R.id.tv_order_number,s.getMerge_order_no());
                viewHolder.setText(R.id.tv_status,s.getStatus_name());
                viewHolder.setText(R.id.tv_address,s.getRoomInfo().getRoom_name());
                viewHolder.setText(R.id.tv_despoit_money,s.getLock_deposit());
                ImageView ivPhoto = viewHolder.getView(R.id.icon_room);
                Picasso.get().load(s.getRoomInfo().getImage()).into(ivPhoto);
                viewHolder.setText(R.id.tv_time,s.getStart_time()+"-"+s.getEnd_time());
                notifyDataSetChanged();
            }
        };
        mListCheckin.setAdapter(mAdapter);
        mListCheckin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyDespoitDetailActivity.toActivity(MyTenantDispoitActivity.this,mData.get(i).getMerge_order_no());
            }
        });
    }

    private void initData(final int type) {

        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = RetrofitHelper.getInstance().getTenantDespoit(page)
                .compose(RxUtil.<DataInfo<ListInfo<TenantDespoitlistBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<TenantDespoitlistBean>>>() {
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
                    public void onNext(DataInfo<ListInfo<TenantDespoitlistBean>> listInfoDataInfo) {
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

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
