package com.konka.renting.landlord;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.igexin.sdk.PushManager;
import com.konka.renting.R;
import com.konka.renting.base.BaseMainActivity;
import com.konka.renting.bean.AppConfigBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.HouseFragment;
import com.konka.renting.landlord.order.OrderFragment;
import com.konka.renting.landlord.user.UserFragment;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.service.GeTuiIntentService;
import com.konka.renting.service.GeTuiPushService;
import com.konka.renting.tenant.TenantMainActivity;
import com.konka.renting.utils.AppManager;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonProgressDialog;
import com.konka.renting.widget.UpdateAppPopupwindow;
import com.konka.renting.widget.UpdateEvent;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 房东主页
 */
public class MainActivity extends BaseMainActivity {

//    private List<HotCity> mCities = new ArrayList<>();
//    private List<City> mAllCities = new ArrayList<>();

    private CommonProgressDialog pBar;
    private static String DOWNLOAD_NAME = "konkasafe";
    private String url;
    DensityUtil densityUtil = new DensityUtil();
    UpdateAppPopupwindow updateAppPopupwindow;
    public AppConfigBean mAppConfigBean;

    public static void toMainActivity(Context context, int type) {
        if (LoginInfo.isLandlord(type))
            toActivity(context);
        else
            TenantMainActivity.toActivity(context);
    }

    private static void toActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiPushService.class);
        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);
        if (LoginUserBean.getInstance().getIs_lodge_identity() == null || LoginUserBean.getInstance().getIs_lodge_identity().equals("")) {
            getUserInfo();
        }
        //neadGoTo(getIntent());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_landlord_main;
    }

    @Override
    public int getFragmentCount() {
        return 3;
    }

    @Override
    public int getDefaultIndex() {
        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        return 0;
    }

    @Override
    public void initFragment(int index) {

        if (mFragments[index] != null)
            return;

        switch (index) {
            case 0:
//                mFragments[index] = HomeFragment.newInstance();
                mFragments[index] = UserFragment.newInstance();
                break;
            case 1:
                mFragments[index] = OrderFragment.newInstance();
                break;
            case 2:
                mFragments[index] = HouseFragment.newInstance();
                break;
        }
    }

//    private void selectionCity() {
//        Log.e(TAG, "selectionCity: ");
//        CityPicker.getInstance()
//                .setFragmentManager(getSupportFragmentManager())    //此方法必须调用
//                .enableAnimation(true)    //启用动画效果
////                .setAnimationStyle(anim)	//自定义动画
//                .setLocatedCity(new LocatedCity("北京市", "北京市", "101210101"))  //APP自身已定位的城市，默认为null（定位失败）
//                .setHotCities(mCities)    //指定热门城市
//                .setAllCities(mAllCities)
//                .setOnPickListener(new OnPickListener() {
//                    @Override
//                    public void onPick(int position, City data) {
//                        if (data == null)
//                            return;
//                        RxBus.getDefault().post(data);
//                    }
//
//                    @Override
//                    public void onLocate() {
//                        //开始定位，这里模拟一下定位
////                        new Handler().postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                //定位完成之后更新数据
////                                CityPicker.getInstance()
////                                        .locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
////                            }
////                        }, 2000);
//                    }
//                })
//                .show();
//    }

    @Override
    public int[] getMenuIds() {
        return new int[]{
                R.id.tv_user, R.id.tv_order, R.id.tv_house};
    }

    public int[] getMenuDrawables() {
        return new int[]{
                R.drawable.navigation_user, R.drawable.navigation_order, R.drawable.navigation_house};
    }

    @Override
    public boolean menuClicked(int index) {
        return false;
    }

    @Override
    public void init() {
        super.init();

        initFirstInfo();
//        getUserInfo();

        initFragment(0);
        switchContent(1, 0);

        int[] drawables = getMenuDrawables();
        for (int i = 0; i < getFragmentCount(); i++) {
            mBottomViews[i] = findViewById(mMenuIds[i]);
            final int finalI = i;
            mBottomViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuItemClicked(finalI);
                }
            });
            Drawable d = ContextCompat.getDrawable(this, drawables[i]);
            d.setBounds(0, 0, densityUtil.dip2px(20f), densityUtil.dip2px(20f));
            ((TextView) mBottomViews[i]).setCompoundDrawables(null, d, null, null);
        }
        PushManager.getInstance().bindAlias(this, LoginUserBean.getInstance().getMobile());

        mBottomViews[0].setSelected(true);
