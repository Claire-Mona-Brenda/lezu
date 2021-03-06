package com.konka.renting.landlord.home.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.event.SearchHisotryEvent;
import com.konka.renting.event.ToSearchResultEvent;
import com.konka.renting.utils.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class SearchActivity extends BaseActivity {

    public Fragment[] mFragments = new Fragment[2];
    @BindView(R.id.edit_search)
    EditText mEditSearch;
    @BindView(R.id.tv_search_nothing)
    TextView mTvSearchTips;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.frame_container)
    FrameLayout frameContainer;

    String city;
    String search;

    public static void toActivity(Context context, String city, String search) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("city", city);
        intent.putExtra("search", search);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_serech;
    }

    @Override
    public void init() {
        city = getIntent().getStringExtra("city");
        search = getIntent().getStringExtra("search");

        mFragments[0] = SearchHistoryFragment.newInstance();
        mFragments[1] = SearchResultFragment.newInstance();
        switchContent(1, 0);

        addRxBusSubscribe(SearchHisotryEvent.class, new Action1<SearchHisotryEvent>() {
            @Override
            public void call(SearchHisotryEvent searchHisotryEvent) {
                toSearchResult(searchHisotryEvent.content);
            }
        });

        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                     当按了搜索之后关闭软键盘

                    String content = mEditSearch.getText().toString();
                    if (TextUtils.isEmpty(content)) {
                        doFailed();
                        return true;
                    }
                    toSearchResult(content);
                    return true;
                }
                return false;
            }
        });
        mEditSearch.setText(search);
        mEditSearch.setSelection(search.length());

    }

    @Override
    protected void onResume() {
        super.onResume();
        toSearchResult(search);
    }

    private void toSearchResult(String content) {
//        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Log.e(TAG, "toSearchResult: ");
        switchContent(0, 1);
        RxBus.getDefault().post(new ToSearchResultEvent(content, city));
    }

    public void switchContent(int fromIndex, int toIndex) {
        Log.e(TAG, "switchContent: form = " + fromIndex + "//toIndex = " + toIndex);
        Fragment from = mFragments[fromIndex];
        Fragment to = mFragments[toIndex];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!to.isAdded()) {    // 先判断是否被add过
            if (from != null) {
                if (!from.isAdded()) {
                    transaction = transaction.add(R.id.frame_container, from);
                }
                transaction.hide(from).add(R.id.frame_container, to).show(to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.add(R.id.frame_container, to).commit();
            }
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }

    }

    public void showTips(boolean is){
        if (is){
            mTvSearchTips.setVisibility(View.VISIBLE);
            frameContainer.setVisibility(View.GONE);
        }else{
            mTvSearchTips.setVisibility(View.GONE);
            frameContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        finish();
    }

}
