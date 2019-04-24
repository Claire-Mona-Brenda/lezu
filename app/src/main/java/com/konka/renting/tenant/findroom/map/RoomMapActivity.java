package com.konka.renting.tenant.findroom.map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LocationRefreshEvent;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.event.LocationEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.search.SearchActivity;
import com.konka.renting.landlord.house.entity.RoomList;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.location.LocationInfo;
import com.konka.renting.location.LocationUtils;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


/**
 * AMapV1地图中介绍如何显示世界图
 */
public class RoomMapActivity implements OnClickListener, LocationSource {

    private MapView mapView;
    private AMap aMap;
    private ImageView bnt_loc;
    AppCompatImageView loc;
    Context context;
    EditText ct_search;
    Button button_query;
    TextView title;
    private CompositeSubscription mCompositeSubscription;
    private AMapLocationClient locationClient = null;

    public void initView(final View view, MapView mapView) {

        this.mapView = mapView;

        context = view.getContext();
        title=view.findViewById(R.id.text_title);
        button_query = view.findViewById(R.id.button_query);
        bnt_loc = (ImageView) view.findViewById(R.id.location);
        bnt_loc.setOnClickListener(this);
        ct_search = view.findViewById(R.id.ct_search);
        ct_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.toActivity(context);
            }
        });
        loc =  view.findViewById(R.id.location);
        loc.setOnClickListener(this);
        button_query.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getDefault().post(new LocationEvent());
            }
        });
        init();
//		drawMarker();
        getData();

        addRxBusSubscribe(LocationRefreshEvent.class, new Action1<LocationRefreshEvent>() {
            @Override
            public void call(LocationRefreshEvent locationRefreshEvent) {
                getData();

                //刷新经纬度
            }
        });


    }

    private static final String TAG = "RoomMapActivity";

    public void getData() {
      //  ShowToastUtil.showLoadingDialog(context);
        LocationInfo locationInfo = LocationUtils.getInstance();
        Log.e(TAG, "getData: " + locationInfo.lat);
        Log.e(TAG, "getData: " + locationInfo.city_id);
        title.setText(locationInfo.name);
        rx.Observable<DataInfo<RoomList>> observable = null;
        observable = RetrofitHelper.getInstance().getRoomList(locationInfo.city_id, locationInfo.lat + "", locationInfo.lng + "");
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                locationInfo.lat, locationInfo.lng)));
        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<RoomList>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomList>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                        ShowToastUtil.showWarningToast(context, "请求失败");
                       // ShowToastUtil.dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<RoomList> homeInfoDataInfo) {
//                        dismiss();
                     //   ShowToastUtil.dismiss();
                        if (homeInfoDataInfo.success()) {
                            homeInfoDataInfo.data();
                            bindData(homeInfoDataInfo.data().lists);
                        } else {
//                            showToast(homeInfoDataInfo.msg());
                            ShowToastUtil.showWarningToast(context, homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    public void bindData(List<RoomInfo> list) {
        LocationInfo locationInfo = LocationUtils.getInstance();
        title.setText(locationInfo.name);
        for (RoomInfo info : list) {
            drawMarker(info);
        }
    }

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    /**
     * 管理各组件之间的通信
     *
     * @param eventType
     * @param act
     * @param <U>
     */
    protected <U> void addRxBusSubscribe(Class<U> eventType, Action1<U> act) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(eventType, act));
    }

    public void drawMarker(RoomInfo info) {
//	LatLng latLng = new LatLng(39.906901,116.397972);
//	final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
        LocationInfo locationInfo = LocationUtils.getInstance();
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(Double.parseDouble(info.lat), Double.parseDouble(info.lng)));
//        markerOption.position(Constants.BEIJING);
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");

        markerOption.draggable(false);//设置Marker可拖动
        if (info.isRentOut()){
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(context.getResources(), R.mipmap.h_ic1)));
        }else {
            if (info.type.equals("1")) {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(context.getResources(), R.mipmap.h_ic3)));
            } else if (info.type.equals("2")) {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(context.getResources(), R.mipmap.h_ic2)));
            }
        }


        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//	markerOption.setFlat(true);//设置marker平贴地图效果
        Marker marke = aMap.addMarker(markerOption);
        marke.setObject(info);
    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();

        }
//		aMap.setLocationSource(this);//设置了定位的监听
        UiSettings settings = aMap.getUiSettings();
//		aMap.setLocationSource(this);//设置了定位的监听
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(false);

//        aMap.setMyLocationEnabled(true);
        aMap.setMapLanguage(AMap.CHINESE);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
//		aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        aMap.setInfoWindowAdapter(new RoomMapWindow(context));

    }


    @Override
    public void onClick(View v) {
//         AMapLocationListener mLocationListener = new AMapLocationListener() {
//
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                if (aMapLocation != null) {
//                    if (aMapLocation.getErrorCode() == 0) {
//                        int locationType = aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                        double latitude = aMapLocation.getLatitude();//获取纬度
//                        double longitude = aMapLocation.getLongitude();//获取经度
//                        float accuracy = aMapLocation.getAccuracy();//获取精度信息
//                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
//                                latitude, longitude)));
//                        Log.e("定位", locationType + " +" + latitude + "+" + longitude + "+" + accuracy);
////                        double distance = GetJuLiUtils.getDistance(118.924079
////                                , 42.249544, longitude, latitude);
////                        Log.e("定位",distance+"米");
//                    } else {
//
//
//                    }
//                }
//            }
//        };
//        GaoDeUtils.getJuLi(mLocationListener,context);
        LocationInfo locationInfo = LocationUtils.getInstance();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                locationInfo.lat, locationInfo.lng)));
        AMap aMap = mapView.getMap();
        aMap.clear();
        getData();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }
}