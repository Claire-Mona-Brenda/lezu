package com.konka.renting.tenant.findroom.roominfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.RoomSearchInfoBean;
import com.konka.renting.event.TenantRoomInfoEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.AppBarStateChangeListener;
import com.konka.renting.landlord.house.PicViewPagerActivity;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.view.OnTouchMapView;
import com.konka.renting.landlord.house.widget.PicstandardWidget;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class RoomInfoActivity extends BaseActivity implements OnClickListener {

    private Context context;
    AppBarLayout mAppBarLayout;
    ViewPager pager;
    LinearLayout bottom_views;
    Button btn_req;
    Button look_room;
    TextView tvTitle;
    TextView tvRoomName;
    TextView room_no;
    TextView comment_num;
    TextView tvImgSum, tvRoomMoney, tvRoomMoneyUnit, tvRentType, tvRoomPublicTime, tvRoomType, tvRoomArea, tvRoomFloor, tvRoomIntroduce, tvRoomAddress;
    //    RoomInfo roomInfo;
    MapView mapView;
    OnTouchMapView mRlMapviewAddress;
    NestedScrollView nestedScrollView;

    PicstandardWidget picstandardWidget;
    AutoGridView autoGridView;
    RecyclerView mRecyclerConfig;
    private CompositeSubscription mCompositeSubscription;
    String roomid;
    RoomSearchInfoBean infoBean;

    List<String> imageList;
    PagerAdapter viewPagerAdapter;
    private CommonAdapter<HouseConfigBean> confitAdapter;
    private List<HouseConfigBean> confitList;

    AMap aMap;

    public static void toActivity(Context context, String roomid) {
        Intent intent = new Intent(context, RoomInfoActivity.class);
        intent.putExtra("roomid", roomid);
        context.startActivity(intent);
    }


    @Override
    public int getLayoutId() {
        return R.layout.lib_room_info;
    }

    public void init() {
        context = this;
        roomid = getIntent().getStringExtra("roomid");
        initData();
        initConfit();
        getData();
        addRxBusSubscribe(TenantRoomInfoEvent.class, new Action1<TenantRoomInfoEvent>() {
            @Override
            public void call(TenantRoomInfoEvent event) {
                if (event.isFinish())
                    finish();
            }
        });

        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        UIUtils.setDarkStatusIcon(this, true);
        ViewGroup viewGroup = (ViewGroup) tvTitle.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    tvTitle.setVisibility(View.GONE);

                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    tvTitle.setVisibility(View.VISIBLE);
                    tvTitle.setAlpha(1f);
                } else {
                    //中间状态
                    tvTitle.setVisibility(View.VISIBLE);
                    tvTitle.setAlpha(0.5f);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void initData() {
        mAppBarLayout = findViewById(R.id.activity_room_info_appbarlayout);
        picstandardWidget = findViewById(R.id.stantard);
        pager = (ViewPager) findViewById(R.id.fliper);
        btn_req = (Button) findViewById(R.id.request_room);
        look_room = (Button) findViewById(R.id.look_room);
        btn_req.setOnClickListener(this);
        tvTitle = findViewById(R.id.text_title);
        tvImgSum = findViewById(R.id.activity_room_info_tv_img_sum);
        tvRoomName = (TextView) findViewById(R.id.tv_room_name);
        tvRoomMoney = (TextView) findViewById(R.id.tv_room_money);
        tvRoomMoneyUnit = findViewById(R.id.tv_room_money_unit);
        tvRentType = findViewById(R.id.tv_rent_type);
        tvRoomPublicTime = (TextView) findViewById(R.id.tv_room_public_time);
        tvRoomType = (TextView) findViewById(R.id.tv_room_type);
        tvRoomArea = (TextView) findViewById(R.id.tv_room_area);
        tvRoomFloor = (TextView) findViewById(R.id.tv_room_floor);
        tvRoomIntroduce = (TextView) findViewById(R.id.tv_room_introduce);
        mRecyclerConfig = (RecyclerView) findViewById(R.id.activity_room_info_recylerview_Config);
        tvRoomAddress = (TextView) findViewById(R.id.tv_room_address);
        mapView = findViewById(R.id.room_info_mapview);
        mRlMapviewAddress = findViewById(R.id.activity_room_info_rl_mapview_address);
        nestedScrollView = findViewById(R.id.activity_room_info_nestedScrollView);
        room_no = (TextView) findViewById(R.id.room_no);
        comment_num = (TextView) findViewById(R.id.comment_num);
        autoGridView = (AutoGridView) findViewById(R.id.auto_grid);
        bottom_views = findViewById(R.id.linear_renter);

        mRlMapviewAddress.setScrollView(nestedScrollView);
    }

    private void initConfit() {
        mRecyclerConfig.setLayoutManager(new GridLayoutManager(this, 6));
        if (confitList == null)
            confitList = new ArrayList<>();
        confitAdapter = new CommonAdapter<HouseConfigBean>(this, confitList, R.layout.adapter_house_info_config) {
            @Override
            public void convert(ViewHolder viewHolder, final HouseConfigBean houseConfigBean) {
                viewHolder.setText(R.id.tv_config_name, houseConfigBean.getName());
                viewHolder.setSelected(R.id.tv_config_name, houseConfigBean.getStatus() == 1);
                ImageView img = viewHolder.getView(R.id.check_config);
                Picasso.get().load(houseConfigBean.getStatus() == 0 ? houseConfigBean.getUn_own_logo() : houseConfigBean.getOwn_logo()).into(img);
            }
        };
        mRecyclerConfig.setAdapter(confitAdapter);
    }

    public void getData() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().getRenterRoomInfo(roomid)
                .compose(RxUtil.<DataInfo<RoomSearchInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomSearchInfoBean>>() {

                    @Override
                    public void onNext(DataInfo<RoomSearchInfoBean> dataInfo) {
                        if (dataInfo.success()) {
                            infoBean = dataInfo.data();
                            imageList = dataInfo.data().getImage();
                            confitList.clear();
                            if (dataInfo.data().getConfig() != null) {
                                confitList.addAll(dataInfo.data().getConfig());
                            }
                            bindData();
                            changeCamera();
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    public void isRenting() {

        if (infoBean.getRoom_status() == 4 && infoBean.getType() == 2 && infoBean.getIs_rent() == 0) {
            ShowToastUtil.showWarningToast(RoomInfoActivity.this, "重复申请");
            return;
        }
        Intent intent2 = new Intent(RoomInfoActivity.this, ReqRoomActivity.class);
        intent2.putExtra("RoomSearchInfoBean", infoBean);
        RoomInfoActivity.this.startActivity(intent2);
    }


    public void bindData() {
        bottom_views.setVisibility(View.VISIBLE);
        tvRoomPublicTime.setText(infoBean.getTime().split(" ")[0] + " 发布");
        if (infoBean.getRoom_type() != null && infoBean.getRoom_type().contains("_")) {
            String[] t = infoBean.getRoom_type().split("_");
            tvRoomType.setText(t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫"));
        } else {
            tvRoomType.setText(infoBean.getRoom_type());
        }
        tvRoomArea.setText(infoBean.getMeasure_area() + "平米");
        tvRoomFloor.setText(infoBean.getFloor() + "/" + infoBean.getTotal_floor() + "层");
        tvRoomIntroduce.setText(TextUtils.isEmpty(infoBean.getRemark()) ? "暂无介绍" : infoBean.getRemark());
        tvRoomAddress.setText(infoBean.getProvince() + infoBean.getCity() + infoBean.getArea() + infoBean.getMap_address() + infoBean.getRoom_group() + infoBean.getAddress());
        room_no.setText(infoBean.getRoom_no());
        tvImgSum.setText("1/" + imageList.size());
        if (infoBean.getType()==1 && infoBean.getIs_device() == 1) {
            btn_req.setVisibility(View.VISIBLE);
            look_room.setBackgroundResource(R.drawable.shape_white);
            look_room.setTextColor(getResources().getColor(R.color.text_black));
        } else {
            btn_req.setVisibility(View.GONE);
            look_room.setBackgroundResource(R.drawable.selector_enable_org_gray);
            look_room.setTextColor(getResources().getColor(R.color.color_ffffff));
        }
        if (confitAdapter != null)
            confitAdapter.notifyDataSetChanged();
        viewPagerAdapter = new PicViewPagerAdapter(this, imageList);
        pager.setAdapter(viewPagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tvImgSum.setText(i + 1 + "/" + imageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        String price = infoBean.getHousing_price();
        if (!TextUtils.isEmpty(price)) {
            float priceF = Float.valueOf(infoBean.getHousing_price());
            int priceI = (int) priceF;
            if (priceF > priceI) {
                price = priceF + "";
            } else {
                price = priceI + "";
            }
        } else {
            price = "";
        }
        tvRoomMoney.setText("¥ " + price);
        if (infoBean.getType() == 1) {
            tvRoomMoneyUnit.setText("/天");
            tvRentType.setText(R.string.short_rent);
            tvRentType.setSelected(false);
        } else {
            tvRoomMoneyUnit.setText("/月");
            tvRentType.setText(R.string.long_rent);
            tvRentType.setSelected(true);
        }
        tvRoomName.setText(infoBean.getRoom_name());

        if (infoBean.getType() == 2) {
            switch (infoBean.getRoom_status()) {
                case 4://未出租
//                    if (infoBean.getIs_rent() == 0) {
//                        btn_req.setEnabled(false);
//                    }
//                    btn_req.setText("申请租房");
                    break;
                case 5://已确定
                case 6://已出租
                    look_room.setVisibility(View.GONE);
//                    if (infoBean.getIs_rent() == 1) {
//                        btn_req.setText("申请合租");
//                        btn_req.setEnabled(true);
//                    } else {
//                        btn_req.setEnabled(false);
//                    }
                    break;
            }
        }

        ViewGroup viewGroup = (ViewGroup) picstandardWidget.getParent();
        if (infoBean.getAuth_image().size() > 0) {
            List<String> imgs;
            if (infoBean.getThumb_auth_image().size() > 0) {
                imgs = infoBean.getThumb_auth_image();
            } else {
                imgs = infoBean.getAuth_image();
            }
            for (String str : imgs) {
                MissionEnity missionEnity = new MissionEnity();
                missionEnity.imgpath = str;
                missionEnity.isNet = true;
                picstandardWidget.addImg(missionEnity);
            }
            picstandardWidget.setItemClickCall(new PicstandardWidget.OnItemClickCall() {
                @Override
                public void onClick(int position) {
                    PicViewPagerActivity.toActivity(RoomInfoActivity.this, (ArrayList<String>) infoBean.getAuth_image(), position);
                }
            });
        } else {
            viewGroup.setVisibility(View.GONE);
        }


    }

    private boolean isJointRent() {//是否属于申请合租
        if (infoBean.getRoom_status() > 4 && infoBean.getType() == 2) {
            if (infoBean.getIs_rent() == 1) {
                return true;
            } else if (infoBean.getMember_phone() != null) {
                if (!infoBean.getMember_phone().equals(LoginUserBean.getInstance().getMobile())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.request_room:
                if (infoBean == null)
                    return;
                if (LoginUserBean.getInstance().getIs_lodge_identity().equals("1")) {
                    RoomOrderActivity.toActivity(mActivity, infoBean);
//                    if (btn_req.getText().toString().contains("合租")) {
//                        if (infoBean.getIs_rent() == 0) {
//                            ShowToastUtil.showWarningToast(context, "已申请，请勿重复申请");
//                        } else {
//                            new AlertDialog.Builder(this).setTitle(R.string.renter_joint_rent).setMessage(R.string.dialog_apply_to_joint_rent)
//                                    .setNegativeButton(R.string.warn_cancel, null)
//                                    .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            req_join();
//                                        }
//                                    }).create().show();
//                        }
//                    } else {
//                        isRenting();
//                    }
                } else {
                    NewFaceDectectActivity.toActivity(this, 1);
                }
                break;
            case R.id.back:
                this.finish();
                break;

            case R.id.look_room:
                if (infoBean == null)
                    return;
                String s = "是否联系房东";
                if (isJointRent()) {
                    s = "是否联系租客";
                }
                TextView textView = new TextView(this);
                textView.setTextSize(17);
                textView.setText(s);
                textView.setPadding(60, 30, 60, 10);
                new AlertDialog.Builder(context).setView(textView).setPositiveButton("联系", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phone;
                        if (isJointRent()) {
                            phone = infoBean.getMember_phone();
                        } else {
                            phone = infoBean.getLandlord_phone();
                        }
                        if (!TextUtils.isEmpty(phone)) {
                            PhoneUtil.call(phone, context);
                        } else {
                            ShowToastUtil.showWarningToast(context, "暂无号码");
                        }
                    }
                }).setNeutralButton("取消", null).show();


                break;
            default:
                break;
        }

    }

    private void changeCamera() {
        LatLng latLng = new LatLng(Double.valueOf(infoBean.getLat()), Double.valueOf(infoBean.getLng()));
        aMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        latLng, 15, 30, 30)));
        aMap.clear();
        aMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_icon)));

    }


    public void req_join() {
        Subscription subscription = SecondRetrofitHelper.getInstance().jointRentApply(infoBean.getRoom_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ShowToastUtil.showWarningToast(
                                context, "操作失败");
                    }

                    @Override
                    public void onNext(DataInfo homeInfoDataInfo) {

                        if (homeInfoDataInfo.success()) {
                            infoBean.setIs_rent(0);
//                            btn_req.setEnabled(false);
                            ShowToastUtil.showSuccessToast(
                                    context, homeInfoDataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(
                                    context, homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }


    class PicViewPagerAdapter extends PagerAdapter {
        List<String> mList;
        Context mContext;

        public PicViewPagerAdapter(Context mContext, List<String> mList) {
            this.mList = mList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (!TextUtils.isEmpty(imageList.get(position)))
                Picasso.get().load(imageList.get(position)).placeholder(R.mipmap.fangchan_jiazai).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}