package com.konka.renting.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

/**
 * @author 郭阳鹏
 * @date 创建时间：2017/3/13
 * @description Application
 */
public abstract class BaseApplication extends Application {

    private static Context mContext;
    public IPayResCall curPay;

    public static int statusHeight=-1;

    public static BaseApplication getInstance() {
        return (BaseApplication) mContext;
    }

    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
//        CrashReport.initCrashReport(getApplicationContext(), getBuglyKey(), true);
        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                e.printStackTrace();
            }
        });
//        ButterKnife.setDebug(true);
        init();
    }

    public abstract String getBuglyKey();

    public abstract void init();

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
