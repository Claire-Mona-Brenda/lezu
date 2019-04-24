package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.tenant.payrent.view.PayHouseAdapter;
import com.konka.renting.tenant.payrent.view.PayHouseAdapter2;
import com.konka.renting.tenant.payrent.view.PayHouseAdapter3;
import com.konka.renting.tenant.payrent.view.PayHouseAdapter4;
import com.konka.renting.tenant.payrent.view.PayViewPager;
import com.konka.renting.tenant.payrent.view.RenewEvent;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by jbl on 18-3-19.
 */

public class PayRentPresent extends BasePresenter {
    RecyclerView recyclerView;
    TabLayout mTab;
    Context context;
    String status = "0";
    int curpoi;
    List<String> title = new ArrayList<>();
    ViewPager viewPager;
    int curPage = 1;

    public void initView(View view) {

        context = view.getContext();
        viewPager = view.findViewById(R.id.viewpager);
        mTab = view.findViewById(R.id.tab_pay);
        mTab.addTab(mTab.newTab().setText(context.getString(R.string.apply_ing)));
        mTab.addTab(mTab.newTab().setText(context.getString(R.string.check_in)));
        mTab.addTab(mTab.newTab().setText(context.getString(R.string.check_out)));
        mTab.addTab(mTab.newTab().setText(context.getString(R.string.expire)));
        title.add(mTab.getTabAt(0).getText().toString());
        title.add(mTab.getTabAt(1).getText().toString());
        title.add(mTab.getTabAt(2).getText().toString());
        title.add(mTab.getTabAt(3).getText().toString());
        mTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                status = tab.getPosition() + "";
                curpoi = tab.getPosition();
                curPage = 1;
                getData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setOffscreenPageLimit(title.size());

        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            View v = getView(i + "", view.getContext());
            viewList.add(v);
        }
        PayViewPager payViewPager = new PayViewPager(viewList, title);
        viewPager.setAdapter(payViewPager);
        mTab.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        mTab.setTabsFromPagerAdapter(payViewPager);//给Tabs设置适配器
//        getData();


    }

    public void getData() {
        int type = Integer.valueOf(status) + 1;
        Subscription subscription = (SecondRetrofitHelper.getInstance().getRenterOrderList(type + "", curPage + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterOrderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterOrderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterOrderListBean>> homeInfoDataInfo) {
                        if (homeInfoDataInfo.success()) {
                            bindData(homeInfoDataInfo.data().getList());

                        }
                        ShowToastUtil.dismiss();
                    }
                }));
        addSubscrebe(subscription);

    }

    public void getDatamore() {
        curPage++;
        int type = Integer.valueOf(status) + 1;
        Subscription subscription = (SecondRetrofitHelper.getInstance().getRenterOrderList(type + "", curPage + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterOrderListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterOrderListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        curPage--;
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterOrderListBean>> homeInfoDataInfo) {
                        if (homeInfoDataInfo.success()) {
                            if (homeInfoDataInfo.data().getTotalPage() < homeInfoDataInfo.data().getPage())
                                curPage--;
                            bindMoreData(homeInfoDataInfo.data().getList());
                        } else {
                            curPage--;
                            ShowToastUtil.showNormalToast(context, homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);

    }

    PayHouseAdapter payHouseAdapter0;
    PayHouseAdapter2 payHouseAdapter1;
    PayHouseAdapter3 payHouseAdapter2;
    PayHouseAdapter4 payHouseAdapter3;
    List<RenterOrderListBean> list0 = new ArrayList<>();
    List<RenterOrderListBean> list1 = new ArrayList<>();
    List<RenterOrderListBean> list2 = new ArrayList<>();
    List<RenterOrderListBean> list3 = new ArrayList<>();

    public void bindData(List<RenterOrderListBean> list) {
        if (status.equals("0")) {
            list0.clear();
            list0.addAll(list);
            payHouseAdapter0.notifyDataSetChanged();
        } else if (status.equals("1")) {
            list1.clear();
            list1.addAll(list);
            payHouseAdapter1.notifyDataSetChanged();
        }
        if (status.equals("2")) {
            list2.clear();
            list2.addAll(list);
            payHouseAdapter2.notifyDataSetChanged();
        }
        if (status.equals("3")) {
            list3.clear();
            list3.addAll(list);
            payHouseAdapter3.notifyDataSetChanged();
        }
    }

    public void bindMoreData(List<RenterOrderListBean> list) {
        if (status.equals("0")) {
            list0.addAll(list);
            payHouseAdapter0.notifyDataSetChanged();
        } else if (status.equals("1")) {
            list1.addAll(list);
            payHouseAdapter1.notifyDataSetChanged();
        }
        if (status.equals("2")) {
            list2.addAll(list);
            payHouseAdapter2.notifyDataSetChanged();
        }
        if (status.equals("3")) {
            list3.addAll(list);
            payHouseAdapter3.notifyDataSetChanged();
        }
    }

    public View getView(String st, Context ctx) {
        View parent = LayoutInflater.from(ctx).inflate(R.layout.lib_payrent_pager, null);
        final SmartRefreshLayout refreshLayout = parent.findViewById(R.id.refreshLayout);
        recyclerView = parent.findViewById(R.id.listView1);
        LinearLayoutManager lm = new LinearLayoutManager(parent.getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        if (st.equals("0")) {
            payHouseAdapter0 = new PayHouseAdapter(context, list0);
            payHouseAdapter0.setRefresh(new PRefresh() {
                @Override
                public void refresh() {
                    refreshLayout.autoRefresh();
                }
            });
            recyclerView.setAdapter(payHouseAdapter0);
        } else if (st.equals("1")) {
            payHouseAdapter1 = new PayHouseAdapter2(context, list1);
            payHouseAdapter1.setRefresh(new PRefresh() {
                @Override
                public void refresh() {
                    refreshLayout.autoRefresh();
                }
            });
            recyclerView.setAdapter(payHouseAdapter1);
            addRxBusSubscribe(UpdateRentingEvent.class, new Action1<UpdateRentingEvent>() {
                @Override
                public void call(UpdateRentingEvent updateRentingEvent) {
                    refreshLayout.autoRefresh();
                }
            });
        }
        if (st.equals("2")) {
            payHouseAdapter2 = new PayHouseAdapter3(context, list2);
            payHouseAdapter2.setRefresh(new PRefresh() {
                @Override
                public void refresh() {
                    refreshLayout.autoRefresh();
                }
            });
            payHouseAdapter2.setItemClickListener(new PayHouseAdapter3.MissionitemClick() {
                @Override
                public void missionItemClick(int position) {

                }
            });
            recyclerView.setAdapter(payHouseAdapter2);
            addRxBusSubscribe(UpdateRentingEvent.class, new Action1<UpdateRentingEvent>() {
                @Override
                public void call(UpdateRentingEvent updateRentingEvent) {
                    refreshLayout.autoRefresh();
                }
            });
        }
        if (st.equals("3")) {
            payHouseAdapter3 = new PayHouseAdapter4(context, list3);
            addRxBusSubscribe(RenewEvent.class, new Action1<RenewEvent>() {
                @Override
                public void call(RenewEvent renewEvent) {
                    refreshLayout.autoRefresh();
                }
            });
            payHouseAdapter3.setRefresh(new PRefresh() {
                @Override
                public void refresh() {
                    refreshLayout.autoRefresh();
                }
            });
            recyclerView.setAdapter(payHouseAdapter3);
        }
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
                refreshlayout.finishRefresh();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getDatamore();
                refreshlayout.finishLoadmore();

            }
        });
        return parent;
    }


    public interface PRefresh {
        public void refresh();
    }
}
