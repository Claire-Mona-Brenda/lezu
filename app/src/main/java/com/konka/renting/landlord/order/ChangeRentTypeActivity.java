package com.konka.renting.landlord.order;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OrderBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class ChangeRentTypeActivity extends BaseActivity {

    @BindView(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.icon_room)
    ImageView mIconRoom;
    @BindView(R.id.tv_address)
    TextView mRoomName;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tv_pay_type)
    TextView mTvPayType;
    @BindView(R.id.tv_last_time)
    TextView mTvLastTime;
    @BindView(R.id.tv_next_time)
    TextView mtvNextTime;
    private OrderBean orderBean;

    public static void toActivity(Context context, OrderBean orderBean) {
        Intent intent = new Intent(context, ChangeRentTypeActivity.class);
        intent.putExtra("orderbean",orderBean);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_rent_type;
    }

    @Override
    public void init() {

        setTitleText(R.string.text_change);
        orderBean = (OrderBean) getIntent().getSerializableExtra("orderbean");
        initData();
    }

    private void initData() {
        mTvOrderNumber.setText(orderBean.getMerge_order_no());
        mTvStatus.setText(orderBean.getStatus_name());
        Picasso.get().load(orderBean.getRoomInfo().getImage()).into(mIconRoom);
        mRoomName.setText(orderBean.getRoomInfo().getRoom_name());
        mTvTime.setText(orderBean.getStart_time()+"-"+orderBean.getEnd_time());
        mTvMoney.setText(orderBean.getRoomInfo().getHousing_price());
        if (orderBean.getRenting_mode().equals("0"))
            mTvPayType.setText("线下支付");
        else
            mTvPayType.setText("线上支付");
        mTvLastTime.setText(orderBean.getStart_time());
        mtvNextTime.setText(orderBean.getEnd_time());


    }

    @OnClick({R.id.iv_back, R.id.btn_agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_agree:
                changePayType();
                break;
        }
    }
    private void changePayType(){
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().changepayType(orderBean.getMerge_order_no(),1)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()){
                            RxBus.getDefault().post(new ChangeRentTypeActivity());
                            finish();
                        }else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
