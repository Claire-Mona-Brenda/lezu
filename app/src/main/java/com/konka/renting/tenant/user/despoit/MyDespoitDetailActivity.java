package com.konka.renting.tenant.user.despoit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.TenantDespoitDetailBean;
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

public class MyDespoitDetailActivity extends BaseActivity {

    @BindView(R.id.list_despoit)
    ListView mListDespoit;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private List<TenantDespoitDetailBean.ListsBean> mData = new ArrayList<>();
    private String orderNum;

    int REFRESH = 1;
    int LOADMORE = 2;
    private int page;
    private CommonAdapter<TenantDespoitDetailBean.ListsBean> mAdapter;
    private TextView tvOrderNum;
    private TextView tvroomName;
    private TextView tvStatus;
    private TextView tvtime;
    private TextView tvDespoitMoney;
    private ImageView ivPhoto;

    public static void toActivity(Context context, String merge_order_no) {
        Intent intent = new Intent(context, MyDespoitDetailActivity.class);
        intent.putExtra("orderNum", merge_order_no);
        context.startActivity(intent);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_despoit_detail;
    }

    @Override
    public void init() {
        setTitleText(R.string.tenant_my_despoit);
        orderNum = getIntent().getStringExtra("orderNum");
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
        mAdapter = new CommonAdapter<TenantDespoitDetailBean.ListsBean>(this, mData, R.layout.item_tenant_despoit_detail) {
            @Override
            public void convert(ViewHolder viewHolder, TenantDespoitDetailBean.ListsBean s, int i) {
                viewHolder.setText(R.id.tv_money, s.getAmount());
                viewHolder.setText(R.id.tv_time, s.getStart_time() + "-" + s.getEnd_time());
                notifyDataSetChanged();
            }
        };

        mListDespoit.setAdapter(mAdapter);
        View view = LayoutInflater.from(this).inflate(R.layout.header_despoit_detail, null);
        tvOrderNum = view.findViewById(R.id.tv_order_number);
        tvroomName = view.findViewById(R.id.tv_address);
        tvStatus = view.findViewById(R.id.tv_order_number);
        tvtime = view.findViewById(R.id.tv_time);
        tvDespoitMoney = view.findViewById(R.id.tv_despoit_money);
        ivPhoto = view.findViewById(R.id.icon_room);
        mListDespoit.addHeaderView(view);
    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = RetrofitHelper.getInstance().getTenantDespoitDetail(orderNum, page)
                .compose(RxUtil.<DataInfo<TenantDespoitDetailBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<TenantDespoitDetailBean>>() {
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
                    public void onNext(DataInfo<TenantDespoitDetailBean> tenantDespoitDetailBeanDataInfo) {
                        if (type == REFRESH) {
                            mData.clear();
                            mRefresh.finishRefresh();
                        } else
                            mRefresh.finishLoadmore();
                        if (tenantDespoitDetailBeanDataInfo.success()) {
                            mData.addAll(tenantDespoitDetailBeanDataInfo.data().getLists());
                            tvOrderNum.setText(tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getMerge_order_no());
                            tvroomName.setText(tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getRoomInfo().getRoom_name());
                            tvStatus.setText(tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getStatus_name());
                            tvtime.setText(tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getStart_time()+"-"+tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getEnd_time());
                            tvDespoitMoney.setText("￥"+tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getLock_deposit());
                            Picasso.get().load(tenantDespoitDetailBeanDataInfo.data().getRentingInfo().getRoomInfo().getImage()).into(ivPhoto);
                            mAdapter.notifyDataSetChanged();
                        }else {
                            showToast(tenantDespoitDetailBeanDataInfo.msg());
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
