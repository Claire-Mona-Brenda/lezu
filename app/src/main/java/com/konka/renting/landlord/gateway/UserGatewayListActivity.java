package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.event.DelGatewayEvent;
import com.konka.renting.event.RebootEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.widget.RebootPopup;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.sql.KonkaSqlHelper;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class UserGatewayListActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView mIvLeft;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_setting_tips)
    TextView tvSettingTips;

    private CommonAdapter<GatewayInfo> mGatewayInfoCommonAdapter;

    private List<GatewayInfo> mGatewayInfos = new ArrayList<>();
    //private MachineInfo mMachineInfo;
    private String room_id;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, UserGatewayListActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gateway_list_user;
    }

    @Override
    public void init() {
        //mMachineInfo = getIntent().getParcelableExtra(MachineInfo.class.getSimpleName());
        setTitleText(R.string.text_gateway);
        mIvRight.setImageResource(R.mipmap.device_icon_add);
        mIvRight.setVisibility(View.VISIBLE);

        room_id = getIntent().getStringExtra("room_id");

        addRxBusSubscribe(DelGatewayEvent.class, new Action1<DelGatewayEvent>() {
            @Override
            public void call(DelGatewayEvent delGatewayEvent) {
                mRefreshLayout.autoRefresh();
            }
        });
        mGatewayInfoCommonAdapter = new CommonAdapter<GatewayInfo>(this, mGatewayInfos, R.layout.item_gateway_user) {
            @Override
            public void convert(ViewHolder viewHolder, final GatewayInfo gatewayInfo, int i) {
                viewHolder.setText(R.id.tv_name, gatewayInfo.getGateway_name());
                switch (gatewayInfo.getNetwork()) {
                    case 1:
                        viewHolder.setText(R.id.tv_connect_way, getString(R.string.gateway_conn_2G));
                        break;
                    case 2:
                        viewHolder.setText(R.id.tv_connect_way, getString(R.string.gateway_conn_WiFi));
                        break;
                    case 3:
                        viewHolder.setText(R.id.tv_connect_way, getString(R.string.gateway_conn_Ethernet));
                        break;
                }
                int gatewayVersion ;
                try{
                    gatewayVersion=Integer.valueOf(gatewayInfo.getGateway_version());
                }catch (Exception e){
                    gatewayVersion=2;
                }
                viewHolder.setImageResource(R.id.img_gateway, gatewayVersion < 4 ? R.mipmap.wangguan_1_72px_png : R.mipmap.wangguan_2_72px_png);
                viewHolder.setVisible(R.id.tv_manage, false);


            }
        };


        mListview.setAdapter(mGatewayInfoCommonAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GatewaySettingActivity.toActivity(UserGatewayListActivity.this, room_id, mGatewayInfos.get(position).getId(), mGatewayInfos.get(position).getGateway_version(),GatewaySettingActivity.TYPE_LANDLORD);
            }
        });
//        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                GatewayDetailActivity.toActivity(UserGatewayListActivity.this, mGatewayInfos.get(position).getId());
//
//            }
//        });


        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshlayout) {
                getGatewayList(true);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getGatewayList(false);

            }
        });

//        initGatewayCache();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.autoRefresh();
    }


    private void showReboot(String id) {
        // 开启 popup 时界面透明
        RebootPopup servicePopup = new RebootPopup(this);
        servicePopup.setId(id);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        servicePopup.showAtLocation(findViewById(R.id.user_gateway), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        servicePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

//    private void initGatewayCache() {
//        try {
//            Dao<GatewayInfo, String> dao = KonkaSqlHelper.getHelper(this).getDao(GatewayInfo.class);
//            List<GatewayInfo> list = dao.queryForAll();
//            mGatewayInfos.addAll(list);
//            mGatewayInfoCommonAdapter.notifyDataSetChanged();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                if (mGatewayInfos.size() <= 0)
                    BindGatewayActivity.toActivity(this, room_id);
                else
                    ShowToastUtil.showNormalToast(this, getString(R.string.warm_gateway_no_to_add));
                break;
        }
    }

    private void getGatewayList(boolean isRefresh) {
        if (isRefresh)
            mGatewayInfos.clear();

        Subscription subscription = SecondRetrofitHelper.getInstance().gatewayList(room_id)
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
                            mGatewayInfos.addAll(listInfoDataInfo.data());
                            //mGatewayInfos = listInfoDataInfo.data().list;
                            mGatewayInfoCommonAdapter.notifyDataSetChanged();
//                            saveGatewayCache(listInfoDataInfo.data());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void saveGatewayCache(List<GatewayInfo> list) {
        if (list == null || list.isEmpty())
            return;
        try {
            Dao<GatewayInfo, String> dao = KonkaSqlHelper.getHelper(this).getDao(GatewayInfo.class);
            KonkaSqlHelper.getHelper(this).deleteTable(GatewayInfo.class);
            dao.create(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
