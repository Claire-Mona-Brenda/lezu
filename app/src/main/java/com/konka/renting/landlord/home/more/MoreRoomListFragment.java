package com.konka.renting.landlord.home.more;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterSearchListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.location.LocationInfo;
import com.konka.renting.location.LocationUtils;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
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
import rx.Subscription;

/**
 * Created by jzxiang on 31/03/2018.
 */
public class MoreRoomListFragment extends BaseFragment {


    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private int mPage = 1;
    Unbinder unbinder;

    private List<RenterSearchListBean> mRoomInfos = new ArrayList<>();
    CommonAdapter<RenterSearchListBean> mAdapter;

    public static MoreRoomListFragment newInstance() {
        MoreRoomListFragment fragment = new MoreRoomListFragment();
        return fragment;
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
//        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.autoRefresh();

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: position = " + position);
                RoomInfoActivity.toActivity(mActivity, mRoomInfos.get(position).getRoom_id());
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
        LocationInfo locationInfo = LocationUtils.getInstance();
        String city_id = locationInfo == null ? "" : locationInfo.city_id;

        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList("1", "", city_id, "", "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        finishLoad(isRefresh, false, null);
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> listInfoDataInfo) {
                        finishLoad(isRefresh, listInfoDataInfo.success(), listInfoDataInfo.data());
                        if (listInfoDataInfo.success()) {
                            refreshData(isRefresh, listInfoDataInfo.data().getList());
                        } else {
                            showToast(listInfoDataInfo.msg());
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    private void finishLoad(boolean isRefresh, boolean isSuccess, PageDataBean<RenterSearchListBean> listInfo) {
        mRefreshLayout.finishLoadmore();
        mRefreshLayout.finishRefresh();

        if (listInfo == null)
            return;

        if (!isSuccess)
            return;

        if (isRefresh) {
            mPage = 1;
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setEnableLoadmore(listInfo.getTotalPage() > mPage);
        }

        if (!isRefresh) {
            if (mPage < listInfo.getTotalPage()) {
                mPage++;
            } else {
                mRefreshLayout.setEnableLoadmore(false);
            }
        }
    }

    private void refreshData(boolean isRefresh, List<RenterSearchListBean> listInfo) {
        if (isRefresh) {
            mRoomInfos.clear();
        }

        mRoomInfos.addAll(listInfo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initList() {
        mAdapter = new CommonAdapter<RenterSearchListBean>(getActivity(), mRoomInfos, R.layout.item_landlord_home_hot) {
            @Override
            public void convert(com.mcxtzhang.commonadapter.lvgv.ViewHolder viewHolder, RenterSearchListBean bean, int i) {
//                String title = String.format(getString(R.string.roomlist_title_des), roomInfo.address, roomInfo.building_no, roomInfo.door_no, roomInfo.room_name);
                viewHolder.setText(R.id.tv_title, bean.getRoom_name());

                String description = getString(R.string.roomlist_description_des);
                description = String.format(description, bean.getRoom_type(), bean.getMeasure_area() + "", bean.getFloor() + "", bean.getTotal_floor() + "");

                viewHolder.setText(R.id.tv_date, description);

                viewHolder.setText(R.id.tv_type, bean.getType() == 1 ? "短租" : "长租");
                viewHolder.setSelected(R.id.tv_type, bean.getType() == 2);

                TextView tv_price = viewHolder.getView(R.id.tv_price);
                String unit = bean.getType() == 1 ? "/天" : "/月";
                tv_price.setText(bean.getHousing_price() + unit);
//                tv_price.setText(Html.fromHtml("<font color='#ff4707'>¥" + roomInfo.housing_price + "</font>/月"));
                if (!TextUtils.isEmpty(bean.getThumb_image()))
                    Picasso.get().load(bean.getThumb_image()).into((ImageView) viewHolder.getView(R.id.iv_icon));
            }
        };
        mListview.setAdapter(mAdapter);
    }

}
