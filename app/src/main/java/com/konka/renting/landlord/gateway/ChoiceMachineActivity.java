package com.konka.renting.landlord.gateway;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.adapter.ChoiceDeviceAdapter;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class ChoiceMachineActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private ChoiceDeviceAdapter mChoiceDeviceAdapter;

    private List<MachineInfo> mMachineInfos;

    private String mRoomId;
    private int status;

    public static void toActivity(Activity context, String room_id,int status) {
        Intent intent = new Intent(context, ChoiceMachineActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("status", status);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choice_machine;
    }

    @Override
    public void init() {
//        mIvRight.setImageResource(R.mipmap.device_icon_scan);
        setTitleText(R.string.device_model_title);
        mRoomId = getIntent().getStringExtra("room_id");
        status = getIntent().getIntExtra("status", 0);
        mMachineInfos = new ArrayList<>();
        mChoiceDeviceAdapter = new ChoiceDeviceAdapter(this, mMachineInfos);

        mListview.setAdapter(mChoiceDeviceAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GatewayListActivity.toActivity(mActivity, mRoomId,mMachineInfos.get(position),status);
                finish();
            }
        });

        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        getData();
    }

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getDeviceType("1")
                .compose(RxUtil.<DataInfo<PageDataBean<MachineInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<MachineInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mRefreshLayout.finishRefresh(100, false);
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<MachineInfo>> info) {
                        mRefreshLayout.finishRefresh(100, info.success());
                        if (info.success()) {
                            mMachineInfos.clear();
                            mMachineInfos.addAll(info.data().getList());
                            mChoiceDeviceAdapter.notifyDataSetChanged();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
