package com.konka.renting.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.UserProtocolBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class UserProtocolActivity extends BaseActivity {


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
    @BindView(R.id.activity_user_protocol_tv_context)
    TextView tvContext;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, UserProtocolActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_protocol;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.regist_protocol);
        getData();
    }

    private void getData() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().userProtocol()
                .compose(RxUtil.<DataInfo<UserProtocolBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<UserProtocolBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<UserProtocolBean> info) {
                        dismiss();
                        if (info.success()) {
                            tvContext.setText(Html.fromHtml(info.data().getUser_protocol()));
                        } else {
                            showToast(info.msg());
                        }
                        //setResult(RESULT_OK);
                        //finish();
                    }
                });
        addSubscrebe(subscription);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
