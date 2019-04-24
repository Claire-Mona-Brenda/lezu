package com.konka.renting.landlord.home.copy;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class CopyDesActivity extends BaseActivity {


    @BindView(R.id.iv_icon)
    ImageView mIvIcon;
//    @BindView(R.id.tv_title)
    TextView mTvItemTitle;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.tv_last_water)
    TextView mTvLastWater;
    @BindView(R.id.tv_current_water)
    EditText mTvCurrentWater;
    @BindView(R.id.tv_last_electricity)
    TextView mTvLastElectricity;
    @BindView(R.id.tv_current_electricity)
    EditText mTvCurrentElectricity;

    @BindView(R.id.relative)
    RelativeLayout mRelativeLayout;

    private OrderInfo mOrderInfo;

    public static void toActivity(Context context, OrderInfo orderInfo) {
        Intent intent = new Intent(context, CopyDesActivity.class);
        setIntentData(intent, orderInfo);

        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_copy_des;
    }

    @Override
    public void init() {
        setTitleText(R.string.copy_title);

        mOrderInfo = getIntentData(OrderInfo.class);

        mTvItemTitle = mRelativeLayout.findViewById(R.id.tv_title);

        RoomInfo roomInfo = mOrderInfo.roomInfo;
        mTvItemTitle.setText(roomInfo.getRoomTitle());
        String date = getString(R.string.roominfo_date_time);
        date = String.format(date, mOrderInfo.start_time, mOrderInfo.end_time);
        mTvDate.setText(date);
        Picasso.get().load(roomInfo.image).into(mIvIcon);
        getInfo();
    }

    private void getInfo() {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().getReadingInfo(mOrderInfo.roomInfo.id)
                .compose(RxUtil.<DataInfo<OrderInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<OrderInfo>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<OrderInfo> orderInfoDataInfo) {
                        dismiss();
                        if (orderInfoDataInfo.success()) {
                            mOrderInfo.start_electric = orderInfoDataInfo.data().start_electric;
                            mOrderInfo.start_water = orderInfoDataInfo.data().start_water;
                            bind();
                        } else {
                            showToast(orderInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void commitInfo() {
        if (UIUtils.showHint(mTvCurrentWater, mTvCurrentElectricity)) {
            return;
        }

        showLoadingDialog();
        String end_water = mTvCurrentWater.getText().toString();
        String end_electric = mTvCurrentElectricity.getText().toString();

        Subscription subscription = RetrofitHelper.getInstance().addHydropowerOrder(mOrderInfo.merge_order_no, end_electric, end_water)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        dismiss();
                        if (info.success()) {
                            doSuccess();
                            finish();
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void bind() {
        mTvLastWater.setText(mOrderInfo.start_water);
        mTvLastElectricity.setText(mOrderInfo.start_electric);
    }


    @OnClick({R.id.iv_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_commit:
                commitInfo();
                break;
        }
    }


}
