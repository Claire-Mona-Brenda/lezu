package com.konka.renting.tenant.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterSearchListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.more.MoreHotRoomListActivity;
import com.konka.renting.landlord.home.search.SearchActivity;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.konka.renting.tenant.main.Map.MapFindHouseActivity;
import com.konka.renting.utils.RxUtil;
import com.lljjcoder.utils.PinYinUtils;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.squareup.picasso.Picasso;
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
import butterknife.Unbinder;
import rx.Subscription;

import static android.content.Context.MODE_PRIVATE;

public class TenantMainFragment extends BaseFragment implements GeocodeSearch.OnGeocodeSearchListener, DistrictSearch.OnDistrictSearchListener {

    Unbinder unbinder;
    @BindView(R.id.fragment_tenant_main_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragment_tenant_main_tv_city)
    TextView mTvCity;
    @BindView(R.id.fragment_tenant_main_tv_choose_city)
    TextView mTvChooseCity;
    @BindView(R.id.fragment_tenant_main_img_choose_city)
    ImageView mImgChooseCity;
    @BindView(R.id.fragment_tenant_main_edt_address)
    EditText mEdtAddress;
    @BindView(R.id.fragment_tenant_main_tv_search)
    TextView mTvSearch;
    @BindView(R.id.fragment_tenant_main_tv_long_rent)
    TextView mTvLongRent;
    @BindView(R.id.fragment_tenant_main_tv_short_rent)
    TextView mTvShortRent;
    @BindView(R.id.fragment_tenant_main_tv_find_house)
    TextView mTvFindHouse;
    @BindView(R.id.fragment_tenant_main_tv_house_more)
    TextView mTvHouseMore;
    @BindView(R.id.fragment_tenant_main_ll_house)
    LinearLayout mLlHouse;
    @BindView(R.id.fragment_tenant_main_rv_house)
    RecyclerView mRvHouse;
    @BindView(R.id.fragment_tenant_main_tv_short_more)
    TextView mTvShortMore;
    @BindView(R.id.fragment_tenant_main_ll_short)
    LinearLayout mLlShort;
    @BindView(R.id.fragment_tenant_main_rv_short)
    RecyclerView mRvShort;
    @BindView(R.id.fragment_tenant_main_ll_recommend)
    LinearLayout mLlRecommend;
    @BindView(R.id.fragment_tenant_main_rv_recommend)
    RecyclerView mRvRecommend;
    @BindView(R.id.fragment_tenant_main_refresh_recommend)
    SmartRefreshLayout mRefreshRecommend;
    @BindView(R.id.fragment_tenant_main_appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.fragment_tenant_main_collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.fragment_tenant_main_tv_type_long)
    TextView mTvTypeLong;
    @BindView(R.id.fragment_tenant_main_view_type_long)
    View mTypeLong;
    @BindView(R.id.fragment_tenant_main_ll_type_long)
    LinearLayout mLlTypeLong;
    @BindView(R.id.fragment_tenant_main_tv_type_short)
    TextView mTvTypeShort;
    @BindView(R.id.fragment_tenant_main_view_type_short)
    View mTypeShort;
    @BindView(R.id.fragment_tenant_main_ll_type_short)
    LinearLayout mLlTypeShort;
    @BindView(R.id.fragment_tenant_main_coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fragment_tenant_main_nestedScrollView)
    NestedScrollView mNestedScrollView;

    public final String COUNTRY = "country"; // 行政区划，国家级

    public final String PROVINCE = "province"; // 行政区划，省级

    public final String CITY = "city"; // 行政区划，市级
    public final String CITY_NAME = "cityName";//SharedPreferences 键名

    LocatedCity locationCity;
    SharedPreferences sharedPreferences;

    PinYinUtils pinYinUtils;


    //当前选中的级别
    private String selectedLevel = COUNTRY;

    int page_long = 1;
    int page_short = 1;
    int rent_type = 2;

    boolean is_long_enable_loading = false;
    boolean is_short_enable_loading = false;

