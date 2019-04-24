package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.RenterOrderInfoBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.user.manager.TenantManagerActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class TenantOrderDetailActivity extends BaseActivity {

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

    @BindView(R.id.tv_rent_month)
    TextView mTvRentMonth;
    @BindView(R.id.view_long)
    LinearLayout mViewLong;

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

    @BindView(R.id.tv_tenant_manager)
    TextView mTvTenantManager;
    @BindView(R.id.lin_checkin)
    LinearLayout mLinCheckin;


    @BindView(R.id.tv_apply_check_out)
    TextView mTvApplyCheckOut;
    @BindView(R.id.tv_cancel_check_out)
    TextView mTvCancelCheckOut;
    @BindView(R.id.tv_landlord_phone)
    TextView mTvLandlordPhone;
    @BindView(R.id.tv_cancel_apply)
    TextView mTvCancelApply;
    @BindView(R.id.tv_landlord_name)
    TextView mTvLandlordName;
    @BindView(R.id.tv_pay_type_checkin)
    TextView mTvPayTypeCheckin;
    @BindView(R.id.tv_public_joint_rent)
    TextView mTvPublicJointRent;
    @BindView(R.id.tv_cancel_joint_rent)
    TextView mTvCancelJointRent;
    @BindView(R.id.tv_short_mark)
    TextView mTvShortMark;


    private String order_id;
    RenterOrderInfoBean orderDetailInfo;
    private String type;
//    PayOrder payOrder;

    public static void toActivity(Context context, String order_id, String type) {

        Intent intent = new Intent(context, TenantOrderDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("order_id", order_id);
        bundle.putString("type", type);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tenant_order_detail;
    }

    @Override
    public void init() {
        setTitleText(R.string.order_detail);
        Bundle bundle = getIntent().getExtras();
        order_id = bundle.getString("order_id");
        type = bundle.getString("type");
        addRxBusSubscribe(UpdateRentingEvent.class, new Action1<UpdateRentingEvent>() {
            @Override
            public void call(UpdateRentingEvent updateRentingEvent) {
                finish();
            }
        });
        addRxBusSubscribe(PayRentRefreshEvent.class, new Action1<PayRentRefreshEvent>() {
            @Override
            public void call(PayRentRefreshEvent payRentRefreshEvent) {
                getOrderDetail();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderDetail();
    }

    private void getOrderDetail() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterOrderInfo(order_id)
                .compose(RxUtil.<DataInfo<RenterOrderInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RenterOrderInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        e.printStackTrace();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<RenterOrderInfoBean> dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            orderDetailInfo = dataInfo.data();
                            setData(orderDetailInfo);

                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void setData(final RenterOrderInfoBean dataInfo) {
        if (type.equals("1")) {
            mViewLong.setVisibility(View.GONE);
            mTvType.setText(R.string.short_rent);
        } else {
            mViewLong.setVisibility(View.GONE);
            mTvType.setText(R.string.long_rent);
        }

        if (dataInfo.getMember().size() > 0) {
            mTvTenantNum.setText(dataInfo.getMember().size() + "");

            if (dataInfo.getMember() == null)
                return;
            int size = dataInfo.getMember().size();
            mIvGridImage.removeAllViews();
            for (int i = 0; i < size; i++) {
                CircleImageView imageView = new CircleImageView(mActivity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UIUtils.dip2px(45), UIUtils.dip2px(45));
                lp.setMargins(UIUtils.dip2px(5), 0, 0, 0);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (!dataInfo.getMember().get(i).getThumb_headimgurl().equals(""))
                    Picasso.get().load(dataInfo.getMember().get(i).getThumb_headimgurl()).into(imageView);
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).into(imageView);
                mIvGridImage.addView(imageView);
            }
        } else
            mTvTenantNum.setText("0");
        mTvShortMark.setText(dataInfo.getRemark());
        String tel = dataInfo.getLandlord().getPhone();
        if (!tel.equals("")) {
            int len = tel.length();
            String str = tel.substring(0, 3);
            for (int i = 3; i < len - 2; i++) {
                str += "*";
            }
            str += tel.substring(len - 2, len);
            tel = str;
        }
        mTvLandlordPhone.setText(tel);
        String status = "";
        switch (dataInfo.getStatus()) {
            case 1:
                status = getString(R.string.order_status_1);
                break;
            case 2:
                status = getString(R.string.order_status_2);
                break;
            case 3:
                status = getString(R.string.order_status_3);
                break;
            case 4:
                status = getString(R.string.order_status_4);
                break;
            case 5:
                status = getString(R.string.order_status_5);
                break;
            case 6:
                status = getString(R.string.order_status_6);
                break;
            case 7:
                status = getString(R.string.order_status_7);
                break;
        }
        mTvStatus.setText(status);
        mTvOrderNum.setText(dataInfo.getOrder_no());
        mTvTime.setText(dataInfo.getCreate_time());
        if (!TextUtils.isEmpty(dataInfo.getThumb_image()))
            Picasso.get().load(dataInfo.getThumb_image()).into(mIconRoom);
        mTvRoomName.setText(dataInfo.getRoom_name());
        mTvStartTime.setText(dataInfo.getStart_time());
        mTvEndTime.setText(dataInfo.getEnd_time());
        String unit = dataInfo.getType() == 1 ? "/天" : "/月";
        mTvRentMoney.setText("¥" + (int) Float.parseFloat(dataInfo.getHousing_price()) + unit);
        mTvLandlordName.setText(dataInfo.getLandlord().getReal_name());
        if (dataInfo.getRenting_mode() == 0) {

            mTvPayTypeCheckin.setText("线下支付");

        } else if (dataInfo.getRenting_mode() == 1) {

            mTvPayTypeCheckin.setText("线上支付");
        } else {

            mTvPayTypeCheckin.setText("");
        }
        mTvRentMonth.setText("¥" + (int) Float.parseFloat(dataInfo.getHousing_price()) + unit);
        mTvServiceTime.setText(dataInfo.getService_date());
        if (dataInfo.getStatus() == 1) {
            if (dataInfo.getType() != 1) {
                mTvCancelApply.setVisibility(View.GONE);
                mLinCheckin.setVisibility(View.VISIBLE);
            }
            mLinLandord.setVisibility(View.VISIBLE);
            mReTenant.setVisibility(View.GONE);
            mTvTenantManager.setVisibility(View.GONE);

        }
        if (dataInfo.getStatus() == 2) {
            mTvApplyCheckOut.setVisibility(View.VISIBLE);
            if (dataInfo.getType() == 1) {
                mTvServiceFee.setVisibility(View.GONE);
                mTvServiceTime.setVisibility(View.GONE);
            } else {
                mTvServiceFee.setVisibility(View.VISIBLE);
                mTvServiceTime.setVisibility(View.VISIBLE);
            }
        }

        if (dataInfo.getStatus() == 3) {

            mLinLandord.setVisibility(View.VISIBLE);
            mTvApplyCheckOut.setVisibility(View.VISIBLE);
            if (dataInfo.getType() == 1) {
                mTvServiceFee.setVisibility(View.GONE);
                mTvServiceTime.setVisibility(View.GONE);
                mReTenant.setVisibility(View.GONE);
            } else {
                mTvServiceFee.setVisibility(View.VISIBLE);
                mTvServiceTime.setVisibility(View.VISIBLE);
                mReConfirm.setVisibility(View.VISIBLE);
                mReTenant.setVisibility(View.VISIBLE);
                if (dataInfo.getIs_share() == 1) {//开启合租发布
                    mTvPublicJointRent.setVisibility(View.GONE);
                    mTvCancelJointRent.setVisibility(View.VISIBLE);
                } else {
                    mTvPublicJointRent.setVisibility(View.VISIBLE);
                    mTvCancelJointRent.setVisibility(View.GONE);
                }
            }

        }
        if (dataInfo.getStatus() == 4) {
            if (dataInfo.getType() == 1) {
                mTvServiceFee.setVisibility(View.GONE);
                mTvServiceTime.setVisibility(View.GONE);
            } else {
                mTvServiceFee.setVisibility(View.VISIBLE);
                mTvServiceTime.setVisibility(View.VISIBLE);
            }

            mReConfirm.setVisibility(View.VISIBLE);
            mLinLandord.setVisibility(View.VISIBLE);
            mReTenant.setVisibility(View.VISIBLE);
            mLinCheckin.setVisibility(View.VISIBLE);

            mTvCancelCheckOut.setVisibility(View.VISIBLE);
        }
        if (dataInfo.getStatus() >= 5) {
            mTvServiceFee.setVisibility(View.GONE);
            mTvServiceTime.setVisibility(View.GONE);
            mLinLandord.setVisibility(View.VISIBLE);
            mReTenant.setVisibility(View.GONE);
            mLinCheckin.setVisibility(View.VISIBLE);
            mTvTenantManager.setVisibility(View.GONE);

        }

    }


    @OnClick({R.id.iv_back, R.id.tv_apply_check_out, R.id.tv_cancel_check_out, R.id.tv_tenant_manager, R.id.tv_public_joint_rent, R.id.tv_cancel_joint_rent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_apply_check_out://申请退房
//                ApplyCheckoutActivity.toActivity(this, orderDetailInfo);
                new AlertDialog.Builder(this).setTitle(R.string.renter_apply_to_end).setMessage(R.string.renter_dialog_apply_to_end)
                        .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                applyCheckout();
                            }
                        }).setNegativeButton(R.string.warn_cancel, null).create().show();

                break;
            case R.id.tv_cancel_check_out://取消申请
                cancelCheckout();
                break;
            case R.id.tv_tenant_manager://租客管理
                TenantManagerActivity.toActivity(mActivity, orderDetailInfo.getOrder_id());
                break;
            case R.id.tv_public_joint_rent://开启合租发布
                setJointRentStatus("1");
                break;
            case R.id.tv_cancel_joint_rent://禁止合租发布
                setJointRentStatus("0");
                break;
        }
    }

    private void applyCheckout() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().checkOut(order_id)
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
                        if (dataInfo.success()) {
                            showToast(dataInfo.msg());
                            RxBus.getDefault().post(new UpdateRentingEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void cancelCheckout() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().cancelCheckOut(order_id)
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
                        if (dataInfo.success()) {
                            showToast(dataInfo.msg());
                            RxBus.getDefault().post(new CncelCheckoutEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     *
     * */
    private void setJointRentStatus(final String jointRentType) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().setJointRentStatus(order_id, jointRentType)
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
                        if (dataInfo.success()) {
                            showToast(dataInfo.msg());
                            if (jointRentType.equals("1")) {
                                mTvPublicJointRent.setVisibility(View.GONE);
                                mTvCancelJointRent.setVisibility(View.VISIBLE);
                                orderDetailInfo.setIs_share(1);
                            } else {
                                mTvPublicJointRent.setVisibility(View.VISIBLE);
                                mTvCancelJointRent.setVisibility(View.GONE);
                                orderDetailInfo.setIs_share(0);
                            }

                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
