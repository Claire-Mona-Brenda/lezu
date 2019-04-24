package com.konka.renting.landlord.order;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.landlord.user.bill.BillVPAdapter;
import com.konka.renting.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 */
public class OrderFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tab_order)
    TabLayout mTabOrder;
    @BindView(R.id.vp_order)
    ViewPager mVpOrder;
    Unbinder unbinder;

    public static OrderFragment newInstance() {

        Bundle args = new Bundle();

        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        ViewGroup viewGroup= (ViewGroup) tvTitle.getParent();
        viewGroup.setPadding(viewGroup.getPaddingLeft(),viewGroup.getPaddingTop()+UIUtils.getStatusHeight(),viewGroup.getPaddingRight(),viewGroup.getPaddingBottom());
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void init() {
        super.init();
        initTab();

    }
    private void initTab() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> listTab = new ArrayList<>();
        listTab.add(getString(R.string.apply_ing));
        listTab.add(getString(R.string.check_in));
        listTab.add(getString(R.string.check_out));
        listTab.add(getString(R.string.expire));
        for (int i = 0; i < listTab.size(); i++) {
            mTabOrder.addTab(mTabOrder.newTab().setText(listTab.get(0)));
        }
        fragmentList.add(ApplyFragment.newInstance());  // 申请中
        fragmentList.add(CheckinFragment.newInstance());   // 入住中
        fragmentList.add(CheckoutFragment.newInstance());
        fragmentList.add(ExpireFragment.newInstance());
        FragmentManager manager = getActivity().getSupportFragmentManager();
        BillVPAdapter adapter = new BillVPAdapter(manager, fragmentList, listTab);
        mVpOrder.setAdapter(adapter);
        mTabOrder.setupWithViewPager(mVpOrder);
        mVpOrder.setOffscreenPageLimit(listTab.size());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
