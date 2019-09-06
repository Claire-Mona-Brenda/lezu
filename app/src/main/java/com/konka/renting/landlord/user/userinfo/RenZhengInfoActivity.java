package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class RenZhengInfoActivity extends BaseActivity {


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
    @BindView(R.id.activity_renzheng_info_tv_name)
    TextView tvName;
    @BindView(R.id.activity_renzheng_info_tv_sex)
    TextView tvSex;
    @BindView(R.id.activity_renzheng_info_tv_number)
    TextView tvNumber;
    @BindView(R.id.activity_renzheng_info_tv_date)
    TextView tvDate;

    LandlordUserDetailsInfoBean infoBean;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, RenZhengInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_renzheng_info;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.info_renzheng_title);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        getData();
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    /****************************************************接口**************************************************/
    private void getData() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordUserDetailsInfo()
                .compose(RxUtil.<DataInfo<LandlordUserDetailsInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<LandlordUserDetailsInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        doFailed();
                        finish();
                    }

                    @Override
                    public void onNext(DataInfo<LandlordUserDetailsInfoBean> dataInfo) {
                        super.onNext(dataInfo);
                        dismiss();
                        if (dataInfo.success()) {
                            infoBean = dataInfo.data();
                            tvName.setText(infoBean.getReal_name());
                            tvSex.setText(infoBean.getSex());
                            if (!TextUtils.isEmpty(infoBean.getIdentity())) {
                                String id = infoBean.getIdentity().charAt(0) + "*****************" + infoBean.getIdentity().charAt(infoBean.getIdentity().length() - 1);
                                tvNumber.setText(id);
                            }
                            String startTime;
                            String endTime;
                            if (infoBean.getStart_time().length() >= 8 && infoBean.getEnd_time().length() >= 8) {
                                startTime = infoBean.getStart_time().substring(0, 4) + "." + infoBean.getStart_time().substring(4, 6) + "." + infoBean.getStart_time().substring(6);
                                endTime = infoBean.getEnd_time().substring(0, 4) + "." + infoBean.getEnd_time().substring(4, 6) + "." + infoBean.getEnd_time().substring(6);
                            } else {
                                startTime = infoBean.getStart_time();
                                endTime = infoBean.getEnd_time();
                            }
                            tvDate.setText(startTime + " - " + endTime);

                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
