package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddHouseCompleteActivity extends BaseActivity {

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
    @BindView(R.id.activity_add_house_complete_img_pic)
    ImageView mImgPic;
    @BindView(R.id.activity_add_house_complete_tv_name)
    TextView mTvName;
    @BindView(R.id.activity_add_house_complete_tv_info)
    TextView mTvInfo;
    @BindView(R.id.activity_add_house_complete_btn_bind)
    Button mBtnBind;
    @BindView(R.id.activity_add_house_complete_btn_rent)
    Button mBtnRent;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, AddHouseIntroduceActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_house_complete;
    }

    @Override
    public void init() {

    }


    @OnClick({R.id.iv_back, R.id.activity_add_house_complete_btn_bind, R.id.activity_add_house_complete_btn_rent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_add_house_complete_btn_bind:
                break;
            case R.id.activity_add_house_complete_btn_rent:
                break;
        }
    }
}
