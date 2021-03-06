package com.konka.renting.landlord.order;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RenterOrderInfoBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.CircleTransform;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class OrderInfoLActivity extends BaseActivity {
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
    @BindView(R.id.activity_order_info_img_icon)
    ImageView mImgIcon;
    @BindView(R.id.activity_order_info_card_img)
    CardView mCardImg;
    @BindView(R.id.activity_order_info_tv_room_name)
    TextView mTvRoomName;
    @BindView(R.id.activity_order_info_tv_room_info)
    TextView mTvRoomInfo;
    @BindView(R.id.activity_order_info_tv_room_price)
    TextView mTvRoomPrice;
    @BindView(R.id.activity_order_info_ll_push_type)
    LinearLayout mLlPushType;
    @BindView(R.id.activity_order_info_tv_push_type)
    TextView mTvPushType;
    @BindView(R.id.activity_order_info_tv_order_type)
    TextView mTvOrderType;

    @BindView(R.id.activity_order_info_img_icon_person)
    ImageView mImgIconPerson;
    @BindView(R.id.activity_order_info_tv_person_name)
    TextView mTvPersonName;
    @BindView(R.id.activity_order_info_tv_phone)
    TextView mTvPhone;
    @BindView(R.id.activity_order_info_img_call)
    ImageView mImgCall;
    @BindView(R.id.activity_order_info_rl_person)
    RelativeLayout mRlPerson;
    @BindView(R.id.tv_tips_code_status)
    TextView tvTipsCodeStatus;
    @BindView(R.id.activity_order_info_rl_rent_code)
    RelativeLayout mRlRentCode;
    @BindView(R.id.activity_order_info_tv_rent_in_code)
    TextView mTvRentInCode;
    @BindView(R.id.activity_order_info_tv_rent_code_copy)
    TextView mTvRentCodeCopy;
    @BindView(R.id.activity_order_info_tv_code_status)
    TextView mTvCodeStatus;


    @BindView(R.id.activity_order_info_tv_rent_start)
    TextView mTvRentStart;
    @BindView(R.id.activity_order_info_tv_rent_end)
    TextView mTvRentEnd;

    @BindView(R.id.tv_tips_order_no)
    TextView tvTipsOrderNo;
    @BindView(R.id.activity_order_info_tv_order_no)
    TextView mTvOrderNo;
    @BindView(R.id.tv_tips_create_time)
    TextView tvTipsCreateTime;
    @BindView(R.id.activity_order_info_tv_create_time)
    TextView mTvCreateTime;
    @BindView(R.id.tv_tips_cancel_type)
    TextView tvTipsCancelType;
    @BindView(R.id.activity_order_info_tv_cancel_type)
    TextView tvCancelType;
    @BindView(R.id.tv_tips_refund_type)
    TextView tvTipsRefundType;
    @BindView(R.id.activity_order_info_tv_refund_type)
    TextView tvRefundType;

    RenterOrderInfoBean infoBean;
    int type = 0;//0 进行中  1已完成
    String order_id;


    public static void toActivity(Context context, String order_id, int type) {
        Intent intent = new Intent(context, OrderInfoLActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_info_l;
    }

    @Override
    public void init() {
        order_id = getIntent().getStringExtra("order_id");
        type = getIntent().getIntExtra("type", 0);

        tvTitle.setText(R.string.order_info_title);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        getOrderDetail();

    }


    @OnClick({R.id.iv_back, R.id.activity_order_info_tv_rent_code_copy, R.id.activity_order_info_img_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_order_info_img_call:
                if (infoBean != null && infoBean.getMember().size() > 0 && infoBean.getMember().get(0).getPhone() != null) {
                    PhoneUtil.call(infoBean.getMember().get(0).getPhone(), this);
                }
                break;
            case R.id.activity_order_info_tv_rent_code_copy:
                if (infoBean == null)
                    return;
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", infoBean.getAccount());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                showToast(R.string.toach_copy_to_clipboard);
                break;
        }
    }

    private void setData() {
        if (CacheUtils.checkFileExist(infoBean.getThumb_image())) {
            Picasso.get().load(CacheUtils.getFile(infoBean.getThumb_image())).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(mImgIcon);
        } else if (!TextUtils.isEmpty(infoBean.getThumb_image())) {
            CacheUtils.saveFile(infoBean.getThumb_image(), this);
            Picasso.get().load(infoBean.getThumb_image()).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(mImgIcon);
        } else
            Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai).into(mImgIcon);

        mTvRoomName.setText(infoBean.getRoom_name());

        String room_type;
        if (infoBean.getRoom_type().contains("_")) {
            String[] t = infoBean.getRoom_type().split("_");
            room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
        } else {
            room_type = infoBean.getRoom_type();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(room_type + "|");
        spannableStringBuilder.append(getArea(infoBean.getMeasure_area()));
        spannableStringBuilder.append("|" + infoBean.getFloor() + "/" + infoBean.getTotal_floor() + "层");
        mTvRoomInfo.setText(spannableStringBuilder);


        if (!TextUtils.isEmpty(infoBean.getHousing_price()) && Float.valueOf(infoBean.getHousing_price()) != 0) {
            mTvRoomPrice.setVisibility(View.VISIBLE);
            String unit = infoBean.getType() == 1 ? getString(R.string.public_house_pay_unit_day) : getString(R.string.public_house_pay_unit_mon);

            String price = infoBean.getHousing_price();
            if (!TextUtils.isEmpty(price)) {
                float priceF = Float.valueOf(infoBean.getHousing_price());
                int priceI = (int) priceF;
                if(priceF<=0){
                    price="";
                    unit=getString(R.string.negotiable);
                }else if (priceF > priceI) {
                    price = priceF + "";
                } else {
                    price = priceI + "";
                }
            } else {
                price = "";
            }
            mTvRoomPrice.setText( price + unit);
            mLlPushType.setVisibility(View.VISIBLE);
            mTvPushType.setText(getStringStatus(infoBean.getType()));
            mTvOrderType.setText(type == 0 ? R.string.order_title_underway : R.string.order_title_done);
        } else {
            mTvRoomPrice.setVisibility(View.GONE);
            mLlPushType.setVisibility(View.GONE);
        }


        if (infoBean.getStatus() > 1) {

            mRlRentCode.setVisibility(View.GONE);
            if (infoBean.getMember() != null && infoBean.getMember().size() > 0) {
                mRlPerson.setVisibility(View.VISIBLE);
                if (CacheUtils.checkFileExist(infoBean.getLandlord().getThumb_headimgurl())) {
                    Picasso.get().load(CacheUtils.getFile(infoBean.getLandlord().getThumb_headimgurl())).placeholder(R.mipmap.fangdong_xuanzhong).error(R.mipmap.fangdong_xuanzhong)
                            .transform(new CircleTransform()).into(mImgIconPerson);
                } else if (!TextUtils.isEmpty(infoBean.getLandlord().getThumb_headimgurl())) {
                    CacheUtils.saveFile(infoBean.getLandlord().getThumb_headimgurl(), this);
                    Picasso.get().load(infoBean.getLandlord().getThumb_headimgurl()).placeholder(R.mipmap.fangdong_xuanzhong).error(R.mipmap.fangdong_xuanzhong)
                            .transform(new CircleTransform()).into(mImgIconPerson);
                } else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai).transform(new CircleTransform()).into(mImgIconPerson);

                mTvPersonName.setText(getString(R.string.tips_tenant_) + infoBean.getMember().get(0).getReal_name());

                String tel = infoBean.getMember().get(0).getPhone();
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
            }else{
                mRlPerson.setVisibility(View.GONE);
            }
        } else {
            mRlPerson.setVisibility(View.GONE);
            mRlRentCode.setVisibility(View.VISIBLE);
            mTvRentInCode.setText(infoBean.getAccount());
        }

        mTvRentStart.setText(infoBean.getStart_time());
        mTvRentEnd.setText(infoBean.getEnd_time());

        mTvOrderNo.setText(infoBean.getOrder_no());
        mTvCreateTime.setText(infoBean.getCreate_time());

        if (infoBean.getStatus() == 7) {
            tvTipsCancelType.setVisibility(View.VISIBLE);
            tvCancelType.setVisibility(View.VISIBLE);
            tvCancelType.setText(R.string.order_status_7);
            if (infoBean.getRefund_status() != 0) {
                tvTipsRefundType.setVisibility(View.VISIBLE);
                tvRefundType.setVisibility(View.VISIBLE);
                tvRefundType.setText(R.string.refund_back);
            } else {
                tvTipsRefundType.setVisibility(View.GONE);
                tvRefundType.setVisibility(View.GONE);
            }
        }else if (infoBean.getStatus() == 8) {
            tvRefundType.setVisibility(View.GONE);
            tvTipsRefundType.setVisibility(View.GONE);
            tvTipsCancelType.setVisibility(View.VISIBLE);
            tvCancelType.setVisibility(View.VISIBLE);
            tvCancelType.setText(R.string.order_status_8);
        }  else {
            tvTipsCancelType.setVisibility(View.GONE);
            tvCancelType.setVisibility(View.GONE);
            tvTipsRefundType.setVisibility(View.GONE);
            tvRefundType.setVisibility(View.GONE);

        }

    }

    private String getStringStatus(int type) {
        String str = "";
        switch (type) {
            case 1:
                str = "【" + getString(R.string.short_rent) + "】";
                break;
            case 2:
                str = "【" + getString(R.string.long_rent) + "】";
                break;
        }
        return str;
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }

    /*******************************************************接口********************************************/
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
                            infoBean = orderDetailBeanDataInfo.data();
                            setData();
                        } else {
                            showToast(orderDetailBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
