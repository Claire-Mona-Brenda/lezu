package com.konka.renting.landlord.user.collection;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.ReminderListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.SharedPreferenceUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.addapp.pickers.picker.NumberPicker;
import rx.Subscription;


public class MyCollectionActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.activity_collection_rl_warm)
    RelativeLayout activityCollectionRlWarm;
    @BindView(R.id.activity_collection_recycle_mycollection)
    RecyclerView mycollection;
    @BindView(R.id.activity_collection_refresh)
    SmartRefreshLayout mRefresh;

    final String IS_KNOW_COLLECTION_ABOUT="is_know_collection_about";

    CollectionRecycleAdapter mRecycleAdapter;
    List<ReminderListBean> beanList=new ArrayList<>();
    boolean isEnableChoose=true;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MyCollectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_collection;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.bill_collection);
        activityCollectionRlWarm.setVisibility( SharedPreferenceUtil.getBoolean(this, IS_KNOW_COLLECTION_ABOUT)?View.GONE:View.VISIBLE);
        mycollection.setLayoutManager(new LinearLayoutManager(this));
        mRecycleAdapter = new CollectionRecycleAdapter(this, beanList);
        mRecycleAdapter.setItemClickListener(new CollectionRecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                showSelectDialog(position);
            }

            @Override
            public void onChoose(int position, boolean isCheck) {
                if (isCheck&&isEnableChoose){//关闭催租
                    closeSet(position);
                }else if(!isCheck&&isEnableChoose){//开启催租
                    openSet(position);
                }
            }
        });
        mycollection.setAdapter(mRecycleAdapter);
        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        mRefresh.autoRefresh();
    }

    private void openSet(final int position) {
        isEnableChoose=false;
        showLoadingDialog();
        ReminderListBean bean=beanList.get(position);
        Subscription subscription=SecondRetrofitHelper.getInstance().reminderSet(bean.getRoom_id()+"",bean.getDay()==0?"1":bean.getDay()+"")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        doFailed();
                        beanList.get(position).setStatus(0);
                        mRecycleAdapter.notifyDataSetChanged();
                        isEnableChoose=true;
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()){
                            beanList.get(position).setStatus(1);
                        }else{
                            showToast(dataInfo.msg());
                            beanList.get(position).setStatus(0);
                        }
                        mRecycleAdapter.notifyDataSetChanged();
                        isEnableChoose=true;
                    }
                });
        addSubscrebe(subscription);
    }

    private void closeSet(final int position) {
        showLoadingDialog();
        ReminderListBean bean=beanList.get(position);
        Subscription subscription=SecondRetrofitHelper.getInstance().reminderClose(bean.getRoom_id()+"")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        doFailed();
                        beanList.get(position).setStatus(1);
                        mRecycleAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()){
                            beanList.get(position).setStatus(0);
                        }else{
                            showToast(dataInfo.msg());
                            beanList.get(position).setStatus(1);
                        }
                        mRecycleAdapter.notifyDataSetChanged();
                    }
                });
        addSubscrebe(subscription);
    }

    private void getData() {
        Subscription subscription=SecondRetrofitHelper.getInstance().getReminderList("1")
                .compose(RxUtil.<DataInfo<PageDataBean<ReminderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<ReminderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRefresh.finishRefresh();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<ReminderListBean>> dataInfo) {
                        mRefresh.finishRefresh();
                        if (dataInfo.success()){
                            beanList.clear();
                            beanList.addAll(dataInfo.data().getList());
                            mRecycleAdapter.notifyDataSetChanged();
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @OnClick({R.id.iv_back, R.id.activity_collection_tv_know})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_collection_tv_know:
                SharedPreferenceUtil.setBoolean(this, IS_KNOW_COLLECTION_ABOUT, true);
                activityCollectionRlWarm.setVisibility(View.GONE);
                break;
        }
    }

    private void showSelectDialog(final int position) {
        NumberPicker picker = new NumberPicker(this);
        picker.setRange(1, 28);
        picker.setItemWidth(getResources().getDisplayMetrics().widthPixels);
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int i, Number number) {
                beanList.get(position).setDay((Integer) number);
                mRecycleAdapter.notifyDataSetChanged();
            }
        });
        picker.show();
    }
}
