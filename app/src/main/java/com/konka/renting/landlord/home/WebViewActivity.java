package com.konka.renting.landlord.home;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class WebViewActivity extends BaseActivity {

    String mUrl;
    @BindView(R.id.webview)
    WebView mWebview;

    public static void toActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void init() {
        setTitleText(R.string.landlord_article_title);
        mUrl = getIntent().getStringExtra("url");
        mWebview.loadUrl(mUrl);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
