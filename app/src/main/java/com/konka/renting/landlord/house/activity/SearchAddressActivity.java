package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.amap.api.services.geocoder.BusinessArea;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RoomGroupListBean;
import com.konka.renting.event.ChooseCityEvent;
import com.konka.renting.event.ChooseEstateEvent;
import com.konka.renting.event.ChooseEstateFinishEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.ChooseLocationEvent;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class SearchAddressActivity extends BaseActivity implements TextWatcher, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener, DistrictSearch.OnDistrictSearchListener, GeocodeSearch.OnGeocodeSearchListener {
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
    public final String CITY_NAME = "cityName";//SharedPreferences 键名

    SharedPreferences sharedPreferences;


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

    CommonInputPopupWindow inputPopupWindow;
    PoiItem choosePoiItem;
    GeocodeSearch geocoderSearch;

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
        sharedPreferences = getSharedPreferences(CITY, MODE_PRIVATE);

        if (city != null) {
            tvLocation.setText(city.getName());
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        poiItems = new ArrayList<>();
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
        query = new PoiSearch.Query(str, "", city == null ? "" : city.getName());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
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
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(CITY_NAME, city.getName());
                            editor.commit();
                            RxBus.getDefault().post(new ChooseCityEvent(data.getName()));
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

    /*******************************************弹窗***********************************************************/
    private void showInputPop(String name) {
        if (inputPopupWindow == null) {
            inputPopupWindow = new CommonInputPopupWindow(mActivity);
            inputPopupWindow.setTvTitle(getString(R.string.estate_name));
            inputPopupWindow.setBtnRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("12313","====onClick======"+choosePoiItem.getBusinessArea());
                    String name = inputPopupWindow.getEdtContent().getText().toString();
                    if (!TextUtils.isEmpty(name.replace(" ",""))&&!TextUtils.isEmpty(choosePoiItem.getBusinessArea())) {
                        inputPopupWindow.dismiss();
                        roomGroupAdd(name);
                    } else if (TextUtils.isEmpty(choosePoiItem.getBusinessArea())){

                    }else {
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
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.000000");
        Subscription subscription = SecondRetrofitHelper.getInstance().roomGroupAdd(name, choosePoiItem.getProvinceName(), choosePoiItem.getCityName(),
                choosePoiItem.getAdName(), choosePoiItem.getBusinessArea(), choosePoiItem.getSnippet(),
                df.format(choosePoiItem.getLatLonPoint().getLongitude()), df.format(choosePoiItem.getLatLonPoint().getLatitude()))
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
                            RxBus.getDefault().post(new ChooseLocationEvent(choosePoiItem));
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

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
