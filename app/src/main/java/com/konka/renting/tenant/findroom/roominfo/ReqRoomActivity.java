package com.konka.renting.tenant.findroom.roominfo;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.bean.RentingDateBean;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.bean.RoomSearchInfoBean;
import com.konka.renting.event.ChooseDateEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.ChooseDateActivity;
import com.konka.renting.landlord.house.DateUtils;
import com.konka.renting.landlord.house.view.AutoChooseWidget;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.tenant.TenantMainActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class ReqRoomActivity extends BaseActivity implements OnClickListener {

    private Context context;
    RoomInfoThread roomInfoThread;
    ViewPager pager;
    LinearLayout ponit_layout;
    RelativeLayout rlChooseIn, rlChooseOut;
    //	LinearLayout bottom_view;
    Button btn_req;
    EditText h_edit1, h_edit2, h_edit3, h_edit4, h_edit5, h_edit6, h_edit7, h_edit8;
    TextView text1, text2, text3, text4, text5;
    TextView chose1, chose2, chose3, chose4, chose5;
    TextView lx;
    //    RoomInfo roomInfo;
    private CompositeSubscription mCompositeSubscription;
    TextView submit;
    private Calendar cal;
    private int year, month, day;
    Switch aSwitch;
    private CountDownTime mTime;
    ArrayList<RentingDateBean> rentingDates=new ArrayList<>();
    RoomSearchInfoBean mBean;


    @Override
    public int getLayoutId() {
        context = this;
        return R.layout.lib_req_room_activity;
    }

    @Override
    public void init() {
        mTime = new CountDownTime(59000, 1000);
        mBean = (RoomSearchInfoBean) getIntent().getParcelableExtra("RoomSearchInfoBean");

        rlChooseIn = findViewById(R.id.rl_choose_in);
        rlChooseOut = findViewById(R.id.rl_choose_out);

        aSwitch = findViewById(R.id.switch1);
        h_edit1 = findViewById(R.id.h_edit1);
        h_edit2 = findViewById(R.id.h_edit2);
        h_edit3 = findViewById(R.id.h_edit3);
        h_edit4 = findViewById(R.id.h_edit4);
        h_edit5 = findViewById(R.id.h_edit5);
        h_edit6 = findViewById(R.id.h_edit6);
        h_edit7 = findViewById(R.id.h_edit7);
        h_edit8 = findViewById(R.id.h_edit8);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        lx = findViewById(R.id.lx);

        chose1 = findViewById(R.id.chose1);
        chose2 = findViewById(R.id.chose2);
        chose3 = findViewById(R.id.chose3);
        chose4 = findViewById(R.id.chose4);
        chose5 = findViewById(R.id.chose5);
        submit = findViewById(R.id.text_right);
        ViewGroup viewGroup = (ViewGroup) h_edit3.getParent();
        viewGroup.setVisibility(View.GONE);
        rlChooseIn.setOnClickListener(this);
        rlChooseOut.setOnClickListener(this);
        submit.setOnClickListener(this);
        chose1.setOnClickListener(this);
        chose2.setOnClickListener(this);
        chose3.setOnClickListener(this);
        ViewGroup group_chose3 = findViewById(R.id.group_chose3);
        group_chose3.setOnClickListener(this);
        chose4.setOnClickListener(this);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ViewGroup viewGroup = (ViewGroup) h_edit3.getParent();
                if (b) {
                    viewGroup.setVisibility(View.VISIBLE);
                } else {
                    viewGroup.setVisibility(View.GONE);
                }
            }
        });

        if (mBean.getType() == 1) {
            //短8fc320
            hideForShort();
            lx.setText("/天");
        } else if (mBean.getType() == 2) {
            lx.setText("/月");
            //长
        } else {
        }
