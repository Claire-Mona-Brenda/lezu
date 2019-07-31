package com.konka.renting.landlord.home.bill;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.tenanter.TenanterDesDialog;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
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
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;

/**
 */
public abstract class BaseBillFragment extends BaseFragment {


    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private int mPage = 1;
    Unbinder unbinder;

    private List<OrderInfo> mRoomInfos = new ArrayList<>();
    CommonAdapter<OrderInfo> mAdapter;
    private TenanterDesDialog mTenanterDesDialog;

    public BaseBillFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tenanter, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void init() {
        super.init();
        initList();

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });

        mRefreshLayout.autoRefresh();

    }

    private void getData(final boolean isRefresh) {
        int page = mPage;
        if (isRefresh) {
            mPage = 1;
            page = 1;
        } else {
            page++;
        }

        Subscription subscription = getObservable(page)
                .compose(RxUtil.<DataInfo<ListInfo<OrderInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<OrderInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        finishLoad(isRefresh, false, null);
                    }

                    @Override
                    public void onNext(DataInfo<ListInfo<OrderInfo>> listInfoDataInfo) {
                        finishLoad(isRefresh, listInfoDataInfo.success(), listInfoDataInfo.data());
                        if (listInfoDataInfo.success()) {
                            refreshData(isRefresh, listInfoDataInfo.data());
                        } else {
                            showToast(listInfoDataInfo.msg());
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    private void finishLoad(boolean isRefresh, boolean isSuccess, ListInfo listInfo) {
        mRefreshLayout.finishLoadmore();
        mRefreshLayout.finishRefresh();

        if (listInfo == null)
            return;

        if (!isSuccess)
            return;

        if (isRefresh) {
            mPage = 1;
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setEnableLoadmore(listInfo.totalPages > mPage);
        }

        if (!isRefresh) {
            if (mPage < listInfo.totalPages) {
                mPage++;
            } else {
                mRefreshLayout.setEnableLoadmore(false);
            }
        }
    }

    private void refreshData(boolean isRefresh, ListInfo<OrderInfo> listInfo) {
        if (isRefresh) {
            mRoomInfos.clear();
        }

        mRoomInfos.addAll(listInfo.lists);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    abstract Observable<DataInfo<ListInfo<OrderInfo>>> getObservable(int page);

    abstract boolean isPayed();

    private void initList() {
        mAdapter = new CommonAdapter<OrderInfo>(getContext(), mRoomInfos, R.layout.item_landlord_bill) {
            @Override
            public void convert(ViewHolder viewHolder, final OrderInfo orderInfo, int i) {
                final RoomInfo roomInfo = orderInfo.roomInfo;
                viewHolder.setText(R.id.tv_title, roomInfo.getRoomTitle());
                String date = getString(R.string.roominfo_date_time);
                date = String.format(date, orderInfo.start_time, orderInfo.end_time);
                viewHolder.setText(R.id.tv_date, date);

                viewHolder.setText(R.id.tv_type,
                        String.format(getString(R.string.room_type_title), roomInfo.getType()));
                viewHolder.setSelected(R.id.tv_type, roomInfo.isLongRent());

                viewHolder.setText(R.id.tv_price, roomInfo.housing_price);
                Picasso.get().load(roomInfo.image).into((ImageView) viewHolder.getView(R.id.iv_icon));

                viewHolder.setText(R.id.tv_serial_num, String.format(getString(R.string.bill_serial_num), orderInfo.order_no));
                viewHolder.setText(R.id.tv_pay_result, getString(isPayed() ? R.string.bill_pay : R.string.bill_unpay));
                viewHolder.setVisible(R.id.frame_bill, !isPayed());
                Button mBtnCollection = viewHolder.getView(R.id.btn_collection);
                LinearLayout mLinBill = viewHolder.getView(R.id.layout_bill);
                mBtnCollection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        collection(orderInfo.order_no);
                    }
                });
                mLinBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RoomInfoActivity.toActivity(mActivity,orderInfo.roomInfo.id);
                    }
                });

            }
        };
        mListview.setAdapter(mAdapter);

    }
    private void collection(String order_no) {

//        showLoadingDialog();
//        Subscription subscription = RetrofitHelper.getInstance().reminderRent(order_no)
//                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
//                    }
//
//                    @Override
//                    public void onNext(DataInfo dataInfo) {
//
//                        dismiss();
//                        if (dataInfo.success()){
//                            showToast(dataInfo.msg());
//                        }else {
//                            showToast(dataInfo.msg());
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
    }
}
