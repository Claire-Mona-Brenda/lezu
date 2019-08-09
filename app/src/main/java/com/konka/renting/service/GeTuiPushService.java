package com.konka.renting.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.igexin.sdk.GTServiceManager;
import com.igexin.sdk.PushService;

/**
 * @author Nate Robinson
 * @Time 2018/1/19
 */

public class GeTuiPushService extends PushService {

//    @Override
//    public void onCreate() {
//        super.onCreate();
////        Log.e("cid", PushManager.getInstance().getClientid(getApplicationContext()));
////        GTServiceManager.getInstance().onActivityCreate();Create(this);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return GTServiceManager.getInstance().onBind(intent);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
////        GTServiceManager.getInstance().onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
////        GTServiceManager.getInstance().onLowMemory();
//    }
}