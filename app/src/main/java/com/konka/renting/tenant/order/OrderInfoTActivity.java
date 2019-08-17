package com.konka.renting.tenant.order;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import butterknife.OnClick;
import rx.Subscription;

public class OrderInfoTActivity extends BaseActivity {
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
    @BindView(R.id.tv_title_rent_info)
    TextView tvTitleRentInfo;
    @BindView(R.id.tv_tips_limit)
    TextView tvTipsLimit;
    @BindView(R.id.activity_order_info_tv_rent_limit)
    TextView mTvRentLimit;
    @BindView(R.id.tv_tips_pay_way)
    TextView tvTipsPayWay;
    @BindView(R.id.activity_order_info_tv_pay_style)
    TextView mTvPayStyle;
    @BindView(R.id.tv_tips_rent_money)
    TextView tvTipsRentMoney;
    @BindView(R.id.activity_order_info_tv_rent_money)
    TextView mTvRentMoney;
    @BindView(R.id.tv_tips_deposit)
    TextView tvTipsDeposit;
    @BindView(R.id.activity_order_info_tv_deposit)
    TextView mTvDeposit;
    @BindView(R.id.tv_title_order_info)
    TextView tvTitleOrderInfo;
    @BindView(R.id.tv_tips_order_no)
    TextView tvTipsOrderNo;
    @BindView(R.id.activity_order_info_tv_order_no)
    TextView mTvOrderNo;
    @BindView(R.id.tv_tips_create_time)
    TextView tvTipsCreateTime;
    @BindView(R.id.activity_order_info_tv_create_time)
    TextView mTvCreateTime;

    RenterOrderInfoBean infoBean;
    int type = 0;//0 进行中  1已完成
    String order_id;

    public static void toActivity(Context context, String order_id,int type) {
        Intent intent = new Intent(context, OrderInfoTActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_info;
    }

    @Override
    public void init() {
        order_id = getIntent().getStringExtra("order_id");
        type = getIntent().getIntExtra("type",0);

        tvTitle.setText(R.string.tenant_main_payrent);

        getOrderDetail();

    }


    @OnClick({R.id.iv_back, R.id.activity_order_info_img_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_order_info_img_call:
                if (infoBean != null && infoBean.getLandlord().getPhone() != null) {
                    PhoneUtil.call(infoBean.getLandlord().getPhone(), this);
                }
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

        String unit = infoBean.getType() == 1 ? "/天" : "/月";
        mTvRoomPrice.setText("¥ " + (int) Float.parseFloat(infoBean.getHousing_price()) + unit);

        mTvPushType.setText(getStringStatus(infoBean.getType()));
        mTvOrderType.setText(type == 0 ? R.string.order_title_underway : R.string.order_title_done);

        if (CacheUtils.checkFileExist(infoBean.getLandlord().getThumb_headimgurl())) {
            Picasso.get().load(CacheUtils.getFile(infoBean.getLandlord().getThumb_headimgurl())).placeholder(R.mipmap.fangdong_xuanzhong).error(R.mipmap.fangdong_xuanzhong)
                    .transform(new CircleTransform()).into(mImgIconPerson);
        } else if (!TextUtils.isEmpty(infoBean.getLandlord().getThumb_headimgurl())) {
            CacheUtils.saveFile(infoBean.getLandlord().getThumb_headimgurl(), this);
            Picasso.get().load(infoBean.getLandlord().getThumb_headimgurl()).placeholder(R.mipmap.fangdong_xuanzhong).error(R.mipmap.fangdong_xuanzhong)
                    .transform(new CircleTransform()).into(mImgIconPerson);
        } else
            Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai) .transform(new CircleTransform()).into(mImgIconPerson);

        mTvPersonName.setText(getString(R.string.tips_landlord_) + infoBean.getLandlord().getReal_name());

        String tel = infoBean.getLandlord().getPhone();
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

        mTvRentLimit.setText(infoBean.getCreate_time() + "至" + infoBean.getEnd_time());
        mTvPayStyle.setText(infoBean.getIs_online() == 0 ? R.string.tips_pay_way_no_online : R.string.tips_pay_way_online);
        String unit1 = infoBean.getType() == 1 ? "元/天" : "元/月";
        mTvRentMoney.setText((int) Float.parseFloat(infoBean.getHousing_price()) + unit1);

        mTvOrderNo.setText(infoBean.getOrder_no());
        mTvCreateTime.setText(infoBean.getCreate_time());

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
