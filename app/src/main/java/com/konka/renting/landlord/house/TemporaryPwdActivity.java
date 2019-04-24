package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.bean.ShareBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class TemporaryPwdActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_pwd_1)
    TextView tvPwd1;
    @BindView(R.id.tv_pwd_2)
    TextView tvPwd2;
    @BindView(R.id.tv_pwd_3)
    TextView tvPwd3;
    @BindView(R.id.tv_pwd_4)
    TextView tvPwd4;
    @BindView(R.id.tv_pwd_5)
    TextView tvPwd5;
    @BindView(R.id.tv_pwd_6)
    TextView tvPwd6;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_more)
    TextView tvMore;

    String room_id;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;

    int queryTime = 0;
    String password_id;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, TemporaryPwdActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_temporary_pwd;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.house_info_setting_temporary_pwd);
        room_id = getIntent().getStringExtra("room_id");
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                sharePwd(room_id);
            }
        });
        refresh.setEnableLoadmore(false);
        refresh.autoRefresh();
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    private void sharePwd(String roomid) {

        Subscription subscription = SecondRetrofitHelper.getInstance().sharePassword(roomid)
                .compose(RxUtil.<DataInfo<PwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        refresh.finishRefresh();
                        UIUtils.displayToast("获取数据失败");
                    }

                    @Override
                    public void onNext(DataInfo<PwdBean> info) {
                        if (info.success()) {
                            String pwd = info.data().getPassword();
                            tvPwd1.setText(pwd.charAt(0) + "");
                            tvPwd2.setText(pwd.charAt(1) + "");
                            tvPwd3.setText(pwd.charAt(2) + "");
                            tvPwd4.setText(pwd.charAt(3) + "");
                            tvPwd5.setText(pwd.charAt(4) + "");
                            tvPwd6.setText(pwd.charAt(5) + "");
                            tvEndTime.setText(info.data().getExpire_time());
                        } else if (info.code() == 2) {
                            password_id = info.data().getPassword_id();
                            timeQuery();
                        } else {
                            refresh.finishRefresh();
                            UIUtils.displayToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void timeQuery() {
        if (queryTime > 15) {
            queryTime = 0;
            if (refresh.isRefreshing())
                refresh.finishRefresh();
            return;
        }
        queryTime++;
        Subscription subscription = Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {
                        timeQuery();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (password_id != null)
                            queryPwdResult(password_id);
                        else if (refresh.isRefreshing())
                            refresh.finishRefresh();

                    }
                });

        addSubscrebe(subscription);
    }

    private void queryPwdResult(String password_id) {

        Subscription subscription = SecondRetrofitHelper.getInstance().queryPasswordResult(password_id)
                .compose(RxUtil.<DataInfo<PwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        timeQuery();
                    }

                    @Override
                    public void onNext(DataInfo<PwdBean> shareBeanDataInfo) {
                        if (shareBeanDataInfo.success()) {
                            queryTime = 0;
                            if (refresh.isRefreshing())
                                refresh.finishRefresh();
                            String pwd = shareBeanDataInfo.data().getPassword();
                            tvPwd1.setText(pwd.charAt(0) + "");
                            tvPwd2.setText(pwd.charAt(1) + "");
                            tvPwd3.setText(pwd.charAt(2) + "");
                            tvPwd4.setText(pwd.charAt(3) + "");
                            tvPwd5.setText(pwd.charAt(4) + "");
                            tvPwd6.setText(pwd.charAt(5) + "");
                            tvEndTime.setText(shareBeanDataInfo.data().getExpire_time());
                        } else {
                            timeQuery();
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
