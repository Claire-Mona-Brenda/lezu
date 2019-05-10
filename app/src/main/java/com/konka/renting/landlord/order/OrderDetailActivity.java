package com.konka.renting.landlord.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RenterOrderInfoBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.tv_wait_confirm)
    TextView mTvStatus;
    @BindView(R.id.tv_order_num)
    TextView mTvOrderNum;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.icon_room)
    ImageView mIconRoom;
    @BindView(R.id.tv_address)
    TextView mTvRoomName;
    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.tv_time_expire)
    TextView mTvTimeExpire;

    @BindView(R.id.tv_mark)
    TextView mTvMark;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.view_long)
    LinearLayout mViewLong;


    @BindView(R.id.tv_short_mark)
    TextView mTvShortMark;
    @BindView(R.id.view_short)
    LinearLayout mViewShort;
    @BindView(R.id.tv_service_fee)
    TextView mTvServiceFee;
    @BindView(R.id.tv_service_time)
    TextView mTvServiceTime;
    @BindView(R.id.tv_rent_type_hint)
    TextView mTvTypeHint;
    @BindView(R.id.tv_rent_type)
    TextView mTvType;
    @BindView(R.id.tv_rent_money_hint)
    TextView mTvRentMoneyHint;
    @BindView(R.id.tv_rent_money)
    TextView mTvRentMoney;
    @BindView(R.id.lin_landord)
    LinearLayout mLinLandord;
    @BindView(R.id.iv_grid_image)
    LinearLayout mIvGridImage;
    @BindView(R.id.tv_tenant)
    TextView tvTenant;
    @BindView(R.id.tv_tenant_num)
    TextView mTvTenantNum;
    @BindView(R.id.re_tenant)
    RelativeLayout mReTenant;
    @BindView(R.id.re_confirm)
    LinearLayout mReConfirm;
    @BindView(R.id.tv_phone)
    TextView mTvPhone;
    @BindView(R.id.tv_landlord_name)
    TextView mTvLandordName;
    private String order_id;
    RenterOrderInfoBean orderDetailInfo;
    private String type;
    private String order_no;
    private String apply_status;
    private OrderDetailTenanterDesDialog mTenanterDesDialog;

    public static void toActivity(Context context, String order_id, String type, String order_no, String apply_status) {

        Intent intent = new Intent(context, OrderDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("order_id", order_id);
        bundle.putString("type", type);
        bundle.putString("order_no", order_no);
        bundle.putString("apply", apply_status);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void init() {
        setTitleText(R.string.order_detail);
        Bundle bundle = getIntent().getExtras();
        order_id = bundle.getString("order_id");
        type = bundle.getString("type");
        order_no = bundle.getString("order_no");
        apply_status = bundle.getString("apply");
        getOrderDetail();
        addRxBusSubscribe(UpdateCheckOutEvent.class, new Action1<UpdateCheckOutEvent>() {
            @Override
            public void call(UpdateCheckOutEvent updateCheckOutEvent) {
                finish();
            }
        });
    }

    private void getOrderDetail() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordOrderInfo(order_id)
                .compose(RxUtil.<DataInfo<RenterOrderInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RenterOrderInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<RenterOrderInfoBean> orderDetailBeanDataInfo) {

                        dismiss();
                        if (orderDetailBeanDataInfo.success()) {
                            setData(orderDetailBeanDataInfo.data());
                        } else {
                            showToast(orderDetailBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void setData(final RenterOrderInfoBean orderDetailBeanDataInfo) {
        if (type.equals("1")) {
            mViewLong.setVisibility(View.GONE);
            mViewShort.setVisibility(View.VISIBLE);
            mTvType.setText(R.string.short_rent);
        } else {
            mViewLong.setVisibility(View.GONE);
            mViewShort.setVisibility(View.VISIBLE);
            mTvType.setText(R.string.long_rent);
        }
        if (orderDetailBeanDataInfo.getMember().size() > 0) {
            mTvTenantNum.setText(orderDetailBeanDataInfo.getMember().size() + "");
            if (orderDetailBeanDataInfo.getMember() == null)
                return;
            for (final RenterOrderInfoBean.MemberBean url : orderDetailBeanDataInfo.getMember()) {
                CircleImageView imageView = new CircleImageView(mActivity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UIUtils.dip2px(45), UIUtils.dip2px(45));
                lp.setMargins(UIUtils.dip2px(6), 0, 0, 0);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (!TextUtils.isEmpty(url.getThumb_headimgurl()))
                    Picasso.get().load(url.getThumb_headimgurl()).error(R.mipmap.fangchan_jiazai).into(imageView);
                else
                    Picasso.get().load(R.mipmap.touxiang).into(imageView);
                mIvGridImage.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toDes(url);
                    }
                });

            }
        } else
            mTvTenantNum.setText("0");
        String tel = orderDetailBeanDataInfo.getLandlord().getPhone();
        if (!tel.equals("")) {
            int len = tel.length();
            String str = tel.substring(0, 3);
            for (int i = 3; i < len - 2; i++) {
                str += "*";
            }
            str += tel.substring(len - 2, len);
            tel = str;
        }
        mTvPhone.setText(tel);
        mTvLandordName.setText(orderDetailBeanDataInfo.getLandlord().getReal_name());
        mTvStatus.setText(getStringStatus(orderDetailBeanDataInfo.getStatus()));
        mTvOrderNum.setText(orderDetailBeanDataInfo.getOrder_no());
        mTvTime.setText(orderDetailBeanDataInfo.getCreate_time());
        if (!TextUtils.isEmpty(orderDetailBeanDataInfo.getThumb_image()))
            Picasso.get().load(orderDetailBeanDataInfo.getThumb_image()).into(mIconRoom);
        else
            Picasso.get().load(R.mipmap.fangchan_jiazai).into(mIconRoom);
        mTvRoomName.setText(orderDetailBeanDataInfo.getRoom_name());
        mTvStartTime.setText(orderDetailBeanDataInfo.getStart_time());
        mTvEndTime.setText(orderDetailBeanDataInfo.getEnd_time());
        String unit = orderDetailBeanDataInfo.getType() == 1 ? "元/天" : "元/月";
        mTvRentMoney.setText((int) Float.parseFloat(orderDetailBeanDataInfo.getHousing_price()) + unit);
        mTvServiceTime.setText(orderDetailBeanDataInfo.getService_date());

        mTvShortMark.setText(orderDetailBeanDataInfo.getRemark());
        mTvMark.setText(orderDetailBeanDataInfo.getRemark());
        if (orderDetailBeanDataInfo.getStatus() == 1) {
            mTvCancel.setVisibility(View.GONE);
            mTvConfirm.setVisibility(View.GONE);
            mLinLandord.setVisibility(View.VISIBLE);
            mTvServiceFee.setVisibility(View.GONE);
            mTvServiceTime.setVisibility(View.GONE);
            mReTenant.setVisibility(View.VISIBLE);
        }


        if (orderDetailBeanDataInfo.getStatus() == 2) {

            mTvServiceFee.setVisibility(View.VISIBLE);
            mTvServiceTime.setVisibility(View.VISIBLE);
            mLinLandord.setVisibility(View.VISIBLE);
            mReTenant.setVisibility(View.VISIBLE);
            if (type.equals("1")) {
                mReConfirm.setVisibility(View.GONE);
            } else {
                mReConfirm.setVisibility(View.VISIBLE);
                mTvCancel.setVisibility(View.GONE);
                mTvConfirm.setVisibility(View.GONE);
                mTvCancel.setText(R.string.income_history);
                mTvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CollectRentActivity.toActivity(OrderDetailActivity.this, orderDetailBeanDataInfo.getOrder_id());
                    }
                });
            }

        }
        if (orderDetailBeanDataInfo.getStatus() == 3) {
            mTvServiceFee.setVisibility(View.VISIBLE);
            mTvServiceTime.setVisibility(View.VISIBLE);
            mReConfirm.setVisibility(View.VISIBLE);
            mTvCancel.setVisibility(View.GONE);
            mLinLandord.setVisibility(View.VISIBLE);
            mReTenant.setVisibility(View.VISIBLE);
//            mTvCancel.setText(R.string.income_history);
//            mTvCancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    CollectRentActivity.toActivity(OrderDetailActivity.this, orderDetailBeanDataInfo.getOrder_id());
//                }
//            });
            mTvConfirm.setVisibility(View.GONE);
            mTvConfirm.setText(R.string.confirm_apply_checkout);
            mTvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    RefundsActivity.toDetailActivity(OrderDetailActivity.this, orderDetailBeanDataInfo, "detail");
                }
            });
        }
        if (orderDetailBeanDataInfo.getStatus() == 4) {

            mTvServiceFee.setVisibility(View.VISIBLE);
            mTvServiceTime.setVisibility(View.VISIBLE);
            mReConfirm.setVisibility(View.GONE);
            if (type.equals("1")) {
                mLinLandord.setVisibility(View.GONE);
                mReTenant.setVisibility(View.GONE);
            } else {
                mTvCancel.setVisibility(View.GONE);
                mTvConfirm.setVisibility(View.GONE);
                mLinLandord.setVisibility(View.VISIBLE);
                mReTenant.setVisibility(View.VISIBLE);
//                mTvCancel.setText(R.string.income_history);
//                mTvCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        CollectRentActivity.toActivity(OrderDetailActivity.this, orderDetailBeanDataInfo.data().getInfo().getMerge_order_no());
//                    }
//                });
            }

        }

    }

    private void toDes(RenterOrderInfoBean.MemberBean memberList) {
        if (mTenanterDesDialog == null) {
            mTenanterDesDialog = new OrderDetailTenanterDesDialog(mActivity);
        }
        mTenanterDesDialog.show(memberList);
    }


    private void updateOrder(String orderNum, int state) {

        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().updateRenting(orderNum, state)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new ConfirmEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void confirmOrder(String orderNum, int state) {

        Log.e("ordernum:", orderNum);
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().updateRenting(orderNum, state)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new ConfirmEvent());
                            getOrderDetail();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    private void confirmRenting(String order_no) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().confirmRenting(order_no)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new UpdateRentingEvent());
                            RxBus.getDefault().post(new ConfirmEvent());
                            finish();
                        } else
                            showToast(dataInfo.msg());
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick({R.id.iv_back, R.id.tv_confirm, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_confirm:
                break;
            case R.id.tv_cancel:
                break;
        }
    }

    private void confirm(String order_no) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().confirmRenting(order_no)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            finish();
                            RxBus.getDefault().post(new UpdateRentingEvent());
                        } else
                            showToast(dataInfo.msg());
                    }
                });
        addSubscrebe(subscription);
    }

    private String getStringStatus(int status) {
        String s = "";
        switch (status) {
            case 1:
                s = getString(R.string.order_status_1);
                break;
            case 2:
                s = getString(R.string.order_status_2);
                break;
            case 3:
                s = getString(R.string.order_status_3);
                break;
            case 4:
                s = getString(R.string.order_status_4);
                break;
            case 5:
                s = getString(R.string.order_status_5);
                break;
            case 6:
                s = getString(R.string.order_status_6);
                break;
            case 7:
                s = getString(R.string.order_status_7);
                break;

        }
        return s;
    }

}
