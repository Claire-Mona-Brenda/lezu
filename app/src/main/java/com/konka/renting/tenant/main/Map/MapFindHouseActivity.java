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

import com.amap.api.location.AMapLocationClientOption;
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
        mMapview.onCreate(savedInstanceState);
        if (aMap == null)
            aMap = mMapview.getMap();
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
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double lng = location.getLongitude();
                double lat = location.getLatitude();
                Log.d(TAG, lng + ", " + lat);
            }
        });
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
                    geocoderSearch.getFromLocationNameAsyn(query);
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
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    /**
     * 地理编码（地址转坐标）
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (geocodeResult != null) {
            GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
            LatLng latLng = new LatLng(address.getLatLonPoint().getLatitude(),
                    address.getLatLonPoint().getLongitude());
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(latLng, 11, 0, 0)));
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
}
