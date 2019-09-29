package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.DeviceHistoryBean;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.activity.AddHouseAddressActivity;
import com.konka.renting.utils.DateTimeUtil;
import com.konka.renting.utils.RxUtil;
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

public class OpenHistoryActivity extends BaseActivity {
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
    @BindView(R.id.activity_open_history_rv_history)
    RecyclerView mRvHistory;
    @BindView(R.id.activity_open_history_srl_history)
    SmartRefreshLayout mSrlHistory;

    private CommonAdapter<DeviceHistoryBean> mHistoryInfoCommonAdapter;
    List<DeviceHistoryBean> historyList;

    String room_id;
    int page = 1;


    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, OpenHistoryActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_open_history;
    }

    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");

        tvTitle.setText(R.string.title_history_open);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        historyList = new ArrayList<>();
        initAdapter();

        mSrlHistory.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(true);
            }
        });
        mSrlHistory.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                initData(false);
            }
        });
        mSrlHistory.setEnableLoadmore(false);

        mSrlHistory.autoRefresh();


    }

    private void initAdapter() {
        mHistoryInfoCommonAdapter = new CommonAdapter<DeviceHistoryBean>(this, historyList, R.layout.adapter_device_history) {
            @Override
            public void convert(ViewHolder viewHolder, DeviceHistoryBean deviceHistoryBean) {
                boolean isShow = false;
                long currentMills = DateTimeUtil.getTimeMills(deviceHistoryBean.getTime());
                String currentDate = DateTimeUtil.getChatTime(currentMills);
                String hhDD = DateTimeUtil.getHourAndMin(currentMills);

                String statusText = String.format(getString(R.string.device_item_history_content), deviceHistoryBean.getTime(), getListItemContent(deviceHistoryBean.getDetails()));
                viewHolder.setText(R.id.tv_status, statusText);
                int i = historyList.indexOf(deviceHistoryBean);
                if (i == 0) {
                    isShow = true;
                } else {
                    long lastMills = DateTimeUtil.getTimeMills(historyList.get(i - 1).getTime());
                    String lastDate = DateTimeUtil.getChatTime(lastMills);
                    isShow = !currentDate.equals(lastDate);
                }

                if (isShow) {
                    viewHolder.setText(R.id.tv_date, currentDate);
                    viewHolder.setText(R.id.tv_week, DateTimeUtil.getWeekOfDate(currentMills));
                }
                viewHolder.setVisible(R.id.frame_top, isShow);
            }


        };
        mRvHistory.setLayoutManager(new LinearLayoutManager(this));
        mRvHistory.setAdapter(mHistoryInfoCommonAdapter);

    }

    private String getListItemContent(String content) {
        String[] cont = content.split("】");
        if (cont.length > 1) {
            String name = cont[0].replace("【", "");
            int len = name.length();
            if (len > 7) {
                name = name.substring(0, 3) + "..." + name.substring(len - 2, len);
            }
            return "【" + name + "】" + cont[1];
        } else {
            return content;
        }
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
    /****************************************接口*****************************************/
    /**
     * 获取开门记录
     */
    private void initData(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            page++;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().openRoomRecord(room_id, page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<DeviceHistoryBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<DeviceHistoryBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (isRefresh) {
                            mSrlHistory.finishRefresh();
                        } else {
                            page--;
                            mSrlHistory.finishLoadmore();
                        }

                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<DeviceHistoryBean>> payOrderDataInfo) {

                        if (payOrderDataInfo.success()) {
                            if (isRefresh) {
                                historyList.clear();
                            }
                            historyList.addAll(payOrderDataInfo.data().getList());
                            mHistoryInfoCommonAdapter.notifyDataSetChanged();
                            mSrlHistory.setEnableLoadmore(payOrderDataInfo.data().getPage() < payOrderDataInfo.data().getTotalPage());

                        } else {
                            showToast(payOrderDataInfo.msg());
                            if (!isRefresh) {
                                page--;
                            }
                        }

                        if (isRefresh) {
                            mSrlHistory.finishRefresh();
                        } else {
                            mSrlHistory.finishLoadmore();
                        }

                    }
                });
        addSubscrebe(subscription);
    }
}
