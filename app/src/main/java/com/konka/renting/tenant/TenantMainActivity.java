package com.konka.renting.tenant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.igexin.sdk.PushManager;
import com.konka.renting.R;
import com.konka.renting.base.BaseMainActivity;
import com.konka.renting.bean.AppConfigBean;
import com.konka.renting.bean.CityBean;
import com.konka.renting.bean.CityInfo;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.OpenCityBean;
import com.konka.renting.event.AddCodeSuccessEvent;
import com.konka.renting.event.LocationEvent;
import com.konka.renting.event.RefreshFindRoomEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.HomeFragment;
import com.konka.renting.landlord.house.entity.DicEntity;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.service.GeTuiIntentService;
import com.konka.renting.service.GeTuiPushService;
import com.konka.renting.tenant.main.TenantMainFragment;
import com.konka.renting.tenant.opendoor.OpenFragment;
import com.konka.renting.tenant.order.TenantOrderFragment;
import com.konka.renting.tenant.payrent.PayRentFragment;
import com.konka.renting.tenant.user.TenantUserFragment;
import com.konka.renting.utils.AppManager;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.UpdateAppPopupwindow;
import com.konka.renting.widget.UpdateEvent;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class TenantMainActivity extends BaseMainActivity {
    private List<HotCity> mCities = new ArrayList<>();
    private List<City> mAllCities = new ArrayList<>();
    DensityUtil densityUtil = new DensityUtil();
    UpdateAppPopupwindow updateAppPopupwindow;
    public AppConfigBean mAppConfigBean;


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, TenantMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tenant_main;
    }

    @Override
    public int getFragmentCount() {
        return 4;
    }

    @Override
    public int getDefaultIndex() {
        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        UIUtils.setDarkStatusIcon(this, true);
        return 0;
    }

    @Override
    public void initFragment(int index) {
        if (index == 1) {
            RxBus.getDefault().post(new RefreshFindRoomEvent());
        }

        if (mFragments[index] != null)
            return;

        switch (index) {
            case 0:
//                mFragments[index] = HomeFragment.newInstance();
                mFragments[index] = TenantMainFragment.newInstance();
                break;
//            case 1:
//                mFragments[index] = FindRoomFragment.newInstance();
//                RxBus.getDefault().post(new RefreshFindRoomEvent());
//                break;
            case 1:
                mFragments[index] = OpenFragment.newInstance();
                break;
            case 2:
                mFragments[index] = TenantOrderFragment.newInstance();
                break;
            case 3:
                mFragments[index] = TenantUserFragment.newInstance();
                break;
        }
    }

    private void initFirstInfo() {
        RxPermissions rxPermission = new RxPermissions(mActivity);
        rxPermission.request(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            checkVersion();
                        }
                    }
                });

        addRxBusSubscribe(UpdateEvent.class, new Action1<UpdateEvent>() {
            @Override
            public void call(UpdateEvent updateEvent) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(updateEvent.url);//此处填链接
                intent.setData(content_url);
                startActivity(intent);
            }
        });
    }

    @Override
    public int[] getMenuIds() {
        return new int[]{R.id.tv_main,
//                R.id.tv_findroom,
                R.id.tv_opendoor, R.id.tv_payrent, R.id.tv_user};
    }

    public int[] getMenuDrawables() {
        return new int[]{
                R.drawable.navigation_home,
//                R.drawable.navigation_findroom,
                R.drawable.navigation_opendoor, R.drawable.navigation_payrent, R.drawable.navigation_user};
    }

    @Override
    public boolean menuClicked(int index) {
        return false;
    }

    @Override
    public void init() {
        super.init();
        initFirstInfo();
        initFragment(0);
        switchContent(1, 0);

        int[] drawables = getMenuDrawables();
        for (int i = 0; i < getFragmentCount(); i++) {
            mBottomViews[i] = findViewById(mMenuIds[i]);
            final int finalI = i;
            mBottomViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuItemClicked(finalI);
                }
            });
//            if (i==1)
//                continue;
            Drawable d = ContextCompat.getDrawable(this, drawables[i]);
            d.setBounds(0, 0, densityUtil.dip2px(20f), densityUtil.dip2px(20f));
            ((TextView) mBottomViews[i]).setCompoundDrawables(null, d, null, null);
        }

        mBottomViews[0].setSelected(true);

        addRxBusSubscribe(LocationEvent.class, new Action1<LocationEvent>() {
            @Override
            public void call(LocationEvent locationEvent) {
                selectionCity();
            }
        });
//        getServerCity();
        getAllCity();
//        Log.e("mobile", LoginUserBean.getInstance().getMobile());

        PushManager.getInstance().bindAlias(this, LoginUserBean.getInstance().getMobile());
