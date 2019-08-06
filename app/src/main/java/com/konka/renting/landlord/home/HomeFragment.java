package com.konka.renting.landlord.home;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.ArticleInfo;
import com.konka.renting.bean.BannerListbean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.KInfo;
import com.konka.renting.bean.LocationRefreshEvent;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterSearchListBean;
import com.konka.renting.event.LocationEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.bill.BillListActivity;
import com.konka.renting.landlord.home.copy.CopyListActivity;
import com.konka.renting.landlord.home.freehouse.FreeHouseActivity;
import com.konka.renting.landlord.home.more.MoreHotRoomListActivity;
import com.konka.renting.landlord.home.search.SearchActivity;
import com.konka.renting.landlord.home.tenanter.TenantListActivity;
import com.konka.renting.landlord.user.collection.MyCollectionActivity;
import com.konka.renting.landlord.user.message.MessageActivity;
import com.konka.renting.location.AMapHelp;
import com.konka.renting.location.LocationInfo;
import com.konka.renting.location.LocationUtils;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonProgressDialog;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zaaach.citypicker.model.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 房东首页
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.tv_location)
    TextView mTvLocation;
    @BindView(R.id.tv_search)
    TextView mTvSearch;
    @BindView(R.id.iv_message)
    ImageView mIvMessage;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    Unbinder unbinder;

    private String mArticleUrl = "";

    private CommonProgressDialog pBar;
    private static String DOWNLOAD_NAME = "konkasafe";
    private String url;
    private int page = 1;
    private final int REFRESH = 1;
    private final int LOADMORE = 2;

    private ViewHolder mHeadViewHolder;
    private List<BannerListbean> mBannerAdvListInfos = new ArrayList<>();
    private List<RenterSearchListBean> mHoustList = new ArrayList<>();
    private List<ArticleInfo> mArticleInfos = new ArrayList<>();

    private BannerAdapter mBannerAdapter;
    private CommonAdapter mCommonAdapter;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(REFRESH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        ViewGroup viewGroup = (ViewGroup) mTvLocation.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());

//        getUserInfo();
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void init() {
        super.init();
//        RxPermissions rxPermission = new RxPermissions(mActivity);
//        rxPermission.request(
//                android.Manifest.permission.CAMERA,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//        )
//                .subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if (aBoolean) {
////                            checkVersion();
//                        }
//                    }
//                });

//        addRxBusSubscribe(UpdateEvent.class, new Action1<UpdateEvent>() {
//            @Override
//            public void call(UpdateEvent updateEvent) {

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
//                // downFile(URLData.DOWNLOAD_URL);
//                final DownloadTask downloadTask = new DownloadTask(
//                        mActivity);
//                downloadTask.execute(url);
//            }
//        });

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.item_home_headview, null);
        mListview.addHeaderView(headView);
        mListview.setVerticalScrollBarEnabled(false);
        initHeaderView(headView);
        mCommonAdapter = new CommonAdapter<RenterSearchListBean>(getActivity(), mHoustList, R.layout.item_landlord_home_hot) {
            @Override
            public void convert(com.mcxtzhang.commonadapter.lvgv.ViewHolder viewHolder, RenterSearchListBean bean, int i) {
//                String title = String.format(getString(R.string.roomlist_title_des), roomInfo.address, roomInfo.building_no, roomInfo.door_no, roomInfo.room_name);
                viewHolder.setText(R.id.tv_title, bean.getRoom_name());

                String description = getString(R.string.roomlist_description_des);
                String room_type ;
                if (bean.getRoom_type() != null && bean.getRoom_type().contains("_")) {
                    String[] t = bean.getRoom_type().split("_");
                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                } else {
                    room_type = bean.getRoom_type();
                }
                description = String.format(description, room_type, bean.getMeasure_area() + "", bean.getFloor() + "", bean.getTotal_floor() + "");

                viewHolder.setText(R.id.tv_date, description);

                viewHolder.setText(R.id.tv_type, bean.getType() == 1 ? getString(R.string.short_rent) : getString(R.string.long_rent));
                viewHolder.setSelected(R.id.tv_type, bean.getType() == 2);

                TextView tv_price = viewHolder.getView(R.id.tv_price);
                String unit = bean.getType() == 1 ? "/天" : "/月";
                tv_price.setText("¥ " + (int) Float.parseFloat(bean.getHousing_price()) + unit);
                if (!TextUtils.isEmpty(bean.getThumb_image()))
                    Picasso.get().load(bean.getThumb_image()).resize(1000, 1000).error(R.mipmap.fangchan_jiazai).into((ImageView) viewHolder.getView(R.id.iv_icon));
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).resize(1000, 1000).into((ImageView) viewHolder.getView(R.id.iv_icon));

            }
        };


        mListview.setAdapter(mCommonAdapter);
        mListview.setHeaderDividersEnabled(false);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomInfoActivity.toActivity(mActivity, mHoustList.get(position - 1).getRoom_id());
            }
        });

        addRxBusSubscribe(City.class, new Action1<City>() {
            @Override
            public void call(City city) {
                if (!TextUtils.isEmpty(city.getCode())) {
                    mTvLocation.setText(city.getName());
                    location(city);
                }
            }
        });
        if (!TextUtils.isEmpty(LocationUtils.getInstance().city_id)) {
            mTvLocation.setText(LocationUtils.getInstance().name);
        }
        if (!LocationUtils.isChoiceCity()) {
            RxPermissions rxPermissions = new RxPermissions(mActivity);
            rxPermissions.request(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                AMapHelp aMapHelp = new AMapHelp(mActivity.getApplicationContext());
                            }
                        }
                    });
        }
        mSmartRefreshLayout.setEnableLoadmore(true);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(REFRESH);
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(LOADMORE);
            }
        });
        mSmartRefreshLayout.autoRefresh();
    }

