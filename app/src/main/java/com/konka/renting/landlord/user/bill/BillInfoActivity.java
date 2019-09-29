package com.konka.renting.landlord.user.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.BillDetailBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class BillInfoActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.activityt_bill_info_tv_money)
    TextView tvMoney;
    @BindView(R.id.activityt_bill_info_tv_time)
    TextView tvTime;
    @BindView(R.id.activity_info_tv_number)
    TextView tvNumber;
    @BindView(R.id.activity_info_tv_type)
    TextView tvType;
    @BindView(R.id.activity_info_tv_endtime)
    TextView tvEndtime;
    @BindView(R.id.activity_info_tv_more)
    TextView tvMore;

    String id;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activityt_bill_info_tv_way)
    TextView mTvWay;
    @BindView(R.id.activityt_bill_info_tv_status)
    TextView mTvStatus;
    @BindView(R.id.activityt_bill_info_rl_status)
    RelativeLayout mRlStatus;
    @BindView(R.id.activity_info_rl_endtime)
    RelativeLayout mRlEndtime;
    @BindView(R.id.activity_info_tv_belance)
    TextView tvBelance;
    @BindView(R.id.activity_info_rl_belance)
    RelativeLayout rlBelance;

    public static void toActivity(Context context, String id) {
        Intent intent = new Intent(context, BillInfoActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill_info;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.bill_info);
        id = getIntent().getStringExtra("id");
        getData();
    }

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getBillDetail2(id)
                .compose(RxUtil.<DataInfo<BillDetailBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<BillDetailBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<BillDetailBean> dataInfo) {
                        super.onNext(dataInfo);
                        if (dataInfo.success()) {
                            BillDetailBean bean = dataInfo.data();
                            tvMoney.setText(switchPay(bean.getType())+bean.getAmount());
                            tvBelance.setText(getString(R.string.money_unit_1) + bean.getBalance());
                            tvTime.setText(bean.getCreate_time());
                            tvNumber.setText(bean.getOrder_no());
                            mTvWay.setText(switchType(bean.getType()));
                            tvType.setText(payType(bean.getType()));
                            tvMore.setText(bean.getRemark());
                            if (bean.getType()==5){
                                mRlStatus.setVisibility(View.VISIBLE);
                                mTvStatus.setText(switchStatus(bean.getStatus()));
                            }else{
                                mRlStatus.setVisibility(View.GONE);
                            }
                        } else {
                            showToast(dataInfo.msg());
                            finish();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private String switchType(int type) {
        String s = "";
        switch (type) {
            case 1:
                s = getString(R.string.bill_info_type_1);
                break;
            case 2:
                s = getString(R.string.bill_info_type_2);
                break;
            case 3:
                s = getString(R.string.bill_info_type_3);
                break;
            case 4:
                s = getString(R.string.bill_info_type_4);
                break;
            case 5:
                s = getString(R.string.bill_info_type_5);
                break;
            case 6:
                s = getString(R.string.bill_info_type_6);
                break;
        }
        return s;
    }

    private String payType(int type) {
        String s = "";
        switch (type) {
            case 1://充值
            case 4://服务费退款
            case 6://房租
                s = getString(R.string.bill_info_pay_type_1);
                break;
            case 2://服务费支付
            case 3://安装费
            case 5://提现
                s = getString(R.string.bill_info_pay_type_2);
                break;
        }
        return s;
    }

    private String switchStatus(int status) {
        String s = "";
        switch (status) {
            case 0:
                s = getString(R.string.bill_info_get_status_1);
                break;
            case 1:
                s = getString(R.string.bill_info_get_status_2);
                break;
            case 2:
                s = getString(R.string.bill_info_get_status_3);
                break;

        }
        return s;
    }

    private String switchPay(int type) {
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


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
