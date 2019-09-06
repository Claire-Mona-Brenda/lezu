package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.geocoder.BusinessArea;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterSearchListBean;
import com.konka.renting.bean.RoomGroupListBean;
import com.konka.renting.event.ChooseCityEvent;
import com.konka.renting.event.ChooseEstateEvent;
import com.konka.renting.event.ChooseEstateFinishEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.ChooseLocationEvent;
import com.konka.renting.tenant.opendoor.OpeningPopupwindow;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonInputPopupWindow;
import com.lljjcoder.utils.PinYinUtils;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ChooseLocationActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener, DistrictSearch.OnDistrictSearchListener, GeocodeSearch.OnGeocodeSearchListener {
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
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.activity_choose_location_mapview)
    MapView mMapview;
    @BindView(R.id.activity_choose_location_rv_location)
    RecyclerView mRvLocation;
    @BindView(R.id.activity_choose_location_srv_location)
    SmartRefreshLayout mSrvLocation;

    public final String COUNTRY = "country"; // 行政区划，国家级

    public final String PROVINCE = "province"; // 行政区划，省级

    public final String CITY = "city"; // 行政区划，市级

    public final String DISTRICT = "district"; // 行政区划，区级

    public final String BUSINESS = "biz_area"; // 行政区划，商圈级

    public final String CITY_NAME = "cityName";//SharedPreferences 键名

    SharedPreferences sharedPreferences;


    private final int STROKE_COLOR = Color.argb(0, 3, 145, 255);
    private final int FILL_COLOR = Color.argb(0, 0, 0, 180);

    AMap aMap;
    MyLocationStyle myLocationStyle;
    Marker screenMarker = null;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    private List<HotCity> mCities = new ArrayList<>();
    private List<City> mAllCities = new ArrayList<>();

    //当前选中的级别
    private String selectedLevel = COUNTRY;

    CommonAdapter<PoiItem> commonAdapter;
    PinYinUtils pinYinUtils;
    GeocodeSearch geocoderSearch;
    City city;

    PoiItem choosePoiItem;

    CommonInputPopupWindow inputPopupWindow;


    public static void toActivity(Context context, String cityName) {
        Intent intent = new Intent(context, ChooseLocationActivity.class);
        intent.putExtra("cityName", cityName);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_location;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_choose_location);
