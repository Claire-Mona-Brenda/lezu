package com.konka.renting.landlord.user.other;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.AppConfigBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class AfterProcessActivity extends BaseActivity {
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
    @BindView(R.id.activity_after_process_tv_process)
    TextView tvProcess;

    AppConfigBean.DisclaimerBean disclaimerBean;

    public static void toActivity(Context context, AppConfigBean.DisclaimerBean disclaimerBean) {
        Intent intent = new Intent(context, AfterProcessActivity.class);
        intent.putExtra("DisclaimerBean", disclaimerBean);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_after_process;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.after_the_process);
        tvRight.setText(R.string.disclaimer);
        tvRight.setVisibility(View.GONE);
        disclaimerBean = getIntent().getParcelableExtra("DisclaimerBean");
        if (disclaimerBean == null) {
            getData();
        } else {
            tvProcess.setText(Html.fromHtml(disclaimerBean.getContent()));
        }
    }

    private void getData() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().appConfig("1", getVerName(mActivity))
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
                            tvProcess.setText(Html.fromHtml(info.data().getDisclaimer().getContent()));
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

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                DisclaimerActivity.toActivity(this,disclaimerBean.getContent());
                break;
        }
    }
}
