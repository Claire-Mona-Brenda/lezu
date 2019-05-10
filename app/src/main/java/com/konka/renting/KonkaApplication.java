package com.konka.renting;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.konka.renting.base.BaseApplication;
import com.konka.renting.event.LogInAgainEvent;
import com.konka.renting.event.NoNetworkEvent;
import com.konka.renting.utils.RxBus;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jzxiang on 11/03/2018.
 */

public class KonkaApplication extends BaseApplication {
    CompositeSubscription mCompositeSubscription;
    public static boolean isAgainLogin = false;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context);
            }
        });
    }

    @Override
    public String getBuglyKey() {
        return "";
    }

    @Override
    public void init() {
//安卓7.0相机权限判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        MobSDK.init(this);
        ZXingLibrary.initDisplayOpinion(this);
//        RxTool.init(this);
        mCompositeSubscription = new CompositeSubscription();
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(NoNetworkEvent.class, new Action1<NoNetworkEvent>() {
            @Override
            public void call(NoNetworkEvent noNetworkEvent) {
                ToastUtils.showShortToast(KonkaApplication.this, getString(R.string.no_NetworkConnected));
            }
        }));
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(LogInAgainEvent.class, new Action1<LogInAgainEvent>() {
            @Override
            public void call(LogInAgainEvent event) {
                isAgainLogin = true;
            }
        }));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }
}
