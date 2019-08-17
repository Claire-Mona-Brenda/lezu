package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RentListBean;
import com.konka.renting.bean.RentingDateBean;
import com.konka.renting.event.CancelOrderEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class RentPeopleListActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activity_rents_list_rv_rent)
    RecyclerView mRvRent;
    @BindView(R.id.activity_rents_list_srl_refresh)
    SmartRefreshLayout mSrlRefresh;

    int page;
    String room_id;
    List<RentListBean> rentListBeans;
    CommonAdapter<RentListBean> commonAdapter;
    CommonPopupWindow commonPopupWindow;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, RentPeopleListActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rents_list;
    }

    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");

        tvTitle.setText(R.string.house_info_setting_rent);
        rentListBeans = new ArrayList<>();
        commonAdapter = new CommonAdapter<RentListBean>(this, rentListBeans, R.layout.adapter_rent_people_list) {
            @Override
            public void convert(ViewHolder viewHolder, RentListBean rentListBean) {
                String start = "";
                if (!TextUtils.isEmpty(rentListBean.getStart_time())) {
                    String[] s = rentListBean.getStart_time().split(":");
                    start = s[0] + ":00";
                }
                String end = "";
                if (!TextUtils.isEmpty(rentListBean.getEnd_time())) {
                    String[] e = rentListBean.getEnd_time().split(":");
                    end = e[0] + ":00";
                }
                viewHolder.setText(R.id.adapter_rent_people_tv_start_time, start);
                viewHolder.setText(R.id.adapter_rent_people_tv_end_time, end);
                viewHolder.setText(R.id.adapter_rent_people_tv_phone, rentListBean.getPhone());
                viewHolder.setOnClickListener(R.id.adapter_rent_people_tv_remove, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(getString(R.string.dialog_dissmiss_rent), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonPopupWindow.dismiss();
                                cancle(rentListBean.getOrder_id() + "");
                            }
                        });
                    }
                });
            }
        };

        mRvRent.setLayoutManager(new LinearLayoutManager(this));
        mRvRent.setAdapter(commonAdapter);

        mSrlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getRentDate(true);
            }
        });
        mSrlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getRentDate(false);
            }
        });
        mSrlRefresh.setEnableLoadmore(false);
        mSrlRefresh.autoRefresh();

    }


    @OnClick({R.id.iv_back, R.id.tv_right, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                break;
            case R.id.iv_right:
                break;
        }
    }

    private void removeBean(String order_id) {
        int size = rentListBeans.size();
        for (int i = 0; i < size; i++) {
            if (order_id.equals(rentListBeans.get(i).getOrder_id() + "")) {
                rentListBeans.remove(i);
                commonAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /*******************************************************接口********************************************/

    private void getRentDate(boolean isRe) {
        if (isRe) {
            page = 1;
        } else {
            page++;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().renterList(page + "", room_id)
                .compose(RxUtil.<DataInfo<PageDataBean<RentListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RentListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        if (isRe) {
                            mSrlRefresh.finishRefresh();
                        } else {
                            mSrlRefresh.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RentListBean>> dataInfo) {
                        if (isRe) {
                            mSrlRefresh.finishRefresh();
                        } else {
                            mSrlRefresh.finishLoadmore();
                        }
                        if (dataInfo.success()) {
                            if (dataInfo.data().getPage() >= dataInfo.data().getTotalPage()) {
                                mSrlRefresh.setEnableLoadmore(false);
                            } else {
                                mSrlRefresh.setEnableLoadmore(true);
                            }
                            if (isRe) {
                                rentListBeans.clear();
                            }
                            rentListBeans.addAll(dataInfo.data().getList());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(dataInfo.msg());
                            if (!isRe) {
                                page--;
                            }
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
                            RxBus.getDefault().post(new CancelOrderEvent(order_id));
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
