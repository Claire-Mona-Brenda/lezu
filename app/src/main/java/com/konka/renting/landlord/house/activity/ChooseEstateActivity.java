package com.konka.renting.landlord.house.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseOrderInfoBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RoomGroupListBean;
import com.konka.renting.event.ChooseCityEvent;
import com.konka.renting.event.ChooseEstateEvent;
import com.konka.renting.event.ChooseEstateFinishEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.search.SearchActivity;
import com.konka.renting.landlord.house.ChooseLocationEvent;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
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
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ChooseEstateActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener, DistrictSearch.OnDistrictSearchListener {
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
    @BindView(R.id.sv_search)
    SearchView svSearch;
    @BindView(R.id.activity_choose_estate_rv_location)
    RecyclerView mRvLocation;
    @BindView(R.id.activity_choose_estate_srv_location)
    SmartRefreshLayout mSrvLocation;
    @BindView(R.id.activity_choose_estate_add_estate)
    LinearLayout mLlAddEstate;

    public final String COUNTRY = "country"; // 行政区划，国家级
    public final String PROVINCE = "province"; // 行政区划，省级
    public final String CITY = "city"; // 行政区划，市级

    public final String CITY_NAME = "cityName";//SharedPreferences 键名

    PinYinUtils pinYinUtils;
    //当前选中的级别
    private String selectedLevel = COUNTRY;

    private List<HotCity> mCities = new ArrayList<>();
    private List<City> mAllCities = new ArrayList<>();
    City city;
    LocatedCity locationCity;
    SharedPreferences sharedPreferences;
    GeocodeSearch geocoderSearch;

    int page = 1;
    String search;
    List<RoomGroupListBean> roomGroupListBeans;
    CommonAdapter<RoomGroupListBean> commonAdapter;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public static void toActivity(Context context, String cityName) {
        Intent intent = new Intent(context, ChooseEstateActivity.class);
        intent.putExtra("cityName", cityName);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_estate;
    }

    @Override
    public void init() {
        String cityName=getIntent().getStringExtra("cityName");

        pinYinUtils = new PinYinUtils();
        pinYinUtils.getCharPinYin('a');
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        sharedPreferences = getSharedPreferences(CITY, MODE_PRIVATE);
         String cityName2 = sharedPreferences.getString(CITY_NAME, "");
        if (!TextUtils.isEmpty(cityName)) {
            city = new City(cityName, cityName, pinYinUtils.getStringPinYin(cityName), "");
            tvLocation.setText(cityName);
        }else if (!TextUtils.isEmpty(cityName2)){
            city = new City(cityName2, cityName2, pinYinUtils.getStringPinYin(cityName2), "");
            tvLocation.setText(cityName2);
        }

        roomGroupListBeans = new ArrayList<>();
        commonAdapter = new CommonAdapter<RoomGroupListBean>(this, roomGroupListBeans, R.layout.adapter_choose_location) {
            @Override
            public void convert(ViewHolder viewHolder, RoomGroupListBean bean) {
                viewHolder.setText(R.id.adapter_location_tv_name, bean.getName());
                viewHolder.setText(R.id.adapter_location_tv_address, bean.getAddress());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.getDefault().post(new ChooseEstateEvent(bean));
                        finish();
                    }
                });
            }
        };
        mRvLocation.setLayoutManager(new LinearLayoutManager(this));
        mRvLocation.setAdapter(commonAdapter);

        //搜索事件：
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (TextUtils.isEmpty(s)) {
                    showToast(R.string.please_input_search_string);
                } else if (city == null) {
                    showToast(R.string.please_choose_city);
                } else {
                    page = 1;
                    search = s;
                    getRoomList(s);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s) && city != null) {
                    page = 1;
                    search = s;
                    getRoomList(s);
                }
                return false;
            }
        });

        mSrvLocation.setEnableRefresh(false);
        mSrvLocation.setEnableLoadmore(false);
        mSrvLocation.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getRoomList(search);
            }
        });

        addRxBusSubscribe(ChooseEstateFinishEvent.class, new Action1<ChooseEstateFinishEvent>() {
            @Override
            public void call(ChooseEstateFinishEvent chooseEstateFinishEvent) {
                finish();
            }
        });
        addRxBusSubscribe(ChooseCityEvent.class, new Action1<ChooseCityEvent>() {
            @Override
            public void call(ChooseCityEvent chooseCityEvent) {
                if (!TextUtils.isEmpty(chooseCityEvent.getCityName())) {
                    city = new City(chooseCityEvent.getCityName(), chooseCityEvent.getCityName(), pinYinUtils.getStringPinYin(chooseCityEvent.getCityName()), "");
                    tvLocation.setText(chooseCityEvent.getCityName());
                }
            }
        });

        initCity();
        setSearchAttribute();
        initLocation();
    }


    /**
     * 定位
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                locationCity = new LocatedCity(aMapLocation.getCity(), aMapLocation.getCity(), aMapLocation.getCityCode());
                if (city == null) {
                    city = new City(aMapLocation.getCity(), aMapLocation.getCity(), pinYinUtils.getStringPinYin(aMapLocation.getCity()), aMapLocation.getCityCode());
                    tvLocation.setText(city.getName());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CITY_NAME, city.getName());
                    editor.commit();
                }
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 设置Searchview属性：
     */
    private void setSearchAttribute() {
        //默认刚进去就打开搜索栏：
        svSearch.setIconified(false);
        svSearch.onActionViewExpanded();// 当展开无输入内容的时候，没有关闭的图标
        //敲掉消除按键，下划线：
        svSearch.findViewById(R.id.search_plate).setBackground(null);
        svSearch.findViewById(R.id.search_close_btn).setBackground(null);
        //设置字体颜色、大小、背景：
        SearchView.SearchAutoComplete textView = svSearch.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.text_black));
        textView.setHintTextColor(getResources().getColor(R.color.text_gray));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        //设置搜索图标在编辑框外，ture时在框内
        svSearch.setIconifiedByDefault(false);
    }


    @OnClick({R.id.iv_back, R.id.tv_location, R.id.activity_choose_estate_add_estate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.tv_location://选择城市
                selectionCity();
                break;
            case R.id.activity_choose_estate_add_estate://添加小区
                if (TextUtils.isEmpty(city.getName())) {
                    showToast(R.string.please_choose_city);
                } else {
                    ChooseLocationActivity.toActivity(this, city.getName());
                }
                break;
        }
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
                .setLocatedCity(locationCity)  //APP自身已定位的城市，默认为null（定位失败）
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
     * 逆地理编码（坐标转地址）结果回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        String c = regeocodeResult.getRegeocodeAddress().getCity();
        if (!TextUtils.isEmpty(c)) {
            tvLocation.setText(c);
            city = new City(c, regeocodeResult.getRegeocodeAddress().getProvince(), pinYinUtils.getStringPinYin(c), regeocodeResult.getRegeocodeAddress().getCityCode());
        }
    }

    /**
     * 地理编码（地址转坐标）
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /*********************************************接口******************************************************/
    private void getRoomList(String seartch) {
        Subscription subscription = (SecondRetrofitHelper.getInstance().roomGroupList(page + "", city.getName(), seartch)
                .compose(RxUtil.<DataInfo<PageDataBean<RoomGroupListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RoomGroupListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (page > 1) {
                            page--;
                        }
                        if (mSrvLocation.isRefreshing())
                            mSrvLocation.finishRefresh();
                        if (mSrvLocation.isLoading())
                            mSrvLocation.finishLoadmore();
                    }


                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<PageDataBean<RoomGroupListBean>> dataInfo) {
                        if (mSrvLocation.isRefreshing())
                            mSrvLocation.finishRefresh();
                        if (mSrvLocation.isLoading())
                            mSrvLocation.finishLoadmore();
                        if (dataInfo.success()) {
                            if (page == 1) {
                                roomGroupListBeans.clear();
                            } else if (dataInfo.data().getTotalPage() < dataInfo.data().getPage()) {
                                page--;
                            }
                            mSrvLocation.setEnableLoadmore(dataInfo.data().getTotalPage() > dataInfo.data().getPage());
                            roomGroupListBeans.addAll(dataInfo.data().getList());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            if (page > 1) {
                                page--;
                            }
                            ShowToastUtil.showNormalToast(mActivity, dataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }
}