//        getRoomDescription(mBean.getRoom_id());

        addRxBusSubscribe(ChooseDateEvent.class, new Action1<ChooseDateEvent>() {
            @Override
            public void call(ChooseDateEvent chooseDateEvent) {
                chose1.setText(chooseDateEvent.startDate);
                chose2.setText(chooseDateEvent.endDate);
            }
        });
        if (mBean.getType() == 1) {
            getRentDate();
        }


        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        UIUtils.setDarkStatusIcon(this, true);
        ViewGroup viewG = (ViewGroup) submit.getParent();
        ViewGroup.LayoutParams lp = viewG.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewG.setLayoutParams(lp);
        viewG.setPadding(viewG.getPaddingLeft(), viewG.getPaddingTop() + UIUtils.getStatusHeight(), viewG.getPaddingRight(), viewG.getPaddingBottom());

        bindData(null);
    }

    private void getRentDate() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rentingDate2(mBean.getRoom_id())
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
                            rentingDates .addAll(dataInfo.data());
                            if (rentingDates == null)
                                rentingDates = new ArrayList<>();
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    public void bindData(RoomInfo.RoomDescription descri) {
        if (descri != null) {
            text1.setText(descri.water_rent + " 元/吨");
            text2.setText(descri.electric_rent + " 元/度");
            text3.setText(descri.property_rent + " 元/月");
            text4.setText(descri.litter_rent + " 元/月");
            text5.setText(descri.cost_rent + " 元/月");
            h_edit1.setText(descri.lock_deposit + " 元");

        }
        h_edit4.setEnabled(false);
        h_edit5.setEnabled(false);
        h_edit7.setEnabled(false);
        h_edit7.setText("");
        h_edit4.setText(LoginUserBean.getInstance().getRealname());
        if (TextUtils.isEmpty(LoginUserBean.getInstance().getRealname())) {
            h_edit4.setText("");
        }
        h_edit5.setText(LoginUserBean.getInstance().getMobile());
        h_edit7.setText(LoginUserBean.getInstance().getIdentity());
//        if(TextUtils.isEmpty(LoginUserBean.getInstance().getIdentity())){
//            h_edit7.setText("");
//        }

    }

    public void hideForShort() {
        ViewGroup viewGroup;
        viewGroup = (ViewGroup) text1.getParent();
        viewGroup.setVisibility(View.GONE);
        viewGroup = (ViewGroup) text2.getParent();
        viewGroup.setVisibility(View.GONE);
        viewGroup = (ViewGroup) text3.getParent();
        viewGroup.setVisibility(View.GONE);
        viewGroup = (ViewGroup) text4.getParent();
        viewGroup.setVisibility(View.GONE);
        viewGroup = (ViewGroup) text5.getParent();
        viewGroup.setVisibility(View.GONE);
        viewGroup = (ViewGroup) chose3.getParent();
        viewGroup.setVisibility(View.GONE);
        viewGroup = (ViewGroup) h_edit2.getParent();
        viewGroup.setVisibility(View.GONE);

        RelativeLayout re = (RelativeLayout) h_edit3.getParent();
        LinearLayout.LayoutParams le = (LinearLayout.LayoutParams) re.getLayoutParams();
        le.topMargin = 20;

        RelativeLayout re2 = (RelativeLayout) aSwitch.getParent();
        LinearLayout.LayoutParams le2 = (LinearLayout.LayoutParams) re2.getLayoutParams();
        le2.topMargin = 20;

    }

