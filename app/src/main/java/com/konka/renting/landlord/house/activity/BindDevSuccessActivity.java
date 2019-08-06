package com.konka.renting.landlord.house.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.HouseInfoActivity;
import com.konka.renting.landlord.house.PaySeverActivity;
import com.konka.renting.landlord.house.PicViewPagerActivity;
import com.konka.renting.utils.RxUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class BindDevSuccessActivity extends BaseActivity {
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
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.btn_red_back)
    Button btnRedBack;
    @BindView(R.id.btn_pay)
    Button btnPay;

    String room_id;
    boolean is_Install;

    HouseDetailsInfoBean2 infoBean;

    public static void toActivity(Context context, String room_id, boolean is_Install) {
        Intent intent = new Intent(context, BindDevSuccessActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("is_Install", is_Install);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bind_dev_success;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.bind_dev);

        room_id = getIntent().getStringExtra("room_id");
        is_Install = getIntent().getBooleanExtra("is_Install", false);

        if (!is_Install) {
            getData();
        }

    }


    @OnClick({R.id.iv_back, R.id.btn_red_back, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_red_back:
                finish();
                break;
            case R.id.btn_pay:
                if (is_Install) {
                    PayAllMoneyActivity.toActivity(this, room_id);
                } else {
                    PaySeverActivity.toActivity(this, room_id, infoBean == null ? "" : infoBean.getAddress(), infoBean == null ? "" : infoBean.getService_date(), 1);
                }
                finish();
                break;
        }
    }


    private void getData() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().getHouseInfo2(room_id)
                .compose(RxUtil.<DataInfo<HouseDetailsInfoBean2>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<HouseDetailsInfoBean2>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<HouseDetailsInfoBean2> dataInfo) {
                        if (dataInfo.success()) {
                            infoBean = dataInfo.data();
                        }

                    }
                }));
        addSubscrebe(subscription);
    }
}
