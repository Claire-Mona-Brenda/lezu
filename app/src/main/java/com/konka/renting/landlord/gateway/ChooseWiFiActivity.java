package com.konka.renting.landlord.gateway;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.event.ChooseOtherWiFiEvent;
import com.konka.renting.location.LocationUtils;
import com.konka.renting.utils.LocationManagerUtils;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.WiFiUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class ChooseWiFiActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.activity_choose_wifi_refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.activity_choose_wifi_recycler)
    RecyclerView mRecyclerView;

    private CommonAdapter<ScanResult> mAdapter;
    private List<ScanResult> wifiList = new ArrayList<>();
    WiFiUtils wiFiUtils;
    LocationManagerUtils locationUtils;
    CommonPopupWindow popupWindow;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, ChooseWiFiActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_wifi;
    }

    @Override
    public void init() {
        setTitleText(R.string.choose_wifi_title);

        wiFiUtils = new WiFiUtils(this);
        locationUtils = new LocationManagerUtils(this);

        mAdapter = new CommonAdapter<ScanResult>(this, wifiList, R.layout.adapter_choose_wifi) {
            @Override
            public void convert(ViewHolder viewHolder, final ScanResult scanResult) {
                viewHolder.setText(R.id.adapter_choose_wifi_tv_name, scanResult.SSID);
                viewHolder.setImageResource(R.id.adapter_choose_wifi_img_icon, isNeedPwd(scanResult.capabilities) ? R.drawable.wifi_lock : R.drawable.wifi);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.getDefault().post(new ChooseOtherWiFiEvent(false, scanResult.SSID));
                        finish();
                    }
                });
            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.autoRefresh(1000);
    }

    private void getData() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission.request(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (locationUtils.isLocServiceEnable(mActivity)) {
                                getWifiList();
                            }else {
                                showOpenLocationPopup();
                                refreshLayout.finishRefresh();
                            }
                        } else {
                            refreshLayout.finishRefresh();
                        }
                    }
                });

    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    public void getWifiList() {
        wifiList.clear();
        wiFiUtils.startScan(this);
        wifiList.addAll(wiFiUtils.getWifiList());
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        List<ScanResult> scanWifiList = wifiManager.getScanResults();
//        if (scanWifiList != null && scanWifiList.size() > 0) {
//            for (int i = 0; i < scanWifiList.size(); i++) {
//                ScanResult scanResult = scanWifiList.get(i);
//                if (!scanResult.SSID.isEmpty() && !containName(wifiList, scanResult.SSID)) {
//                    wifiList.add(scanResult);
//                }
//            }
//        }
        mAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    public boolean containName(List<ScanResult> sr, String name) {
        for (ScanResult result : sr) {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }

    private boolean isNeedPwd(String capabilities) {
        if (!TextUtils.isEmpty(capabilities)) {

            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return true;

            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return true;
            }
        }
        return false;
    }

    private void showOpenLocationPopup() {
        popupWindow = new CommonPopupWindow.Builder(mActivity)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.open_location_service))
                .setRightBtnString(getString(R.string.to_open_location_service))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            mActivity.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            intent.setAction(Settings.ACTION_SETTINGS);
                            try {
                                mActivity.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        popupWindow.dismiss();
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) refreshLayout.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }
}
