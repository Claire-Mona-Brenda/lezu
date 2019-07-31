package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.TenantOrderDetailBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class ApplyCheckoutActivity extends BaseActivity {


    @BindView(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.icon_room)
    ImageView mIconRoom;
    @BindView(R.id.tv_long)
    TextView mTvLong;
    @BindView(R.id.tv_short)
    TextView mTvShort;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.et_checkout_reson)
    EditText mEtCheckoutReson;
    String ordernum;
    private TenantOrderDetailBean orderDetailBean;

    public static void toActivity(Context context, TenantOrderDetailBean data) {
        Intent intent = new Intent(context, ApplyCheckoutActivity.class);
        intent.putExtra("orderdetail",data);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_apply_checkout;
    }

    @Override
    public void init() {
        setTitleText("申请退房");
        orderDetailBean = (TenantOrderDetailBean) getIntent().getSerializableExtra("orderdetail");
        ordernum = orderDetailBean.getInfo().getMerge_order_no();
        mTvOrderNumber.setText(ordernum);
        mTvStatus.setText(orderDetailBean.getInfo().getState_name());
        Picasso.get().load(orderDetailBean.getInfo().getRoomInfo().getImage()).into(mIconRoom);
        if (orderDetailBean.getInfo().getRoomInfo().getType().equals("1")){
            mTvLong.setVisibility(View.GONE);
            mTvShort.setVisibility(View.VISIBLE);
        }else{
            mTvLong.setVisibility(View.VISIBLE);
            mTvShort.setVisibility(View.GONE);
        }
        mTvAddress.setText(orderDetailBean.getInfo().getRoomInfo().getRoom_name());
        mTvTime.setText(orderDetailBean.getInfo().getStart_time()+"-"+orderDetailBean.getInfo().getEnd_time());
        mTvMoney.setText("￥"+orderDetailBean.getInfo().getHousing_price());
    }

    @OnClick({R.id.iv_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_commit:
                applyCheckout();
                break;
        }
    }
    private void applyCheckout(){
//        showLoadingDialog();
//        Subscription subscription = RetrofitHelper.getInstance().updateRenting(ordernum,mEtCheckoutReson.getText().toString(),"2")
//                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
//                        showError(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(DataInfo dataInfo) {
//
//                        dismiss();
//                        if (dataInfo.success()){
//                            showToast(dataInfo.msg());
//                            RxBus.getDefault().post(new UpdateRentingEvent());
//                            finish();
//                        }else {
//                            showToast(dataInfo.msg());
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
    }
}
