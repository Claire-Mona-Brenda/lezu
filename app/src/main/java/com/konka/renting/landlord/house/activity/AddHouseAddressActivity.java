package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.event.AddHouseCompleteEvent;
import com.konka.renting.landlord.house.ChooseLocationEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class AddHouseAddressActivity extends BaseActivity {
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
    @BindView(R.id.activity_add_house_address_tv_location)
    TextView mTvLocation;
    @BindView(R.id.activity_add_house_address_ll_location)
    LinearLayout mLlLocation;
    @BindView(R.id.activity_add_house_address_edt_real_location)
    EditText mEdtRealLocation;
    @BindView(R.id.activity_add_house_address_ll_real_location)
    LinearLayout mLlRealLocation;

    PoiItem mPoiItem;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, AddHouseAddressActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_house_address;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.add_house);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);
        tvRight.setText(R.string.common_next);
        tvRight.setTextColor(getResources().getColor(R.color.text_blue));
        tvRight.setVisibility(View.VISIBLE);

        addRxBusSubscribe(ChooseLocationEvent.class, new Action1<ChooseLocationEvent>() {
            @Override
            public void call(ChooseLocationEvent event) {
                mPoiItem = event.getPoiItem();
                mTvLocation.setText(mPoiItem.getSnippet());
                mTvLocation.setTextColor(getResources().getColor(R.color.text_black));
            }
        });
        addRxBusSubscribe(AddHouseCompleteEvent.class, new Action1<AddHouseCompleteEvent>() {
            @Override
            public void call(AddHouseCompleteEvent addHouseCompleteEvent) {
                finish();
            }
        });

    }


    @OnClick({R.id.iv_back, R.id.tv_right, R.id.activity_add_house_address_ll_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if (mPoiItem == null) {
                    showToast(R.string.add_house_choose_location_tips);
                    return;
                }
                if (TextUtils.isEmpty(mEdtRealLocation.getText().toString())) {
                    showToast(R.string.please_fill_real_location);
                    return;
                }
                AddHouseInfoActivity.toActivity(this, mPoiItem,mEdtRealLocation.getText().toString());
                break;
            case R.id.activity_add_house_address_ll_location:
                ChooseLocationActivity.toActivity(this);
                break;
        }
    }
}
