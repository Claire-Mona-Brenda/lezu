package com.konka.renting.location;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.konka.renting.utils.RxBus;
import com.zaaach.citypicker.model.City;

/**
 * Created by jzxiang on 06/04/2018.
 */

public class AMapHelp implements AMapLocationListener {
    private static final String TAG = "AMapHelp";
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器

    private Context mContext;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    public AMapHelp(Context context) {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                Log.e(TAG, "onLocationChanged: " + amapLocation.getCity());
                Log.e(TAG, "onLocationChanged: " + amapLocation.getCityCode());

                LocationInfo locationInfo = new LocationInfo();
//                locationInfo.city_id = amapLocation.getCityCode();
                locationInfo.name = amapLocation.getCity();
                locationInfo.lng = amapLocation.getLongitude();
                locationInfo.lat = amapLocation.getLatitude();
                locationInfo.baseLat = amapLocation.getLatitude();
                locationInfo.baseLng = amapLocation.getLongitude();
                LocationUtils.saveBase(locationInfo);

                City city = new City(amapLocation.getCity(), amapLocation.getProvince(), "", amapLocation.getCityCode());
                RxBus.getDefault().post(city);

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }
}
