package com.konka.renting.landlord.home.search;


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
import com.konka.renting.event.ToSearchResultEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

/**
 *
 */
public class
SearchResultFragment extends BaseFragment {


    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    private CommonAdapter<RenterSearchListBean> mCommonAdapter;

    private List<RenterSearchListBean> mRoomInfos = new ArrayList<>();

    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        return fragment;
    }

    public SearchResultFragment() {
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
        Log.e(TAG, "init: ");
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setEnableRefresh(false);

        mCommonAdapter = new CommonAdapter<RenterSearchListBean>(mActivity, mRoomInfos, R.layout.item_landlord_home_hot) {
            @Override
            public void convert(ViewHolder viewHolder, RenterSearchListBean bean, int i) {
//                String title = String.format(getString(R.string.roomlist_title_des), roomInfo.address, roomInfo.building_no, roomInfo.door_no, roomInfo.room_name);
                viewHolder.setText(R.id.tv_title, bean.getRoom_name());

                String description = getString(R.string.roomlist_description_des);
                description = String.format(description, bean.getRoom_type(), bean.getMeasure_area() + "", bean.getFloor() + "", bean.getTotal_floor() + "");
                viewHolder.setText(R.id.tv_date, description);

                viewHolder.setText(R.id.tv_type, bean.getType() == 1 ? "短租" : "长租");
                viewHolder.setSelected(R.id.tv_type, bean.getType() == 2);

                TextView tv_price = viewHolder.getView(R.id.tv_price);
//                tv_price.setText(Html.fromHtml("<font color='#ff4707'>¥" + roomInfo.housing_price + "</font>/月"));
                String unit = bean.getType() == 1 ? "天" : "月";
                tv_price.setText("¥" + (int) Float.parseFloat(bean.getHousing_price()) + "/" + unit);
                if (!TextUtils.isEmpty(bean.getThumb_image()))
                    Picasso.get().load(bean.getThumb_image()).into((ImageView) viewHolder.getView(R.id.iv_icon));
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).into((ImageView) viewHolder.getView(R.id.iv_icon));
            }
        };
        mListview.setAdapter(mCommonAdapter);

        addRxBusSubscribe(ToSearchResultEvent.class, new Action1<ToSearchResultEvent>() {
            @Override
            public void call(ToSearchResultEvent toSearchResultEvent) {
                SpHistoryStorage.getInstance(mActivity, 10).save(toSearchResultEvent.content);
                getSearchResultList(toSearchResultEvent.content);
//                getSearchResult(toSearchResultEvent.content);
            }
        });

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomInfoActivity.toActivity(mActivity, mRoomInfos.get(position).getRoom_id());
            }
        });
    }
//
//    private void getSearchResult(String keyword) {
//        LocationInfo locationInfo = LocationUtils.getInstance();
//        String city_id = locationInfo == null ? "" : locationInfo.city_id;
//        showLoadingDialog();
//        Subscription subscription = RetrofitHelper.getInstance().getSearchRoomList(keyword,city_id)
//                .compose(RxUtil.<DataInfo<ListInfo<RoomInfo>>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<ListInfo<RoomInfo>>>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        doFailed();
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<ListInfo<RoomInfo>> listInfoDataInfo) {
//                        dismiss();
//                        if (listInfoDataInfo.success()) {
//                            mRoomInfos.clear();
//                            mRoomInfos.addAll(listInfoDataInfo.data().lists);
//                            mCommonAdapter.notifyDataSetChanged();
//                        } else {
//                            dismiss();
//                            showToast(listInfoDataInfo.msg());
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

    private void getSearchResultList(String keyword) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList("1", "", "", "", keyword)
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            mRoomInfos.clear();
                            mRoomInfos.addAll(dataInfo.data().getList());
                            mCommonAdapter.notifyDataSetChanged();
                        } else {
                            dismiss();
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
