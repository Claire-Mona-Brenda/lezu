package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.landlord.house.ChooseLocationEvent;
import com.konka.renting.utils.RxBus;
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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchAddressActivity extends BaseActivity implements TextWatcher, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener, DistrictSearch.OnDistrictSearchListener {
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
    @BindView(R.id.activity_search_address_edt_search)
    EditText editSearch;
    @BindView(R.id.activity_search_address_rv_location)
    RecyclerView mRvLocation;
    @BindView(R.id.activity_search_address_srv_location)
    SmartRefreshLayout mSrvLocation;

    public final String COUNTRY = "country"; // 行政区划，国家级

    public final String PROVINCE = "province"; // 行政区划，省级

    public final String CITY = "city"; // 行政区划，市级

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    CommonAdapter<PoiItem> commonAdapter;

    PinYinUtils pinYinUtils;
    //当前选中的级别
    private String selectedLevel = COUNTRY;

    private List<HotCity> mCities;
    private List<City> mAllCities;
    City city;

    public static void toActivity(Context context, ArrayList<City> mAllCities, ArrayList<HotCity> mCities, City city) {
        Intent intent = new Intent(context, SearchAddressActivity.class);
        intent.putParcelableArrayListExtra("mAllCities", mAllCities);
        intent.putParcelableArrayListExtra("mCities", mCities);
        intent.putExtra("city", city);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_address;
    }

    @Override
    public void init() {
        mAllCities = getIntent().getParcelableArrayListExtra("mAllCities");
        mCities = getIntent().getParcelableArrayListExtra("mCities");
        city = getIntent().getParcelableExtra("city");

        tvTitle.setText(R.string.title_choose_location);

        if (city != null) {
            tvLocation.setText(city.getName());
        }

        poiItems = new ArrayList<>();
        commonAdapter = new CommonAdapter<PoiItem>(this, poiItems, R.layout.adapter_choose_location) {
            @Override
            public void convert(ViewHolder viewHolder, PoiItem poiItem) {
                viewHolder.setText(R.id.adapter_location_tv_name, poiItem.getTitle());
                viewHolder.setText(R.id.adapter_location_tv_address, poiItem.getSnippet());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.getDefault().post(new ChooseLocationEvent(poiItem));
                        finish();
                    }
                });
            }
        };

        mRvLocation.setLayoutManager(new LinearLayoutManager(this));
        mRvLocation.setAdapter(commonAdapter);

        editSearch.addTextChangedListener(this);

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

        if (mAllCities.size() <= 0) {
            pinYinUtils = new PinYinUtils();
            pinYinUtils.getCharPinYin('a');
            initCity();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        editSearch.setFocusable(true);
        editSearch.requestFocus();
        editSearch.setFocusableInTouchMode(true);
    }
    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String str) {
        query = new PoiSearch.Query(str, "120000", city == null ? "" : city.getName());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步搜索

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

    @OnClick({R.id.iv_back, R.id.tv_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_location:
                selectionCity();
                break;
        }
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
                            String search = editSearch.getText().toString();
                            if (!TextUtils.isEmpty(search)) {
                                currentPage = 1;
                                doSearchQuery(search);
                            } else {
                                poiItems.clear();
                                commonAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位

                    }
                })
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        currentPage = 1;
        if (!TextUtils.isEmpty(newText)) {
            doSearchQuery(newText);
        } else {
            poiItems.clear();
            commonAdapter.notifyDataSetChanged();
        }
//        InputtipsQuery inputquery = new InputtipsQuery(newText, city == null ? "" : city.getName());
//        inputquery.setCityLimit(true);
//        Inputtips inputTips = new Inputtips(this, inputquery);
//        inputTips.setInputtipsListener(this);
//        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 输入提示结果的回调
     *
     * @param tipList
     * @param rCode
     */
    @Override
    public void onGetInputtips(final List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            List<HashMap<String, String>> listString = new ArrayList<HashMap<String, String>>();
            if (tipList != null) {
                int size = tipList.size();
                for (int i = 0; i < size; i++) {
                    Tip tip = tipList.get(i);
                    if (tip != null) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("name", tipList.get(i).getName());
                        map.put("address", tipList.get(i).getDistrict());
                        listString.add(map);
                    }
                }
                commonAdapter.notifyDataSetChanged();
            }

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
}
