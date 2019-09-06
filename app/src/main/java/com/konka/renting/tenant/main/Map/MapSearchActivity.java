package com.konka.renting.tenant.main.Map;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.MapLocationSearchBean;
import com.konka.renting.event.MapSearchChooseEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.DBManager;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class MapSearchActivity extends BaseActivity {

    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.activity_map_search_ll_del_history)
    LinearLayout mLlDelHistory;
    @BindView(R.id.activity_map_search_rv_history)
    RecyclerView mRvHistory;
    @BindView(R.id.activity_map_search_rv_search)
    RecyclerView mRvSearch;
    @BindView(R.id.activity_map_search_srl_search)
    SmartRefreshLayout mSrlSearch;


    private CommonAdapter<MapLocationSearchBean> mCommonAdapter;
    CommonAdapter<MapLocationSearchBean> commonHistoryAdapter;

    private List<MapLocationSearchBean> mRoomInfos = new ArrayList<>();
    private List<MapLocationSearchBean> mHistoryData;

    DBManager dbManager;

    String cityName;
    String search;

    public static void toActivity(Context context, String cityName) {
        Intent intent = new Intent(context, MapSearchActivity.class);
        intent.putExtra("cityName", cityName);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_map_search;
    }

    @Override
    public void init() {
        cityName = getIntent().getStringExtra("cityName");
        dbManager = new DBManager(this);
        initHistory();
        mCommonAdapter = new CommonAdapter<MapLocationSearchBean>(this, mRoomInfos, R.layout.adapter_map_location_search) {
            @Override
            public void convert(ViewHolder viewHolder, MapLocationSearchBean bean) {
                String str="<font color='#ff7500'>"+search+"</font>";
                String name=bean.getName().replace(search,str);
                String address=bean.getAddress().replace(search,str);
                viewHolder.setText(R.id.adapter_map_location_search_tv_name, Html.fromHtml(name));
                viewHolder.setText(R.id.adapter_map_location_search_tv_address, Html.fromHtml(address));
                viewHolder.setText(R.id.adapter_map_location_search_tv_count, bean.getCount() + "");
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mHistoryData.size() < 5) {
                            if (dbManager.querySearchHistoryData(bean.getName()) != null) {
                                dbManager.updateSearchHistoryData(bean.getName(), bean.getAddress(), bean.getLat(), bean.getLng(), bean.getLevel() + "", bean.getCount() + "");
                            } else {
                                dbManager.insertSearchHistoryData(bean.getName(), bean.getAddress(), bean.getLat(), bean.getLng(), bean.getLevel() + "", bean.getCount() + "");
                            }
                        } else {
                            if (dbManager.querySearchHistoryData(bean.getName()) != null) {
                                dbManager.deleteSearchHistoryData(bean.getName());
                                dbManager.insertSearchHistoryData(bean.getName(), bean.getAddress(), bean.getLat(), bean.getLng(), bean.getLevel() + "", bean.getCount() + "");
                            } else {
                                dbManager.deleteSearchHistoryData(mHistoryData.get(0).getName());
                                dbManager.insertSearchHistoryData(bean.getName(), bean.getAddress(), bean.getLat(), bean.getLng(), bean.getLevel() + "", bean.getCount() + "");
                            }
                        }
                        RxBus.getDefault().post(new MapSearchChooseEvent(bean.getName(), bean.getAddress(), bean.getLat(), bean.getLng(), bean.getLevel() , bean.getCount() ));
                        finish();
                    }
                });
            }
        };

        mRvSearch.setLayoutManager(new LinearLayoutManager(this));
        mRvSearch.setAdapter(mCommonAdapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search = editSearch.getText().toString();
                if (!TextUtils.isEmpty(search.replace(" ",""))) {
                    getSearchResultList(search);
                    mLlDelHistory.setVisibility(View.GONE);
                    mRvHistory.setVisibility(View.GONE);
                    mRvSearch.setVisibility(View.VISIBLE);
                }else{
                    mLlDelHistory.setVisibility(View.VISIBLE);
                    mRvHistory.setVisibility(View.VISIBLE);
                    mRvSearch.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                     当按了搜索之后关闭软键盘

                    search = editSearch.getText().toString();
                    if (TextUtils.isEmpty(search.replace(" ",""))) {
                        showToast(R.string.please_input_content);
                        return true;
                    }
                    getSearchResultList(search);
                    mLlDelHistory.setVisibility(View.GONE);
                    mRvHistory.setVisibility(View.GONE);
                    mRvSearch.setVisibility(View.VISIBLE);

                    return true;
                }
                return false;
            }
        });

        mSrlSearch.setEnableLoadmore(false);
        mSrlSearch.setEnableRefresh(false);


    }

    private void initHistory() {
        mHistoryData = new ArrayList<>();
        mHistoryData.addAll(dbManager.queryAlllSearchHistoryData());
        if (mHistoryData.size() > 0) {
            mLlDelHistory.setVisibility(View.VISIBLE);
            mRvHistory.setVisibility(View.VISIBLE);
            mRvSearch.setVisibility(View.GONE);
        }
        commonHistoryAdapter = new CommonAdapter<MapLocationSearchBean>(this, mHistoryData, R.layout.adapter_map_search) {
            @Override
            public void convert(ViewHolder viewHolder, MapLocationSearchBean bean) {
                viewHolder.setText(R.id.adapter_map_search_tv_name, bean.getName());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.getDefault().post(new MapSearchChooseEvent(bean.getName(), bean.getAddress(), bean.getLat(), bean.getLng(), bean.getLevel() , bean.getCount() ));
                        finish();
                    }
                });
            }
        };
        mRvHistory.setLayoutManager(new LinearLayoutManager(this));
        mRvHistory.setAdapter(commonHistoryAdapter);
    }


    @OnClick({R.id.tv_cancel, R.id.activity_map_search_ll_del_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.activity_map_search_ll_del_history:
                int size = mHistoryData.size();
                for (int i = 0; i < size; i++) {
                    dbManager.deleteSearchHistoryData(mHistoryData.get(i).getName());
                }
                mHistoryData.clear();
                commonHistoryAdapter.notifyDataSetChanged();
                mLlDelHistory.setVisibility(View.GONE);
                break;
        }
    }


    private void getSearchResultList(String keyword) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().mapLocationSearch(cityName, keyword)
                .compose(RxUtil.<DataInfo<List<MapLocationSearchBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<MapLocationSearchBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<List<MapLocationSearchBean>> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            mRoomInfos.clear();
                            mRoomInfos.addAll(dataInfo.data());
                            mCommonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
