package com.konka.renting.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.konka.renting.KonkaApplication;
import com.konka.renting.base.IPayResCall;

import java.util.Map;

/**
 * Created by kaite on 2018/5/1.
 */

public class AliPayUtil {
    private static final int SDK_PAY_FLAG = 1;
    private Activity context;
    private ALiPayBuilder builder;

    private AliPayUtil(ALiPayBuilder builder) {
        this.builder = builder;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

//            返回码	含义
//            9000	订单支付成功
//            8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
//            4000	订单支付失败
//            5000	重复请求
//            6001	用户中途取消
//            6002	网络连接出错
//            6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
//            其它	其它支付错误
            AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
            IPayResCall iPayResCall= KonkaApplication.getInstance().curPay;
            switch (payResult.getResultStatus()) {
                case "9000":
                    if(iPayResCall!=null){
                        iPayResCall.payResCall(0,"");
                    }
                    Toast.makeText(context,"支付成功",Toast.LENGTH_SHORT).show();
                    break;
                case "8000":
                    Toast.makeText(context,"正在处理中",Toast.LENGTH_SHORT).show();
                    break;
                case "4000":
                    if(iPayResCall!=null){
                        iPayResCall.payResCall(-1,"订单支付失败");
                    }
                    Toast.makeText(context,"订单支付失败",Toast.LENGTH_SHORT).show();
                    break;
                case "5000":
                    Toast.makeText(context,"重复请求",Toast.LENGTH_SHORT).show();
                    break;
                case "6001":
                    Toast.makeText(context,"已取消支付",Toast.LENGTH_SHORT).show();
                    break;
                case "6002":
                    Toast.makeText(context,"网络连接出错",Toast.LENGTH_SHORT).show();
                    break;
                case "6004":
                    Toast.makeText(context,"正在处理中",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if(iPayResCall!=null){
                        iPayResCall.payResCall(-1,"订单支付失败");
                    }
                    Toast.makeText(context,"支付失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 签名在服务端来做
     * @param context
     * @param orderInfo
     */
    public void toALiPay(final Activity context,final String orderInfo) {
        this.context = context;
        Log.e("orderinfo",orderInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                Map<String, String> result = alipay.payV2
                        (orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public static class ALiPayBuilder {
        private String rsa2="";
        private String rsa="";
        private String appid;
        private String money;
        private String title;
        private String notifyUrl;
        private String orderTradeId;

        public AliPayUtil build() {
            return new AliPayUtil(this);
        }

        public String getOrderTradeId() {
            return orderTradeId;
        }

        public ALiPayBuilder setOrderTradeId(String orderTradeId) {
            this.orderTradeId = orderTradeId;
            return this;
        }

        public String getRsa2() {
            return rsa2;
        }

        public ALiPayBuilder setRsa2(String rsa2) {
            this.rsa2 = rsa2;
            return this;
        }

        public String getRsa() {
            return rsa;
        }

        public ALiPayBuilder setRsa(String rsa) {
            this.rsa = rsa;
            return this;
        }

        public String getAppid() {
            return appid;
        }

        public ALiPayBuilder setAppid(String appid) {
            this.appid = appid;
            return this;
        }

        public String getMoney() {
            return money;
        }

        public ALiPayBuilder setMoney(String money) {
            this.money = money;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public ALiPayBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public ALiPayBuilder setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }
    }

}
