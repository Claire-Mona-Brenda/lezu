package com.konka.renting.landlord.user;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.AppConfigBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.WebType;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.UserProtocolActivity;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class ProblemActivity extends BaseActivity {
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
        Intent intent = new Intent(context, ProblemActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_problem;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.problem);
        getData();
    }

    private void getData() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().appConfig("1",getVerName(mActivity))
                .compose(RxUtil.<DataInfo<AppConfigBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AppConfigBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<AppConfigBean> info) {

                        dismiss();
                        if (info.success()) {
                            tvContext.setText(Html.fromHtml(info.data().getCommon_problem()));
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