//    public void getRoomDescription(String id) {
//        rx.Observable<DataInfo<RoomInfo>> observable = null;
//        observable = RetrofitHelper.getInstance().getRoom(id);
//
//        Subscription subscription = (observable.compose(RxUtil.<DataInfo<RoomInfo>>rxSchedulerHelper()).subscribe(new CommonSubscriber<DataInfo<RoomInfo>>() {
//            @Override
//            public void onError(Throwable e) {
//                Log.d("jia", "");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(DataInfo<RoomInfo> homeInfoDataInfo) {
//                if (homeInfoDataInfo.success()) {
//                    homeInfoDataInfo.data();
//                    bindData(homeInfoDataInfo.data().roomInfo);
//                }
//            }
//        }));
//        addSubscrebe(subscription);
//    }

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public void submitData() {
        String room_id = mBean.getRoom_id();
        String start_time = chose1.getText().toString();
        String end_time = chose2.getText().toString();
        if (start_time.equals("") || end_time.equals("")) {
            showToast(R.string.warn_input_date);
            return;
        }

        if (-1 == DateUtils.compare_date(start_time, DateUtils.getDate())) {
            ShowToastUtil.showWarningToast(context, "日期不能早于当前日期");
//            onClick(findViewById(R.id.chose1));
            return;
        }
        if (-1 == DateUtils.compare_date(end_time, DateUtils.getDate())) {
            ShowToastUtil.showWarningToast(context, "日期不能早于当前日期");
            onClick(findViewById(R.id.chose2));
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (sdf.parse(start_time).getTime() > sdf.parse(end_time).getTime()) {
                ShowToastUtil.showWarningToast(context, "退房时间应大于入住时间");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String rent_type_id = "";
        String renting_mode = "";
        if (chose3.getTag() != null) {
            rent_type_id = chose3.getTag().toString();
        }
//        if(chose4.getTag()!=null){
//            renting_mode=chose4.getTag().toString();
//        }
        if (aSwitch.isChecked()) {
            renting_mode = "1";
        } else {
            renting_mode = "0";
        }

        String deposit_price = h_edit2.getText().toString();
        String housing_price = h_edit3.getText().toString();
        String name = h_edit4.getText().toString();
        String tel = h_edit5.getText().toString();
        String code = h_edit6.getText().toString();
        String identity = h_edit7.getText().toString();
        String mark = h_edit8.getText().toString();

        if (code.equals("")) {
            ShowToastUtil.showWarningToast(context, "验证码不能为空");
        }

        rx.Observable<DataInfo> observable = null;
//        observable = RetrofitHelper.getInstance().addRenting(room_id, start_time, end_time, rent_type_id, renting_mode, housing_price, name, tel, code, identity,mark);
        observable = SecondRetrofitHelper.getInstance().memberAdd(room_id, code, start_time, end_time, mark);
        Subscription subscription = (observable.compose(RxUtil.<DataInfo>rxSchedulerHelper()).subscribe(new CommonSubscriber<DataInfo>() {
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(DataInfo homeInfoDataInfo) {
//                        dismiss();

                if (homeInfoDataInfo.success()) {
                    ShowToastUtil.showSuccessToast(ReqRoomActivity.this, homeInfoDataInfo.msg());
                    RxBus.getDefault().post(new PayRentRefreshEvent());
                    TenantMainActivity.toActivity(context);
                    ReqRoomActivity.this.finish();
                } else {
                    showToast(homeInfoDataInfo.msg());
                }
            }
        }));
        addSubscrebe(subscription);
    }

    private void getDate() {
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);       //获取年月日时分秒
        month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    DatePickerDialog.OnDateSetListener listener;
    DatePickerDialog dialog;

    @Override
    public void onClick(View arg0) {
        Intent intent;

        switch (arg0.getId()) {
            case R.id.text_right:
                submitData();
                break;
            case  R.id.chose1:
            case R.id.rl_choose_in:
                ChooseDateActivity.toActivity(ReqRoomActivity.this, rentingDates);
//                if (mBean.getType() == 1) {
//                } else {
//                    getDate();
//                    listener = new DatePickerDialog.OnDateSetListener() {
//
//                        @Override
//                        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//                            chose1.setText(year + "-" + (++month) + "-" + day);      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
////                       String date=year + "-" + (++month) + "-" + day;
////                       if(1==DateUtils.compare_date(date,DateUtils.getDate())) {
////                           ShowToastUtil.showWarningToast(context,"日期不能早于当前日期");
////                           onClick(findViewById(R.id.chose1));
////                       }
//                        }
//                    };
//                    DatePickerDialog dialog = new DatePickerDialog(this, 0, listener, year, month, day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
//                    dialog.show();
//                }

                break;
            case R.id.chose2:
            case R.id.rl_choose_out:
//                if (mBean.getType() == 1) {
                    ChooseDateActivity.toActivity(this, rentingDates);
//                } else {
//                    getDate();
//                    listener = new DatePickerDialog.OnDateSetListener() {
//
//                        @Override
//                        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//                            chose2.setText(year + "-" + (++month) + "-" + day);      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
////                        String date=year + "-" + (++month) + "-" + day;
////                        if(1==DateUtils.compare_date(date,DateUtils.getDate())) {
////                            ShowToastUtil.showWarningToast(context,"日期不能早于当前日期");
////                            onClick(findViewById(R.id.chose2));
////                        }
//                        }
//                    };
//                    dialog = new DatePickerDialog(this, 0, listener, year, month, day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
//                    dialog.show();
//                }

                break;
            case R.id.chose3:
            case R.id.group_chose3:
                new AutoChooseWidget(this, AutoChooseWidget.RENT_TYPE, chose3).showPopWindow();

                break;
            case R.id.chose4:
                new AutoChooseWidget(this, AutoChooseWidget.PAY_TYPE, chose4).showPopWindow();

                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.yzm:
                mTime.start();
                yzm();
                break;
            default:
                break;
        }

    }

    public void yzm() {
        String tel = h_edit5.getText().toString();
        rx.Observable<DataInfo> observable = null;
        observable = SecondRetrofitHelper.getInstance().getVerify(tel, "1");

        Subscription subscription = (observable.compose(RxUtil.<DataInfo>rxSchedulerHelper()).subscribe(new CommonSubscriber<DataInfo>() {
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(DataInfo homeInfoDataInfo) {
//                        dismiss();

                if (homeInfoDataInfo.success()) {
                    ShowToastUtil.showSuccessToast(ReqRoomActivity.this, homeInfoDataInfo.msg());
                } else {
                    ShowToastUtil.showWarningToast(ReqRoomActivity.this, homeInfoDataInfo.msg());
                }
            }
        }));
        addSubscrebe(subscription);
    }

    //		    new AutoChooseWidget(this,AutoChooseWidget.HOUSE_TYPE, chose1).showPopWindow();
    class CountDownTime extends CountDownTimer {

        //构造函数  第一个参数代表总的计时时长  第二个参数代表计时间隔  单位都是毫秒
        public CountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) { //每计时一次回调一次该方法
            TextView textView = findViewById(R.id.yzm);
            textView.setClickable(false);
            textView.setText(l / 1000 + "s");
        }

        @Override
        public void onFinish() { //计时结束回调该方法
            TextView textView = findViewById(R.id.yzm);
            textView.setClickable(true);
            textView.setText("获取验证码");
        }
    }

}