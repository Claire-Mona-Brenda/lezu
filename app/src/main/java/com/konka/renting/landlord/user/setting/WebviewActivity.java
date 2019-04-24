package com.konka.renting.landlord.user.setting;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.WebType;
import com.konka.renting.bean.WebUrlInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2018/1/2.
 */

public class WebviewActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView mWebView;
    private String type;

    public static void toActivity(Context context,String  type) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void init() {
        type = getIntent().getStringExtra("type");
        if (type.equals(WebType.WEB_ABOUT))
            setTitleText("关于我们");
        if (type.equals(WebType.WEB_PROBLEM))
            setTitleText("常见问题");
        if (type.equals(WebType.WEB_AGREEMENT))
            setTitleText("租房合同");
        if (type.equals(WebType.WEB_VOUCHER))
            setTitleText("电子收据");
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient());
        initData();
        //mWebView.loadUrl(aboutUrlBeanDataInfo.data().getApp_info().getUrl());
    }

    private void initData() {

        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().getWebUrl()
                .compose(RxUtil.<DataInfo<WebUrlInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<WebUrlInfo>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        e.printStackTrace();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<WebUrlInfo> webUrlInfoDataInfo) {

                        dismiss();
                        if (webUrlInfoDataInfo.success()){
                           if (type.equals(WebType.WEB_ABOUT))
                               mWebView.loadUrl(webUrlInfoDataInfo.data().abouts_url);
                           if (type.equals(WebType.WEB_PROBLEM))
                               mWebView.loadUrl(webUrlInfoDataInfo.data().commonProblem_url);
                           if (type.equals(WebType.WEB_AGREEMENT))
                               mWebView.loadUrl(webUrlInfoDataInfo.data().contract_url);
                           if (type.equals(WebType.WEB_VOUCHER))
                               mWebView.loadUrl(webUrlInfoDataInfo.data().receipt_url);
                        }else {
                            showToast(webUrlInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
