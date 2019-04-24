package com.konka.renting.landlord.house.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.konka.renting.R;
import com.konka.renting.bean.RoomInfo;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class PaySuccessActivity extends Activity implements View.OnClickListener {

    RoomInfo missionEnity;
    Button tf;
    String order_no;
    TextView status;
    boolean pay_status;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pay_success_activity);
        missionEnity = (RoomInfo) getIntent().getParcelableExtra("room");
        order_no=getIntent().getStringExtra("order_no");
        status=findViewById(R.id.status);
        tf = findViewById(R.id.next);
        tf.setOnClickListener(this);
        pay_status=getIntent().getBooleanExtra("pay",false);
        if(!pay_status){
            status.setCompoundDrawables(null,this.getResources().getDrawable(R.drawable.icon_open_failer),null,null);
            status.setText("支付失败");
        }

    }

    private CompositeSubscription mCompositeSubscription;

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public void bindData() {

    }


    @Override
    public void onClick(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
