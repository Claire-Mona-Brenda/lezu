package com.konka.renting.tenant.main.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.VisibleRegion;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GroupRoomListBean;
import com.konka.renting.bean.MapSearchBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RoomPriceAreaBean;
import com.konka.renting.bean.RoomTypeListBean;
import com.konka.renting.event.MapSearchChooseEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.lljjcoder.utils.PinYinUtils;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MapFindHouseActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener {

    public static final String TAG = MapFindHouseActivity.class.getSimpleName();

    @BindView(R.id.activity_map_find_house_img_back)
    ImageView imgBack;
    @BindView(R.id.activity_map_find_house_tv_long_rent)
    TextView mTvLongRent;
    @BindView(R.id.activity_map_find_house_view_long_line)
    View mViewLongLine;
    @BindView(R.id.activity_map_find_house_ll_long_rent)
    LinearLayout mLlLongRent;
    @BindView(R.id.activity_map_find_house_tv_short_rent)
    TextView mTvShortRent;
    @BindView(R.id.activity_map_find_house_view_short_line)
    View mViewShortLine;
    @BindView(R.id.activity_map_find_house_ll_short_rent)
    LinearLayout mLlShortRent;
    @BindView(R.id.activity_map_find_house_img_filtrate)
    ImageView mImgFiltrate;
    @BindView(R.id.activity_map_find_house_img_search)
    ImageView mImgSearch;
    @BindView(R.id.activity_map_find_house_mapview)
    TextureMapView mMapview;
    @BindView(R.id.activity_find_house_ll_main)
    LinearLayout mLlMain;
    @BindView(R.id.activity_find_house_tv_title)
    TextView mTvTitle;
    @BindView(R.id.activity_find_house_rv_price)
    RecyclerView mRvPrice;
    @BindView(R.id.activity_find_house_edt_price_min)
    EditText mEdtPriceMin;
    @BindView(R.id.activity_find_house_edt_price_max)
    EditText mEdtPriceMax;
    @BindView(R.id.activity_find_house_rv_house_type)
    RecyclerView mRvHouseType;
    @BindView(R.id.activity_find_house_tv_reset)
    TextView mTvReset;
    @BindView(R.id.activity_find_house_tv_sure)
    TextView mTvSure;
    @BindView(R.id.activity_find_house_ll_filtrate)
    LinearLayout mLlFiltrate;
    @BindView(R.id.activity_find_house_drawerlayout)
    DrawerLayout mDrawerlayout;

    private final int STROKE_COLOR = Color.argb(1, 3, 145, 255);
    public final String COUNTRY = "country"; // 行政区划，国家级
    public final String PROVINCE = "province"; // 行政区划，省级
    public final String CITY = "city"; // 行政区划，市级

    PinYinUtils pinYinUtils;
    //当前选中的级别
    private String selectedLevel = COUNTRY;
    private List<HotCity> mCities = new ArrayList<>();
    private List<City> mAllCities = new ArrayList<>();
    City chooseCity;
    GeocodeSearch geocoderSearch;

    int rent_type = 2;//1 短租 2 长租
    int page = 1;
    boolean is_no_limit_price_r = true;//短租
    boolean is_no_limit_price_l = true;//长租
    int curr_level = 11;
    String price_area_id_l = "";//长租
    String price_area_id_r = "";//短租
    String room_type_id = "";
    String price_area_l = "";//长租
    String price_area_r = "";//短租

    String cityName;
    AMap aMap;

    List<RoomPriceAreaBean> priceAreaList = new ArrayList<>();
    List<RoomTypeListBean> roomTypeList = new ArrayList<>();

    List<RoomPriceAreaBean> chooseLPriceList = new ArrayList<>();
    List<RoomPriceAreaBean> chooseSPriceList = new ArrayList<>();
    List<RoomTypeListBean> chooseTypeList = new ArrayList<>();

    CommonAdapter<RoomPriceAreaBean> priceAreaCommonAdapter;
    CommonAdapter<RoomTypeListBean> roomTypeCommonAdapter;

    List<MapSearchBean> searchBeans = new ArrayList<>();
    List<GroupRoomListBean> roomListBeans = new ArrayList<>();

    EstateListPopup estateListPopup;
    String room_group_id = "";

    Subscription timeSubscription;
    boolean isTimer = false;
    boolean isInitCity = true;

    double curr_lat = 0;
    double curr_lng = 0;

    private boolean isGeocodeSearched=false;
    private HashMap<String, LatLng> geocodeSearchMap;

    public static void toActivity(Context context, String cityName) {
        Intent intent = new Intent(context, MapFindHouseActivity.class);
        intent.putExtra("cityName", cityName);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_map_find_house;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGeocodeSearchMap();
        mMapview.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapview.getMap();
        }
        geocoderSearch = new GeocodeSearch(mActivity);
        geocoderSearch.setOnGeocodeSearchListener(this);
        initMap();
    }

    @Override
    public void init() {
        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        UIUtils.setDarkStatusIcon(this, true);

        cityName = getIntent().getStringExtra("cityName");

        rent_type = 2;
        mTvLongRent.setSelected(true);
        mViewLongLine.setVisibility(View.VISIBLE);
        mTvShortRent.setSelected(false);
        mViewShortLine.setVisibility(View.INVISIBLE);


        initAdapter();
        initPop();
        initListener();
        getRoomPriceAreaList();
        getRoomTypeList();

        addRxBusSubscribe(MapSearchChooseEvent.class, new Action1<MapSearchChooseEvent>() {
            @Override
            public void call(MapSearchChooseEvent event) {
                LatLng latLng = new LatLng(Double.valueOf(event.getLat()), Double.valueOf(event.getLng()));
                int level = 11;
                switch (event.getLevel()) {
                    case 3:
                        level = 11;
                        break;
                    case 4:
                        level = 13;
                        break;
                    case 5:
                        level = 15;
                        break;
                }
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, level, 0, 0)));
            }
        });

    }

    private void initListener() {
        mDrawerlayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (rent_type == 2) {//长租
                    if (priceAreaList.size() <= 0) {
                        return;
                    }
                    if (!TextUtils.isEmpty(price_area_l)) {
                        String[] p = price_area_l.split("-");
                        mEdtPriceMin.setText(p[0]);
                        mEdtPriceMax.setText(p[1]);
                        mEdtPriceMin.setSelection(p[0].length());
                        mEdtPriceMax.setSelection(p[1].length());
                        is_no_limit_price_l = true;
                        priceAreaCommonAdapter.notifyDataSetChanged();

                    } else if (TextUtils.isEmpty(price_area_id_l)) {
                        mEdtPriceMin.setText("");
                        mEdtPriceMax.setText("");
                        is_no_limit_price_l = true;
                        chooseLPriceList.clear();
                        priceAreaCommonAdapter.notifyDataSetChanged();
                    } else {
                        mEdtPriceMin.setText("");
                        mEdtPriceMax.setText("");
                        String[] ids = price_area_id_l.split(",");
                        chooseLPriceList.clear();
                        int len = ids.length;
                        for (int i = 0; i < len; i++) {
                            int size=priceAreaList.size();
                            for (int j = 0; j < size; j++) {
                                if (ids[i].equals(priceAreaList.get(j).getId()+"")){
                                    chooseLPriceList.add(priceAreaList.get(j));
                                    break;
                                }
                            }
                        }
                        priceAreaCommonAdapter.notifyDataSetChanged();
                    }
                } else {//短租
                    if (priceAreaList.size() <= 0) {
                        return;
                    }
                    if (!TextUtils.isEmpty(price_area_r)) {
                        String[] p = price_area_r.split("-");
                        mEdtPriceMin.setText(p[0]);
                        mEdtPriceMax.setText(p[1]);
                        mEdtPriceMin.setSelection(p[0].length());
                        mEdtPriceMax.setSelection(p[1].length());
                        is_no_limit_price_r = true;
                        priceAreaCommonAdapter.notifyDataSetChanged();

                    } else if (TextUtils.isEmpty(price_area_id_r)) {
                        mEdtPriceMin.setText("");
                        mEdtPriceMax.setText("");
                        is_no_limit_price_r = true;
                        chooseSPriceList.clear();
                        priceAreaCommonAdapter.notifyDataSetChanged();
                    } else {
                        mEdtPriceMin.setText("");
                        mEdtPriceMax.setText("");
                        String[] ids = price_area_id_r.split(",");
                        chooseSPriceList.clear();
                        int len = ids.length;
                        for (int i = 0; i < len; i++) {
                            int size=priceAreaList.size();
                            for (int j = 0; j < size; j++) {
                                if (ids[i].equals(priceAreaList.get(j).getId()+"")){
                                    chooseSPriceList.add(priceAreaList.get(j));
                                    break;
                                }
                            }
                        }
                        priceAreaCommonAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.strokeWidth(new Float(0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.showMyLocation(true);

        aMap.setMyLocationStyle(myLocationStyle); // 设置定位蓝点的Style
        aMap.setMyLocationEnabled(true); // 设置为true表示启动显示定位蓝点
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMinZoomLevel(9);
        aMap.setMaxZoomLevel(19);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        // 设置可视范围变化时的回调的接口方法
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition postion) {
                if (!isTimer)
                    addMarkerInScreen();
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                if (geocoderSearch != null) {
                    GeocodeQuery query = new GeocodeQuery(cityName, cityName);
                    Log.d(TAG, "onMapLoaded() cityName = " + cityName);
                    geocoderSearch.getFromLocationNameAsyn(query);

                    LatLng latLng = null;
                    if (!isGeocodeSearched) {
                        latLng = geocodeSearchMap.get(cityName);
                        Log.d(TAG, "从缓存中获取的地址对应的经纬度信息：" +
                                latLng.longitude + ", " + latLng.latitude);
                        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition(latLng, 11, 0, 0)));
                        isGeocodeSearched = true;
                    }
                }
            }
        });

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isTimer = true;
                        checkLocationTime();
                        curr_lat = aMap.getCameraPosition().target.latitude;
                        curr_lng = aMap.getCameraPosition().target.longitude;
                        Log.d(TAG, curr_lng + ", " + curr_lat);
                        break;
                    case MotionEvent.ACTION_UP:
                        isTimer = false;
                        timeSubscription.unsubscribe();
                        removeSubscrebe(timeSubscription);
                        break;
                }
            }
        });

    }


    private void initAdapter() {

        priceAreaCommonAdapter = new CommonAdapter<RoomPriceAreaBean>(this, priceAreaList, R.layout.adapter_find_map_filtrate_price) {
            @Override
            public void convert(ViewHolder viewHolder, RoomPriceAreaBean bean) {
                if (!TextUtils.isEmpty(bean.getName())) {
                    viewHolder.setText(R.id.adapter_find_map_price_tv_price, bean.getName());
                } else {
                    viewHolder.setText(R.id.adapter_find_map_price_tv_price, bean.getMin_price() + "-" + bean.getMax_price());
                }
                if (is_no_limit_price_r && rent_type == 1) {
                    if (bean.getId() == -1) {
                        viewHolder.setSelected(R.id.adapter_find_map_price_tv_price, true);
                    } else {
                        viewHolder.setSelected(R.id.adapter_find_map_price_tv_price, false);
                    }
                } else if (rent_type == 1) {
                    boolean isChoose = false;
                    int size = chooseSPriceList.size();
                    for (int i = 0; i < size; i++) {
                        if (chooseSPriceList.get(i).getId() == bean.getId()) {
                            isChoose = true;
                            break;
                        }
                    }
                    viewHolder.setSelected(R.id.adapter_find_map_price_tv_price, isChoose);
                } else if (is_no_limit_price_l && rent_type == 2) {
                    if (bean.getId() == -1) {
                        viewHolder.setSelected(R.id.adapter_find_map_price_tv_price, true);
                    } else {
                        viewHolder.setSelected(R.id.adapter_find_map_price_tv_price, false);
                    }
                } else if (rent_type == 2) {
                    boolean isChoose = false;
                    int size = chooseLPriceList.size();
                    for (int i = 0; i < size; i++) {
                        if (chooseLPriceList.get(i).getId() == bean.getId()) {
                            isChoose = true;
                            break;
                        }
                    }
                    viewHolder.setSelected(R.id.adapter_find_map_price_tv_price, isChoose);
                }
                viewHolder.setOnClickListener(R.id.adapter_find_map_price_tv_price,
                        new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tv = (TextView) view;
                        boolean is = !tv.isSelected();
                        tv.setSelected(is);
                        if (is && bean.getId() == -1) {
                            if (rent_type == 1) {
                                is_no_limit_price_r = true;
                                chooseSPriceList.clear();
                            } else {
                                is_no_limit_price_l = true;
                                chooseLPriceList.clear();
                            }
                            priceAreaCommonAdapter.notifyDataSetChanged();
                        } else if (is) {
                            if (rent_type == 1) {
                                chooseSPriceList.add(bean);
                                if (is_no_limit_price_r) {
                                    is_no_limit_price_r = false;
                                    priceAreaCommonAdapter.notifyDataSetChanged();
                                }
                            } else {
                                chooseLPriceList.add(bean);
                                if (is_no_limit_price_l) {
                                    is_no_limit_price_l = false;
                                    priceAreaCommonAdapter.notifyDataSetChanged();
                                }
                            }

                        } else if (bean.getId() != -1) {
                            if (rent_type == 1) {
                                int size = chooseSPriceList.size();
                                for (int i = 0; i < size; i++) {
                                    if (chooseSPriceList.get(i).getId() == bean.getId()) {
                                        chooseSPriceList.remove(i);
                                        break;
                                    }
                                }

                            } else {
                                int size = chooseLPriceList.size();
                                for (int i = 0; i < size; i++) {
                                    if (chooseLPriceList.get(i).getId() == bean.getId()) {
                                        chooseLPriceList.remove(i);
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (rent_type == 1) {
                                is_no_limit_price_r = false;
                            } else {
                                is_no_limit_price_l = false;
                            }

                        }

                    }
                });
            }
        };
        roomTypeCommonAdapter = new CommonAdapter<RoomTypeListBean>(this, roomTypeList, R.layout.adapter_find_map_filtrate_type) {
            @Override
            public void convert(ViewHolder viewHolder, RoomTypeListBean bean) {
                viewHolder.setText(R.id.adapter_find_map_type_tv_type, bean.getName());
                boolean isChoose = false;
                int size = chooseTypeList.size();
                for (int i = 0; i < size; i++) {
                    if (chooseTypeList.get(i).getId() == bean.getId()) {
                        isChoose = true;
                        break;
                    }
                }
                viewHolder.setSelected(R.id.adapter_find_map_type_tv_type, isChoose);
                viewHolder.setOnClickListener(R.id.adapter_find_map_type_tv_type,
                        new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tv = (TextView) view;
                        boolean is = !tv.isSelected();
                        tv.setSelected(is);
                        if (is) {
                            chooseTypeList.add(bean);
                        } else {
                            chooseTypeList.remove(bean);
                        }
                    }
                });
            }
        };

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);// 定义瀑布流管理器，第一个参数是列数，第二个是方向。
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//        不设置的话，图片闪烁错位，有可能有整列错位的情况。
        mRvPrice.setLayoutManager(layoutManager);
        mRvPrice.setAdapter(priceAreaCommonAdapter);

        mRvHouseType.setLayoutManager(new GridLayoutManager(this, 3));
        mRvHouseType.setAdapter(roomTypeCommonAdapter);
    }

    private void initPop() {
        if (estateListPopup == null) {
            estateListPopup = new EstateListPopup(mActivity);
            estateListPopup.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshlayout) {
                    groupRoomList(true);
                }
            });
            estateListPopup.setOnLoadmoreListener(new OnLoadmoreListener() {
                @Override
                public void onLoadmore(RefreshLayout refreshlayout) {
                    groupRoomList(false);
                }
            });
        }
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
        if (null != mMapview) {
            mMapview.onDestroy();
        }
        if (null != aMap) {
            aMap.stopAnimation();
            aMap.clear();
        }
        mMapview = null;
        aMap = null;
    }

    /**
     * 在屏幕添加Marker
     */
    private void addMarkerInScreen() {
        VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion();
        LatLng farLeft = visibleRegion.farLeft;//左上角
        LatLng nearRight = visibleRegion.nearRight;//右下角;
        int level = (int) aMap.getCameraPosition().zoom;
        String point1 = farLeft.latitude + "," + farLeft.longitude;
        String point2 = nearRight.latitude + "," + nearRight.longitude;
        mapSearch(level, point1, point2, "");
    }

    private void addMarkerCircle(double lat, double lng, MapSearchBean mapSearchBean, int z) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(lat, lng));
        markerOption.anchor(0.5f, 0.5f);
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), rent_type == 2 ? R.mipmap.map_orange_bg : R.mipmap.map_green_bg)));
        markerOption.alpha(0.9f);
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        markerOption.zIndex(z);
        Marker marker = aMap.addMarker(markerOption);
        marker.setObject(mapSearchBean);
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                float level = aMap.getCameraPosition().zoom;
                MapSearchBean bean = (MapSearchBean) marker.getObject();
                double latbean = Double.valueOf(bean.getNext_lat());
                double lngbean = Double.valueOf(bean.getNext_lng());
                if (level < 11) {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latbean, lngbean), 11, 0, 0)));
                } else if (level < 13) {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latbean, lngbean), 13, 0, 0)));
                } else if (level <= 15) {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latbean, lngbean), 15, 0, 0)));
                }
                return true;
            }
        });

        TextOptions textOptions1 = new TextOptions();
        String name = mapSearchBean.getName();
        if (name.length() > 5) {
            name = name.substring(0, 5) + "...";
        }
        textOptions1.text(name);
        textOptions1.position(new LatLng(lat, lng));
        textOptions1.align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_BOTTOM);
        textOptions1.backgroundColor(Color.parseColor("#00FF7500"));
        textOptions1.fontColor(getResources().getColor(R.color.color_ffffff));
        textOptions1.fontSize(getResources().getDimensionPixelSize(R.dimen.sp_12));
        textOptions1.zIndex(z + 1);
        aMap.addText(textOptions1);

        TextOptions textOptions2 = new TextOptions();
        textOptions2.position(new LatLng(lat, lng));
        textOptions2.align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_TOP);
        textOptions2.text(mapSearchBean.getCount() + "套");
        textOptions2.backgroundColor(Color.parseColor("#00FF7500"));
        textOptions2.fontColor(getResources().getColor(R.color.color_ffffff));
        textOptions2.fontSize(getResources().getDimensionPixelSize(R.dimen.sp_10));
        textOptions2.zIndex(z + 1);
        aMap.addText(textOptions2);
    }

    private void addMarkerEstate(double lat, double lng, MapSearchBean mapSearchBean, int z) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.infoWindowEnable(false);
        markerOption.position(new LatLng(lat, lng));
        markerOption.anchor(0.5f, 0.5f);
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),
                        rent_type == 2 ? R.mipmap.map_2_orange_bg : R.mipmap.map_2_green_bg)));
        markerOption.alpha(0.9f);
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        markerOption.zIndex(z);
        Marker marker = aMap.addMarker(markerOption);
        marker.setObject(mapSearchBean);
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MapSearchBean bean = (MapSearchBean) marker.getObject();
                if (!TextUtils.isEmpty(room_group_id) && room_group_id.equals(bean.getRoom_group_id() + "")) {
                    showRoomListPop(bean.getName() + " " + bean.getCount() + "套",
                            false);
                } else {
                    roomListBeans.clear();
                    room_group_id = bean.getRoom_group_id() + "";
                    showRoomListPop(bean.getName() + " " + bean.getCount() + "套",
                            true);

                }
                return true;
            }
        });

        TextOptions textOptions = new TextOptions();
        textOptions.position(new LatLng(lat, lng));
        String name = mapSearchBean.getName();
        if (name.length() > 6) {
            name = name.substring(0, 6) + "...";
        }
        textOptions.text(name + " " + mapSearchBean.getCount() + "套");
        textOptions.backgroundColor(Color.parseColor("#00FF7500"));
        textOptions.fontColor(getResources().getColor(R.color.color_ffffff));
        textOptions.fontSize(getResources().getDimensionPixelSize(R.dimen.sp_12));
        textOptions.zIndex(z + 1);
        aMap.addText(textOptions);
    }


    @OnClick({
            R.id.activity_map_find_house_img_back,
            R.id.activity_map_find_house_ll_long_rent,
            R.id.activity_map_find_house_ll_short_rent,
            R.id.activity_map_find_house_img_filtrate,
            R.id.activity_map_find_house_img_search,
            R.id.activity_find_house_tv_reset,
            R.id.activity_find_house_tv_sure
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_map_find_house_img_back://返回
                finish();
                break;
            case R.id.activity_map_find_house_ll_long_rent://切换长租
                if (rent_type != 2) {
                    rent_type = 2;
                    mTvLongRent.setSelected(true);
                    mViewLongLine.setVisibility(View.VISIBLE);
                    mTvShortRent.setSelected(false);
                    mViewShortLine.setVisibility(View.INVISIBLE);
                    if (aMap != null)
                        aMap.clear();
                    curr_level = 0;
                    room_group_id = "-1";
                    addMarkerInScreen();
                }
                break;
            case R.id.activity_map_find_house_ll_short_rent://切换短租
                if (rent_type != 1) {
                    rent_type = 1;
                    mTvLongRent.setSelected(false);
                    mViewLongLine.setVisibility(View.INVISIBLE);
                    mTvShortRent.setSelected(true);
                    mViewShortLine.setVisibility(View.VISIBLE);
                    if (aMap != null)
                        aMap.clear();
                    curr_level = 0;
                    room_group_id = "-1";
                    addMarkerInScreen();
                }
                break;
            case R.id.activity_map_find_house_img_filtrate://筛选
                String title = getString(rent_type == 1 ? R.string.competitive_house_short : R.string.competitive_house_long);
                if (!mTvTitle.getText().toString().equals(title)) {
                    priceAreaList.clear();
                    priceAreaCommonAdapter.notifyDataSetChanged();
                    getRoomPriceAreaList();

                }
                mTvTitle.setText(title);
                mDrawerlayout.openDrawer(mLlFiltrate);
                break;
            case R.id.activity_map_find_house_img_search://搜索
                MapSearchActivity.toActivity(this, cityName, rent_type);
                break;
            case R.id.activity_find_house_tv_reset:
                if (rent_type == 1) {
                    is_no_limit_price_r = true;
                    chooseSPriceList.clear();
                    price_area_r = "";
                    price_area_id_r = "";
                } else {
                    is_no_limit_price_l = true;
                    chooseLPriceList.clear();
                    price_area_l = "";
                    price_area_id_l = "";
                }

                chooseTypeList.clear();
                priceAreaCommonAdapter.notifyDataSetChanged();
                roomTypeCommonAdapter.notifyDataSetChanged();
                if (aMap != null)
                    aMap.clear();
                room_group_id = "-1";
                curr_level = 0;
                addMarkerInScreen();
                break;
            case R.id.activity_find_house_tv_sure://筛选确定
                String priceMin = mEdtPriceMin.getText().toString();
                String priceMax = mEdtPriceMax.getText().toString();

                if (!TextUtils.isEmpty(priceMax) && !TextUtils.isEmpty(priceMin)) {
                    if (Integer.valueOf(priceMin) >= Integer.valueOf(priceMax)) {
                        showToast(R.string.please_choose_price);
                        return;
                    }
                    if (rent_type == 1) {
                        price_area_r = priceMin + "-" + priceMax;
                        price_area_id_r = "";
                    } else {
                        price_area_l = priceMin + "-" + priceMax;
                        price_area_id_l = "";
                    }

                } else if (rent_type == 1) {
                    if (is_no_limit_price_r) {
                        price_area_r = "";
                        price_area_id_r = "";
                    } else {
                        price_area_r = "";
                        price_area_id_r = "";
                        int size = chooseSPriceList.size();
                        for (int i = 0; i < size; i++) {
                            price_area_id_r += chooseSPriceList.get(i).getId() + ",";
                        }
                        if (price_area_id_r.length() > 0)
                            price_area_id_r = price_area_id_r.substring(0, price_area_id_r.length() - 1);

                    }
                } else {
                    if (is_no_limit_price_l) {
                        price_area_l = "";
                        price_area_id_l = "";
                    } else {
                        price_area_l = "";
                        price_area_id_l = "";
                        int size = chooseLPriceList.size();
                        for (int i = 0; i < size; i++) {
                            price_area_id_l += chooseLPriceList.get(i).getId() + ",";
                        }
                        if (price_area_id_l.length() > 0)
                            price_area_id_l = price_area_id_l.substring(0, price_area_id_l.length() - 1);

                    }
                }
                room_type_id = "";
                int size2 = chooseTypeList.size();
                for (int i = 0; i < size2; i++) {
                    room_type_id += chooseTypeList.get(i).getId() + ",";
                }
                if (room_type_id.length() > 0)
                    room_type_id = room_type_id.substring(0, room_type_id.length() - 1);
                mDrawerlayout.closeDrawer(mLlFiltrate);
                if (aMap != null)
                    aMap.clear();
                room_group_id = "-1";
                curr_level = 0;
                addMarkerInScreen();
                break;
        }
    }

    /**
     * 逆地理编码（坐标转地址）结果回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {

    }

    /**
     * 地理编码（地址转坐标）
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        Log.d(TAG, "get into onGeocodeSearched()");
        if (geocodeResult != null) {
            GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
            LatLng latLng = new LatLng(address.getLatLonPoint().getLatitude(),
                    address.getLatLonPoint().getLongitude());
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,
                    11, 0, 0)));
            isGeocodeSearched = true;
            Log.d(TAG, "onGeocodeSearch() 依据地址返回的经纬度信息：" +
                    latLng.longitude + ", " + latLng.latitude);
        }
    }

    private boolean isHaveMarker(MapSearchBean mapSearchBean) {
        int size = searchBeans.size();
        for (int i = 0; i < size; i++) {
            MapSearchBean bean = searchBeans.get(i);
            if (bean.getId() == mapSearchBean.getId()) {
                return true;
            }
        }
        return false;
    }


    /*********************************************接口******************************************************/
    /**
     * 价格区间
     */
    private void getRoomPriceAreaList() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().roomPriceArea(rent_type + "")
                .compose(RxUtil.<DataInfo<List<RoomPriceAreaBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<RoomPriceAreaBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }


                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<List<RoomPriceAreaBean>> dataInfo) {

                        if (dataInfo.success()) {
                            priceAreaList.clear();
                            RoomPriceAreaBean priceAreaBean = new RoomPriceAreaBean();
                            priceAreaBean.setId(-1);
                            priceAreaBean.setName(getString(R.string.no_limit));
                            priceAreaList.add(priceAreaBean);
                            priceAreaList.addAll(dataInfo.data());
                            priceAreaCommonAdapter.notifyDataSetChanged();
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    /**
     * 房型
     */
    private void getRoomTypeList() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().roomTypeList()
                .compose(RxUtil.<DataInfo<List<RoomTypeListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<RoomTypeListBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }


                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<List<RoomTypeListBean>> dataInfo) {

                        if (dataInfo.success()) {
                            roomTypeList.clear();
                            roomTypeList.addAll(dataInfo.data());
                            roomTypeCommonAdapter.notifyDataSetChanged();
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    /**
     * 地图房源
     */
    private void mapSearch(int level, String point1, String point2, String keyword) {
        String price_area_id = rent_type == 1 ? price_area_id_r : price_area_id_l;
        String price_area = rent_type == 1 ? price_area_r : price_area_l;
        Subscription subscription = (SecondRetrofitHelper.getInstance().mapSearch(
                level + "", point1, point2, rent_type + "",
                price_area_id, room_type_id, price_area, keyword)
                .compose(RxUtil.<DataInfo<List<MapSearchBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<MapSearchBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }


                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<List<MapSearchBean>> dataInfo) {
                        if (dataInfo.success()) {
                            if (level != curr_level) {
                                searchBeans.clear();
                                searchBeans.addAll(dataInfo.data());
                                aMap.clear();
                                curr_level = level;
                                int size = searchBeans.size();
                                for (int i = 0; i < size; i++) {
                                    MapSearchBean mapSearchBean = searchBeans.get(i);
                                    if (level < 15) {
                                        addMarkerCircle(Double.valueOf(mapSearchBean.getLat()), Double.valueOf(mapSearchBean.getLng()), mapSearchBean, i + 1);
                                    } else {
                                        addMarkerEstate(Double.valueOf(mapSearchBean.getLat()), Double.valueOf(mapSearchBean.getLng()), mapSearchBean, i + 1);
                                    }
                                }
                            } else {
                                int size = dataInfo.data().size();
                                for (int i = 0; i < size; i++) {
                                    MapSearchBean mapSearchBean = dataInfo.data().get(i);
                                    boolean is = isHaveMarker(mapSearchBean);
                                    if (!is) {
                                        searchBeans.add(mapSearchBean);
                                        if (level < 15) {
                                            addMarkerCircle(Double.valueOf(mapSearchBean.getLat()), Double.valueOf(mapSearchBean.getLng()), mapSearchBean, i + 1);
                                        } else {
                                            addMarkerEstate(Double.valueOf(mapSearchBean.getLat()), Double.valueOf(mapSearchBean.getLng()), mapSearchBean, i + 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    /**
     * 房屋列表
     */
    private void groupRoomList(boolean isResh) {
        if (isResh) {
            page = 1;
        } else {
            page++;
        }
        String price_area_id = rent_type == 1 ? price_area_id_r : price_area_id_l;
        String price_area = rent_type == 1 ? price_area_r : price_area_l;
        Subscription subscription = (SecondRetrofitHelper.getInstance().groupRoomList(page + "", room_group_id, rent_type + "", price_area_id, room_type_id, price_area, "")
                .compose(RxUtil.<DataInfo<PageDataBean<GroupRoomListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<GroupRoomListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (estateListPopup == null)
                            return;
                        if (isResh) {
                            estateListPopup.finishRefresh();
                        } else {
                            page--;
                            estateListPopup.finishLoadmore();
                        }

                    }
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<PageDataBean<GroupRoomListBean>> dataInfo) {
                        if (estateListPopup == null)
                            return;
                        if (isResh) {
                            estateListPopup.finishRefresh();
                        } else {
                            estateListPopup.finishLoadmore();
                        }
                        if (dataInfo.success()) {
                            if (isResh)
                                roomListBeans.clear();
                            roomListBeans.addAll(dataInfo.data().getList());
                            estateListPopup.setDate(roomListBeans);
                            estateListPopup.setEnableLoadmore(dataInfo.data().getPage() < dataInfo.data().getTotalPage());
                        } else {
                            if (!isResh)
                                page--;
                        }


                    }
                }));
        addSubscrebe(subscription);
    }

    /**********************************************弹窗****************************************************/
    /**
     * 小区房源
     */
    private void showRoomListPop(String name, boolean isResh) {

        if (isResh) {
            estateListPopup.setDate(roomListBeans);
            groupRoomList(isResh);
        }
        estateListPopup.setTitle(name);
        showPopup(estateListPopup);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        popupWindow.showAtLocation(mLlMain, Gravity.BOTTOM, 0, 0);
        Log.d(TAG, "popup window弹出");

        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);

            }
        });
    }

    /**********************************************定时***********************************************/
    private void checkLocationTime() {
        timeSubscription = Observable.interval(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        LatLng latLng = aMap.getCameraPosition().target;
                        int level = (int) aMap.getCameraPosition().zoom;
                        if (curr_lat != latLng.latitude || curr_lng != latLng.longitude || curr_level != level) {
                            curr_lat = latLng.latitude;
                            curr_lng = latLng.longitude;
                            addMarkerInScreen();
                        }
                    }
                });
        addSubscrebe(timeSubscription);
    }

    private void initGeocodeSearchMap() {
        geocodeSearchMap = new HashMap<>();
        geocodeSearchMap.put("北京", new LatLng(39.915019, 116.403613));
        geocodeSearchMap.put("上海", new LatLng(31.256182, 121.46128));
        geocodeSearchMap.put("广州", new LatLng(23.155341, 113.264674));
        geocodeSearchMap.put("深圳", new LatLng(22.538843, 114.123193));
        geocodeSearchMap.put("天津", new LatLng(39.145072, 117.21614));
        geocodeSearchMap.put("杭州", new LatLng(30.249838, 120.190214));
        geocodeSearchMap.put("南京", new LatLng(32.094757, 118.803572));
        geocodeSearchMap.put("成都", new LatLng(30.704521, 104.079069));
        geocodeSearchMap.put("武汉", new LatLng(30.616867, 114.429731));

        geocodeSearchMap.put("阿拉善盟", new LatLng(38.854287, 105.732031));
        geocodeSearchMap.put("鞍山市", new LatLng(41.119835, 122.991741));
        geocodeSearchMap.put("阿勒泰地区", new LatLng(47.863112, 88.136428));
        geocodeSearchMap.put("阿拉尔市", new LatLng(40.5662, 81.277577));
        geocodeSearchMap.put("阿克苏地区", new LatLng(41.153522, 80.312932));
        geocodeSearchMap.put("安阳市", new LatLng(36.106873, 114.399647));
        geocodeSearchMap.put("安顺市", new LatLng(26.238774, 105.949817));
        geocodeSearchMap.put("安康市", new LatLng(32.720408, 109.022381));
        geocodeSearchMap.put("阿里地区", new LatLng(32.506988, 80.112565));
        geocodeSearchMap.put("安庆市", new LatLng(30.554595, 117.071657));
        geocodeSearchMap.put("阿坝藏族羌族自治州", new LatLng(31.901772, 102.23271));

        geocodeSearchMap.put("本溪市", new LatLng(41.4887, 123.684605));
        geocodeSearchMap.put("巴彦淖尔市", new LatLng(40.749686, 107.393681));
        geocodeSearchMap.put("包头市", new LatLng(40.61278, 109.841941));
        geocodeSearchMap.put("滨州市", new LatLng(37.460416, 118.023393));
        geocodeSearchMap.put("北屯市", new LatLng(47.331244, 87.768152));
        geocodeSearchMap.put("博尔塔拉蒙古自治州", new LatLng(44.90954, 82.121778));
        geocodeSearchMap.put("巴音郭楞蒙古自治州", new LatLng(41.753928, 86.203022));
        geocodeSearchMap.put("宝鸡市", new LatLng(34.381548, 107.158335));
        geocodeSearchMap.put("毫州市", new LatLng(33.854959, 115.803438));
        geocodeSearchMap.put("蚌埠市", new LatLng(32.949886, 117.384016));
        geocodeSearchMap.put("毕节市", new LatLng(27.30665, 105.308938));
        geocodeSearchMap.put("北海市", new LatLng(21.461811, 109.13458));
        geocodeSearchMap.put("百色市", new LatLng(23.894179, 106.671156));
        geocodeSearchMap.put("白沙黎族自治县", new LatLng(19.228307, 109.449135));
        geocodeSearchMap.put("保亭黎族苗族自治县", new LatLng(18.648349, 109.710381));
        geocodeSearchMap.put("保定市", new LatLng(38.870326, 115.486408));
        geocodeSearchMap.put("北區", new LatLng(22.2824605, 114.16157090000002));
        geocodeSearchMap.put("巴中市", new LatLng(31.881229, 106.771417));
        geocodeSearchMap.put("白城市", new LatLng(45.626514, 122.869733));
        geocodeSearchMap.put("白山市", new LatLng(41.960445, 126.462973));
        geocodeSearchMap.put("白银市", new LatLng(36.549145, 104.183038));
        geocodeSearchMap.put("保山市", new LatLng(25.132235, 99.175695));
        geocodeSearchMap.put("北京城区", new LatLng(39.924095, 116.402895));

        geocodeSearchMap.put("朝阳市", new LatLng(41.606797, 120.415527));
        geocodeSearchMap.put("赤峰市", new LatLng(42.264037, 118.893079));
        geocodeSearchMap.put("昌吉回族自治州", new LatLng(43.955208, 87.198866));
        geocodeSearchMap.put("潮州市", new LatLng(23.546326, 116.593246));
        geocodeSearchMap.put("滁州市", new LatLng(32.206295, 118.32676));
        geocodeSearchMap.put("池州市", new LatLng(30.627389, 117.530044));
        geocodeSearchMap.put("昌都市", new LatLng(31.133208, 97.188689));
        geocodeSearchMap.put("常州市", new LatLng(31.817637, 119.98019));
        geocodeSearchMap.put("常德市", new LatLng(29.078416, 111.701137));
        geocodeSearchMap.put("郴州市", new LatLng(25.81624, 113.03928));
        geocodeSearchMap.put("崇左市", new LatLng(22.41172, 107.362896));
        geocodeSearchMap.put("昌江黎族自治县", new LatLng(19.283799, 109.062751));
        geocodeSearchMap.put("澄迈县", new LatLng(19.745166, 110.014086));
        geocodeSearchMap.put("承德市", new LatLng(40.972673, 117.96221));
        geocodeSearchMap.put("沧州市", new LatLng(38.316783, 116.884674));
        geocodeSearchMap.put("楚雄彝族自治州", new LatLng(25.089215, 101.552234));

        geocodeSearchMap.put("丹东市", new LatLng(40.006795, 124.360903));
        geocodeSearchMap.put("大连市", new LatLng(38.930405, 121.637726));
        geocodeSearchMap.put("大兴安岭地区", new LatLng(50.414562, 124.147825));
        geocodeSearchMap.put("大庆市", new LatLng(46.594625, 125.10363));
        geocodeSearchMap.put("东营市", new LatLng(37.463248, 118.477289));
        geocodeSearchMap.put("德州市", new LatLng(37.458039, 116.294838));
        geocodeSearchMap.put("东沙群岛", new LatLng(21.030139, 116.280382));
        geocodeSearchMap.put("东莞市", new LatLng(23.0978, 113.864777));
        geocodeSearchMap.put("定安县", new LatLng(19.687086, 110.364635));
        geocodeSearchMap.put("东方市", new LatLng(19.097006, 108.696511));
        geocodeSearchMap.put("儋州市", new LatLng(19.525646, 109.606432));
        geocodeSearchMap.put("大埔區", new LatLng(22.47606026547892, 114.30970148268467));
        geocodeSearchMap.put("东區", new LatLng(22.2733889, 114.23607779999998));
        geocodeSearchMap.put("德阳市", new LatLng(31.174403, 104.395218));
        geocodeSearchMap.put("达州市", new LatLng(31.205165, 107.45677));
        geocodeSearchMap.put("大堂區", new LatLng(22.187466003813267, 113.55116695881462));
        geocodeSearchMap.put("定西市", new LatLng(35.599513, 104.632129));
        geocodeSearchMap.put("大理白族自治州", new LatLng(25.598835, 100.256186));
        geocodeSearchMap.put("迪庆藏族自治州", new LatLng(27.825568, 99.708453));
        geocodeSearchMap.put("德宏傣族景颇族自治州", new LatLng(24.435116, 98.592434));
        geocodeSearchMap.put("大同市", new LatLng(40.127722, 113.309885));

        geocodeSearchMap.put("鄂尔多斯市", new LatLng(39.615206, 109.78895));
        geocodeSearchMap.put("恩施土家族苗族自治州", new LatLng(30.357998, 109.490569));
        geocodeSearchMap.put("鄂州市", new LatLng(30.406912, 114.898593));
        geocodeSearchMap.put("抚顺市", new LatLng(41.889834, 123.951294));
        geocodeSearchMap.put("阜新市", new LatLng(42.035737, 121.665484));
        geocodeSearchMap.put("佛山市", new LatLng(23.054098, 113.110974));
        geocodeSearchMap.put("福州市", new LatLng(26.123256, 119.325317));
        geocodeSearchMap.put("阜阳市", new LatLng(26.123256, 119.325317));
        geocodeSearchMap.put("防城港市", new LatLng(21.720272, 108.38356));
        geocodeSearchMap.put("抚州市", new LatLng(27.927065, 116.359363));
        geocodeSearchMap.put("風順堂區", new LatLng(22.18326022593767, 113.5363746449279));
        geocodeSearchMap.put("贵阳市", new LatLng(26.660589, 106.641359));
        geocodeSearchMap.put("贵港市", new LatLng(23.109737, 109.619314));
        geocodeSearchMap.put("桂林市", new LatLng(25.268301, 110.288354));
        geocodeSearchMap.put("固原市", new LatLng(36.028858, 106.31354));
        geocodeSearchMap.put("赣州市", new LatLng(25.827631, 114.971553));
        geocodeSearchMap.put("果洛藏族自治州", new LatLng(34.471479, 100.256483));
        geocodeSearchMap.put("觀塘區", new LatLng(22.309955604836553, 114.2303490379538));
        geocodeSearchMap.put("广元市", new LatLng(32.458436, 105.828572));
        geocodeSearchMap.put("广安市", new LatLng(30.462868, 106.638835));
        geocodeSearchMap.put("甘孜藏族自治州", new LatLng(30.052777, 101.970126));
        geocodeSearchMap.put("甘南藏族自治州", new LatLng(34.991741, 102.922905));

        geocodeSearchMap.put("葫芦岛市", new LatLng(40.76175, 120.874444));
        geocodeSearchMap.put("呼伦贝尔市", new LatLng(49.173488, 119.785016));
        geocodeSearchMap.put("呼和浩特市", new LatLng(40.839908, 111.672149));
        geocodeSearchMap.put("鹤岗市", new LatLng(47.348438, 130.289485));
        geocodeSearchMap.put("哈尔滨市", new LatLng(45.810029, 126.542473));
        geocodeSearchMap.put("黑河市", new LatLng(50.251457, 127.534337));
        geocodeSearchMap.put("菏泽市", new LatLng(35.237757, 115.513133));
        geocodeSearchMap.put("黄冈市", new LatLng(30.453693, 114.881579));
        geocodeSearchMap.put("黄石市", new LatLng(30.162001, 114.946643));
        geocodeSearchMap.put("和田地区", new LatLng(37.166243, 79.913563));
        geocodeSearchMap.put("哈密市", new LatLng(42.859038, 93.510864));
        geocodeSearchMap.put("胡杨河市", new LatLng(44.706393, 84.835492));
        geocodeSearchMap.put("惠州市", new LatLng(23.159694, 114.422985));
        geocodeSearchMap.put("河源市", new LatLng(23.765561, 114.689045));
        geocodeSearchMap.put("鹤壁市", new LatLng(35.767355, 114.280307));
        geocodeSearchMap.put("汉中市", new LatLng(33.09921, 107.036326));
        geocodeSearchMap.put("淮北市", new LatLng(33.974591, 116.809998));
        geocodeSearchMap.put("黄山市", new LatLng(29.729918, 118.328189));
        geocodeSearchMap.put("合肥市", new LatLng(31.891596, 117.323703));
        geocodeSearchMap.put("淮南市", new LatLng(32.637442, 117.026221));
        geocodeSearchMap.put("淮安市", new LatLng(33.557029, 119.119766));
        geocodeSearchMap.put("衡阳市", new LatLng(26.897255, 112.63709));
        geocodeSearchMap.put("怀化市", new LatLng(27.565682, 109.973444));
        geocodeSearchMap.put("河池市", new LatLng(24.698846, 108.091216));
        geocodeSearchMap.put("贺州市", new LatLng(24.462877, 111.544778));
        geocodeSearchMap.put("海口市", new LatLng(20.035628, 110.166849));
        geocodeSearchMap.put("海东市", new LatLng(36.479197, 102.417231));
        geocodeSearchMap.put("海西蒙古族藏族自治州", new LatLng(37.375867, 97.367891));
        geocodeSearchMap.put("黄南藏族自治州", new LatLng(35.525921, 102.021706));
        geocodeSearchMap.put("海南藏族自治州", new LatLng(36.302458, 100.629638));
        geocodeSearchMap.put("海北藏族自治州", new LatLng(36.960866, 100.907286));
        geocodeSearchMap.put("邯郸市", new LatLng(36.609712, 114.480665));
        geocodeSearchMap.put("衡水市", new LatLng(37.751354, 115.698399));
        geocodeSearchMap.put("湖州市", new LatLng(30.869459, 120.030127));
        geocodeSearchMap.put("杭州市", new LatLng(30.249838, 120.188489));
        geocodeSearchMap.put("黃大仙區", new LatLng(22.3548115, 114.19743979999998));
        geocodeSearchMap.put("花地瑪堂區", new LatLng(22.204722941362547, 113.54842266412193));
        geocodeSearchMap.put("花王堂區", new LatLng(22.194910635104907, 113.54295215978848));
        geocodeSearchMap.put("红河哈尼族彝族自治州", new LatLng(23.370361, 103.380726));

        geocodeSearchMap.put("江门市", new LatLng(22.585271, 113.087761));
        geocodeSearchMap.put("揭阳市", new LatLng(23.615885, 116.342295));
        geocodeSearchMap.put("济宁市", new LatLng(35.400172, 116.607412));
        geocodeSearchMap.put("济南市", new LatLng(36.657813, 117.126435));
        geocodeSearchMap.put("济源市", new LatLng(35.11898, 112.578682));
        geocodeSearchMap.put("焦作市", new LatLng(35.221908, 113.248118));
        geocodeSearchMap.put("锦州市", new LatLng(41.121914, 121.153871));
        geocodeSearchMap.put("鸡西市", new LatLng(45.310814, 130.984387));
        geocodeSearchMap.put("佳木斯市", new LatLng(46.81023, 130.389449));
        geocodeSearchMap.put("九江市", new LatLng(29.711591, 116.014126));
        geocodeSearchMap.put("吉安市", new LatLng(27.111367, 115.027679));
        geocodeSearchMap.put("景德镇市", new LatLng(29.293402, 117.228268));
        geocodeSearchMap.put("嘉兴市", new LatLng(30.770338, 120.769586));
        geocodeSearchMap.put("金华市", new LatLng(29.118724, 119.641943));
        geocodeSearchMap.put("九龍城區", new LatLng(22.3232097, 114.18555049999998));
        geocodeSearchMap.put("金昌市", new LatLng(38.527289, 102.194316));
        geocodeSearchMap.put("酒泉市", new LatLng(39.738357, 98.505496));
        geocodeSearchMap.put("嘉峪关市", new LatLng(39.771528, 98.259549));
        geocodeSearchMap.put("吉林市", new LatLng(43.866656, 126.58208));
        geocodeSearchMap.put("晋城市", new LatLng(35.482412, 112.878699));
        geocodeSearchMap.put("晋中市", new LatLng(37.693812, 112.759162));
        geocodeSearchMap.put("荆门市", new LatLng(31.057449, 112.218606));
        geocodeSearchMap.put("荆州市", new LatLng(30.378487, 112.218498));
        geocodeSearchMap.put("嘉模堂區", new LatLng(22.156726848813165, 113.56898009924788));

        geocodeSearchMap.put("开封市", new LatLng(34.804308, 114.3202));
        geocodeSearchMap.put("昆玉市", new LatLng(37.218143, 79.29855));
        geocodeSearchMap.put("可克达拉市", new LatLng(43.958606, 81.018007));
        geocodeSearchMap.put("喀什地区", new LatLng(39.499487, 76.052737));
        geocodeSearchMap.put("克孜勒苏柯尔克孜自治州", new LatLng(39.720735, 76.173864));
        geocodeSearchMap.put("克拉玛依市", new LatLng(45.608401, 84.881381));
        geocodeSearchMap.put("葵青區", new LatLng(22.3549077, 114.12609910000003));
        geocodeSearchMap.put("昆明市", new LatLng(24.88837, 102.839874));

        geocodeSearchMap.put("聊城市", new LatLng(36.464384, 115.949619));
        geocodeSearchMap.put("临沂市", new LatLng(35.043793, 118.350889));
        geocodeSearchMap.put("洛阳市", new LatLng(34.627353, 112.460143));
        geocodeSearchMap.put("漯河市", new LatLng(33.577845, 114.04929));
        geocodeSearchMap.put("辽阳市", new LatLng(41.290425, 123.174376));
        geocodeSearchMap.put("连云港市", new LatLng(34.604507, 119.230276));
        geocodeSearchMap.put("龙岩市", new LatLng(25.105827, 117.011372));
        geocodeSearchMap.put("六盘水市", new LatLng(26.598576, 104.8594));
        geocodeSearchMap.put("拉萨市", new LatLng(29.659989, 91.177596));
        geocodeSearchMap.put("林芝市", new LatLng(29.67463, 94.362736));
        geocodeSearchMap.put("娄底市", new LatLng(27.746441, 112.011852));
        geocodeSearchMap.put("柳州市", new LatLng(24.314839, 109.393606));
        geocodeSearchMap.put("来宾市", new LatLng(23.736699, 109.247721));
        geocodeSearchMap.put("临高县", new LatLng(19.902821, 109.703048));
        geocodeSearchMap.put("乐东黎族自治县", new LatLng(18.75546, 109.179358));
        geocodeSearchMap.put("陵水黎族自治县", new LatLng(18.544417, 110.04044));
        geocodeSearchMap.put("丽水市", new LatLng(28.45016, 119.961191));
        geocodeSearchMap.put("廊坊市", new LatLng(39.516208, 116.715586));
        geocodeSearchMap.put("離島區", new LatLng(22.218662097481857, 114.02429529475069));
        geocodeSearchMap.put("兰州市", new LatLng(36.066885, 103.832619));
        geocodeSearchMap.put("陇南市", new LatLng(33.380857, 104.965858));
        geocodeSearchMap.put("临夏回族自治州", new LatLng(35.607679, 103.215528));
        geocodeSearchMap.put("辽源市", new LatLng(42.913286, 125.13605));
        geocodeSearchMap.put("泸州市", new LatLng(28.952284, 105.461749));
        geocodeSearchMap.put("乐山市", new LatLng(29.609451, 103.719073));
        geocodeSearchMap.put("凉山彝族自治州", new LatLng(27.899629, 102.272855));
        geocodeSearchMap.put("六安市", new LatLng(31.724008, 116.50744));
        geocodeSearchMap.put("丽江市", new LatLng(26.819138, 100.257183));
        geocodeSearchMap.put("临沧市", new LatLng(23.8906, 100.095371));
        geocodeSearchMap.put("临汾市", new LatLng(36.095376, 111.544212));
        geocodeSearchMap.put("吕梁市", new LatLng(37.576912, 111.1424));
        geocodeSearchMap.put("路氹填海區", new LatLng(22.14677, 113.569232));

        geocodeSearchMap.put("茂名市", new LatLng(21.668862, 110.932044));
        geocodeSearchMap.put("梅州市", new LatLng(24.26519, 116.137877));
        geocodeSearchMap.put("牡丹江市", new LatLng(44.599189, 129.618272));
        geocodeSearchMap.put("绵阳市", new LatLng(31.466764, 104.723792));
        geocodeSearchMap.put("眉山市", new LatLng(30.083527, 103.854842));
        geocodeSearchMap.put("马鞍山市", new LatLng(31.713625, 118.509269));

        geocodeSearchMap.put("南阳市", new LatLng(33.004797, 112.518981));
        geocodeSearchMap.put("南京市", new LatLng(32.094757, 118.803572));
        geocodeSearchMap.put("南通市", new LatLng(32.076912, 120.867096));
        geocodeSearchMap.put("宁德市", new LatLng(26.676632, 119.589005));
        geocodeSearchMap.put("南平市", new LatLng(27.482725, 118.098851));
        geocodeSearchMap.put("那曲市", new LatLng(31.451145, 91.997552));
        geocodeSearchMap.put("南宁市", new LatLng(22.821974, 108.334503));
        geocodeSearchMap.put("南昌市", new LatLng(28.670949, 115.922652));
        geocodeSearchMap.put("宁波市", new LatLng(29.870328, 121.542882));
        geocodeSearchMap.put("南區", new LatLng(22.2432164, 114.19743979999998));
        geocodeSearchMap.put("南充市", new LatLng(30.815254, 106.089333));
        geocodeSearchMap.put("内江市", new LatLng(29.574139, 105.063693));
        geocodeSearchMap.put("怒江傈僳族自治州", new LatLng(25.825936, 98.863704));

        geocodeSearchMap.put("濮阳市", new LatLng(25.825936, 98.863704));
        geocodeSearchMap.put("平顶山市", new LatLng(33.727144, 113.311063));
        geocodeSearchMap.put("盘锦市", new LatLng(40.725899, 122.177122));
        geocodeSearchMap.put("莆田市", new LatLng(25.358299, 119.069138));
        geocodeSearchMap.put("萍乡市", new LatLng(27.653738, 113.847557));
        geocodeSearchMap.put("平凉市", new LatLng(35.558864, 106.713413));
        geocodeSearchMap.put("攀枝花市", new LatLng(26.551585, 101.858924));
        geocodeSearchMap.put("普洱市", new LatLng(22.831178, 100.972801));

        geocodeSearchMap.put("清远市", new LatLng(23.703318, 113.139504));
        geocodeSearchMap.put("七台河市", new LatLng(45.783364, 131.012029));
        geocodeSearchMap.put("齐齐哈尔市", new LatLng(47.346499, 124.002183));
        geocodeSearchMap.put("青岛市", new LatLng(36.070827, 120.319031));
        geocodeSearchMap.put("潜江市", new LatLng(30.399137, 112.945721));
        geocodeSearchMap.put("泉州市", new LatLng(24.980873, 118.573785));
        geocodeSearchMap.put("钦州市", new LatLng(21.990883, 108.624935));
        geocodeSearchMap.put("衢州市", new LatLng(28.931805, 118.887869));
        geocodeSearchMap.put("秦皇岛市", new LatLng(39.973289, 119.597835));
        geocodeSearchMap.put("庆阳市", new LatLng(39.973289, 119.597835));
        geocodeSearchMap.put("荃灣區", new LatLng(22.3713227, 114.11416009999994));
        geocodeSearchMap.put("琼中黎族苗族自治县", new LatLng(19.039232,109.844978));
        geocodeSearchMap.put("琼海市", new LatLng(19.249784, 110.497792));
        geocodeSearchMap.put("曲靖市", new LatLng(25.532145, 103.798987));
        geocodeSearchMap.put("黔西南布依族苗族自治州", new LatLng(25.094066, 104.912849));
        geocodeSearchMap.put("黔南布依族苗族自治州", new LatLng(26.268683, 107.520746));
        geocodeSearchMap.put("黔东南苗族侗族自治州", new LatLng(26.590027, 107.989267));

        geocodeSearchMap.put("日照市", new LatLng(35.41272, 119.538592));
        geocodeSearchMap.put("日喀则市", new LatLng(29.229019, 88.92302));

        geocodeSearchMap.put("汕头市", new LatLng(23.380057, 116.762118));
        geocodeSearchMap.put("汕尾市", new LatLng(22.81978, 115.434441));
        geocodeSearchMap.put("韶关市", new LatLng(22.81978, 115.434441));
        geocodeSearchMap.put("三门峡市", new LatLng(34.767177, 111.240163));
        geocodeSearchMap.put("商丘市", new LatLng(34.453785, 115.663024));
        geocodeSearchMap.put("绥化市", new LatLng(46.651916, 127.022501));
        geocodeSearchMap.put("双鸭山市", new LatLng(46.651601, 131.158156));
        geocodeSearchMap.put("双河市", new LatLng(44.85094, 82.37734));
        geocodeSearchMap.put("石河子市", new LatLng(44.276669, 86.067343));
        geocodeSearchMap.put("沈阳市", new LatLng(41.683722, 123.471167));
        geocodeSearchMap.put("十堰市", new LatLng(32.612925, 110.787856));
        geocodeSearchMap.put("神农架林区", new LatLng(31.751234, 110.682378));
        geocodeSearchMap.put("随州市", new LatLng(31.696762, 113.388586));
        geocodeSearchMap.put("宿迁市", new LatLng(33.982734, 118.27552));
        geocodeSearchMap.put("苏州市", new LatLng(31.308193, 120.627697));
        geocodeSearchMap.put("商洛市", new LatLng(33.880072, 109.941662));
        geocodeSearchMap.put("山南市", new LatLng(29.248069, 91.777529));
        geocodeSearchMap.put("宿州市", new LatLng(33.648009, 116.999289));
        geocodeSearchMap.put("三明市", new LatLng(26.235519, 117.595791));
        geocodeSearchMap.put("厦门市", new LatLng(24.474095, 118.122306));
        geocodeSearchMap.put("邵阳市", new LatLng(27.221373, 111.465233));
        geocodeSearchMap.put("石嘴山市", new LatLng(38.924816, 106.427392));
        geocodeSearchMap.put("上饶市", new LatLng(28.497964, 118.013854));
        geocodeSearchMap.put("绍兴市", new LatLng(30.021862, 120.584902));
        geocodeSearchMap.put("石家庄市", new LatLng(38.017616, 114.490771));
        geocodeSearchMap.put("聖方濟各堂區", new LatLng(22.12314422680785, 113.56897790004258));
        geocodeSearchMap.put("深水埗區", new LatLng(22.3320934, 114.14690799999994));
        geocodeSearchMap.put("沙田區", new LatLng(22.386408, 114.20932870000001));
        geocodeSearchMap.put("三沙市", new LatLng(16.838538, 112.346231));
        geocodeSearchMap.put("三亚市", new LatLng(18.305697, 109.499013));
        geocodeSearchMap.put("遂宁市", new LatLng(30.548801, 105.548256));
        geocodeSearchMap.put("松原市", new LatLng(45.116678, 124.822856));
        geocodeSearchMap.put("四平市", new LatLng(43.17473, 124.380917));
        geocodeSearchMap.put("朔州市", new LatLng(39.358761, 112.437649));
        geocodeSearchMap.put("上海城区", new LatLng(31.236176, 121.478815));

        geocodeSearchMap.put("铁门关市", new LatLng(41.862064, 85.706657));
        geocodeSearchMap.put("塔城地区", new LatLng(46.706471, 82.992407));
        geocodeSearchMap.put("图木舒克市", new LatLng(39.875474, 79.080646));
        geocodeSearchMap.put("吐鲁番市", new LatLng(42.95701, 89.196185));
        geocodeSearchMap.put("通辽市", new LatLng(43.611837, 122.276969));
        geocodeSearchMap.put("泰安市", new LatLng(36.179992, 117.0393));
        geocodeSearchMap.put("铁岭市", new LatLng(42.300626, 123.841026));
        geocodeSearchMap.put("天门市", new LatLng(30.655518, 113.181464));
        geocodeSearchMap.put("泰州市", new LatLng(32.462625, 119.929571));
        geocodeSearchMap.put("铜川市", new LatLng(34.893875, 108.951972));
        geocodeSearchMap.put("铜陵市", new LatLng(30.92571, 117.866774));
        geocodeSearchMap.put("台州市", new LatLng(28.692869, 121.296351));
        geocodeSearchMap.put("唐山市", new LatLng(39.632362, 118.124947));
        geocodeSearchMap.put("天水市", new LatLng(34.587471, 105.738967));
        geocodeSearchMap.put("屯門區", new LatLng(22.37281274392078, 113.95522279557129));
        geocodeSearchMap.put("屯昌县", new LatLng(19.357511, 110.109655));
        geocodeSearchMap.put("通化市", new LatLng(41.726279, 125.954803));
        geocodeSearchMap.put("天津城区", new LatLng(39.12537, 117.21729));
        geocodeSearchMap.put("太原市", new LatLng(37.866735, 112.593765));
        geocodeSearchMap.put("铜仁市", new LatLng(27.747633, 109.210379));

        geocodeSearchMap.put("五家渠市", new LatLng(27.747633, 109.210379));
        geocodeSearchMap.put("乌鲁木齐市", new LatLng(43.848865, 87.535901));
        geocodeSearchMap.put("乌海市", new LatLng(39.67678, 106.812181));
        geocodeSearchMap.put("乌兰察布市", new LatLng(40.970688, 113.165627));
        geocodeSearchMap.put("威海市", new LatLng(37.435556, 122.151117));
        geocodeSearchMap.put("潍坊市", new LatLng(36.704549, 119.104558));
        geocodeSearchMap.put("武汉市", new LatLng(30.615127, 114.431168));
        geocodeSearchMap.put("无锡市", new LatLng(31.594577, 120.311683));
        geocodeSearchMap.put("渭南市", new LatLng(34.491673, 109.500495));
        geocodeSearchMap.put("芜湖市", new LatLng(31.357303, 118.398041));
        geocodeSearchMap.put("梧州市", new LatLng(31.357303, 118.398041));
        geocodeSearchMap.put("吴忠市", new LatLng(37.982556, 106.254235));
        geocodeSearchMap.put("温州市", new LatLng(27.987307, 120.691244));
        geocodeSearchMap.put("望德堂區", new LatLng(22.199689, 113.556874));
        geocodeSearchMap.put("武威市", new LatLng(37.911834, 102.631333));
        geocodeSearchMap.put("灣仔區", new LatLng(22.2762468, 114.1825781));
        geocodeSearchMap.put("五指山市", new LatLng(18.780789, 109.523181));
        geocodeSearchMap.put("万宁市", new LatLng(18.815438, 110.387073));
        geocodeSearchMap.put("文昌市", new LatLng(19.55097, 110.804653));
        geocodeSearchMap.put("文山壮族苗族自治州", new LatLng(23.385033, 104.249019));

        geocodeSearchMap.put("许昌市", new LatLng(34.025876, 113.826277));
        geocodeSearchMap.put("信阳市", new LatLng(32.135404, 114.088573));
        geocodeSearchMap.put("新乡市", new LatLng(35.31506, 113.866332));
        geocodeSearchMap.put("兴安盟", new LatLng(46.084462, 122.071813));
        geocodeSearchMap.put("锡林郭勒盟", new LatLng(43.933712, 116.047058));
        geocodeSearchMap.put("襄阳市", new LatLng(32.064505, 112.162166));
        geocodeSearchMap.put("孝感市", new LatLng(30.961444, 113.936445));
        geocodeSearchMap.put("仙桃市", new LatLng(30.330845, 113.447452));
        geocodeSearchMap.put("咸宁市", new LatLng(29.887395, 114.294886));
        geocodeSearchMap.put("徐州市", new LatLng(34.273066, 117.214685));
        geocodeSearchMap.put("西安市", new LatLng(34.285014, 108.970037));
        geocodeSearchMap.put("咸阳市", new LatLng(34.351214, 108.74158));
        geocodeSearchMap.put("宣城市", new LatLng(30.954529, 118.78106));
        geocodeSearchMap.put("湘西土家族苗族自治州", new LatLng(28.30999, 109.742274));
        geocodeSearchMap.put("湘潭市", new LatLng(27.88348, 112.919419));
        geocodeSearchMap.put("西宁市", new LatLng(36.626861, 101.820091));
        geocodeSearchMap.put("新余市", new LatLng(27.816934, 114.946536));
        geocodeSearchMap.put("邢台市", new LatLng(37.077837, 114.499098));
        geocodeSearchMap.put("西貢區", new LatLng(22.3833893, 114.27097600000002));
        geocodeSearchMap.put("忻州市", new LatLng(38.426905, 112.753556));
        geocodeSearchMap.put("西双版纳傣族自治州", new LatLng(21.978212, 100.796975));

        geocodeSearchMap.put("营口市", new LatLng(40.63121, 122.225477));
        geocodeSearchMap.put("伊春市", new LatLng(47.73429, 128.847544));
        geocodeSearchMap.put("阳江市", new LatLng(21.818981, 111.997686));
        geocodeSearchMap.put("云浮市", new LatLng(22.921177, 112.051009));
        geocodeSearchMap.put("扬州市", new LatLng(32.398481, 119.356757));
        geocodeSearchMap.put("盐城市", new LatLng(33.354076, 120.16923));
        geocodeSearchMap.put("伊犁哈萨克自治州", new LatLng(43.916798504175475, 81.3240045682034));
        geocodeSearchMap.put("烟台市", new LatLng(37.471529, 121.454273));
        geocodeSearchMap.put("宜昌市", new LatLng(30.692198217397618, 111.28681802417128));
        geocodeSearchMap.put("榆林市", new LatLng(38.285198842328256, 109.73500210224944));
        geocodeSearchMap.put("延安市", new LatLng(36.564448, 109.48853));
        geocodeSearchMap.put("玉树藏族自治州", new LatLng(33.018246, 97.098264));
        geocodeSearchMap.put("玉林市", new LatLng(22.605124, 110.162007));
        geocodeSearchMap.put("岳阳市", new LatLng(29.38458, 113.126289));
        geocodeSearchMap.put("益阳市", new LatLng(28.545244, 112.347719));
        geocodeSearchMap.put("永州市", new LatLng(26.426123, 111.620101));
        geocodeSearchMap.put("鹰潭市", new LatLng(28.243132, 117.033604));
        geocodeSearchMap.put("宜春市", new LatLng(27.795294, 114.442245));
        geocodeSearchMap.put("银川市", new LatLng(38.499466, 106.18043));
        geocodeSearchMap.put("宜宾市", new LatLng(28.759274, 104.606572));
        geocodeSearchMap.put("雅安市", new LatLng(30.036554, 103.061612));
        geocodeSearchMap.put("油尖旺區", new LatLng(22.300089588150634, 114.17065988121989));
        geocodeSearchMap.put("元朗區", new LatLng(22.44787500066878, 114.02689399665846));
        geocodeSearchMap.put("延边朝鲜族自治州", new LatLng(42.893441, 129.503225));
        geocodeSearchMap.put("玉溪市", new LatLng(24.346912, 102.519637));
        geocodeSearchMap.put("阳泉市", new LatLng(37.863217, 113.59897));
        geocodeSearchMap.put("运城市", new LatLng(35.032706, 110.996569));

        geocodeSearchMap.put("肇庆市", new LatLng(23.05402, 112.471632));
        geocodeSearchMap.put("湛江市", new LatLng(21.272413, 110.372309));
        geocodeSearchMap.put("珠海市", new LatLng(22.22224, 113.554961));
        geocodeSearchMap.put("中山市", new LatLng(22.550888, 113.439089));
        geocodeSearchMap.put("驻马店市", new LatLng(32.989017, 114.050889));
        geocodeSearchMap.put("周口市", new LatLng(33.587043, 114.672336));
        geocodeSearchMap.put("郑州市", new LatLng(34.755101, 113.665336));
        geocodeSearchMap.put("镇江市", new LatLng(32.18860091630976, 119.4240006213168));
        geocodeSearchMap.put("重庆城区", new LatLng(29.562700254148464, 106.55200292189768));
        geocodeSearchMap.put("重庆郊县", new LatLng(30.977700386622725, 109.66528886977954));
        geocodeSearchMap.put("漳州市", new LatLng(24.513201171482315, 117.64700230882687));
        geocodeSearchMap.put("长沙市", new LatLng(28.22870017937842, 112.93899686468923));
        geocodeSearchMap.put("张家界市", new LatLng(29.117000977747562, 110.47899946059418));
        geocodeSearchMap.put("株洲市", new LatLng(27.827500780925853, 113.1340030137263));
        geocodeSearchMap.put("中卫市", new LatLng(37.499897627930785, 105.19699721766287));
        geocodeSearchMap.put("舟山市", new LatLng(29.991349, 122.213934));
        geocodeSearchMap.put("淄博市", new LatLng(36.797125, 118.06318));
        geocodeSearchMap.put("枣庄市", new LatLng(34.81009923866448, 117.3239973504831));
        geocodeSearchMap.put("中西區", new LatLng(22.28505172329579, 114.15735851809663));
        geocodeSearchMap.put("张掖市", new LatLng(38.92579849111838, 100.4499979783989));
        geocodeSearchMap.put("自贡市", new LatLng(29.338651173629728, 104.77885482924104));
        geocodeSearchMap.put("资阳市", new LatLng(30.128997110824482, 104.62731461726322));
        geocodeSearchMap.put("长春市", new LatLng(43.81669768367892, 125.32366018074694));
        geocodeSearchMap.put("昭通市", new LatLng(27.33819930644172, 103.7169982016981));
        geocodeSearchMap.put("长治市", new LatLng(36.195082021397276, 113.11720489002118));
        geocodeSearchMap.put("张家口市", new LatLng(40.76880173254034, 114.88582978576773));
    }
}

