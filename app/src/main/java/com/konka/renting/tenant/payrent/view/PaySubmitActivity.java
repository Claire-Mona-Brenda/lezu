package com.konka.renting.tenant.payrent.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class PaySubmitActivity extends Activity implements View.OnClickListener{
    TextView xh;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    AppCompatImageView imageView;
    PayOrder missionEnity;

   TextView detail_text1,
            detail_text2;
TextView total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_payrent_submitl_activity);
        missionEnity= (PayOrder) getIntent().getParcelableExtra("pay");
        total=findViewById(R.id.total);
        xh=(TextView)findViewById(R.id.xh);
        status=(TextView)findViewById(R.id.status);
        adress=(TextView)findViewById(R.id.adress);
        price=(TextView)findViewById(R.id.price);
        status=(TextView)findViewById(R.id.status);
        dis_time=(TextView)findViewById(R.id.dis_time);
        imageView=(AppCompatImageView)findViewById(R.id.img_house);

                 detail_text1= findViewById(R.id.detail_text1);
                detail_text2= findViewById(R.id.detail_text2);

        bindData();
        getData();
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
        adress.setText(missionEnity.roomInfo.address);
        price.setText("￥"+missionEnity.deposit_price);
        status.setText(missionEnity.status_name);
        dis_time.setText(missionEnity.start_time+"-"+missionEnity.end_time);

    }
    public void bindData2Order(RoomInfo.RoomDescription room){
                detail_text1.setText("￥"+room.monthly_fee);
                detail_text2.setText("￥"+room.year_fee);
                if((!TextUtils.isEmpty(room.housing_price))&&(!TextUtils.isEmpty(room.year_fee))&&(!TextUtils.isEmpty(room.monthly_fee))){
                    double a=Double.parseDouble(room.housing_price);
                    double b=Double.parseDouble(room.monthly_fee);
                    double c=Double.parseDouble(room.year_fee);
                    double res=a+b+c;
                    total.setText(res+"￥");
                }


    }
    public void getData(){
        rx.Observable<DataInfo<RoomInfo>> observable = null;
        observable= RetrofitHelper.getInstance().getRoom(missionEnity.room_id);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<RoomInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomInfo>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia","");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<RoomInfo> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            homeInfoDataInfo.data();
                         RoomInfo   roomInfo=homeInfoDataInfo.data();
                            bindData2Order(roomInfo.info);
                        } else {
//                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.house_pay){
            Intent intent=new Intent(this,PayDetailActivity.class);
            intent.putExtra("pay",missionEnity);
            this.startActivity(intent);

        }else{
            this.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCompositeSubscription!=null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
