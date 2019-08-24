package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.AddRentingBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseOrderInfoBean;
import com.konka.renting.bean.RentingDateBean;
import com.konka.renting.bean.TenantListBean;
import com.konka.renting.event.ChooseDateEvent;
import com.konka.renting.event.CreateOrderEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ChooseHourPopup;
import com.konka.renting.landlord.order.PwsOrderDetailsActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonPopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class CreateOrderActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activity_create_order_tv_id)
    TextView tvId;
    @BindView(R.id.activity_create_order_tv_start)
    TextView tvStart;
    @BindView(R.id.activity_create_order_ll_start)
    LinearLayout llStart;
    @BindView(R.id.activity_create_order_tv_end)
    TextView tvEnd;
    @BindView(R.id.activity_create_order_ll_end)
    LinearLayout llEnd;
    @BindView(R.id.activity_create_order_btn_toCreate)
    Button btnToCreate;
    @BindView(R.id.activity_create_order_edt_mobile)
    EditText edtMobile;
    @BindView(R.id.activity_create_order_tv_mobile_tips)
    TextView tvMobileTips;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.activity_create_order_ll_choose_date)
    LinearLayout mLlChooseDate;
    @BindView(R.id.activity_create_order_tv_start_hour)
    TextView mTvStartHour;
    @BindView(R.id.activity_create_order_ll_start_hour)
    LinearLayout mLlStartHour;
    @BindView(R.id.activity_create_order_tv_end_hour)
    TextView mTvEndHour;
    @BindView(R.id.activity_create_order_ll_end_hour)
    LinearLayout mLlEndHour;


    private Calendar cal;
    private int year, month, day;
    String startDate, endDate;
    String room_id, room_name;
    ArrayList<RentingDateBean> rentingDates;
    ArrayList<Integer> startHours;
    ArrayList<Integer> endHours;
    ChooseHourPopup chooseHourPopup;
    boolean isChooseStart = true;
    CommonPopupWindow commonPopupWindow;

    public static void toActivity(Context context, String room_id, String room_name) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("room_name", room_name);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");
        room_name = getIntent().getStringExtra("room_name");

        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        UIUtils.setDarkStatusIcon(this, true);
        ViewGroup.LayoutParams lp = linTitle.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        linTitle.setLayoutParams(lp);
        linTitle.setPadding(linTitle.getPaddingLeft(), linTitle.getPaddingTop() + UIUtils.getStatusHeight(), linTitle.getPaddingRight(), linTitle.getPaddingBottom());

        tvTitle.setText(R.string.house_creat_order);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);
        linTitle.setBackgroundColor(getResources().getColor(R.color.color_fafafa));

        rentingDates = new ArrayList<>();
        tvId.setText(room_name);
        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString();
                if (isMobileNO(phone)) {
                    check(phone);
                } else if (phone.length() < 11) {
                    tvMobileTips.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addRxBusSubscribe(ChooseDateEvent.class, new Action1<ChooseDateEvent>() {
            @Override
            public void call(ChooseDateEvent chooseDateEvent) {
                startDate = fromDate(chooseDateEvent.startDate);
                endDate = fromDate(chooseDateEvent.endDate);
                String[] s = startDate.split("-");
                String[] e = endDate.split("-");
                tvStart.setText(s[0] + "年" + s[1] + "月" + s[2] + "日");
                tvEnd.setText(e[0] + "年" + e[1] + "月" + e[2] + "日");
//                setChooseHours();
//                mTvStartHour.setText(startHours.size() > 0 && startHours.get(0) < 12 ? "12" : (startHours.size() == 0 ? "24" : startHours.get(0) + ""));
//                mTvEndHour.setText(endHours.size() > 0 && endHours.get(endHours.size() - 1) > 12 ? "12" : (endHours.size() == 0 ? "0" : endHours.get(endHours.size() - 1) + ""));
            }
        });
        getRentDate();
        initChooseHours();
    }

    private void getRentDate() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rentingDate2(room_id)
                .compose(RxUtil.<DataInfo<List<RentingDateBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<RentingDateBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<List<RentingDateBean>> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            rentingDates.addAll(dataInfo.data());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void check(String phone) {
        Subscription subscription = SecondRetrofitHelper.getInstance().check(phone)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            tvMobileTips.setVisibility(View.INVISIBLE);
                        } else {
                            tvMobileTips.setVisibility(View.VISIBLE);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void initChooseHours() {
        startHours = new ArrayList<>();
        endHours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            startHours.add(i);
            endHours.add(i);
        }
    }

    private void setChooseHours() {
        startHours.clear();
        endHours.clear();
        int si = 0;
        int ei = 24;
        int size = rentingDates.size();
        for (int i = 0; i < size; i++) {
            RentingDateBean dateBean = rentingDates.get(i);
            if (dateBean.getEnd_time().contains(startDate)) {
                String s = dateBean.getEnd_time().split(" ")[1].split(":")[0];
                si = Integer.valueOf(s) + 1;
            }
            if (dateBean.getStart_time().contains(endDate)) {
                String e = dateBean.getStart_time().split(" ")[1].split(":")[0];
                ei = Integer.valueOf(e);
            }
        }


        for (int j = si; j < 24; j++) {
            startHours.add(j);
        }

        for (int k = 0; k < ei; k++) {
            endHours.add(k);
        }
    }

    private String fromDate(String date) {
        String[] s = date.split("-");
        String w = s[1];
        if (w.length() < 2) {
            s[1] = "0" + w;
        }

        return s[0] + "-" + s[1] + "-" + s[2];
    }


    @OnClick({R.id.iv_back, R.id.activity_create_order_ll_choose_date, R.id.activity_create_order_btn_toCreate, R.id.activity_create_order_ll_start_hour, R.id.activity_create_order_ll_end_hour})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_create_order_ll_choose_date://选择日期
                ChooseDateActivity.toActivity(this, rentingDates);
                break;
            case R.id.activity_create_order_btn_toCreate://生成订单
                String mobile = edtMobile.getText().toString();
                if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
                    showToast(R.string.warn_input_date);
                } else if (!mobile.equals("") && !isMobileNO(mobile)) {
                    showToast(R.string.warn_input_mobile);
                } else  if (TextUtils.isEmpty(mobile)) {
                    createOrder();
                } else {
                    showFinishPopup(mobile);
                }
                break;
            case R.id.activity_create_order_ll_start_hour://选择开始入住小时
                showHour(true);
                break;
            case R.id.activity_create_order_ll_end_hour://选择结束小时
                showHour(false);
                break;
        }
    }

    private void createOrder() {
        btnToCreate.setEnabled(false);
        showLoadingDialog();
        String mobile = edtMobile.getText().toString();
        String start = startDate + " 12:00:00";
        String end = endDate + " 12:00:00";
        Subscription subscription = SecondRetrofitHelper.getInstance().addOrderPay2(room_id, mobile, start, end)
                .compose(RxUtil.<DataInfo<AddRentingBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AddRentingBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                        btnToCreate.setEnabled(true);
                    }

                    @Override
                    public void onNext(DataInfo<AddRentingBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            if (dataInfo.success()) {
                                RxBus.getDefault().post(new CreateOrderEvent());
                                if (TextUtils.isEmpty(mobile)) {
                                    PwsOrderDetailsActivity.toActivity(CreateOrderActivity.this, dataInfo.data().getOrder_id());
                                }else{
                                    showToast(R.string.order_create_success);
                                }
                                finish();
                            }
                        } else {
                            btnToCreate.setEnabled(true);
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public boolean isMobileNO(String mobiles) {
        return mobiles.length() == 11 && mobiles.startsWith("1");
    }

    /*****************************************************弹窗***********************************************/
    private void showHour(boolean isStart) {
        isChooseStart = isStart;
        if (chooseHourPopup == null) {
            chooseHourPopup = new ChooseHourPopup(this);
            chooseHourPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mLlStartHour.setBackgroundResource(R.drawable.shape_button_gray);
                    mLlEndHour.setBackgroundResource(R.drawable.shape_button_gray);
                }
            });
            chooseHourPopup.setOnCall(new ChooseHourPopup.OnCall() {
                @Override
                public void onClick(Integer item) {
                    if (isChooseStart) {
                        mTvStartHour.setText(item + "");
                    } else {
                        mTvEndHour.setText(item + "");
                    }
                }
            });
        }

        if (isStart) {

            mLlStartHour.setBackgroundResource(R.drawable.shape_choose_hour_up);
            chooseHourPopup.showAsDropDown(mLlStartHour, 0, -getResources().getDimensionPixelOffset(R.dimen.dp_1));
            chooseHourPopup.setHours(startHours);
        } else {
            mLlEndHour.setBackgroundResource(R.drawable.shape_choose_hour_up);
            chooseHourPopup.showAsDropDown(mLlEndHour, 0, -getResources().getDimensionPixelOffset(R.dimen.dp_1));
            chooseHourPopup.setHours(endHours);
        }

    }

    private void showFinishPopup(String phone) {
        if (commonPopupWindow == null)
            commonPopupWindow = new CommonPopupWindow.Builder(this)
                    .setTitle(getString(R.string.house_user) + " " + phone)
                    .setContent(getString(R.string.house_user_will_get_open))
                    .setRightTextColor("#4A90E1")
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonPopupWindow.dismiss();
                            createOrder();
                        }
                    })
                    .create();
        showPopup(commonPopupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) findViewById(R.id.lin_title).getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }
}
