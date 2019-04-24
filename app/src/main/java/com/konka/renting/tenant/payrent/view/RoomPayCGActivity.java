package com.konka.renting.tenant.payrent.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.view.AutoChooseWidget;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class RoomPayCGActivity extends Activity implements View.OnClickListener{
    LinearLayout linearLayout;
    TextView xh;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    TextView data_status;
    AppCompatImageView imageView;
    PayOrder missionEnity;
    Button tf;
    TextView chose1,chose2,chose3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_room_paycg);
        missionEnity= (PayOrder) getIntent().getParcelableExtra("pay");
        linearLayout=findViewById(R.id.btn_group);
        linearLayout.setVisibility(View.GONE);

            xh=(TextView)findViewById(R.id.xh);
            status=(TextView)findViewById(R.id.status);
            adress=(TextView)findViewById(R.id.adress);
            price=(TextView)findViewById(R.id.price);
            status=(TextView)findViewById(R.id.status);
            dis_time=(TextView)findViewById(R.id.dis_time);
            data_status=(TextView)findViewById(R.id.data_status);
            imageView=(AppCompatImageView)findViewById(R.id.img_house);
            chose1=findViewById(R.id.chose1);
            chose2=findViewById(R.id.chose2);
            chose3=findViewById(R.id.chose3);
            tf=findViewById(R.id.btn_tf);

            tf.setOnClickListener(this);
        chose1.setOnClickListener(this);
        chose2.setOnClickListener(this);
        chose3.setOnClickListener(this);
            bindData();
    }
    private CompositeSubscription mCompositeSubscription;

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
    public void bindData(){
        Picasso.get().load(missionEnity.roomInfo.image).into(imageView);
     xh.setText("订单编号："+missionEnity.merge_order_no);
     data_status.setText(missionEnity.status);
     adress.setText(missionEnity.roomInfo.address);
     price.setText("￥"+missionEnity.deposit_price);
     status.setText(missionEnity.status_name);
     dis_time.setText(missionEnity.start_time+"-"+missionEnity.end_time);
    }
    public  void orderCG(String merge_order_no,
                             String ty
    ){
        rx.Observable<DataInfo> observable = null;
        observable= RetrofitHelper.getInstance().updateRentingMode(ty,merge_order_no
                );

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia","");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo homeInfoDataInfo) {

                        if (homeInfoDataInfo.success()) {
                            RoomPayCGActivity.this.finish();
                            ShowToastUtil.showSuccessToast(RoomPayCGActivity.this,homeInfoDataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(RoomPayCGActivity.this,homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }
    private void getDate() {
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy", "year" + year);
        month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day = cal.get(Calendar.DAY_OF_MONTH);
    }
    private Calendar cal;
    private int year, month, day;
    DatePickerDialog.OnDateSetListener listener;
    DatePickerDialog dialog;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chose1:
                new AutoChooseWidget(this,AutoChooseWidget.PAY_TYPE,chose1).showPopWindow();
                break;
            case R.id.chose2:
                getDate();
                listener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        chose2.setText(year + "-" + (++month) + "-" + day);      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                 dialog = new DatePickerDialog(this, 0, listener, year, month, day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
                break;
            case R.id.chose3:
                getDate();
                listener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        chose3.setText(year + "-" + (++month) + "-" + day);      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                 dialog = new DatePickerDialog(this, 0, listener, year, month, day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
                break;
            case R.id.btn_tf:
                if(chose1.getTag()!=null){
                    String type=chose1.getTag().toString();
                    if(TextUtils.isEmpty(type)){
                        type="0";
                    }
                    orderCG(missionEnity.merge_order_no,type);
                }else{
                    ShowToastUtil.showWarningToast(this,"请选择交租方式");
                }

                break;
            case R.id.back:
                this.finish();
                break;
        }
    }
}
