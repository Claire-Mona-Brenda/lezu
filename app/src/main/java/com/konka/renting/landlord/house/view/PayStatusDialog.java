package com.konka.renting.landlord.house.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.konka.renting.R;
import com.konka.renting.event.LandlordHouseInfoEvent;
import com.konka.renting.utils.RxBus;

/**
 * Created by ATI on 2018/5/4.
 */

public class PayStatusDialog extends AppCompatDialog {
    boolean paystatus;
    Button tf;
    String order_no;
    TextView status;
    TextView tile;
    TextView p_total;
    PayReTry payReTry;
    String total;
    ImageView back;
    String reason;
    Activity activity;
    public PayStatusDialog(@NonNull Activity context, boolean paystatu, String total) {
        super(context);
        this.paystatus=paystatu;
        this.total=total;
        activity=context;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    public PayStatusDialog setPayReTry(PayReTry payReTry){
            this.payReTry=payReTry;
            return this;
    }
    public PayStatusDialog setFailReason(String reason){
        this.reason=reason;
        return this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置一个布局
        setContentView(R.layout.lib_pay_success_activity);
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        status=findViewById(R.id.status);
        p_total=findViewById(R.id.total);
        tile=findViewById(R.id.tile);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paystatus){
                    activity.finish();
                    RxBus.getDefault().post(new LandlordHouseInfoEvent(-11));
                }
                PayStatusDialog.this.dismiss();
            }
        });
        p_total.setText(total);
        tf = findViewById(R.id.next);
        tf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paystatus){
                    activity.finish();
                    RxBus.getDefault().post(new LandlordHouseInfoEvent(-11));
                    PayStatusDialog.this.dismiss();
                }else{
                    if(payReTry!=null){
                        payReTry.reTry();
                    }
                }
            }
        });
        if(!paystatus){
            status.setCompoundDrawablesWithIntrinsicBounds(null,this.getContext().getResources().getDrawable(R.drawable.icon_open_failer),null,null);
            status.setText("支付失败");
            tile.setText("失败原因");
            if(reason!=null){
                p_total.setText(reason);
            }
            tf.setText("重试");
        }
    }
    public interface PayReTry{
        public void reTry();
    }
}