package com.konka.renting.tenant.payrent.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class RoomTFActivity extends Activity implements View.OnClickListener{
    LinearLayout linearLayout;
    TextView xh;
    TextView h_lx;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    TextView data_status;
    AppCompatImageView imageView;
    PayOrder missionEnity;
    EditText ms;
    Button tf;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_room_tf);
        ms=findViewById(R.id.h_edit8);
        missionEnity= (PayOrder) getIntent().getParcelableExtra("pay");
        linearLayout=findViewById(R.id.btn_group);
        linearLayout.setVisibility(View.GONE);
        h_lx=(TextView)findViewById(R.id.h_lx);
            xh=(TextView)findViewById(R.id.xh);
            status=(TextView)findViewById(R.id.status);
            adress=(TextView)findViewById(R.id.adress);
            price=(TextView)findViewById(R.id.price);
            status=(TextView)findViewById(R.id.status);
            dis_time=(TextView)findViewById(R.id.dis_time);
            data_status=(TextView)findViewById(R.id.data_status);
            imageView=(AppCompatImageView)findViewById(R.id.img_house);
            tf=findViewById(R.id.btn_tf);
            tf.setOnClickListener(this);
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
        if(missionEnity.roomInfo.type!=null){
           h_lx.setText("【"+missionEnity.roomInfo.getType()+"】");
            int color;
            if(missionEnity.roomInfo.type.equals("1")){
                //短8fc320
                color=this.getResources().getColor(R.color.color_short);

            } else if(missionEnity.roomInfo.type.equals("2")){
                color=this.getResources().getColor(R.color.color_long);
                //长
            }else{
                color=this.getResources().getColor(R.color.color_nor);
            }
           h_lx.setTextColor(color);
        }
    }
    public  void orderCancel(String merge_order_no,
                             String refund_reason
    ){
        rx.Observable<DataInfo> observable = null;
        observable= RetrofitHelper.getInstance().updateRenting(merge_order_no,
                refund_reason,"2");

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
                            RoomTFActivity.this.finish();
                            ShowToastUtil.showSuccessToast(RoomTFActivity.this,homeInfoDataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(RoomTFActivity.this,homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back){
            this.finish();
        }else{
            orderCancel(missionEnity.merge_order_no,ms.getText().toString());

        }
    }
}
