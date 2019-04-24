package com.konka.renting.landlord.home.freehouse;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
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

public class FreeHouseActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int mPage = 1;
    private CommonAdapter<RoomInfo> mRoomInfoCommonAdapter;

    private List<RoomInfo> mData = new ArrayList<>();

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, FreeHouseActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_free_house;
    }

    @Override
    public void init() {
        setTitleText(R.string.freehouse_title);
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


        Subscription subscription = RetrofitHelper.getInstance().getLandlordFreeRoomListData(page)
                .compose(RxUtil.<DataInfo<ListInfo<RoomInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<RoomInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        finishLoad(isRefresh, false, null);
                    }

                    @Override
                    public void onNext(DataInfo<ListInfo<RoomInfo>> listInfoDataInfo) {
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
        } else {
            if (mPage < listInfo.totalPages) {
                mPage++;
            } else {
                mRefreshLayout.setEnableLoadmore(false);
            }
        }


    }

    private void refreshData(boolean isRefresh, ListInfo<RoomInfo> listInfo) {
        if (isRefresh) {
            mData.clear();
        }

        mData.addAll(listInfo.lists);
        mRoomInfoCommonAdapter.notifyDataSetChanged();
    }

    private void initList() {
        mRoomInfoCommonAdapter = new CommonAdapter<RoomInfo>(this, mData, R.layout.item_landlord_freehouse) {
            @Override
            public void convert(ViewHolder viewHolder, final RoomInfo roomInfo, int i) {
                viewHolder.setText(R.id.tv_title, roomInfo.getRoomTitle());
//                String date = getString(R.string.roominfo_date_time);
//                date = String.format(date, roomInfo.lease_start_time, roomInfo.lease_end_time);
                viewHolder.setVisible(R.id.tv_date, false);
                viewHolder.setText(R.id.tv_password, roomInfo.lock_pwd);
                viewHolder.setText(R.id.tv_price, roomInfo.housing_price);

                viewHolder.setText(R.id.tv_type,
                        String.format(getString(R.string.room_type_title), roomInfo.getType()));
                viewHolder.setSelected(R.id.tv_type, roomInfo.isLongRent());

                Picasso.get().load(roomInfo.image).into((ImageView) viewHolder.getView(R.id.iv_icon));

                viewHolder.setOnClickListener(R.id.btn_copy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String copyText = roomInfo.lock_pwd;
                        ClipData clipData = ClipData.newPlainText("", copyText);
                        ((ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
                        doSuccess();
                    }
                });
            }
        };
        mListview.setAdapter(mRoomInfoCommonAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