//    private void checkVersion() {
//
//        showLoadingDialog();
//        Subscription subscription = RetrofitHelper.getInstance().checkVersion(String.valueOf(getVerName(mActivity)))
//                .compose(RxUtil.<DataInfo<CheckVersionBean>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<CheckVersionBean>>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<CheckVersionBean> checkVersionBeanDataInfo) {
//
//                        dismiss();
//                        if (checkVersionBeanDataInfo.success()) {
//                            if (checkVersionBeanDataInfo.data().is_renew == 1) {
//                                showUpdatePopup(checkVersionBeanDataInfo.data());
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

    public static int getVerName(Context context) {
        int verName = 0;
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    private void initHeaderView(View headView) {
        mHeadViewHolder = new ViewHolder(headView);

        mBannerAdapter = new BannerAdapter(mHeadViewHolder.mViewpager, getActivity(), mBannerAdvListInfos);
        mHeadViewHolder.mViewpager.setHintView(new ColorPointHintView(getActivity(), Color.YELLOW, Color.WHITE));
        mHeadViewHolder.mViewpager.setAdapter(mBannerAdapter);
        if (mBannerAdvListInfos.size() <= 1) {
            mHeadViewHolder.mViewpager.setHintView(null);
        }

        mHeadViewHolder.mTvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreHotRoomListActivity.toActivity(mActivity);
            }
        });

        mHeadViewHolder.mTvTips.setVisibility(View.GONE);
        mHeadViewHolder.mTvMoreTitle.setText(R.string.landlord_home_hot_house);
        mHeadViewHolder.mTvTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.toActivity(getActivity());
            }
        });


        String[] grid_strings = getActivity().getResources().getStringArray(R.array.landlord_home_grid);
        int[] grid_image_ids = new int[]{
                R.mipmap.home_grid_icon1,
                R.mipmap.home_grid_icon2,
                R.mipmap.home_grid_icon3,
                R.mipmap.home_grid_icon4,
                R.mipmap.home_grid_icon5};
        for (int i = 0; i < 5; i++) {
            TextView view = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.item_home_grid, mHeadViewHolder.mLinearGrid, false);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            }
            params.width = 0;
            params.weight = 1.0f;
            params.leftMargin = UIUtils.dip2px(10);
            view.setLayoutParams(params);
            mHeadViewHolder.mLinearGrid.addView(view);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridClicked(finalI);
                }
            });

            view.setText(grid_strings[i]);
            view.setCompoundDrawablesWithIntrinsicBounds(0, grid_image_ids[i], 0, 0);
        }

    }


    private void gridClicked(int index) {
        switch (index) {
            case 0:
                TenantListActivity.toActivity(getActivity());
                break;
            case 1:
                BillListActivity.toActivity(getActivity());
                break;
            case 2:
                MyCollectionActivity.toActivity(getActivity());
                break;
            case 3:
                CopyListActivity.toActivity(getActivity());
                break;
            case 4:
                FreeHouseActivity.toActivity(getActivity());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_location, R.id.tv_search, R.id.iv_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                RxBus.getDefault().post(new LocationEvent());
                break;
            case R.id.tv_search:
//                DeviceListActivity.toActivity(getActivity());
                SearchActivity.toActivity(getActivity());
                break;
            case R.id.iv_message:
                MessageActivity.toActivity(getActivity());
                break;
        }
    }

    private void getData(int type) {
        LocationInfo locationInfo = LocationUtils.getInstance();
        String city_id = locationInfo.city_id;
        getBannerListData();
        getHotHoustListData(type, city_id);
    }


    private void getBannerListData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().index()
                .compose(RxUtil.<DataInfo<List<BannerListbean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<BannerListbean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (mSmartRefreshLayout != null)
                            mSmartRefreshLayout.finishRefresh(false);

                    }

                    @Override
                    public void onNext(DataInfo<List<BannerListbean>> homeInfoDataInfo) {
                        mSmartRefreshLayout.finishRefresh(homeInfoDataInfo.success());
                        if (homeInfoDataInfo.success()) {
                            bindBannerData(homeInfoDataInfo.data());
                        } else {
                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void bindBannerData(List<BannerListbean> list) {
        mBannerAdvListInfos.clear();
        mBannerAdvListInfos.addAll(list);
        mBannerAdapter.notifyDataSetChanged();

    }

    private void getHotHoustListData(final int type, String city_id) {
        if (type == REFRESH) {
            page = 1;
        } else {
            page = page + 1;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList(page + "", "", city_id, "", "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (type == REFRESH)
                            mSmartRefreshLayout.finishRefresh();
                        else {
                            mSmartRefreshLayout.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> homeInfoDataInfo) {
                        if (type == REFRESH) {
                            mSmartRefreshLayout.finishRefresh();
                        } else {
                            mSmartRefreshLayout.finishLoadmore();
                        }
                        if (homeInfoDataInfo.success()) {
                            if (page == 1) {
                                mHoustList.clear();
                            } else if (homeInfoDataInfo.data().getTotalPage() < homeInfoDataInfo.data().getPage()) {
                                page--;
                            }
                            mSmartRefreshLayout.setEnableLoadmore(homeInfoDataInfo.data().getTotalPage() > page);
                            bindHotHoustListData(homeInfoDataInfo.data().getList());
                        } else {
                            if (page > 1)
                                page--;
                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void bindHotHoustListData(List<RenterSearchListBean> list) {
        mHoustList.addAll(list);
        mCommonAdapter.notifyDataSetChanged();
    }


    private void location(City city) {
//        Subscription subscription = RetrofitHelper.getInstance().location(city.getName())
//                .compose(RxUtil.<DataInfo<KInfo<LocationInfo>>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<KInfo<LocationInfo>>>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<KInfo<LocationInfo>> kInfoDataInfo) {
//                        if (kInfoDataInfo.success()) {
//                            LocationUtils.save(kInfoDataInfo.data().info);
//                            Log.e(TAG, "onNext: lng" + LocationUtils.getInstance().lng);
//                            Log.e(TAG, "onNext: lat" + LocationUtils.getInstance().lat);
//                            mSmartRefreshLayout.autoRefresh();
//                            RxBus.getDefault().post(new LocationRefreshEvent());
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.name = city.getName();
        locationInfo.city_id = city.getCode();
        locationInfo.pinyin = city.getPinyin();
        LocationUtils.save(locationInfo);
        mSmartRefreshLayout.autoRefresh();
        RxBus.getDefault().post(new LocationRefreshEvent());
    }


    static class ViewHolder {
        @BindView(R.id.viewpager)
        RollPagerView mViewpager;
        @BindView(R.id.tv_tips)
        TextView mTvTips;
        @BindView(R.id.linear_grid)
        LinearLayout mLinearGrid;
        @BindView(R.id.tv_more)
        TextView mTvMore;
        @BindView(R.id.tv_more_title)
        TextView mTvMoreTitle;
        @BindView(R.id.linear_articles)
        LinearLayout mLinearArticles;
        @BindView(R.id.linear_articles_items)
        LinearLayout mLinearArticles_items;
        @BindView(R.id.tv_article_more)
        TextView mTvArticleMore;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class RenterArticleViewHolder {
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_title)
        TextView mTvTitle;

        RenterArticleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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

//    private void showUpdatePopup(CheckVersionBean mData) {
//        // 开启 popup 时界面透明
//        UpdateAppPopupwindow updateAppPopupwindow = new UpdateAppPopupwindow(mActivity, mData);
//        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//        lp.alpha = 0.7f;
//        mActivity.getWindow().setAttributes(lp);
//        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        // popupwindow 第一个参数指定popup 显示页面
//        updateAppPopupwindow.showAtLocation(mActivity.findViewById(R.id.main_layout), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
//        // popup 退出时界面恢复
//        updateAppPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 1f;
//                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                mActivity.getWindow().setAttributes(lp);
//            }
//        });
//    }
}
