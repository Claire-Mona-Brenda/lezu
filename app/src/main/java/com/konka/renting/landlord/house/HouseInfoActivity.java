package com.konka.renting.landlord.house;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.HouseDetailsInfoBean;
import com.konka.renting.bean.NativePwdBean;
import com.konka.renting.event.HouseInfoSettingPopupEvent;
import com.konka.renting.event.LandlordHouseInfoEvent;
import com.konka.renting.event.UpdataHouseInfoEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.view.HouseInfoSettingPopupwindow;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.KeyPwdPopup;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class HouseInfoActivity extends BaseActivity {
    @BindView(R.id.activity_house_info_appbarlayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView imgSetting;
    @BindView(R.id.activity_house_info_viewpage_img)
    ViewPager viewpageImg;
    @BindView(R.id.activity_house_info_tv_id)
    TextView tvId;
    @BindView(R.id.activity_house_info_tv_img_sum)
    TextView tvImgSum;
    @BindView(R.id.activity_house_info_tv_name)
    TextView tvName;
    @BindView(R.id.activity_info_tv_rent_pay)
    TextView tvRentPay;
    @BindView(R.id.activity_info_tv_rent_pay_unit)
    TextView tvRentPayUnit;
    @BindView(R.id.activity_info_tv_rent_type)
    TextView tvRentType;
    @BindView(R.id.activity_info_tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.activity_info_tv_create_time_right)
    TextView tvCreateTimeRight;
    @BindView(R.id.activity_house_info_tv_type)
    TextView tvType;
    @BindView(R.id.activity_house_info_tv_area)
    TextView tvArea;
    @BindView(R.id.activity_house_info_tv_floor)
    TextView tvFloor;
    @BindView(R.id.activity_house_info_tv_confit)
    TextView tvConfit;
    @BindView(R.id.activity_house_info_recylerview_Config)
    RecyclerView mRecyclerConfig;
    @BindView(R.id.activity_house_info_tv_introduce)
    TextView tvIntroduce;
    @BindView(R.id.activity_house_info_ll_official)
    LinearLayout llOfficial;
    @BindView(R.id.activity_house_info_recycler_official)
    RecyclerView recyclerOfficial;
    @BindView(R.id.activity_house_info_tv_address)
    TextView tvAddress;

    String room_id;
    List<String> imageList;

    PagerAdapter viewPagerAdapter;
    HouseInfoSettingPopupwindow mPopupwindow;
    KeyPwdPopup keyPwdPopup;
    HouseDetailsInfoBean bean;
    private CommonAdapter<HouseConfigBean> confitAdapter;
    private List<HouseConfigBean> confitList;

    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, HouseInfoActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_house_info;
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
        imgSetting.setImageResource(R.mipmap.nav_listmenu_pngbg);
        room_id = getIntent().getStringExtra("room_id");

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

        initConfit();

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
        addRxBusSubscribe(HouseInfoSettingPopupEvent.class, new Action1<HouseInfoSettingPopupEvent>() {
            @Override
            public void call(HouseInfoSettingPopupEvent houseInfoSettingPopupEvent) {
                switch (houseInfoSettingPopupEvent.getType()) {
                    case HouseInfoSettingPopupEvent.TYPE_KEYPWD:
                        getNativePwd();
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
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
                if (houseConfigBean.getStatus() == 0) {
                    if (CacheUtils.checkFileExist(houseConfigBean.getUn_own_logo())) {
                        Picasso.get().load(CacheUtils.getFile(houseConfigBean.getUn_own_logo())).into(img);
                    } else if (!TextUtils.isEmpty(houseConfigBean.getUn_own_logo())) {
                        CacheUtils.saveFile(houseConfigBean.getUn_own_logo(), mActivity);
                        Picasso.get().load(houseConfigBean.getUn_own_logo()).into(img);
                    }
                } else {
                    if (CacheUtils.checkFileExist(houseConfigBean.getOwn_logo())) {
                        Picasso.get().load(CacheUtils.getFile(houseConfigBean.getOwn_logo())).into(img);
                    } else if (!TextUtils.isEmpty(houseConfigBean.getOwn_logo())) {
                        CacheUtils.saveFile(houseConfigBean.getOwn_logo(), mActivity);
                        Picasso.get().load(houseConfigBean.getOwn_logo()).into(img);
                    }
                }
//                Picasso.get().load(houseConfigBean.getStatus() == 0 ? houseConfigBean.getUn_own_logo() : houseConfigBean.getOwn_logo()).into(img);
            }
        };
        mRecyclerConfig.setAdapter(confitAdapter);
    }


    private void getData() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().getHouseInfo(room_id)
                .compose(RxUtil.<DataInfo<HouseDetailsInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<HouseDetailsInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<HouseDetailsInfoBean> dataInfo) {
                        if (dataInfo.success()) {
                            imgSetting.setVisibility(View.VISIBLE);
                            bean = dataInfo.data();
                            imageList = bean.getImage();
                            tvId.setText(bean.getRoom_no());
                            tvImgSum.setText("1/" + imageList.size());
                            tvName.setText(bean.getRoom_name());
                            tvRentPay.setText("¥" + (int) Float.parseFloat(bean.getHousing_price()));
                            tvRentPayUnit.setText(bean.getType() == 1 ? "/天" : "/月");
                            tvCreateTime.setText(bean.getTime());
                            tvType.setText(bean.getRoom_type().getName());
                            tvArea.setText(getArea(bean.getMeasure_area() + ""));
                            tvFloor.setText(bean.getFloor() + "/" + bean.getTotal_floor() + getString(R.string.house_info_floor_unit));
                            tvConfit.setText((bean.getRemark() == null || bean.getRemark().equals("")) ? "暂无信息" : bean.getRemark());

                            tvIntroduce.setText(bean.getExplain().equals("") ? "暂无信息" : bean.getExplain());
                            tvAddress.setText(bean.getProvince_name().getRegion_name() + bean.getCity_name().getRegion_name() + bean.getArea_name().getRegion_name() + bean.getAddress());
                            if (bean.getRoom_status() >= 4) {
                                tvRentType.setText(bean.getType() == 1 ? R.string.short_rent : R.string.long_rent);
                                tvRentType.setSelected(bean.getType() == 2);
                                tvRentType.setVisibility(View.VISIBLE);
                                tvCreateTimeRight.setVisibility(View.VISIBLE);
                            } else {
                                tvRentType.setVisibility(View.GONE);
                                tvCreateTimeRight.setVisibility(View.GONE);
                            }

                            viewPagerAdapter = new PicViewPagerAdapter(HouseInfoActivity.this, imageList);
                            viewpageImg.setAdapter(viewPagerAdapter);
                            viewpageImg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                            if (bean.getAuth_image() != null && bean.getRoom_status() >= 3) {
                                llOfficial.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HouseInfoActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                recyclerOfficial.setLayoutManager(linearLayoutManager);
                                PicRecycleAdapter picAdapter = new PicRecycleAdapter(HouseInfoActivity.this, bean.getThumb_auth_image());
                                picAdapter.setItemClickCall(new OnItemClickCall() {
                                    @Override
                                    public void omItemClick(int position) {
                                        PicViewPagerActivity.toActivity(HouseInfoActivity.this, (ArrayList<String>) bean.getAuth_image(), position);
                                    }
                                });
                                recyclerOfficial.setAdapter(picAdapter);

                            }
                            confitList.clear();
                            if (bean.getConfig() != null) {
                                confitList.addAll(bean.getConfig());
                            }
                            if (confitAdapter != null)
                                confitAdapter.notifyDataSetChanged();
                            if (mPopupwindow != null)
                                mPopupwindow.setBean(bean);

                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                showSettingDialog();
                break;
        }
    }

    private void showSettingDialog() {
        // 开启 popup 时界面透明
        mPopupwindow = new HouseInfoSettingPopupwindow(this, bean);
        mPopupwindow.initStatus(bean.getRoom_status());
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        mPopupwindow.showAsDropDown(imgSetting);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        mPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private void showKeyPwdPopup(String pwd) {
        // 开启 popup 时界面透明
        if (keyPwdPopup == null)
            keyPwdPopup = new KeyPwdPopup(this);
        keyPwdPopup.setPwd(pwd);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        keyPwdPopup.showAtLocation((View) imgSetting.getParent(), Gravity.CENTER, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        keyPwdPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private void getNativePwd() {
        showLoadingDialog();
        Subscription subscription = (SecondRetrofitHelper.getInstance().native_password(room_id)
                .compose(RxUtil.<DataInfo<NativePwdBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<NativePwdBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        finish();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<NativePwdBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            showKeyPwdPopup(dataInfo.data().getPassword());

                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

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
            if (CacheUtils.checkFileExist(imageList.get(position))) {
                Picasso.get().load(CacheUtils.getFile(imageList.get(position))).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(imageView);
            } else if (!TextUtils.isEmpty(imageList.get(position))) {
                CacheUtils.saveFile(imageList.get(position), mActivity);
                Picasso.get().load(imageList.get(position)).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(imageView);
            } else
                Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
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