//        Log.e("eee", PushManager.getInstance().bindAlias(this, LoginUserBean.getInstance().getMobile()) + "");

    }

    private void getAllCity() {
//        Subscription subscription = RetrofitHelper.getInstance().getAllLetterCity()
//                .compose(RxUtil.<DataInfo<DicEntity>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<DicEntity>>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<DicEntity> cityInfoDataInfo) {
//                        if (cityInfoDataInfo.success()) {
//                            mAllCities.clear();
//                            int size = cityInfoDataInfo.data().lists.size();
//                            for (int i = 0; i < size; i++) {
//                                DicEntity.Content content = cityInfoDataInfo.data().lists.get(i);
//                                City city = new City(content.name, "", content.index, content.index);
//                                mAllCities.add(city);
//                            }
//
//                        }
//                    }
//                });
        Subscription subscription = SecondRetrofitHelper.getInstance().getCityList()
                .compose(RxUtil.<DataInfo<List<OpenCityBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<OpenCityBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo<List<OpenCityBean>> info) {
                        if (info.success()){
                            mAllCities.clear();
                            mCities.clear();
                            List<OpenCityBean> list=info.data();
                            if (list!=null) {
                                int size = info.data().size();
                                for (int i = 0; i <size; i++) {
                                    OpenCityBean openCityBean=list.get(i);
                                    City city=new City(openCityBean.getCity_name(),openCityBean.getProvince()+"",openCityBean.getPinyin(),openCityBean.getCity_id()+"");
                                    mAllCities.add(city);
                                    if (openCityBean.getIs_hot()==1){
                                        HotCity hotCity = new HotCity(openCityBean.getCity_name(), openCityBean.getProvince() + "", openCityBean.getCity_id() + "");
                                        mCities.add(hotCity);
                                    }
                                }
                            }
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void selectionCity() {
        Log.e(TAG, "selectionCity: ");
        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())    //此方法必须调用
                .enableAnimation(true)    //启用动画效果
//                .setAnimationStyle(anim)	//自定义动画
//                .setLocatedCity(null)  //APP自身已定位的城市，默认为null（定位失败）
                .setHotCities(mCities)    //指定热门城市
                .setAllCities(mAllCities)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        if (data == null)
                            return;
                        RxBus.getDefault().post(data);
                    }

                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //定位完成之后更新数据
//                                CityPicker.getInstance()
//                                        .locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
//                            }
//                        }, 2000);
                    }
                })
                .show();
    }

//    private void getServerCity() {
//        Subscription subscription = RetrofitHelper.getInstance().getHotCity()
//                .compose(RxUtil.<DataInfo<CityInfo>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<CityInfo>>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<CityInfo> cityInfoDataInfo) {
//                        if (cityInfoDataInfo.success()) {
//                            mCities.addAll(cityInfoDataInfo.data().lists);
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiPushService.class);
        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);
        if (LoginUserBean.getInstance().getIs_lodge_identity() == null || LoginUserBean.getInstance().getIs_lodge_identity().equals("")) {
            getUserInfo();
        }
        //neadGoTo(getIntent());
    }

    private void getUserInfo() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordUserDetailsInfo()
                .compose(RxUtil.<DataInfo<LandlordUserDetailsInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<LandlordUserDetailsInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        //doFailed();
                        showToast("请先登录");
                        LoginUserBean.getInstance().reset();
                        LoginUserBean.getInstance().save();
                        startActivity(new Intent(mActivity, LoginNewActivity.class));
                        mActivity.finish();
                    }

                    @Override
                    public void onNext(DataInfo<LandlordUserDetailsInfoBean> tenantUserinfoBeanDataInfo) {

                        dismiss();
                        if (tenantUserinfoBeanDataInfo.success()) {
                            LoginUserBean.getInstance().setIs_lodge_identity(tenantUserinfoBeanDataInfo.data().getIs_identity() + "");
                            LoginUserBean.getInstance().setRealname(tenantUserinfoBeanDataInfo.data().getReal_name());
                            LoginUserBean.getInstance().setIdentity(tenantUserinfoBeanDataInfo.data().getIdentity());
                            LoginUserBean.getInstance().setMobile(tenantUserinfoBeanDataInfo.data().getPhone());
                            LoginUserBean.getInstance().setIs_login_pass(tenantUserinfoBeanDataInfo.data().getIs_login_pass());
                            LoginUserBean.getInstance().setIs_withdraw_pass(tenantUserinfoBeanDataInfo.data().getIs_withdraw_pass());
                            LoginUserBean.getInstance().save();
                        } else {
                            showToast(tenantUserinfoBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        synchronized (mActivities) {
            AppManager.getInstance().killAllActivity();
        }
    }

    private void checkVersion() {

        showLoadingDialog();
//        Subscription subscription = SecondRetrofitHelper.getInstance().appConfig(String.valueOf(getVerName(mActivity)))
        Subscription subscription = SecondRetrofitHelper.getInstance().appConfig("1", getVerName(mActivity))
                .compose(RxUtil.<DataInfo<AppConfigBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AppConfigBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<AppConfigBean> info) {

                        dismiss();
                        if (info.success()) {
                            mAppConfigBean=info.data();
                            if (info.data().getVersion().getIs_forced() != 2) {
                                showUpdatePopup(info.data().getVersion());
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    private void showUpdatePopup(AppConfigBean.VersionBean mData) {
        // 开启 popup 时界面透明
        if (updateAppPopupwindow == null)
            updateAppPopupwindow = new UpdateAppPopupwindow(this, mData);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        updateAppPopupwindow.showAtLocation(findViewById(R.id.main_layout), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        updateAppPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (updateAppPopupwindow!=null&& updateAppPopupwindow.isShowing()){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
