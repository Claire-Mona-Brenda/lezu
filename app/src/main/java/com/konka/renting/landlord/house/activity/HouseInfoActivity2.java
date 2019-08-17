package com.konka.renting.landlord.house.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.event.HousePublishEvent;
import com.konka.renting.event.LandlordHouseInfoEvent;
import com.konka.renting.event.PublicCancelEvent;
import com.konka.renting.event.UpdataHouseInfoEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.CreateOrderActivity;
import com.konka.renting.landlord.house.HouseEditActivity;
import com.konka.renting.landlord.house.OpenManageActivity;
import com.konka.renting.landlord.house.PicViewPagerActivity;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.view.HousePublishActivity;
import com.konka.renting.landlord.house.view.OnTouchMapView;
import com.konka.renting.landlord.house.widget.PicstandardWidget;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class HouseInfoActivity2 extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activity_house_info_tv_name)
    TextView mTvName;
    @BindView(R.id.activity_house_info_tv_type)
    TextView mTvType;
    @BindView(R.id.activity_house_info_tv_area)
    TextView mTvArea;
    @BindView(R.id.activity_house_info_tv_floor)
    TextView mTvFloor;
    @BindView(R.id.activity_house_info_ll_bind)
    LinearLayout mLlBind;
    @BindView(R.id.activity_house_info_ll_open_manege)
    LinearLayout mLlOpenManege;
    @BindView(R.id.activity_house_info_ll_ren_list)
    LinearLayout mLlRenList;
    @BindView(R.id.activity_house_info_ll_edit)
    LinearLayout mLlEdit;
    @BindView(R.id.activity_house_info_tv_config)
    TextView mTvConfig;
    @BindView(R.id.activity_house_info_tv_introduce)
    TextView mTvIntroduce;
    @BindView(R.id.activity_house_info_pw_pic)
    PicstandardWidget mPwPic;
    @BindView(R.id.activity_house_info_recycler_official)
    RecyclerView mRecyclerOfficial;
    @BindView(R.id.activity_house_info_ll_official)
    LinearLayout mLlOfficial;
    @BindView(R.id.activity_house_info_tv_address)
    TextView mTvAddress;
    @BindView(R.id.activity_house_info_mapview_address)
    MapView mapView;
    @BindView(R.id.activity_house_info_img_bind)
    ImageView mImgBind;
    @BindView(R.id.activity_house_info_tv_bind)
    TextView mTvBind;
    @BindView(R.id.activity_house_info_img_open_manege)
    ImageView mImgOpenManege;
    @BindView(R.id.activity_house_info_tv_open_manege)
    TextView mTvOpenManege;
    @BindView(R.id.activity_house_info_img_ren_list)
    ImageView mImgRenList;
    @BindView(R.id.activity_house_info_tv_ren_list)
    TextView mTvRenList;
    @BindView(R.id.activity_house_info_img_edit)
    ImageView mImgEdit;
    @BindView(R.id.activity_house_info_tv_edit)
    TextView mTvEdit;
    @BindView(R.id.activity_house_info_tv_public)
    TextView mTvPublic;
    @BindView(R.id.activity_house_info_tv_create)
    TextView mTvCreate;
    @BindView(R.id.activity_house_info_ll_button)
    LinearLayout mLlButton;
    @BindView(R.id.activity_house_info_rl_mapview_address)
    OnTouchMapView mRlMapviewAddress;
    @BindView(R.id.activity_house_info_nestedScrollView)
    NestedScrollView nestedScrollView;

    String room_id;
    List<String> imageList;

    HouseDetailsInfoBean2 bean;

    private AMap aMap;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, HouseInfoActivity2.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_house_info_2;
    }

    @Override
    public void init() {
        UIUtils.setStatusBarFullTransparent(this);
        UIUtils.setFitSystemWindow(false, this);
        UIUtils.setDarkStatusIcon(this, true);
        ViewGroup viewGroup = (ViewGroup) tvTitle.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());


        tvTitle.setText(R.string.house_info_title);
        room_id = getIntent().getStringExtra("room_id");

        mRlMapviewAddress.setScrollView(nestedScrollView);

        addRxBusSubscribe(UpdataHouseInfoEvent.class, new Action1<UpdataHouseInfoEvent>() {
            @Override
            public void call(UpdataHouseInfoEvent updataHouseInfoEvent) {
                getData();
            }
        });
        addRxBusSubscribe(LandlordHouseInfoEvent.class, new Action1<LandlordHouseInfoEvent>() {
            @Override
            public void call(LandlordHouseInfoEvent landlordHouseInfoEvent) {
                if (landlordHouseInfoEvent.isGetdata())
                    getData();
                if (landlordHouseInfoEvent.isFinish())
                    finish();
            }
        });
        addRxBusSubscribe(HousePublishEvent.class, new Action1<HousePublishEvent>() {
            @Override
            public void call(HousePublishEvent housePublishEvent) {
                mTvPublic.setText(R.string.end_to_rent);
                bean.setRoom_status(4);
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


    @OnClick({R.id.iv_back, R.id.activity_house_info_ll_bind, R.id.activity_house_info_ll_open_manege, R.id.activity_house_info_ll_ren_list,
            R.id.activity_house_info_ll_edit, R.id.activity_house_info_tv_public, R.id.activity_house_info_tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.activity_house_info_ll_bind://绑定设备
                if (bean != null)
                    DevListActivity.toActivity(mActivity, bean.getRoom_id() + "", bean.getRoom_status(), bean.getIs_install() == 0, false);
                break;
            case R.id.activity_house_info_ll_open_manege://开门管理
                if (bean != null && !bean.getDevice_id().equals(""))
                    OpenManageActivity.toActivity(mActivity, bean);
                else if (bean != null && bean.getDevice_id().equals("")) {
                    showToast(R.string.warm_open_no_device);
                }
                break;
            case R.id.activity_house_info_ll_ren_list://租客列表
                RentPeopleListActivity.toActivity(this, bean.getRoom_id());
                break;
            case R.id.activity_house_info_ll_edit://编辑房产
                if (bean != null)
                    HouseEditActivity.toActivity(mActivity, bean);
                break;
            case R.id.activity_house_info_tv_public://发布或取消发布
                if (bean == null) {
                    return;
                }
                if (bean.getRoom_status() > 3) {
                    cancelPublic();
                } else {
                    HousePublishActivity.toActivity(mActivity, bean.getRoom_id());
                }
                break;
            case R.id.activity_house_info_tv_create://生成订单
                if (bean != null) {
                    CreateOrderActivity.toActivity(mActivity, bean.getRoom_id(), bean.getRoom_name());
                }
                break;
        }
    }

    private void changeCamera() {
        LatLng latLng = new LatLng(Double.valueOf(bean.getLat()), Double.valueOf(bean.getLng()));
        aMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        latLng, 15, 30, 30)));
        aMap.clear();
        aMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_icon)));

    }

    /*******************************************接口********************************************************/
    private void getData() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().getHouseInfo2(room_id)
                .compose(RxUtil.<DataInfo<HouseDetailsInfoBean2>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<HouseDetailsInfoBean2>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<HouseDetailsInfoBean2> dataInfo) {
                        if (dataInfo.success()) {
                            bean = dataInfo.data();
                            mLlButton.setVisibility(View.VISIBLE);
                            mTvPublic.setText(bean.getRoom_status() < 4 ? R.string.start_to_rent : R.string.end_to_rent);
                            imageList = bean.getImage();
                            mTvName.setText(bean.getRoom_name());
                            mImgBind.setImageResource(bean.getDevice_id().equals("") ? R.mipmap.unbounded : R.mipmap.binding);
                            if (bean.getRoom_type() != null && bean.getRoom_type().contains("_")) {
                                String[] t = bean.getRoom_type().split("_");
                                mTvType.setText(t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫"));
                            } else {
                                mTvType.setText(bean.getRoom_type());
                            }
                            mTvArea.setText(getArea(bean.getMeasure_area() + ""));
                            mTvFloor.setText(bean.getFloor() + "/" + bean.getTotal_floor() + getString(R.string.house_info_floor_unit));
                            mTvConfig.setText(getConfig(bean.getConfig()));
                            mTvIntroduce.setText(TextUtils.isEmpty(bean.getRemark()) ? getString(R.string.nothing) : bean.getRemark());
                            mTvAddress.setText(bean.getProvince() + bean.getCity() + bean.getArea() + bean.getMap_address() + bean.getAddress());
                            //平台认证
                            if (bean.getAuth_image() != null && bean.getAuth_image().size() > 0) {
                                mLlOfficial.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                mRecyclerOfficial.setLayoutManager(linearLayoutManager);
                                PicRecycleAdapter picAdapter = new PicRecycleAdapter(mActivity, bean.getThumb_auth_image());
                                picAdapter.setItemClickCall(new OnItemClickCall() {
                                    @Override
                                    public void omItemClick(int position) {
                                        PicViewPagerActivity.toActivity(mActivity, (ArrayList<String>) bean.getAuth_image(), position);
                                    }
                                });
                                mRecyclerOfficial.setAdapter(picAdapter);
                            }
                            //图片
                            ViewGroup viewGroup = (ViewGroup) mPwPic.getParent();
                            if (bean.getImage().size() > 0) {

                                for (String str : bean.getImage()) {
                                    MissionEnity missionEnity = new MissionEnity();
                                    missionEnity.imgpath = str;
                                    missionEnity.isNet = true;
                                    mPwPic.addImg(missionEnity);
                                }
                                mPwPic.setItemClickCall(new PicstandardWidget.OnItemClickCall() {
                                    @Override
                                    public void onClick(int position) {
                                        PicViewPagerActivity.toActivity(mActivity, (ArrayList<String>) bean.getImage(), position);
                                    }
                                });
                            } else {
                                viewGroup.setVisibility(View.GONE);
                            }

                            changeCamera();
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }

    public void cancelPublic() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().cancelPublishHouse(bean.getRoom_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new PublicCancelEvent());
                            mTvPublic.setText(R.string.start_to_rent);
                            bean.setRoom_status(3);
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    private String getConfig(List<HouseConfigBean> config) {
        if (config == null)
            return getString(R.string.nothing);
        String c = "";
        int size = config.size();
        for (int i = 0; i < size; i++) {
            HouseConfigBean configBean = config.get(i);
            if (configBean.getStatus() == 1) {
                c += configBean.getName();
                if (i < size - 1) {
                    c += "、";
                }
            }
        }
        return TextUtils.isEmpty(c) ? getString(R.string.nothing) : c;
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }


    class PicRecycleAdapter extends RecyclerView.Adapter<PicRecycleAdapter.VH> {
        List<String> mList;
        Context mContext;
        OnItemClickCall itemClickCall;

        public PicRecycleAdapter(Context mContext, List<String> imgList) {
            this.mList = imgList;
            this.mContext = mContext;
        }

        public void setItemClickCall(OnItemClickCall itemClickCall) {
            this.itemClickCall = itemClickCall;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = View.inflate(mContext, R.layout.adapter_house_info_recycle, null);
            VH vh = new VH(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, final int i) {
            Picasso.get().load(mList.get(i)).placeholder(R.mipmap.fangchan_jiazai).into(vh.img);
            vh.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickCall != null)
                        itemClickCall.omItemClick(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView img;

            public VH(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.adapter_recycler_img);
            }
        }
    }

    interface OnItemClickCall {
        public void omItemClick(int position);
    }

}
