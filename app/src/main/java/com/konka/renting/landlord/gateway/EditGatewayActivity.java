package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.event.EditGatwayEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by kaite on 2018/4/3.
 */

public class EditGatewayActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    EditText mTvName;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    private String gatewayName;
    private String gatewayId;

    public static void toActivity(Context context, String gatewayName, String gatewayId) {
        Intent intent = new Intent(context, EditGatewayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", gatewayName);
        bundle.putString("id", gatewayId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gateway_edit;
    }

    @Override
    public void init() {
        setTitleText(R.string.edit_gateway_name);
        tvRight.setText(R.string.common_save);
        tvRight.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        gatewayName = bundle.getString("name");
        gatewayId = bundle.getString("id");

        mTvName.setText(gatewayName);
        mTvName.setSelection(gatewayName.length());
        mTvName.requestFocus();

        mTvName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String str = s.toString().replace(" ","");
                    mTvName.setText(str);
                    mTvName.setSelection(str.length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if (mTvName.getText().toString().isEmpty()) {
                    ShowToastUtil.showNormalToast(this, getString(R.string.device_gateway_name_hint));
                } else {
                    editGatway();
                }
                break;
        }
    }

    private void editGatway() {

        showLoadingDialog();
        final String name=mTvName.getText().toString();
        Subscription subscription = SecondRetrofitHelper.getInstance().editGateway(gatewayId, name)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new EditGatwayEvent(name));
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
