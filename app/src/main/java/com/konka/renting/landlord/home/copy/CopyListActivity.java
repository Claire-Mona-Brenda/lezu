package com.konka.renting.landlord.home.copy;

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
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.bean.RoomInfo;
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

public class CopyListActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private int mPage = 1;

    private List<OrderInfo> mReadingInfos = new ArrayList<>();
    private CommonAdapter<OrderInfo> mReadingInfoCommonAdapter;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, CopyListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_copy_list;
    }

    @Override
    public void init() {
        setTitleText(R.string.copy_title);
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
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CopyDesActivity.toActivity(mActivity, mReadingInfos.get(position));
            }
        });
    }

    private void getData(final boolean isRefresh) {
        int page = mPage;
        if (isRefresh) {
            mPage = 1;
            page = 1;
        } else {
            page++;
        }

        Subscription subscription = RetrofitHelper.getInstance().getReadingList(page)
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
            mReadingInfos.clear();
        }

        mReadingInfos.addAll(listInfo.lists);
        mReadingInfoCommonAdapter.notifyDataSetChanged();
    }

    private void initList() {
        mReadingInfoCommonAdapter = new CommonAdapter<OrderInfo>(this, mReadingInfos, R.layout.item_landlord_copy) {
            @Override
            public void convert(ViewHolder viewHolder, final OrderInfo orderInfo, int i) {

                RoomInfo roomInfo = orderInfo.roomInfo;
                viewHolder.setText(R.id.tv_title, roomInfo.getRoomTitle());
                String date = getString(R.string.roominfo_date_time);
                date = String.format(date, orderInfo.start_time, orderInfo.end_time);
                viewHolder.setText(R.id.tv_date, date);

                viewHolder.setText(R.id.tv_type,
                        String.format(getString(R.string.room_type_title), roomInfo.getType()));
                viewHolder.setSelected(R.id.tv_type, roomInfo.isLongRent());

                viewHolder.setVisible(R.id.tv_price, false);

                Picasso.get().load(roomInfo.image).into((ImageView) viewHolder.getView(R.id.iv_icon));
                viewHolder.setOnClickListener(R.id.frame_bill, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CopyDesActivity.toActivity(mActivity, orderInfo);
                    }
                });
            }
        };
        mListview.setAdapter(mReadingInfoCommonAdapter);

    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
