package com.konka.renting.landlord.house.view;


import android.content.Context;
import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.event.HousePublishEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;

import butterknife.OnClick;
import rx.Subscription;


public class HousePublishActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.radio_btn_public_type_long)
    RadioButton rbtnTypeLong;
    @BindView(R.id.radio_btn_public_type_short)
    RadioButton rbtnTypeShort;
    @BindView(R.id.tv_pay_date)
    TextView tvPayDate;
    @BindView(R.id.edit_money)
    EditText editMoney;
    @BindView(R.id.tv_rent_money_unit)
    TextView tvUnit;
    @BindView(R.id.lib_ll)
    LinearLayout libLl;

    int type = 2;
    String room_id;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, HousePublishActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.lib_house_publish_activity;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.public_title);
        tvRight.setText(R.string.common_commit);
        tvRight.setTextColor(getResources().getColor(R.color.style_main));

        room_id = getIntent().getStringExtra("room_id");

        rbtnTypeLong.setOnCheckedChangeListener(this);
        rbtnTypeShort.setOnCheckedChangeListener(this);
        rbtnTypeLong.setChecked(true);
        type = 2;
        tvUnit.setText(R.string.public_house_pay_unit_mon);
        tvPayDate.setText(R.string.public_house_pay_mon);

        editMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String num = editMoney.getText().toString();
                if (num.startsWith("0")) {
                    editMoney.setText("");
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
                if (editMoney.getText().equals("")) {
                    showToast(R.string.please_input_rent_money);
                } else {
                    submitData();
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_btn_public_type_long:
                    type = 2;
                    tvUnit.setText(R.string.public_house_pay_unit_mon);
                    tvPayDate.setText(R.string.public_house_pay_mon);
                    break;
                case R.id.radio_btn_public_type_short:
                    type = 1;
                    tvUnit.setText(R.string.public_house_pay_unit_day);
                    tvPayDate.setText(R.string.public_house_pay_day);
                    break;
            }
        }

    }

    public void submitData() {
        int t = type;
        String money = editMoney.getText().toString();
        Subscription subscription = (SecondRetrofitHelper.getInstance().publishHouse2(
                room_id,
                t + "",
                money
        )
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo homeInfoDataInfo) {
                        if (homeInfoDataInfo.success()) {
                            RxBus.getDefault().post(new HousePublishEvent(room_id, t, money));
                            HousePublishActivity.this.finish();
                            ShowToastUtil.showSuccessToast(HousePublishActivity.this, homeInfoDataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(HousePublishActivity.this, homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }


}