    private List<HotCity> mCities = new ArrayList<>();
    private List<City> mAllCities = new ArrayList<>();
    City city;
    GeocodeSearch geocoderSearch;


    List<RenterSearchListBean> houseList;
    List<RenterSearchListBean> shortList;
    List<RenterSearchListBean> recommendList;
    List<RenterSearchListBean> reLongList;
    List<RenterSearchListBean> reShortList;

    CommonAdapter<RenterSearchListBean> houseAdapter;
    CommonAdapter<RenterSearchListBean> shortAdapter;
    CommonAdapter<RenterSearchListBean> recommendAdapter;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    public static TenantMainFragment newInstance() {
        TenantMainFragment fragment = new TenantMainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenant_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void init() {
        super.init();

        rent_type = 2;
        mTvTypeLong.setSelected(true);
        mTvTypeShort.setSelected(false);
        mTvTypeLong.getPaint().setFakeBoldText(true);
        mTvTypeShort.getPaint().setFakeBoldText(false);
        mTypeLong.setVisibility(View.VISIBLE);
        mTypeShort.setVisibility(View.INVISIBLE);


        initAdapter();
        initListent();
        initCity();
        initLocation();


    }

    private void initCity() {
        pinYinUtils = new PinYinUtils();
        pinYinUtils.getCharPinYin('a');
        geocoderSearch = new GeocodeSearch(mActivity);
        geocoderSearch.setOnGeocodeSearchListener(this);

        sharedPreferences = mActivity.getSharedPreferences(CITY, MODE_PRIVATE);
        String cityName = sharedPreferences.getString(CITY_NAME, "");
        if (!TextUtils.isEmpty(cityName)) {
            city = new City(cityName, cityName, pinYinUtils.getStringPinYin(cityName), "");
            mTvCity.setText(getString(R.string.curr_location_) + cityName);
            initDate();
        }
        // 设置行政区划查询监听
        DistrictSearch districtSearch = new DistrictSearch(mActivity);
        districtSearch.setOnDistrictSearchListener(this);
        // 查询中国的区划
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords("中国");
        query.setPageSize(50);
        districtSearch.setQuery(query);
        // 异步查询行政区
        districtSearch.searchDistrictAsyn();
    }

    private void initAdapter() {
        houseList = new ArrayList<>();
        shortList = new ArrayList<>();
        recommendList = new ArrayList<>();
        reLongList = new ArrayList<>();
        reShortList = new ArrayList<>();


        //精品房源
        houseAdapter = new CommonAdapter<RenterSearchListBean>(mActivity, houseList, R.layout.adapter_tenant_main_house) {
            @Override
            public void convert(ViewHolder viewHolder, RenterSearchListBean bean) {
                viewHolder.setText(R.id.adapter_tenant_main_house_tv_name, bean.getRoom_name());
                String unit_rent = bean.getType() == 1 ? getString(R.string.public_house_pay_unit_day) : getString(R.string.public_house_pay_unit_mon);
                int color_text = bean.getType() == 1 ? getResources().getColor(R.color.text_green) : getResources().getColor(R.color.text_ren);
                String price = bean.getHousing_price();
                if (!TextUtils.isEmpty(price)) {
                    float priceF = Float.valueOf(bean.getHousing_price());
                    int priceI = Float.valueOf(bean.getHousing_price()).intValue();
                    if (priceF <= 0) {
                        price = "";
                        unit_rent = getString(R.string.negotiable);
                    } else if (priceF > priceI) {
                        price = priceF + "";
                    } else {
                        price = priceI + "";
                    }
                } else {
                    price = "";
                }
                viewHolder.setText(R.id.adapter_tenant_main_house_tv_price, price);
                viewHolder.setText(R.id.adapter_tenant_main_house_tv_price_unit, unit_rent);
                viewHolder.setTextColor(R.id.adapter_tenant_main_house_tv_price, color_text);
                viewHolder.setTextColor(R.id.adapter_tenant_main_house_tv_price_unit, color_text);

                ImageView picView = viewHolder.getView(R.id.adapter_tenant_main_house_iv_icon);
                if (!TextUtils.isEmpty(bean.getThumb_image())) {
                    Picasso.get().load(bean.getThumb_image()).error(R.mipmap.fangchan_jiazai).into(picView);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RoomInfoActivity.toActivity(mActivity, bean.getRoom_id());
                    }
                });
            }
        };
        //热门短租
        shortAdapter = new CommonAdapter<RenterSearchListBean>(mActivity, shortList, R.layout.adapter_tenant_main_short) {
            @Override
            public void convert(ViewHolder viewHolder, RenterSearchListBean bean) {
                String room_type;
                if (bean.getRoom_type().contains("_")) {
                    String[] t = bean.getRoom_type().split("_");
                    room_type = t[0] + "室" + (t[1].equals("0") ? "" : t[1] + "卫") + t[2] + "厅";
                } else {
                    room_type = bean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + " | ");
                spannableStringBuilder.append(getArea(bean.getMeasure_area() +""));
                spannableStringBuilder.append(" | " + bean.getFloor() + "/" + bean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_tenant_main_short_tv_info, spannableStringBuilder);

                String unit_rent = getString(R.string.public_house_pay_unit_day);
                String price = bean.getHousing_price();
                if (!TextUtils.isEmpty(price)) {
                    float priceF = Float.valueOf(bean.getHousing_price());
                    int priceI = (int) priceF;
                    if (priceF <= 0) {
                        price = "";
                        unit_rent = getString(R.string.negotiable);
                    } else if (priceF > priceI) {
                        price = priceF + "";
                    } else {
                        price = priceI + "";
                    }
                } else {
                    price = "";
                }
                viewHolder.setText(R.id.adapter_tenant_main_short_tv_price, price);
                viewHolder.setText(R.id.adapter_tenant_main_short_tv_price_unit, unit_rent);
                viewHolder.setText(R.id.adapter_tenant_main_short_tv_name, bean.getRoom_name());

                ImageView picView = viewHolder.getView(R.id.adapter_tenant_main_short_iv_icon);
                if (!TextUtils.isEmpty(bean.getImage())) {
                    Picasso.get().load(bean.getImage()).error(R.mipmap.fangchan_jiazai).into(picView);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RoomInfoActivity.toActivity(mActivity, bean.getRoom_id());
                    }
                });

            }
        };
        //为您推荐
        recommendAdapter = new CommonAdapter<RenterSearchListBean>(mActivity, recommendList, R.layout.adapter_tenant_main_recommend) {
            @Override
            public void convert(ViewHolder viewHolder, RenterSearchListBean bean) {
                String room_type;
                if (bean.getRoom_type().contains("_")) {
                    String[] t = bean.getRoom_type().split("_");
                    room_type = t[0] + "室" + (t[1].equals("0") ? "" : t[1] + "卫") + t[2] + "厅";
                } else {
                    room_type = bean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + " | ");
                spannableStringBuilder.append(getArea(bean.getMeasure_area() +""));
                spannableStringBuilder.append(" | " + bean.getFloor() + "/" + bean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_info, spannableStringBuilder);

                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_name, bean.getRoom_name());

                int text_color = bean.getType() == 1 ? getResources().getColor(R.color.text_green) : getResources().getColor(R.color.text_ren);
                String unit_rent = bean.getType() == 1 ? getString(R.string.public_house_pay_unit_day) : getString(R.string.public_house_pay_unit_mon);
                String price = bean.getHousing_price();
                if (!TextUtils.isEmpty(price)) {
                    float priceF = Float.valueOf(bean.getHousing_price());
                    int priceI = (int) priceF;
                    if (priceF <= 0) {
                        price = getString(R.string.negotiable);
                        unit_rent = "";
                    } else if (priceF > priceI) {
                        price = priceF + "";
                    } else {
                        price = priceI + "";
                    }
                } else {
                    price = "";
                }
                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_price, price);
                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_price_unit, unit_rent);
                viewHolder.setTextColor(R.id.adapter_tenant_main_recommend_tv_price,text_color);
                viewHolder.setTextColor(R.id.adapter_tenant_main_recommend_tv_price_unit,text_color);

                ImageView picView = viewHolder.getView(R.id.adapter_tenant_main_recommend_iv_icon);
                if (!TextUtils.isEmpty(bean.getThumb_image())) {
                    Picasso.get().load(bean.getThumb_image()).error(R.mipmap.fangchan_jiazai).into(picView);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RoomInfoActivity.toActivity(mActivity, bean.getRoom_id());
                    }
                });

            }
        };

        LinearLayoutManager houseManager = new LinearLayoutManager(mActivity);
        houseManager.setOrientation(RecyclerView.HORIZONTAL);
        mRvHouse.setLayoutManager(houseManager);
        LinearLayoutManager shortManager = new LinearLayoutManager(mActivity);
        shortManager.setOrientation(RecyclerView.HORIZONTAL);
        mRvShort.setLayoutManager(shortManager);
        mRvRecommend.setLayoutManager(new LinearLayoutManager(mActivity));

        mRvHouse.setAdapter(houseAdapter);
        mRvShort.setAdapter(shortAdapter);
        mRvRecommend.setAdapter(recommendAdapter);


    }

    private void initListent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (city != null) {
                    initDate();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showToast(getString(R.string.please_choose_city));
                }
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

