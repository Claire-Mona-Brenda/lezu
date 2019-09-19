package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
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

public class ResultActivity extends BaseActivity {
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
    @BindView(R.id.activity_result_img_result)
    ImageView mImgResult;
    @BindView(R.id.activity_result_tv_result)
    TextView mTvResult;
    @BindView(R.id.activity_result_tv_tips)
    TextView mTvTips;
    @BindView(R.id.activity_result_btn_sure)
    Button mBtnSure;

    String title;
    String result;
    String tips;
    boolean isSuccess;

    public static void toActivity(Context context, String title, String result, String tips, boolean isSuccess) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("result", result);
        intent.putExtra("tips", tips);
        intent.putExtra("isSuccess", isSuccess);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_result;
    }

    @Override
    public void init() {
        title = getIntent().getStringExtra("title");
        result = getIntent().getStringExtra("result");
        tips = getIntent().getStringExtra("tips");
        isSuccess = getIntent().getBooleanExtra("isSuccess", true);


        tvTitle.setText(title);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        mImgResult.setImageResource(isSuccess ? R.mipmap.icon_success : R.mipmap.icon_failed);
        mTvResult.setText(result);
        mTvTips.setText(tips);


    }

    @OnClick({R.id.iv_back, R.id.activity_result_btn_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.activity_result_btn_sure:
                finish();
                break;
        }
    }
}
