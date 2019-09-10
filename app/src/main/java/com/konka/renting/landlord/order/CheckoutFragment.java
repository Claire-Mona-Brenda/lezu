package com.konka.renting.landlord.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.event.LandlordHouseListEvent;
import com.konka.renting.event.LandlordOrderCheckinEvent;
import com.konka.renting.event.LandlordOrderCheckoutEvent;
import com.konka.renting.event.LandlordOrderExpireEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
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
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by kaite on 2018/3/16.
 */

public class CheckoutFragment extends BaseFragment {
    @BindView(R.id.list_checkout)
    ListView mListCheckout;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    Unbinder unbinder;
    private List<RenterOrderListBean> mData = new ArrayList<>();
    private int page;
    private int status = 3;
    private int REFRESH = 1;
    private int LOADMORE = 2;
    private CommonAdapter<RenterOrderListBean> mAdapter;

    private CommonPopupWindow commonPopupWindow;

    public static CheckoutFragment newInstance() {

        Bundle args = new Bundle();

        CheckoutFragment fragment = new CheckoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CheckoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        initData(REFRESH);
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mRefresh.autoRefresh();
        }
    }

    @Override
    public void init() {
        super.init();
        initList();
        mRefresh.setReboundDuration(100);
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
        addRxBusSubscribe(UpdateCheckOutEvent.class, new Action1<UpdateCheckOutEvent>() {
            @Override
            public void call(UpdateCheckOutEvent updateCheckOutEvent) {
                mRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(LandlordOrderCheckoutEvent.class, new Action1<LandlordOrderCheckoutEvent>() {
            @Override
            public void call(LandlordOrderCheckoutEvent event) {
                if (event.isUpdata()) {
                    mRefresh.autoRefresh();
                }
            }
        });
    }

    private void initData(final int type) {
        if (type == REFRESH)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordOrderList(status + "", page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterOrderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterOrderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        if (type == REFRESH)
                            mRefresh.finishRefresh();
                        else {
                            mRefresh.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterOrderListBean>> listInfoDataInfo) {
                        if (type == REFRESH) {
                            mRefresh.finishRefresh();
                        } else
                            mRefresh.finishLoadmore();
                        if (listInfoDataInfo.success()) {
                            if (page == 1) {
                                mData.clear();
                            } else if (listInfoDataInfo.data().getTotalPage() < listInfoDataInfo.data().getPage()) {
                                page--;
                            }
                            mRefresh.setEnableLoadmore(page<listInfoDataInfo.data().getTotalPage());
                            mData.addAll(listInfoDataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (page > 1)
                                page--;
                            showToast(listInfoDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void initList() {
        mAdapter = new CommonAdapter<RenterOrderListBean>(getContext(), mData, R.layout.item_list_checkout) {
            @Override
            public void convert(ViewHolder viewHolder, final RenterOrderListBean s, int i) {
                String unit = s.getRent_type() == 1 ? "/天" : "/月";
                viewHolder.setText(R.id.tv_money, "¥ " + (int) Float.parseFloat(s.getHousing_price()) + unit);
                viewHolder.setText(R.id.tv_order_number, s.getOrder_no());
                ImageView ivPic = viewHolder.getView(R.id.icon_room);
                if (!TextUtils.isEmpty(s.getThumb_image()))
                    Picasso.get().load(s.getThumb_image()).into(ivPic);
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).into(ivPic);
                viewHolder.setText(R.id.tv_address, s.getRoom_name());
                TextView tvConfirmRefunds = viewHolder.getView(R.id.btn_confirm);
                viewHolder.setText(R.id.tv_status, getStringStatus(s.getStatus()));
                viewHolder.setText(R.id.tv_start_time, s.getStart_time());
                viewHolder.setText(R.id.tv_end_time, s.getEnd_time());

                TextView tvShort = viewHolder.getView(R.id.tv_short);
                TextView tvLong = viewHolder.getView(R.id.tv_long);
                if (s.getRent_type() == 1) {
                    tvShort.setVisibility(View.VISIBLE);
                    tvLong.setVisibility(View.GONE);
                } else {
                    tvLong.setVisibility(View.VISIBLE);
                    tvShort.setVisibility(View.GONE);
                }
                tvConfirmRefunds.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopup(getString(R.string.landlord__dialog_apply_to_end), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonPopupWindow.dismiss();
                                agreeCheckOut(s);
                            }
                        });
                    }
                });
                TextView tvRefuse = viewHolder.getView(R.id.btn_refuse);
                tvRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopup(getString(R.string.landlord__dialog_cancel_to_end), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonPopupWindow.dismiss();
                                refuseOrder(s.getOrder_id());
                            }
                        });

                    }
                });
                notifyDataSetChanged();
            }
        };
        mListCheckout.setAdapter(mAdapter);
        mListCheckout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderDetailActivity.toActivity(getContext(), mData.get(i).getOrder_id(), mData.get(i).getRent_type() + "", mData.get(i).getOrder_no(), mData.get(i).getStatus() + "");
            }
        });
    }

    private void removeBean(String order_id) {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (mData.get(i).getOrder_id().equals(order_id)) {
                mData.remove(i);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void agreeCheckOut(final RenterOrderListBean bean) {
        showLoadingDialog();
        final String order_id = bean.getOrder_id();
        Subscription subscription = SecondRetrofitHelper.getInstance().landlordConfirmCheckOut(order_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo configInfoBeanDataInfo) {

                        dismiss();
                        if (configInfoBeanDataInfo.success()) {
                            removeBean(order_id);
                            RxBus.getDefault().post(new LandlordOrderExpireEvent(11));
                            RxBus.getDefault().post(new LandlordHouseListEvent(11));
                        } else {
                            showToast(configInfoBeanDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }



    private void refuseOrder(final String order_id) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().cancelCheckOut(order_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();

                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            showToast(dataInfo.msg());
                            removeBean(order_id);
                            RxBus.getDefault().post(new LandlordOrderCheckinEvent(11));
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private String getStringStatus(int status) {
        String s = "";
        switch (status) {
            case 1:
                s = getString(R.string.order_status_1);
                break;
            case 2:
                s = getString(R.string.order_status_2);
                break;
            case 3:
                s = getString(R.string.order_status_3);
                break;
            case 4:
                s = getString(R.string.order_status_4);
                break;
            case 5:
                s = getString(R.string.order_status_5);
                break;
            case 6:
                s = getString(R.string.order_status_6);
                break;
            case 7:
                s = getString(R.string.order_status_7);
                break;
        }
        return s;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRefresh.finishRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /******************************************************************************************************************/


    /**
     *
     */
    private void showPopup(String content, View.OnClickListener onClickListener) {
        commonPopupWindow = new CommonPopupWindow.Builder(mActivity)
                .setTitle(getString(R.string.tips))
                .setContent(content)
                .setRightBtnClickListener(onClickListener)
                .create();
        showPopup(commonPopupWindow);

    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) mRefresh.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }
}
