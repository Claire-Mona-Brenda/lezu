package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.AddRentingBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.event.ChooseDateEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.order.PwsOrderDetailsActivity;
import com.konka.renting.utils.RxUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
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


    private Calendar cal;
    private int year, month, day;
    String startDate, endDate;
    String room_id, room_name;
    ArrayList<String> rentingDates;

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
        tvTitle.setText(R.string.house_creat_order);

        room_id = getIntent().getStringExtra("room_id");
        room_name = getIntent().getStringExtra("room_name");
        tvId.setText(room_name);
        addRxBusSubscribe(ChooseDateEvent.class, new Action1<ChooseDateEvent>() {
            @Override
            public void call(ChooseDateEvent chooseDateEvent) {
                startDate = chooseDateEvent.startDate;
                endDate = chooseDateEvent.endDate;
                tvStart.setText(chooseDateEvent.startDate);
                tvEnd.setText(chooseDateEvent.endDate);
            }
        });
        getRentDate();

    }

    private void getRentDate() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().rentingDate(room_id)
                .compose(RxUtil.<DataInfo<List<String>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<String>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            rentingDates = (ArrayList<String>) dataInfo.data();
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @OnClick({R.id.iv_back, R.id.activity_create_order_ll_start, R.id.activity_create_order_ll_end, R.id.activity_create_order_btn_toCreate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_create_order_ll_start:
                ChooseDateActivity.toActivity(this, rentingDates);
                break;
            case R.id.activity_create_order_ll_end:
                ChooseDateActivity.toActivity(this, rentingDates);
                break;
            case R.id.activity_create_order_btn_toCreate:
                if (tvStart.getText().toString().equals("") || tvEnd.getText().toString().equals("")) {
                    showToast(R.string.warn_input_date);
                } else if (!edtMobile.getText().toString().equals("")&&!isMobileNO(edtMobile.getText().toString())) {
                    showToast(R.string.warn_input_mobile);
                }else{
                    createOrder();
                }
                break;
        }
    }

    private void createOrder() {
        showLoadingDialog();
        String mobile=edtMobile.getText().toString();
        Subscription subscription=SecondRetrofitHelper.getInstance().landlordAddShortApply(room_id,mobile,startDate,endDate)
                .compose(RxUtil.<DataInfo<AddRentingBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AddRentingBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<AddRentingBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            if (dataInfo.success()){
                               PwsOrderDetailsActivity.toActivity(CreateOrderActivity.this,room_id, dataInfo.data().getOrder_id(),room_name);
                               finish();
                            }
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
    public  boolean isMobileNO(String mobiles) {
        return mobiles.length()==11&&mobiles.startsWith("1");
    }
}
