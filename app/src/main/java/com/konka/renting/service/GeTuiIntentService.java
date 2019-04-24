package com.konka.renting.service;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.konka.renting.bean.MessageBean;
import com.konka.renting.utils.NotificationUtils;

/**
 * @author Nate Robinson
 * @Time 2018/1/19
 */

public class GeTuiIntentService extends GTIntentService {

    public GeTuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.e(TAG, "onReceiveServicePid -> " + "clientid = ");
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.e(TAG, "onReceiveMessageData -> " + "clientid = " + msg.getMessageId());


        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.e(TAG, "receiver payload = "+data);
            Gson gson = new Gson();
            MessageBean messageBean = gson.fromJson(data, MessageBean.class);
           /* if (TextUtils.equals(messageBean.getType(), "share_device") || TextUtils.equals(messageBean.getTitle(), "共享设备")) {
                //之前改的单次共享设备时，可以选择多个设备，所以这块没法处理 你就不用判断是不是门锁了，只要有共享设备就刷新
                RxBus.getDefault().post(new NewDeviceEvent());
            }*/
            sendSimplestNotificationWithAction(messageBean);
           /* RxBus.getDefault().post(new NewMessageEvent());
            RxBus.getDefault().post(new ShareMessageEvent(messageBean.getTitle()));*/
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.e(TAG, "onReceiveOnlineState -> " + "clientid = ");
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e(TAG, "onReceiveCommandResult -> " + "clientid = ");
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    /**
     * 发送一个点击跳转到MainActivity的消息
     */
    private void sendSimplestNotificationWithAction(MessageBean messageBean) {
        NotificationUtils notificationUtils = new NotificationUtils(this);
        notificationUtils.sendNotification(messageBean.id, messageBean.intro);
    }
}
