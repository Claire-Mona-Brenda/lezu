package com.konka.renting.landlord.order;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.event.CancelOrderEvent;
import com.konka.renting.event.CreateOrderEvent;
import com.konka.renting.event.LandlordOrderApplyEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
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

public class UnderwayLFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_upderway_rv_recycler)
    RecyclerView mRvRecycler;
    @BindView(R.id.fragment_upderway_srl_refresh)
    SmartRefreshLayout mSrlRefresh;

    private List<RenterOrderListBean> mData = new ArrayList<>();
    private int page;
    CommonAdapter<RenterOrderListBean> commonAdapter;
    CommonPopupWindow commonPopupWindow;


    public static UnderwayLFragment newInstance() {
        Bundle args = new Bundle();
        UnderwayLFragment fragment = new UnderwayLFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upderway, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void init() {
        super.init();
        initList();
        mSrlRefresh.setReboundDuration(100);
        mSrlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(true);
            }
        });
        mSrlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                initData(false);
            }
        });
        addRxBusSubscribe(ConfirmEvent.class, new Action1<ConfirmEvent>() {
            @Override
            public void call(ConfirmEvent confirmEvent) {
                mSrlRefresh.autoRefresh();
            }
        });
        addRxBusSubscribe(LandlordOrderApplyEvent.class, new Action1<LandlordOrderApplyEvent>() {
            @Override
            public void call(LandlordOrderApplyEvent landlordOrderApplyEvent) {
                if (landlordOrderApplyEvent.isUpdata()) {
                    mSrlRefresh.autoRefresh();
                }
            }
        });
        addRxBusSubscribe(CancelOrderEvent.class, new Action1<CancelOrderEvent>() {
            @Override
            public void call(CancelOrderEvent cancelOrderEvent) {
                removeBean(cancelOrderEvent.getOrder_id());
            }
        });
        addRxBusSubscribe(CreateOrderEvent.class, new Action1<CreateOrderEvent>() {
            @Override
            public void call(CreateOrderEvent event) {
                mSrlRefresh.autoRefresh();
            }
        });

        mSrlRefresh.autoRefresh();
    }


    private void initList() {
        commonAdapter = new CommonAdapter<RenterOrderListBean>(getContext(), mData, R.layout.adapter_underway_l) {
            @Override
            public void convert(ViewHolder viewHolder, RenterOrderListBean listBean) {
                String unit = listBean.getType() == 1 ? "/天" : "/月";
                viewHolder.setText(R.id.adapter_tv_room_price, "¥ " + (int) Float.parseFloat(listBean.getHousing_price()) + unit);
                ImageView ivPic = viewHolder.getView(R.id.adapter_icon_room);
                if (!TextUtils.isEmpty(listBean.getThumb_image()))
                    Picasso.get().load(listBean.getThumb_image()).into(ivPic);
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).into(ivPic);
                viewHolder.setText(R.id.adapter_tv_room_name, listBean.getRoom_name());
                viewHolder.setText(R.id.adapter_tv_start_time, listBean.getStart_time());
                viewHolder.setText(R.id.adapter_tv_end_time, listBean.getEnd_time());

                String room_type;
                if (listBean.getRoom_type().contains("_")) {
                    String[] t = listBean.getRoom_type().split("_");
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = listBean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + "|");
                spannableStringBuilder.append(getArea(listBean.getMeasure_area()));
                spannableStringBuilder.append("|" + listBean.getFloor() + "/" + listBean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_tv_room_info, spannableStringBuilder);

                viewHolder.setOnClickListener(R.id.adapter_tv_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(getString(R.string.dialog_dissmiss_apply_rent), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonPopupWindow.dismiss();
                                cancle(listBean.getOrder_id());
                            }
                        });
                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderInfoLActivity.toActivity(getActivity(), listBean.getOrder_id(), 0);
                    }
                });
            }
        };
        mRvRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvRecycler.setAdapter(commonAdapter);
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }

    private void removeBean(String order_id) {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (mData.get(i).getOrder_id().equals(order_id)) {
                mData.remove(i);
                commonAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /*******************************************************接口********************************************/
    private void initData(boolean isRefresh) {
        if (isRefresh)
            page = 1;
        else
            page = page + 1;
        Subscription subscription = SecondRetrofitHelper.getInstance().getOrderList2("1", page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterOrderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterOrderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        if (isRefresh)
                            mSrlRefresh.finishRefresh();
                        else {
                            mSrlRefresh.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterOrderListBean>> listInfoDataInfo) {
                        if (isRefresh) {
                            mSrlRefresh.finishRefresh();
                        } else {
                            mSrlRefresh.finishLoadmore();
                        }
                        if (listInfoDataInfo.success()) {
                            if (page == 1) {
                                mData.clear();
                            } else if (listInfoDataInfo.data().getTotalPage() < listInfoDataInfo.data().getPage()) {
                                page--;
                            }
                            mSrlRefresh.setEnableLoadmore(page < listInfoDataInfo.data().getTotalPage());
                            mData.addAll(listInfoDataInfo.data().getList());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            if (page > 1)
                                page--;
                            showToast(listInfoDataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void cancle(final String order_id) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().canceOrder(order_id)
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
                            removeBean(order_id);
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**************************************************弹窗****************************************************************/


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
        popupWindow.showAtLocation((View) mSrlRefresh.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