//        addRxBusSubscribe(LocationEvent.class, new Action1<LocationEvent>() {
//            @Override
//            public void call(LocationEvent locationEvent) {
//                selectionCity();
//            }
//        });
//        getServerCity();
//        getAllCity();
    }

    @Override
    public void switchContent(int fromIndex, int toIndex) {
        super.switchContent(fromIndex, toIndex);
        switch (toIndex) {
            case 0:
                UIUtils.setDarkStatusIcon(this, false);
                break;
            case 1:
                UIUtils.setDarkStatusIcon(this, true);
                break;
            case 2:
                UIUtils.setDarkStatusIcon(this, true);
                break;
        }
    }

    private void initFirstInfo() {
        RxPermissions rxPermission = new RxPermissions(mActivity);
        rxPermission.request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            checkVersion();
                        }
                    }
                });

        addRxBusSubscribe(UpdateEvent.class, new Action1<UpdateEvent>() {
            @Override
            public void call(UpdateEvent updateEvent) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(updateEvent.url);//此处填链接
                intent.setData(content_url);
                startActivity(intent);
//                pBar = new CommonProgressDialog(mActivity);
//                pBar.setCanceledOnTouchOutside(false);
//                pBar.setTitle("正在下载");
//                pBar.setCustomTitle(LayoutInflater.from(
//                        mActivity).inflate(
//                        R.layout.title_dialog, null));
//                pBar.setMessage("正在下载");
//                pBar.setIndeterminate(true);
//                pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                pBar.setCancelable(true);
//                final DownloadTask downloadTask = new DownloadTask(
//                        mActivity);
//                downloadTask.execute(url);
            }
        });
    }

    private void checkVersion() {

        showLoadingDialog();
//        Subscription subscription = SecondRetrofitHelper.getInstance().appConfig(String.valueOf(getVerName(mActivity)))
        Subscription subscription = SecondRetrofitHelper.getInstance().appConfig("1", getVerName(mActivity))
                .compose(RxUtil.<DataInfo<AppConfigBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<AppConfigBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<AppConfigBean> info) {

                        dismiss();
                        if (info.success()) {
                            mAppConfigBean = info.data();
                            if (info.data().getVersion().getIs_forced() != 2) {
                                showUpdatePopup(info.data().getVersion());
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    private void showUpdatePopup(AppConfigBean.VersionBean mData) {
        // 开启 popup 时界面透明
        if (updateAppPopupwindow == null)
            updateAppPopupwindow = new UpdateAppPopupwindow(this, mData);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        updateAppPopupwindow.showAtLocation(findViewById(R.id.main_layout), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        updateAppPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private void getUserInfo() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordUserDetailsInfo()
                .compose(RxUtil.<DataInfo<LandlordUserDetailsInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<LandlordUserDetailsInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        showToast("请先登录");
                        LoginUserBean.getInstance().reset();
                        LoginUserBean.getInstance().save();
                        startActivity(new Intent(MainActivity.this, LoginNewActivity.class));
                        finish();
                    }

                    @Override
                    public void onNext(DataInfo<LandlordUserDetailsInfoBean> userInfoBeanDataInfo) {
                        dismiss();
                        if (userInfoBeanDataInfo.success()) {
                            if (userInfoBeanDataInfo.data() != null) {
                                LoginUserBean.getInstance().setIs_lodge_identity(userInfoBeanDataInfo.data().getIs_identity() + "");
                                LoginUserBean.getInstance().setRealname(userInfoBeanDataInfo.data().getReal_name());
                                LoginUserBean.getInstance().setIdentity(userInfoBeanDataInfo.data().getIdentity());
                                LoginUserBean.getInstance().save();
                            }
                        } else {
                            showToast(userInfoBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

//    private void getAllCity() {
//        Subscription subscription = RetrofitHelper.getInstance().getAllLetterCity()
//                .compose(RxUtil.<DataInfo<DicEntity>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<DicEntity>>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<DicEntity> cityInfoDataInfo) {
//                        if (cityInfoDataInfo.success()) {
//                            mAllCities.clear();
//                            int size=cityInfoDataInfo.data().lists.size();
//                            for (int i = 0; i < size; i++) {
//                                DicEntity.Content content=cityInfoDataInfo.data().lists.get(i);
//                                City city = new City(content.name, "", content.index, content.index);
//                                mAllCities.add(city);
//                            }
//
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }
//
//    private void getServerCity() {
//        Subscription subscription = RetrofitHelper.getInstance().getHotCity()
//                .compose(RxUtil.<DataInfo<CityInfo>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<CityInfo>>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<CityInfo> cityInfoDataInfo) {
//                        if (cityInfoDataInfo.success()) {
//                            mCities.addAll(cityInfoDataInfo.data().lists);
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        synchronized (mActivities) {
            AppManager.getInstance().killAllActivity();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (updateAppPopupwindow != null && updateAppPopupwindow.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void update() {
        File apkFile = new File(Environment
                .getExternalStorageDirectory(), DOWNLOAD_NAME);
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = mActivity.getPackageManager().canRequestPackageInstalls();
                if (b) {
                    Uri apkUri = FileProvider.getUriForFile(mActivity, "com.kangjia.fileProvider", apkFile);//在AndroidManifest中的android:authorities值
                    Log.e("uri...", apkFile.getPath() + "");
                    Log.e("uri...", apkUri + "");
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(install);
                } else {
                    //请求安装未知应用来源的权限
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 111);
                }
            } else {
                Uri apkUri = FileProvider.getUriForFile(mActivity, "com.kangjia.fileProvider", apkFile);//在AndroidManifest中的android:authorities值
                Log.e("uri...", apkFile.getPath() + "");
                Log.e("uri...", apkUri + "");
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(install);
            }

        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);
        }
    }

    /**
     * 下载应用
     *
     * @author Administrator
     */
    public class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(Environment.getExternalStorageDirectory(),
                            DOWNLOAD_NAME);
                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }
                } else {
                    Toast.makeText(mActivity, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pBar.setIndeterminate(false);
            pBar.setMax(100);
            pBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pBar.dismiss();
            if (result != null) {
//                // 申请多个权限。大神的界面
//                AndPermission.with(MainActivity.this)
//                        .requestCode(REQUEST_CODE_PERMISSION_OTHER)
//                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
//                        .rationale(new RationaleListener() {
//                                       @Override
//                                       public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                                           // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
//                                           AndPermission.rationaleDialog(MainActivity.this, rationale).show();
//                                       }
//                                   }
//                        )
//                        .send();
                // 申请多个权限
                /*Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
                Log.e("result+++",result);*/
                //update();
            } else {
                // Toast.makeText(context, "File downloaded",
                // Toast.LENGTH_SHORT)
                // .show();
                update();
            }
        }
    }


}
