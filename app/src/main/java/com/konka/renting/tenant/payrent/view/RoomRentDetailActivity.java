package com.konka.renting.tenant.payrent.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.bean.TenantOrderDetailBean;
import com.konka.renting.bean.WebType;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.setting.WebviewActivity;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class RoomRentDetailActivity extends Activity implements View.OnClickListener {
//    LinearLayout linearLayout;
    TextView xh;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    TextView data_status;
    AppCompatImageView imageView;
    AppCompatImageView fd;
    PayOrder missionEnity2;
//    Button tf;

    TextView detail_text1,
            detail_text2,
            detail_text3,
            detail_text4,
            detail_text5,
            detail_text6,
            detail_text7;
    TextView ll_name, ll_phone;
    TextView total;
boolean notshow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_payrent_hisdetail_activity);
        missionEnity2 = (PayOrder) getIntent().getParcelableExtra("pay");
        notshow=getIntent().getBooleanExtra("notshow",false);
//        linearLayout = findViewById(R.id.btn_group);
//        linearLayout.setVisibility(View.GONE);
        total = findViewById(R.id.total);
        xh = (TextView) findViewById(R.id.xh);
        status = (TextView) findViewById(R.id.status);
        adress = (TextView) findViewById(R.id.adress);
        price = (TextView) findViewById(R.id.price);
        status = (TextView) findViewById(R.id.status);
        dis_time = (TextView) findViewById(R.id.dis_time);
        data_status = (TextView) findViewById(R.id.data_status);
        imageView = (AppCompatImageView) findViewById(R.id.img_house);
        fd = (AppCompatImageView) findViewById(R.id.fd);
        findViewById(R.id.sj).setOnClickListener(this);
        if(notshow){
            findViewById(R.id.my_bottom).setVisibility(View.GONE);
        }else{
            findViewById(R.id.my_bottom).setVisibility(View.VISIBLE);
        }


        detail_text1 = findViewById(R.id.detail_text1);
        detail_text2 = findViewById(R.id.detail_text2);
        detail_text3 = findViewById(R.id.detail_text3);
        detail_text4 = findViewById(R.id.detail_text4);
        detail_text5 = findViewById(R.id.detail_text5);
        detail_text6 = findViewById(R.id.detail_text6);
        detail_text7 = findViewById(R.id.detail_text7);
        ll_name = findViewById(R.id.ll_name);
        ll_phone = findViewById(R.id.ll_phone);

//        tf = findViewById(R.id.btn_tf);
//        tf.setOnClickListener(this);
        getOrderDetail(missionEnity2.merge_order_no);
    }

    private CompositeSubscription mCompositeSubscription;

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }


    public void bindData2Order(TenantOrderDetailBean room) {
        TenantOrderDetailBean.InfoBean.RoomInfoBean missionEnity=room.getInfo().getRoomInfo();
        Picasso.get().load(missionEnity.getImage()).into(imageView);
        Picasso.get().load(missionEnity.getLandlordInfo().getHeadimgurl()).into(fd);
        xh.setText("订单编号：" + room.getInfo().getMerge_order_no());
//        data_status.setText(missionEnity.status);
        adress.setText(missionEnity.getRoom_name());
        price.setText("￥" +  room.getInfo().getHousing_price());
        status.setText(missionEnity2.status_name);
        dis_time.setText(missionEnity2.start_time + "-" + missionEnity2.end_time);
        detail_text1.setText("￥" +  room.getInfo().getHousing_price());
//        detail_text1.setText("￥" + room.housing_price);
        detail_text2.setText("￥" + missionEnity.getWater_rent());
        detail_text3.setText("￥" + missionEnity.getElectric_rent());
        detail_text4.setText("￥" +  room.getInfo().getProperty_amount());
        detail_text5.setText("￥" + room.getInfo().getLitter_amount());
        detail_text6.setText("￥" + room.getInfo().getCost_amount());
        detail_text7.setText("￥" +  room.getInfo().getLock_fee());
        ll_name.setText(missionEnity.getLandlordInfo().getReal_name());
        ll_phone.setText(missionEnity.getLandlordInfo().getTel());
//        double a,b,c,d,e,f,g,h;
//        try{
//            a = Double.parseDouble(missionEnity.deposit_price);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            a = 0;
//        }
//        try{
//            b = Double.parseDouble(room.water_rent);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            b = 0;
//        }
//        try{
//            c = Double.parseDouble(room.electric_rent);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            c = 0;
//        }
//        try{
//            d = Double.parseDouble(room.property_rent);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            d = 0;
//        }
//        try{
//             e= Double.parseDouble(room.monthly_fee);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            e = 0;
//        }
//        try{
//             f= Double.parseDouble(room.litter_rent);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            f = 0;
//        }
//        try{
//            g= Double.parseDouble(room.monthly_fee);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            g = 0;
//        }
//
////             b = Double.parseDouble(room.monthly_fee);
////             c = Double.parseDouble(room.year_fee);
////             d = Double.parseDouble(room.water_rent);
////             e= Double.parseDouble(room.electric_rent);
////             f = Double.parseDouble(room.litter_rent);
////             g = Double.parseDouble(room.monthly_fee);
//            double res = a + b + c+d+e+f+g;
            total.setText( room.getInfo().getTotal() + "￥");

    }

    private void getOrderDetail(String orderNum) {
        ShowToastUtil.showLoadingDialog(this);
        Subscription subscription = RetrofitHelper.getInstance().getTenantOrderDetail(orderNum)
                .compose(RxUtil.<DataInfo<TenantOrderDetailBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<TenantOrderDetailBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ShowToastUtil.dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<TenantOrderDetailBean> orderDetailBeanDataInfo) {

                        if (orderDetailBeanDataInfo.success()) {
                            bindData2Order(orderDetailBeanDataInfo.data());
                            ShowToastUtil.dismiss();

                        } else {
                            ShowToastUtil.showWarningToast(RoomRentDetailActivity.this,orderDetailBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.house_pay) {
            Intent intent = new Intent(this, PayDetailActivity.class);
            intent.putExtra("pay", missionEnity2);
            this.startActivity(intent);
            this.finish();

        } else if(view.getId() == R.id.sj) {
            WebviewActivity.toActivity(this, WebType.WEB_VOUCHER);
        }
        else {
            this.finish();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
