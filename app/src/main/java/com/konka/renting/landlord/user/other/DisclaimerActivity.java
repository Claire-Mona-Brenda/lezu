package com.konka.renting.landlord.user.other;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DisclaimerActivity extends BaseActivity {
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
    @BindView(R.id.activity_disclaimer_tv_context)
    TextView tvContext;

    public static void toActivity(Context context, String content) {
        Intent intent = new Intent(context, DisclaimerActivity.class);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_disclaimer;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.disclaimer);
        String content=getIntent().getStringExtra("content");
        tvContext.setText(Html.fromHtml(content));
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
