package com.konka.renting.landlord.home.tenanter;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.RoomInfo;
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
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;

/**
 */
public abstract class TenanterFragment extends BaseFragment {


    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private int mPage = 1;
    Unbinder unbinder;

    private List<RoomInfo> mRoomInfos = new ArrayList<>();
    CommonAdapter<RoomInfo> mAdapter;
    private TenanterDesDialog mTenanterDesDialog;

    public TenanterFragment() {
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
        if (isRefresh){
            mPage = 1;
            page = mPage;
        }else {
            page = mPage +1;
        }

        Subscription subscription = getObservable(page)
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
        }

        if (!isRefresh) {
            if (mPage < listInfo.totalPages) {
                mPage++;
            } else {
                mRefreshLayout.setEnableLoadmore(false);
            }
        }
    }

    private void refreshData(boolean isRefresh, ListInfo<RoomInfo> listInfo) {
        if (isRefresh) {
            mRoomInfos.clear();
        }

        mRoomInfos.addAll(listInfo.lists);
        mAdapter.notifyDataSetChanged();
    }

    private void toDes(RoomInfo.Member member) {
        if (mTenanterDesDialog == null) {
            mTenanterDesDialog = new TenanterDesDialog(getActivity());
        }
        mTenanterDesDialog.show(member);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public abstract Observable<DataInfo<ListInfo<RoomInfo>>> getObservable(int page);

    private void initList() {
        mAdapter = new CommonAdapter<RoomInfo>(getContext(), mRoomInfos, R.layout.item_landlord_tenant) {
            @Override
            public void convert(ViewHolder viewHolder, RoomInfo roomInfo, int i) {
                viewHolder.setText(R.id.tv_title, roomInfo.getRoomTitle());
                viewHolder.setVisible(R.id.tv_date, roomInfo.isRentOut());
                String date = getString(R.string.roominfo_date_time);
                date = String.format(date, roomInfo.lease_start_time, roomInfo.lease_end_time);
                viewHolder.setText(R.id.tv_date, date);
                viewHolder.setText(R.id.tv_price, roomInfo.housing_price);

                viewHolder.setText(R.id.tv_type,
                        String.format(getString(R.string.room_type_title), roomInfo.getType()));
                viewHolder.setSelected(R.id.tv_type, roomInfo.isLongRent());
                if (!TextUtils.isEmpty(roomInfo.image)){
                    Picasso.get().load(roomInfo.image).resize(1000,1000).into((ImageView) viewHolder.getView(R.id.iv_icon));
                }

                if (!TextUtils.isEmpty(roomInfo.member_count)) {
                    if (Integer.parseInt(roomInfo.member_count) == 0) {
                        return;
                    } else {
                        viewHolder.setVisible(R.id.linear_members_item, true);
                    }
                }else {
                    return;
                }

                viewHolder.setText(R.id.tv_num, String.format(getString(R.string.tenanter_count), roomInfo.member_count));


                //添加房客
                LinearLayout linearLayout = viewHolder.getView(R.id.linear_members);
                linearLayout.removeAllViews();
                List<RoomInfo.Member> members = roomInfo.memberList;
                if (members == null)
                    return;

                for (final RoomInfo.Member member : members) {
                    ImageView imageView = new ImageView(mActivity);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(UIUtils.dip2px(30), UIUtils.dip2px(30)));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.get().load(member.headimgurl).into(imageView);
                    linearLayout.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toDes(member);
                        }
                    });
                }
            }
        };
        mListview.setAdapter(mAdapter);
    }
}
