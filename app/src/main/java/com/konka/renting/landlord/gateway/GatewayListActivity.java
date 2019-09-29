package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.event.RefreshGatewayDataEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class GatewayListActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private CommonAdapter<GatewayInfo> mGatewayInfoCommonAdapter;

    private List<GatewayInfo> mGatewayInfos = new ArrayList<>();

    private MachineInfo mMachineInfo;

    private String mRoomId;
    private int status;

    public static void toActivity(Context context, String room_id,MachineInfo machineInfo,int status) {
        Intent intent = new Intent(context, GatewayListActivity.class);
        intent.putExtra(MachineInfo.class.getSimpleName(), machineInfo);
        intent.putExtra("room_id", room_id);
        intent.putExtra("status", status);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gateway_list;
    }

    @Override
    public void init() {
        mMachineInfo = getIntent().getParcelableExtra(MachineInfo.class.getSimpleName());
        mRoomId = getIntent().getStringExtra("room_id");
        status = getIntent().getIntExtra("status", 0);

        setTitleText(R.string.title_activity_gateway_list);
        setRightText(R.string.common_add);

        addRxBusSubscribe(RefreshGatewayDataEvent.class, new Action1<RefreshGatewayDataEvent>() {
            @Override
            public void call(RefreshGatewayDataEvent refreshGatewayDataEvent) {
                mRefreshLayout.autoRefresh();
            }
        });

        mGatewayInfoCommonAdapter = new CommonAdapter<GatewayInfo>(this, mGatewayInfos, R.layout.item_gateway) {
            @Override
            public void convert(ViewHolder viewHolder, final GatewayInfo gatewayInfo, final int i) {
                if (status>=6)
                    viewHolder.setVisible(R.id.tv_manage,false);
                viewHolder.setText(R.id.tv_name, gatewayInfo.getGateway_name());
                switch (gatewayInfo.getNetwork()){
                    case 1:
                        viewHolder.setText(R.id.tv_connect_way,getString(R.string.gateway_conn_2G));
                        break;
                    case 2:
                        viewHolder.setText(R.id.tv_connect_way,getString(R.string.gateway_conn_WiFi));
                        break;
                    case 3:
                        viewHolder.setText(R.id.tv_connect_way,getString(R.string.gateway_conn_Ethernet));
                        break;
                }
                int gateway_version;
                try{
                    gateway_version=Integer.valueOf(gatewayInfo.getGateway_version());
                }catch (Exception e){
                    gateway_version=2;
                }
                viewHolder.setImageResource(R.id.img_gateway,gateway_version<4?R.mipmap.wangguan_1_72px_png:R.mipmap.wangguan_2_72px_png);
                TextView mTvManage = viewHolder.getView(R.id.tv_manage);
                mTvManage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GatewaySettingActivity.toActivity(GatewayListActivity.this, mRoomId,gatewayInfo.getId(), gatewayInfo.getGateway_version(),GatewaySettingActivity.TYPE_LANDLORD);

                    }
                });
//                viewHolder.setText(R.id.tv_name, gatewayInfo.getGateway_name());
//
//                SwipeMenuLayout swipeMenuLayout = viewHolder.getView(R.id.swipemenulayout);
//                FrameLayout itemFrame = viewHolder.getView(R.id.item_frame);
//                swipeMenuLayout.smoothClose();
//                viewHolder.setOnClickListener(R.id.btn_del, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        deleteDevice(i);
//                    }
//                });
//                viewHolder.setOnClickListener(R.id.btn_check, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        check(i);
//                    }
//                });
//                itemFrame.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        GatewayConnectionActivity.toActivity(mActivity, mMachineInfo, gatewayInfo, mRoomId);
//                        finish();
//                    }
//                });
            }
        };

        mListview.setAdapter(mGatewayInfoCommonAdapter);

        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshlayout) {
                getGatewayList();
            }
        });

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GatewayConnectionActivity.toActivity(mActivity,mGatewayInfos.get(position),mMachineInfo, mRoomId,false,false);
                finish();
            }
        });

//        initGatewayCache();
        getGatewayList();
    }
    private void deleteDevice(final int position){
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().unbindGateway(mGatewayInfos.get(position).getId())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        dismiss();
                        if (info.success()){
                            doSuccess();
                            mGatewayInfos.remove(position);
                            mGatewayInfoCommonAdapter.notifyDataSetChanged();
                        }else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
    private void check(final int position){
       CheckGatewayStatusActivity.toActivity(this,mGatewayInfos.get(position).getId(),mGatewayInfos.get(position).getGateway_name());
    }


    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if(mGatewayInfos.size()<=0) {
                    BindGatewayActivity.toActivity(this, mRoomId);
                }else{
                    ShowToastUtil.showNormalToast(this,getString(R.string.warm_gateway_no_to_add));
                }
                break;
        }
    }

//    private void getGatewayList() {
//        Subscription subscription = SecondRetrofitHelper.getInstance().gatewayList(mRoomId)
//                .compose(RxUtil.<DataInfo<List<GatewayInfo>>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<List<GatewayInfo>>>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        mRefreshLayout.finishRefresh(1000, false);
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<List<GatewayInfo>> listInfoDataInfo) {
//                        mRefreshLayout.finishRefresh(1000, listInfoDataInfo.success());
//                        if (listInfoDataInfo.success()) {
//                            mGatewayInfos.clear();
//                            mGatewayInfos.addAll(listInfoDataInfo.data());
//                            mGatewayInfoCommonAdapter.notifyDataSetChanged();
//                            saveGatewayCache(mGatewayInfos);
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

    private void getGatewayList() {

        Subscription subscription = SecondRetrofitHelper.getInstance().gatewayList(mRoomId)
                .compose(RxUtil.<DataInfo<List<GatewayInfo>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<GatewayInfo>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mRefreshLayout.finishRefresh(200, false);
                    }

                    @Override
                    public void onNext(DataInfo<List<GatewayInfo>> listInfoDataInfo) {
                        mRefreshLayout.finishRefresh(200, listInfoDataInfo.success());

                        if (listInfoDataInfo.success()) {
                            mGatewayInfos.clear();
                            mGatewayInfos.addAll(listInfoDataInfo.data());
                            //mGatewayInfos = listInfoDataInfo.data().list;
                            mGatewayInfoCommonAdapter.notifyDataSetChanged();
//                            saveGatewayCache(listInfoDataInfo.data());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
