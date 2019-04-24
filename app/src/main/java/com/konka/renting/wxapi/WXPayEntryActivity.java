package com.konka.renting.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.IPayResCall;
import com.konka.renting.landlord.user.withdrawcash.RechargeEvent;
import com.konka.renting.utils.Constants;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.UIUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("123123","====="+resp.errCode+"==="+resp.errStr+"===="+resp.openId+"==="+resp.transaction);
        IPayResCall iWechatCall=KonkaApplication.getInstance().curPay;
        if(iWechatCall!=null){
            iWechatCall.payResCall(resp.errCode,"支付失败");
        }
        switch (resp.errCode) {
            case 0:
                // TODO: 2018/4/30 成功
                UIUtils.displayToast("支付成功");
                RxBus.getDefault().post(new RechargeEvent());
                finish();
                break;
            case -1:
                UIUtils.displayToast("支付失败");
                Log.e("error_msg",resp.errStr+111);
                finish();
                break;
            case -2:
                UIUtils.displayToast("支付取消");
                finish();
                break;
        }
    }

}