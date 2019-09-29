package com.konka.renting.landlord.user.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.BillListBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class BillTypeActivity extends BaseActivity {
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
    @BindView(R.id.activity_bill_type_rv_list)
    RecyclerView mRvList;
    @BindView(R.id.activity_bill_type_srl_list)
    SmartRefreshLayout mSrlList;

    CommonAdapter<BillListBean> commonAdapter;
    List<BillListBean> listBeans = new ArrayList<>();
    String type, year, month;
    int page = 1;

    public static void toActivity(Context context, String type, String year, String month) {
        Intent intent = new Intent(context, BillTypeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill_type;
    }

    @Override
    public void init() {
        type = getIntent().getStringExtra("type");
        year = getIntent().getStringExtra("year");
        month = getIntent().getStringExtra("month");

        String title = "";
        if (type.equals("1")) {
            title = getString(R.string.bill_info_pay);
        } else if (type.equals("5")) {
            title = getString(R.string.bill_info_get);
        } else {
            title = getString(R.string.bill_info_house);
        }
        tvTitle.setText(title);


        commonAdapter = new CommonAdapter<BillListBean>(mActivity, listBeans, R.layout.adapter_bill_list_item) {
            @Override
            public void convert(ViewHolder viewHolder, BillListBean bean) {

                viewHolder.setText(R.id.adapter_tv_type, bean.getTitle());
                String str = payType(bean.getType());
                if (str.equals("-")) {
                    viewHolder.setTextColor(R.id.adapter_tv_money, mContext.getResources().getColor(R.color.text_ren));
                } else {
                    viewHolder.setTextColor(R.id.adapter_tv_money, mContext.getResources().getColor(R.color.text_green));
                }
                viewHolder.setText(R.id.adapter_tv_money, str + bean.getAmount());
                viewHolder.setText(R.id.adapter_tv_date, bean.getCreate_time());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BillInfoActivity.toActivity(mActivity, bean.getId());
                    }
                });
            }
        };
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.setAdapter(commonAdapter);

        mSrlList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }
        });
        mSrlList.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });

        mSrlList.autoRefresh();

    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }


    private String payType(int type) {
        String s = "";
        switch (type) {
            case 1://充值
            case 4://服务费退款
            case 6://房租
                s = "+";
                break;
            case 2://服务费支付
            case 3://安装费
            case 5://提现
                s = "-";
                break;
        }
        return s;
    }

    /*******************************************************接口*****************************************/
    private void getData(boolean isRe) {
        if (isRe) {
            page = 1;
        } else {
            page++;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getAccountBillList2(page + "", type, year, month)
                .compose(RxUtil.<DataInfo<PageDataBean<BillListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<BillListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (isRe) {
                            mSrlList.finishRefresh();
                        } else {
                            mSrlList.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<BillListBean>> dataInfo) {
                        if (dataInfo.success()) {
                            listBeans.clear();
                            listBeans.addAll(dataInfo.data().getList());
                            commonAdapter.notifyDataSetChanged();
                            mSrlList.setEnableLoadmore(false);
                        } else {
                            if (!isRe)
                                page--;
                            showToast(dataInfo.msg());
                        }
                        if (isRe) {
                            mSrlList.finishRefresh();
                        } else {
                            mSrlList.finishLoadmore();
                        }

                    }
                });
        addSubscrebe(subscription);
    }

}
