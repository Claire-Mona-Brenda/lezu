package com.konka.renting.landlord.order;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OrderRentingHistoryBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
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

public class CollectRentActivity extends BaseActivity {

    private CommonAdapter<OrderRentingHistoryBean.ListsBean> mAdapter;
    private OrderRentingHistoryBean.RentingInfoBean rentinginfo;
    private TextView tvOrderNum;
    private TextView tvStatus;
    private ImageView ivRoomPhoto;
    private TextView tvRoomName;
    private TextView tvTime;
    private TextView tvHousePrice;
    private TextView tvIncomeMoney;
    private TextView tvTenantNum;
    private LinearLayout linGridImage;

    public static void toActivity(Context context, String orederNum) {
        Intent intent = new Intent(context, CollectRentActivity.class);
        intent.putExtra("ordernum", orederNum);
        context.startActivity(intent);
    }

    @BindView(R.id.list_rent_history)
    ListView mListRentHistory;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private List<OrderRentingHistoryBean.ListsBean> mData = new ArrayList<>();
    int page;
    String orderNum;
    int REFRESH = 1;
    int LOADMORE = 2;

    @Override
    public int getLayoutId() {
        return R.layout.activity_collect_rent;
    }

    @Override
    public void init() {
        orderNum = getIntent().getStringExtra("ordernum");
        showError(orderNum);
        setTitleText(R.string.income_history);
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
        mAdapter = new CommonAdapter<OrderRentingHistoryBean.ListsBean>(this, mData, R.layout.item_collect_history) {
            @Override
            public void convert(ViewHolder viewHolder, OrderRentingHistoryBean.ListsBean s, int i) {
                viewHolder.setText(R.id.tv_money, s.getHousing_price());
                viewHolder.setText(R.id.tv_time, s.getStart_time() + "-" + s.getEnd_time());
                viewHolder.setText(R.id.tv_name, s.getNickname());
                notifyDataSetChanged();
            }
        };
        View view = LayoutInflater.from(this).inflate(R.layout.layout_rent_list_header, null);
        mListRentHistory.addHeaderView(view);
        mListRentHistory.setAdapter(mAdapter);
        tvOrderNum = view.findViewById(R.id.tv_order_number);
        tvStatus = view.findViewById(R.id.tv_status);
        ivRoomPhoto = view.findViewById(R.id.icon_room);
        tvRoomName = view.findViewById(R.id.tv_address);
        tvTime = view.findViewById(R.id.tv_time);
        tvHousePrice = view.findViewById(R.id.tv_money);
        tvIncomeMoney = view.findViewById(R.id.tv_income_money);
        tvTenantNum = view.findViewById(R.id.tv_tenant_num);
        linGridImage = view.findViewById(R.id.iv_grid_image);

    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = RetrofitHelper.getInstance().getOrderRentingHistory(orderNum, page)
                .compose(RxUtil.<DataInfo<OrderRentingHistoryBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<OrderRentingHistoryBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else
                            mRefresh.finishLoadmore();
                        doFailed();
                        showError(e.getMessage());
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(DataInfo<OrderRentingHistoryBean> orderRentingHistoryBeanDataInfo) {
                        if (type == REFRESH) {
                            mRefresh.finishRefresh();
                            mData.clear();
                        } else
                            mRefresh.finishLoadmore();
                        if (orderRentingHistoryBeanDataInfo.success()) {
                            mData.addAll(orderRentingHistoryBeanDataInfo.data().getLists());
                            rentinginfo = orderRentingHistoryBeanDataInfo.data().getRentingInfo();
                            tvOrderNum.setText(rentinginfo.getMerge_order_no());
                            tvStatus.setText(rentinginfo.getStatus_name());
                            Picasso.get().load(rentinginfo.getRoomInfo().getImage()).into(ivRoomPhoto);
                            tvRoomName.setText(rentinginfo.getRoomInfo().getRoom_name());
                            tvTime.setText(rentinginfo.getStart_time() + "-" + rentinginfo.getEnd_time());
                            tvHousePrice.setText(rentinginfo.getHousing_price());
                            tvIncomeMoney.setText(rentinginfo.getTotal_price());
                            tvTenantNum.setText(rentinginfo.getMemberCount() + "");
                            linGridImage.removeAllViews();
                            List<OrderRentingHistoryBean.RentingInfoBean.MemberListBean> mList = orderRentingHistoryBeanDataInfo.data().getRentingInfo().getMemberList();
                            if (mList == null)
                                return;
                            for (OrderRentingHistoryBean.RentingInfoBean.MemberListBean memberListBean : mList) {
                                ImageView imageView = new ImageView(CollectRentActivity.this);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(UIUtils.dip2px(30), UIUtils.dip2px(30)));
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                if (!TextUtils.isEmpty(memberListBean.getHeadimgurl()))
                                    Picasso.get().load(memberListBean.getHeadimgurl()).into(imageView);
                                else
                                    Picasso.get().load(R.mipmap.icon_photo).into(imageView);
                                linGridImage.addView(imageView);

                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast(orderRentingHistoryBeanDataInfo.msg());
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
