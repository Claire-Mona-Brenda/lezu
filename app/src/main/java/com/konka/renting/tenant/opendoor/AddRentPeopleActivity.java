package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.event.AddShareRentEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class AddRentPeopleActivity extends BaseActivity {
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
    @BindView(R.id.activity_add_rent_people_edt_account)
    EditText mEdtAccount;
    @BindView(R.id.activity_add_rent_people_tv_sure)
    TextView mTvSure;

    String order_id;
    boolean isNeedTo;//是否需要跳转租客列表

    public static void toActivity(Context context, String order_id, boolean isNeedTo) {
        Intent intent = new Intent(context, AddRentPeopleActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("isNeedTo", isNeedTo);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_rent_people;
    }

    @Override
    public void init() {
        order_id = getIntent().getStringExtra("order_id");
        isNeedTo = getIntent().getBooleanExtra("isNeedTo", false);

        tvTitle.setText(R.string.title_add_account);

    }


    @OnClick({R.id.iv_back, R.id.activity_add_rent_people_tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_add_rent_people_tv_sure:
                String phone = mEdtAccount.getText().toString();
                if (phone.length() < 11) {
                    showToast(R.string.please_input_phone);
                } else {
                    addAccount(phone);
                }
                break;
        }
    }


    /**********************************************接口*********************************************/
    /**
     * 添加合租
     */
    private void addAccount(String phone) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().addShareOrder(order_id, phone)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            dismiss();
                            if (isNeedTo) {
                                RxBus.getDefault().post(new AddShareRentEvent(1, order_id));
                                ShareRentListActivity.toActivity(mActivity, order_id);
                            } else {
                                RxBus.getDefault().post(new AddShareRentEvent(1, order_id));
                            }
                            finish();
                        } else {
                            dismiss();
                            ShowToastUtil.showNormalToast(getBaseContext(), dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
