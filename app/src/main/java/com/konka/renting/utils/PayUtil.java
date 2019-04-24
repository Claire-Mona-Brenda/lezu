package com.konka.renting.utils;

/**
 * Created by jzxiang on 17/01/2018.
 */

public class PayUtil {
/** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /**
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "";
    public static final String RSA_PRIVATE = "";


    //public static final String WECHAT_ID = ID;
    public static final String ALIPAY_ID = "2018010801688689";

   /* *//**
     *
     * @param baseActivity
     * @param content 处理结果，true代表成功
     *//*
    public static void aliPay(final BaseActivity baseActivity, Action1<Boolean> content) {
        *//**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         *//*
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ALIPAY_ID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;
        Subscription subscription = Observable.just("orderInfo")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Map<String, String>>() {
                    @Override
                    public Map<String, String> call(String s) {
                        PayTask alipay = new PayTask(baseActivity);
                        return alipay.payV2(orderInfo, true);
                    }
                })
                .map(new Func1<Map<String,String>, Boolean>() {
                    @Override
                    public Boolean call(Map<String, String> stringStringMap) {
                        PayResult payResult = new PayResult(stringStringMap);
                        *//**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         *//*
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        return TextUtils.equals(resultStatus, "9000");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(content);
        baseActivity.addSubscrebe(subscription);
    }
*/
   /* *//**
     * 微信支付
     * @param context
     * @param wxPayInfoDataInfo
     *//*
    public static void wechatPay(Context context, DataInfo<WxPayInfo> wxPayInfoDataInfo) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, WECHAT_ID, true);
        iwxapi.registerApp(WECHAT_ID);
        if (wxPayInfoDataInfo.data().getPrepayid() != null){
            PayReq result = new PayReq();
            result.appId = wxPayInfoDataInfo.data().getAppid();
            result.partnerId = wxPayInfoDataInfo.data().getPartnerid();
            result.prepayId = wxPayInfoDataInfo.data().getPrepayid();
            result.packageValue = wxPayInfoDataInfo.data().getPackageX();
            result.nonceStr = wxPayInfoDataInfo.data().getNoncestr();
            result.timeStamp = wxPayInfoDataInfo.data().getTimestamp();
            result.sign = wxPayInfoDataInfo.data().getSign();
            iwxapi.sendReq(result);
        }else {
            UIUtils.displayToast(context.getString(R.string.has_pay));
        }
    }
    *//*
    * 支付宝支付
    * *//*
    public static void aliPay(final BaseActivity context, DataInfo<AliPayInfo> aliPayInfoDataInfo, final Handler mHandler){
        if (aliPayInfoDataInfo.data().getOrder_info() != null) {
            final String orderInfo = aliPayInfoDataInfo.data().getOrder_info();
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(context);
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }
    }*/
}
