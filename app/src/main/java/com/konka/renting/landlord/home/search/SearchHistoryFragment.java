package com.konka.renting.landlord.home.search;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.event.SearchHisotryEvent;
import com.konka.renting.utils.RxBus;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 */
public class SearchHistoryFragment extends BaseFragment {


    @BindView(R.id.listview)
    ListView mListview;
    Unbinder unbinder;

    private View mFooterView;

    private ArrayList<SearchHistoryInfo> mSearchHistoryInfos = new ArrayList<>();

    private CommonAdapter<SearchHistoryInfo> mSearchHistoryInfoCommonAdapter;

    public SearchHistoryFragment() {
        // Required empty public constructor
    }

    public static SearchHistoryFragment newInstance() {

        return new SearchHistoryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void init() {
        super.init();
        mSearchHistoryInfoCommonAdapter = new CommonAdapter<SearchHistoryInfo>(getActivity(), mSearchHistoryInfos, R.layout.item_search_history) {
            @Override
            public void convert(ViewHolder viewHolder, SearchHistoryInfo searchHistoryInfo, int i) {
                viewHolder.setText(R.id.tv_history, searchHistoryInfo.content);
            }

            @Override
            public int getCount() {
                return mSearchHistoryInfos.size() == 0 ? 0 : super.getCount();
            }
        };

        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_history_bottom, null);
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpHistoryStorage.getInstance(getActivity(), 10).clear();
                refreshData();
            }
        });
        mListview.addFooterView(mFooterView);
        mListview.setFooterDividersEnabled(false);

        mListview.setAdapter(mSearchHistoryInfoCommonAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RxBus.getDefault().post(new SearchHisotryEvent(mSearchHistoryInfos.get(position).content));
            }
        });
        refreshData();
    }

    private void refreshData() {
        mSearchHistoryInfos.clear();
        mSearchHistoryInfos.addAll(SpHistoryStorage.getInstance(getActivity(), 10).sortHistory());
        if (mSearchHistoryInfos.isEmpty()) {
            mListview.removeFooterView(mFooterView);
        }
        mSearchHistoryInfoCommonAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
