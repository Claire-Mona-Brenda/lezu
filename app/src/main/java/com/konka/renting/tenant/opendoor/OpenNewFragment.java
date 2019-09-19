package com.konka.renting.tenant.opendoor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.event.AddCodeEvent;
import com.konka.renting.event.AddShareRentEvent;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.opendoor.adapter.OpenItemAdapter;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.SharedPreferenceUtil;
import com.konka.renting.utils.UIUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

public class OpenNewFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.fragment_open_new_img_msg)
    ImageView mImgMsg;
    @BindView(R.id.fragment_open_new_rl_top_bar)
    RelativeLayout mRlTopBar;
    @BindView(R.id.fragment_open_new_tv_item_circle_left)
    TextView mTvItemCircleLeft;
    @BindView(R.id.fragment_open_new_tv_item_circle_concent)
    TextView mTvItemCircleConcent;
    @BindView(R.id.fragment_open_new_tv_item_circle_right)
    TextView mTvItemCircleRight;
    @BindView(R.id.fragment_open_new_view_page_item)
    ViewPager mViewPageItem;
    @BindView(R.id.fragment_open_new_tv_server_pay)
    TextView mTvServerPay;
    @BindView(R.id.fragment_open_new_tv_server_end_time)
    TextView mTvServerEndTime;
    @BindView(R.id.fragment_open_new_img_server_end_time_tips)
    ImageView mImgServerEndTimeTips;
    @BindView(R.id.fragment_open_new_srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private final String KEY_ORDER_ID = "key_open_order_id";

    private String mSelId = "";
    private int current;
    private List<OpenDoorListbean> mData = new ArrayList<>();
    OpenItemAdapter openItemAdapter;

    public static OpenNewFragment newInstance() {
        OpenNewFragment fragment = new OpenNewFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_new, container, false);
        unbinder = ButterKnife.bind(this, view);

        ViewGroup.LayoutParams lp = mRlTopBar.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        mRlTopBar.setLayoutParams(lp);
        mRlTopBar.setPadding(mRlTopBar.getPaddingLeft(), mRlTopBar.getPaddingTop() + UIUtils.getStatusHeight(), mRlTopBar.getPaddingRight(), mRlTopBar.getPaddingBottom());

        init();

        return view;
    }

    @Override
    public void init() {
        super.init();

        mSelId = SharedPreferenceUtil.getString(getActivity(), KEY_ORDER_ID);
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        openItemAdapter = new OpenItemAdapter(mActivity, mData);
        mViewPageItem.setAdapter(openItemAdapter);

        addRxBusSubscribe(OpenDeviceEvent.class, new Action1<OpenDeviceEvent>() {
            @Override
            public void call(OpenDeviceEvent openDeviceEvent) {
                mSelId = mData.get(openDeviceEvent.current).getOrder_id();
                SharedPreferenceUtil.setString(getActivity(), KEY_ORDER_ID, mSelId);
                refreshData();
            }
        });
        addRxBusSubscribe(SetPwdEvent.class, new Action1<SetPwdEvent>() {
            @Override
            public void call(SetPwdEvent setPwdEvent) {
//                if (setPwdEvent.getType() == SetPwdEvent.Type.CHECK)
//                    checkPwdAuth(setPwdEvent.getPwd());
            }
        });
        addRxBusSubscribe(TentantOpenDoorEvent.class, new Action1<TentantOpenDoorEvent>() {
            @Override
            public void call(TentantOpenDoorEvent tentantOpenDoorEvent) {
                if (tentantOpenDoorEvent.isUpdata()) {
                    initData();
                }
            }
        });
        addRxBusSubscribe(AddShareRentEvent.class, new Action1<AddShareRentEvent>() {
            @Override
            public void call(AddShareRentEvent addShareRentEvent) {
                int size = mData.size();
                for (int i = 0; i < size; i++) {
                    if (mData.get(i).getOrder_id().equals(addShareRentEvent.getOrder_id())) {
                        mData.get(i).setIs_rent(addShareRentEvent.getHave());
                        break;
                    }
                }
            }
        });
        addRxBusSubscribe(AddCodeEvent.class, new Action1<AddCodeEvent>() {
            @Override
            public void call(AddCodeEvent addCodeEvent) {
                AddCodeActivity.toActivity(mActivity);
            }
        });
    }

    @OnClick({R.id.fragment_open_new_img_msg, R.id.fragment_open_new_tv_server_pay, R.id.fragment_open_new_img_server_end_time_tips})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_open_new_img_msg:
                break;
            case R.id.fragment_open_new_tv_server_pay:
                break;
            case R.id.fragment_open_new_img_server_end_time_tips:
                break;
        }
    }

    /**
     * 数据更新
     */
    private void refreshData() {

    }

    /********************************************接口*************************************************/
    /**
     * 获取门锁列表数据
     */
    private void initData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getOpenRoomList()
                .compose(RxUtil.<DataInfo<List<OpenDoorListbean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<OpenDoorListbean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mSrlRefresh.setRefreshing(false);

                    }

                    @Override
                    public void onNext(DataInfo<List<OpenDoorListbean>> payOrderDataInfo) {
                        mSrlRefresh.setRefreshing(false);
                        if (payOrderDataInfo.success()) {
                            mData.clear();
                            mData.addAll(payOrderDataInfo.data());
                            openItemAdapter.notifyDataSetChanged();
                            refreshData();
                        } else {
                            showToast(payOrderDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }
}
