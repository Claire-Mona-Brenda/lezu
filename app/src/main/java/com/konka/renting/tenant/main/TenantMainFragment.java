package com.konka.renting.tenant.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.material.appbar.AppBarLayout;
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
        mTypeLong.setVisibility(View.VISIBLE);
        mTypeShort.setVisibility(View.GONE);




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
            mTvCity.setText(cityName);
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
                viewHolder.setText(R.id.adapter_tenant_main_house_tv_price, bean.getHousing_price() + unit_rent);
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
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = bean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + "|");
                spannableStringBuilder.append(bean.getMeasure_area() + getString(R.string.unit_m2));
                spannableStringBuilder.append("|" + bean.getFloor() + "/" + bean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_tenant_main_short_tv_info, spannableStringBuilder);

                viewHolder.setText(R.id.adapter_tenant_main_short_tv_price, bean.getHousing_price());
                viewHolder.setText(R.id.adapter_tenant_main_short_tv_name, bean.getRoom_name());

                ImageView picView = viewHolder.getView(R.id.adapter_tenant_main_short_iv_icon);
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
        //为您推荐
        recommendAdapter = new CommonAdapter<RenterSearchListBean>(mActivity, recommendList, R.layout.adapter_tenant_main_recommend) {
            @Override
            public void convert(ViewHolder viewHolder, RenterSearchListBean bean) {
                String room_type;
                if (bean.getRoom_type().contains("_")) {
                    String[] t = bean.getRoom_type().split("_");
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = bean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + "|");
                spannableStringBuilder.append(bean.getMeasure_area() + getString(R.string.unit_m2));
                spannableStringBuilder.append("|" + bean.getFloor() + "/" + bean.getTotal_floor() + "层");
                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_info, spannableStringBuilder);

                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_name, bean.getRoom_name());
                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_price, bean.getHousing_price());
                viewHolder.setText(R.id.adapter_tenant_main_recommend_tv_price_unit, getString(bean.getType() == 1 ? R.string.house_info_rent_pay_unit_day : R.string.house_info_rent_pay_unit));

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
                getRecommendListData();
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
                    mTvCity.setText(city.getName());
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
        getRecommendListData();
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
                String search = mEdtAddress.getText().toString().replace(" ","");
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
                MoreHotRoomListActivity.toActivity(mActivity, getString(R.string.competitive_house), city == null ? "" : city.getName(), 1, 3);
                break;
            case R.id.fragment_tenant_main_ll_type_long://长租推荐
                if (rent_type == 2) {
                    return;
                }
                rent_type = 2;
                mTvTypeLong.setSelected(true);
                mTvTypeShort.setSelected(false);
                mTypeLong.setVisibility(View.VISIBLE);
                mTypeShort.setVisibility(View.GONE);
                if (reLongList.size() <= 0) {
                    page_long = 1;
                    getRecommendListData();
                } else {
                    recommendList.clear();
                    recommendList.addAll(reLongList);
                    recommendAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.fragment_tenant_main_ll_type_short://短租推荐
                if (rent_type == 1) {
                    return;
                }
                rent_type = 1;
                mTvTypeLong.setSelected(false);
                mTvTypeShort.setSelected(true);
                mTypeLong.setVisibility(View.GONE);
                mTypeShort.setVisibility(View.VISIBLE);
                if (reShortList.size() <= 0) {
                    page_short = 1;
                    getRecommendListData();
                } else {
                    recommendList.clear();
                    recommendList.addAll(reShortList);
                    recommendAdapter.notifyDataSetChanged();
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
    private void getRecommendListData() {
        final int page = (rent_type == 1 ? page_short : page_long);
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList(page + "", "", city == null ? "" : city.getName(), "", "", rent_type + "", "1")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (page > 1) {
                            mRefreshRecommend.finishLoadmore();
                            if (rent_type == 1) {
                                page_short--;
                            } else {
                                page_long--;
                            }
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> homeInfoDataInfo) {
                        if (page > 1)
                            mRefreshRecommend.finishLoadmore();
                        mRefreshRecommend.finishLoadmore();
                        if (homeInfoDataInfo.success()) {
                            recommendList.clear();
                            recommendList.addAll(homeInfoDataInfo.data().getList());
                            recommendAdapter.notifyDataSetChanged();
                            if (rent_type == 1) {
                                reShortList.clear();
                                reShortList.addAll(recommendList);
                            } else {
                                reLongList.clear();
                                reLongList.addAll(recommendList);
                            }
                            mRefreshRecommend.setEnableLoadmore(homeInfoDataInfo.data().getPage() < homeInfoDataInfo.data().getTotalPage());
                            if (recommendList.size() > 0) {
                                mLlRecommend.setVisibility(View.VISIBLE);
                                mRefreshRecommend.setVisibility(View.VISIBLE);
                            } else {
                                mLlRecommend.setVisibility(View.GONE);
                                mRefreshRecommend.setVisibility(View.GONE);
                            }
                        } else {
                            if (page > 1 && rent_type == 1) {
                                page_short--;
                            } else if (page > 1 && rent_type == 2) {
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
        if (!TextUtils.isEmpty(c)) {
            mTvCity.setText(c);
            city = new City(c, regeocodeResult.getRegeocodeAddress().getProvince(), pinYinUtils.getStringPinYin(c), regeocodeResult.getRegeocodeAddress().getCityCode());
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
                            mTvCity.setText(data.getName());
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