//        tvRight.setText(R.string.common_sure);
//        tvRight.setTextColor(getResources().getColor(R.color.text_blue));
//        tvRight.setVisibility(View.VISIBLE);

        poiItems = new ArrayList<>();
        pinYinUtils = new PinYinUtils();
        pinYinUtils.getCharPinYin('a');
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        sharedPreferences = getSharedPreferences(CITY, MODE_PRIVATE);
        String cityName = getIntent().getStringExtra("cityName");
        if (!TextUtils.isEmpty(cityName)) {
            city = new City(cityName, cityName, pinYinUtils.getStringPinYin(cityName), "");
            tvLocation.setText(cityName);
            GeocodeQuery query1 = new GeocodeQuery(cityName, cityName);
            geocoderSearch.getFromLocationNameAsyn(query1);
        }

        commonAdapter = new CommonAdapter<PoiItem>(this, poiItems, R.layout.adapter_choose_location) {
            @Override
            public void convert(ViewHolder viewHolder, PoiItem poiItem) {
                viewHolder.setText(R.id.adapter_location_tv_name, poiItem.getTitle());
                viewHolder.setText(R.id.adapter_location_tv_address, poiItem.getSnippet());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePoiItem = poiItem;
                        showInputPop(choosePoiItem.getTitle());
                        if (TextUtils.isEmpty(choosePoiItem.getBusinessArea())) {
                            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(choosePoiItem.getLatLonPoint().getLatitude(), choosePoiItem.getLatLonPoint().getLongitude()), 100, GeocodeSearch.AMAP);
                            geocoderSearch.getFromLocationAsyn(query);
                        }
//                        RxBus.getDefault().post(new ChooseLocationEvent(poiItem));
//                        finish();
                    }
                });
            }
        };

        mRvLocation.setLayoutManager(new LinearLayoutManager(this));
        mRvLocation.setAdapter(commonAdapter);

        mSrvLocation.setEnableRefresh(false);
        mSrvLocation.setEnableLoadmore(false);
        mSrvLocation.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                currentPage++;
                query.setPageNum(currentPage);
                poiSearch.searchPOIAsyn();// 异步搜索
            }
        });

        addRxBusSubscribe(ChooseLocationEvent.class, new Action1<ChooseLocationEvent>() {
            @Override
            public void call(ChooseLocationEvent event) {
                finish();
            }
        });
        addRxBusSubscribe(ChooseCityEvent.class, new Action1<ChooseCityEvent>() {
            @Override
            public void call(ChooseCityEvent chooseCityEvent) {
                if (!TextUtils.isEmpty(chooseCityEvent.getCityName())) {
                    city = new City(chooseCityEvent.getCityName(), chooseCityEvent.getCityName(), pinYinUtils.getStringPinYin(chooseCityEvent.getCityName()), "");
                    tvLocation.setText(chooseCityEvent.getCityName());
                    GeocodeQuery query2 = new GeocodeQuery(city.getName(), city.getName());
                    geocoderSearch.getFromLocationNameAsyn(query2);
                }
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapview.onCreate(savedInstanceState);

        if (aMap == null)
            aMap = mMapview.getMap();
        setupLocationStyle();
        initListener();
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        initCity();

    }

    private void initListener() {
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.getLatitude(), location.getLongitude()), 200, GeocodeSearch.AMAP);
//                geocoderSearch.getFromLocationAsyn(query);
            }
        });
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
            }
        });

        // 设置可视范围变化时的回调的接口方法
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition postion) {
                if (screenMarker == null) {
                    addMarkerInScreenCenter();
                }
                //屏幕中心的Marker跳动
                startJumpAnimation();
                currentPage = 1;
                doSearchQuery();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }


    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        myLocationStyle.showMyLocation(false);
        aMap.setMyLocationEnabled(false);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_icon)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {

        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= UIUtils.dip2px(80);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        }
    }

    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        query = new PoiSearch.Query("", "", city == null ? "" : city.getName());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (screenMarker != null) {
            LatLonPoint lp = new LatLonPoint(screenMarker.getPosition().latitude, screenMarker.getPosition().longitude);
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.tv_location, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                break;
            case R.id.tv_location:
                selectionCity();
                break;
            case R.id.tv_search:
                SearchAddressActivity.toActivity(this, (ArrayList<City>) mAllCities, (ArrayList<HotCity>) mCities, city);
                break;
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        mSrvLocation.finishLoadmore();
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    if (currentPage <= 1)
                        poiItems.clear();
                    poiItems.addAll(poiResult.getPois());// 取得第一页的poiitem数据，页数从数字0开始
                    commonAdapter.notifyDataSetChanged();
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (currentPage + 1 < result.getPageCount()) {
                        mSrvLocation.setEnableLoadmore(true);
                    } else {
                        mSrvLocation.setEnableLoadmore(false);
                    }
                }
            } else {//无搜索结果

            }
        } else {
            showToast(rcode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 初始化
     */
    private void initCity() {

        // 设置行政区划查询监听
        DistrictSearch districtSearch = new DistrictSearch(this);
        districtSearch.setOnDistrictSearchListener(this);
        // 查询中国的区划
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords("中国");
        query.setPageSize(50);
        districtSearch.setQuery(query);
        // 异步查询行政区
        districtSearch.searchDistrictAsyn();

    }

    /**
     * 查询省级以下城市
     */
    private void searchCity(DistrictItem districtItem) {
        // 设置行政区划查询监听
        DistrictSearch districtSearch = new DistrictSearch(this);
        districtSearch.setOnDistrictSearchListener(this);
        // 查询中国的区划
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(districtItem.getName());
        districtSearch.setQuery(query);
        // 异步查询行政区
        districtSearch.searchDistrictAsyn();

    }

    private void selectionCity() {
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
                        if (!TextUtils.isEmpty(data.getName())) {
                            city = data;
                            tvLocation.setText(data.getName());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(CITY_NAME, city.getName());
                            editor.commit();
                            RxBus.getDefault().post(new ChooseCityEvent(data.getName()));
                            // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                            GeocodeQuery query = new GeocodeQuery(data.getProvince() + data.getName(), data.getName());
                            geocoderSearch.getFromLocationNameAsyn(query);
                        }
                    }


                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位

                    }
                })
                .show();
    }

    /**
     * 返回District（行政区划）异步处理的结果
     */
    @Override
    public void onDistrictSearched(DistrictResult result) {
        if (result != null) {
            if (result.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
                if (selectedLevel.equals(COUNTRY)) {
                    selectedLevel = CITY;
                    List<DistrictItem> district = result.getDistrict().get(0).getSubDistrict();
                    for (int i = 0; i < district.size(); i++) {
                        DistrictItem districtItem = district.get(i);
                        searchCity(districtItem);
                    }
                } else if (selectedLevel.equals(CITY)) {
                    DistrictItem item = result.getDistrict().get(0);
                    List<DistrictItem> district = item.getSubDistrict();
                    for (int i = 0; i < district.size(); i++) {
                        DistrictItem districtItem = district.get(i);
                        City city = new City(districtItem.getName(), item.getName(), pinYinUtils.getStringPinYin(districtItem.getName()), item.getCitycode());
                        mAllCities.add(city);
                    }
                }
            } else {

            }
        }
    }


    /**
     * 逆地理编码（坐标转地址）结果回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        String a ;
        List<BusinessArea> businessAreaList = regeocodeResult.getRegeocodeAddress().getBusinessAreas();
        if (businessAreaList.size() > 0) {
            a = businessAreaList.get(0).getName();
        } else if (!TextUtils.isEmpty(regeocodeResult.getRegeocodeAddress().getNeighborhood())) {
            a = regeocodeResult.getRegeocodeAddress().getNeighborhood();
        } else if (!TextUtils.isEmpty(regeocodeResult.getRegeocodeAddress().getTownship())) {
            a = regeocodeResult.getRegeocodeAddress().getTownship();
        }else{
            a= choosePoiItem.getTitle();
        }
        if (TextUtils.isEmpty(choosePoiItem.getBusinessArea())) {
            choosePoiItem.setBusinessArea(a);
        }
    }

    /**
     * 地理编码（地址转坐标）
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
        changeCamera(address.getLatLonPoint().getLatitude(), address.getLatLonPoint().getLongitude());
    }

    private void changeCamera(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 11, 30, 30)));
    }


    /*******************************************弹窗***********************************************************/
    private void showInputPop(String name) {
        if (inputPopupWindow == null) {
            inputPopupWindow = new CommonInputPopupWindow(mActivity);
            inputPopupWindow.setTvTitle(getString(R.string.estate_name));
            inputPopupWindow.setBtnRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = inputPopupWindow.getEdtContent().getText().toString();
                    if (!TextUtils.isEmpty(name)) {
                        inputPopupWindow.dismiss();
                        roomGroupAdd(name);
                    } else {
                        showToast(R.string.please_input_estate_name);
                    }
                }
            });
        }
        inputPopupWindow.getEdtContent().setText(name);
        inputPopupWindow.getEdtContent().setSelection(name.length());
        showPopup(inputPopupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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

    /*****************************************************接口*********************************************/
    private void roomGroupAdd(String name) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().roomGroupAdd(name, choosePoiItem.getProvinceName(), choosePoiItem.getCityName(),
                choosePoiItem.getAdName(), choosePoiItem.getBusinessArea(), choosePoiItem.getSnippet(),
                choosePoiItem.getLatLonPoint().getLongitude() + "", choosePoiItem.getLatLonPoint().getLatitude() + "")
                .compose(RxUtil.<DataInfo<RoomGroupListBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomGroupListBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<RoomGroupListBean> info) {
                        dismiss();
                        if (info.success()) {
                            RxBus.getDefault().post(new ChooseEstateEvent(info.data()));
                            RxBus.getDefault().post(new ChooseEstateFinishEvent());
                            finish();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
