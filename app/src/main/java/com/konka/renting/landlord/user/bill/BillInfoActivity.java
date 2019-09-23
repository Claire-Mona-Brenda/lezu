package com.konka.renting.landlord.user.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        Subscription subscription = SecondRetrofitHelper.getInstance().getBillDetail(id)
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
                            tvMoney.setText("￥" + bean.getAmount());
                            tvTime.setText(bean.getCreate_time());
                            tvNumber.setText(bean.getOrder_no());
                            tvType.setText(switchType(bean.getType()));
                            if (bean.getStart_date() != null)
                                tvEndtime.setText(bean.getStart_date() + "至" + bean.getEnd_date());
                            tvMore.setText(bean.getRemark());
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
        }
        return s;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