//        mRefreshRecommend.setEnableRefresh(false);
        mRefreshRecommend.setEnableLoadmore(false);
        mRefreshRecommend.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (rent_type == 1) {
                    page_short++;
                } else {
                    page_long++;
                }
                getRecommendListData(rent_type);
            }
        });
    }

    /**
     * 定位
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mActivity);
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                locationCity = new LocatedCity(aMapLocation.getCity(), aMapLocation.getCity(), aMapLocation.getCityCode());
                if (city == null) {
                    city = new City(aMapLocation.getCity(), aMapLocation.getCity(), pinYinUtils.getStringPinYin(aMapLocation.getCity()), aMapLocation.getCityCode());
                    mTvCity.setText(getString(R.string.curr_location_) + city.getName());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CITY_NAME, city.getName());
                    editor.commit();
                    initDate();
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

    private void initDate() {
        getHoustListData();
        getHotShortListData();
        page_long = 1;
        page_short = 1;
        getRecommendListData(2);
        getRecommendListData(1);
        swipeRefreshLayout.setRefreshing(false);
    }


    @OnClick({R.id.fragment_tenant_main_tv_choose_city, R.id.fragment_tenant_main_img_choose_city, R.id.fragment_tenant_main_tv_search,
            R.id.fragment_tenant_main_tv_long_rent, R.id.fragment_tenant_main_tv_short_rent, R.id.fragment_tenant_main_tv_find_house,
            R.id.fragment_tenant_main_tv_house_more, R.id.fragment_tenant_main_tv_short_more, R.id.fragment_tenant_main_ll_type_long, R.id.fragment_tenant_main_ll_type_short})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_tenant_main_tv_choose_city://选择城市
            case R.id.fragment_tenant_main_img_choose_city://选择城市
                selectionCity();
                break;
            case R.id.fragment_tenant_main_tv_search://搜索房产
                String search = mEdtAddress.getText().toString().replace(" ", "");
                if (TextUtils.isEmpty(search)) {
                    showToast(getString(R.string.please_input_search_string));
                } else if (city == null) {
                    showToast(getString(R.string.please_choose_city));
                } else {
                    SearchActivity.toActivity(getActivity(), city.getName(), search);
                }
                break;
            case R.id.fragment_tenant_main_tv_long_rent://长租房源
                MoreHotRoomListActivity.toActivity(mActivity, getString(R.string.competitive_house_long), city == null ? "" : city.getName(), 2, 0);
                break;
            case R.id.fragment_tenant_main_tv_short_rent://短租房源
                MoreHotRoomListActivity.toActivity(mActivity, getString(R.string.competitive_house_short), city == null ? "" : city.getName(), 1, 0);
                break;
            case R.id.fragment_tenant_main_tv_find_house://地图找房
                if (city == null) {
                    showToast(getString(R.string.please_choose_city));
                } else {
                    MapFindHouseActivity.toActivity(getActivity(), city.getName());
                }
                break;
            case R.id.fragment_tenant_main_tv_house_more://精品房源
                MoreHotRoomListActivity.toActivity(mActivity, getString(R.string.competitive_house), city == null ? "" : city.getName(), 0, 2);
                break;
            case R.id.fragment_tenant_main_tv_short_more://热门短租
                MoreHotRoomListActivity.toActivity(mActivity, getString(R.string.competitive_short), city == null ? "" : city.getName(), 1, 3);
                break;
            case R.id.fragment_tenant_main_ll_type_long://长租推荐
                if (rent_type == 2) {
                    return;
                }
                rent_type = 2;
                mTvTypeLong.setSelected(true);
                mTvTypeShort.setSelected(false);
                mTvTypeLong.getPaint().setFakeBoldText(true);
                mTvTypeShort.getPaint().setFakeBoldText(false);
                mTypeLong.setVisibility(View.VISIBLE);
                mTypeShort.setVisibility(View.INVISIBLE);
                if (reLongList.size() <= 0) {
                    page_long = 1;
                    getRecommendListData(rent_type);
                } else {
                    recommendList.clear();
                    recommendList.addAll(reLongList);
                    recommendAdapter.notifyDataSetChanged();
                    mRefreshRecommend.setEnableLoadmore(is_long_enable_loading);
                }
                break;
            case R.id.fragment_tenant_main_ll_type_short://短租推荐
                if (rent_type == 1) {
                    return;
                }
                rent_type = 1;
                mTvTypeLong.setSelected(false);
                mTvTypeShort.setSelected(true);
                mTvTypeLong.getPaint().setFakeBoldText(false);
                mTvTypeShort.getPaint().setFakeBoldText(true);
                mTypeLong.setVisibility(View.INVISIBLE);
                mTypeShort.setVisibility(View.VISIBLE);
                if (reShortList.size() <= 0) {
                    page_short = 1;
                    getRecommendListData(rent_type);
                } else {
                    recommendList.clear();
                    recommendList.addAll(reShortList);
                    recommendAdapter.notifyDataSetChanged();
                    mRefreshRecommend.setEnableLoadmore(is_short_enable_loading);
                }
                break;
        }
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }

    /***************************************************接口*********************************************/
    /**
     * 精品房源
     */
    private void getHoustListData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList("1", "", city == null ? "" : city.getName(), "", "", "0", "2")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mLlHouse.setVisibility(View.GONE);
                        mRvHouse.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> homeInfoDataInfo) {
                        if (homeInfoDataInfo.success()) {
                            houseList.clear();
                            houseList.addAll(homeInfoDataInfo.data().getList());
                            houseAdapter.notifyDataSetChanged();
                            if (houseList.size() > 0) {
                                mLlHouse.setVisibility(View.VISIBLE);
                                mRvHouse.setVisibility(View.VISIBLE);
                            } else {
                                mLlHouse.setVisibility(View.GONE);
                                mRvHouse.setVisibility(View.GONE);
                            }
                        } else {
                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 热门短租
     */
    private void getHotShortListData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList("1", "", city == null ? "" : city.getName(), "", "", "1", "3")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mLlShort.setVisibility(View.GONE);
                        mRvShort.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> homeInfoDataInfo) {
                        if (homeInfoDataInfo.success()) {
                            shortList.clear();
                            shortList.addAll(homeInfoDataInfo.data().getList());
                            shortAdapter.notifyDataSetChanged();
                            if (shortList.size() > 0) {
                                mLlShort.setVisibility(View.VISIBLE);
                                mRvShort.setVisibility(View.VISIBLE);
                            } else {
                                mLlShort.setVisibility(View.GONE);
                                mRvShort.setVisibility(View.GONE);
                            }
                        } else {
                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 推荐房源
     */
    private void getRecommendListData(int type) {
        final int page = (type == 1 ? page_short : page_long);
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList(page + "", "", city == null ? "" : city.getName(), "", "", type + "", "1")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mRefreshRecommend.finishLoadmore();
                        if (page > 1) {
                            if (type == 1) {
                                page_short--;
                            } else {
                                page_long--;
                            }
                        }
//                        AppBarLayout.LayoutParams  mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
                        if (reShortList.size() > 0 || reLongList.size() > 0) {
                            mLlRecommend.setVisibility(View.VISIBLE);
                            mRefreshRecommend.setVisibility(View.VISIBLE);
//                            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
//                            mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);

                        } else {
                            mLlRecommend.setVisibility(View.GONE);
                            mRefreshRecommend.setVisibility(View.GONE);
//                            mAppBarParams.setScrollFlags(0);
//                            mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> homeInfoDataInfo) {
                        mRefreshRecommend.finishLoadmore();
                        if (homeInfoDataInfo.success()) {
                            if (page > 1) {
                                if (type == 1) {
                                    reShortList.addAll(homeInfoDataInfo.data().getList());
                                } else {
                                    reLongList.addAll(homeInfoDataInfo.data().getList());
                                }
                            } else {
                                if (type == 1) {
                                    reShortList.clear();
                                    reShortList.addAll(homeInfoDataInfo.data().getList());

                                } else {
                                    reLongList.clear();
                                    reLongList.addAll(homeInfoDataInfo.data().getList());

                                }
                            }
                            if (rent_type == 1) {
                                recommendList.clear();
                                recommendList.addAll(reShortList);
                            } else {
                                recommendList.clear();
                                recommendList.addAll(reLongList);
                            }
                            recommendAdapter.notifyDataSetChanged();
                            if (type == 1) {
                                is_short_enable_loading = homeInfoDataInfo.data().getPage() < homeInfoDataInfo.data().getTotalPage();
                                mRefreshRecommend.setEnableLoadmore(is_short_enable_loading);
                            } else {
                                is_long_enable_loading = homeInfoDataInfo.data().getPage() < homeInfoDataInfo.data().getTotalPage();
                                mRefreshRecommend.setEnableLoadmore(is_long_enable_loading);
                            }

                            if (reShortList.size() > 0 || reLongList.size() > 0) {
                                mLlRecommend.setVisibility(View.VISIBLE);
                                mRefreshRecommend.setVisibility(View.VISIBLE);
                                mNestedScrollView.setNestedScrollingEnabled(true);
                                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                                    @Override
                                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                                        return true;
                                    }
                                });
                            } else {
                                mLlRecommend.setVisibility(View.GONE);
                                mRefreshRecommend.setVisibility(View.GONE);
                                mNestedScrollView.setNestedScrollingEnabled(false);
                                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                                    @Override
                                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                                        return false;
                                    }
                                });
                            }
                        } else {
                            if (page > 1 && type == 1) {
                                page_short--;
                            } else if (page > 1 && type == 2) {
                                page_long--;
                            }
                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    /**
     * 逆地理编码（坐标转地址）结果回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        String c = regeocodeResult.getRegeocodeAddress().getCity();
        Log.d("MapFindHouseActivity","onRegeocodeSearched() 坐标转换定位的当前地址："+c);
        if (!TextUtils.isEmpty(c)) {
            mTvCity.setText(getString(R.string.curr_location_) + c);
            city = new City(c, regeocodeResult.getRegeocodeAddress().getProvince(),
                    pinYinUtils.getStringPinYin(c),
                    regeocodeResult.getRegeocodeAddress().getCityCode());
        }
    }

    /**
     * 地理编码（地址转坐标）
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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
        DistrictSearch districtSearch = new DistrictSearch(mActivity);
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
                .setFragmentManager(getChildFragmentManager())    //此方法必须调用
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
                            mTvCity.setText(getString(R.string.curr_location_) + data.getName());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(CITY_NAME, city.getName());
                            editor.commit();

                            page_long = 1;
                            page_short = 1;
                            reLongList.clear();
                            reShortList.clear();
                            initDate();
                        }
                    }


                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位

                    }
                })
                .show();
    }


}
