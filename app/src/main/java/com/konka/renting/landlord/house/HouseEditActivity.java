package com.konka.renting.landlord.house;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.UriUtils;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.bean.RoomGroupListBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.event.ChooseEstateEvent;
import com.konka.renting.event.DelHouseEvent;
import com.konka.renting.event.LandlordHouseInfoEvent;
import com.konka.renting.event.LandlordHouseListEvent;
import com.konka.renting.event.UpdataHouseInfoEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.activity.ChooseEstateActivity;
import com.konka.renting.landlord.house.activity.ChooseLocationActivity;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.view.WarmDelHousePopup;
import com.konka.renting.landlord.house.widget.IPopBack;
import com.konka.renting.landlord.house.widget.PicRecordWidget;
import com.konka.renting.landlord.house.widget.PicassoEngine;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.ImagePickerProvider;
import com.konka.renting.utils.PictureUtils;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.ThreeChoosePicker;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.listeners.OnMoreWheelListener;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HouseEditActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.activity_addHouse_edit_name)
    EditText editName;
    @BindView(R.id.activity_addHouse_tv_address)
    TextView tvAddress;
    @BindView(R.id.activity_addHouse_edit_address_more)
    EditText editAddressMore;
    @BindView(R.id.activity_addHouse_edit_floor)
    EditText editFloor;
    @BindView(R.id.activity_addHouse_edit_floor_sum)
    EditText eEditFloorSum;
    @BindView(R.id.activity_addHouse_tv_type)
    TextView tvType;
    @BindView(R.id.text19)
    TextView tvAreaTips;
    @BindView(R.id.activity_addHouse_edit_area)
    EditText editArea;
    @BindView(R.id.activity_editHouse_recylerview_Config)
    RecyclerView mRecyclerConfig;
    @BindView(R.id.activity_addHouse_edit_introduce)
    EditText editIntroduce;
    @BindView(R.id.pic)
    PicRecordWidget pic;
    @BindView(R.id.submit)
    Button submit;

    private final int REQUEST_CODE_CHOOSE_CAMERA = 101;
    private final int REQUEST_CODE_CHOOSE_PHOTO = 102;
    private final int PHOTO_REQUEST_CODE = 111;   //  是否开启相机权限
    private final int PHOTO_MAX_SUM = 6;   //  添加图片的最大数量
    private final int CITY_PROVINCE = 201;//获取省
    private final int CITY_CITY = 202;//获取市
    private final int CITY_DISTRICT = 203;//获取区

    ArrayList<String> firstList = new ArrayList<>();
    ArrayList<String> secondList = new ArrayList<>();
    ArrayList<String> thirdList = new ArrayList<>();
    int mFirst;
    int mSecond;
    int mThird;

    String photoFileName;
    private int picCurSum = 0;

    RoomGroupListBean groupListBean;

    RxPermissions rxPermissions;
    ThreeChoosePicker picker;
    ThreeChoosePicker.DataProvider provider;
    CommonPopupWindow commonPopupWindow;
    CommonPopupWindow delPopupWindow;
    WarmDelHousePopup warmDelHousePopup;

    private List<UploadPicBean> uploadPicBeans = new ArrayList<>();

    private HouseDetailsInfoBean2 bean;
    private CommonAdapter<HouseConfigBean> confitAdapter;
    private List<HouseConfigBean> confitList;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isSumbit();
        }
    };

    public static void toActivity(Context context, HouseDetailsInfoBean2 bean) {
        Intent intent = new Intent(context, HouseEditActivity.class);
        intent.putExtra("HouseDetailsInfoBean", bean);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        rxPermissions = new RxPermissions(this);
        return R.layout.lib_house_edit_activity;
    }

    @Override
    public void init() {
        bean = getIntent().getParcelableExtra("HouseDetailsInfoBean");
        tvTitle.setText(R.string.add_house_title);
        tvRight.setText(R.string.common_del);
        tvRight.setVisibility(View.VISIBLE);

        confitList = bean.getConfig();
        tvAreaTips.setText(getArea(tvAreaTips.getText().toString() + "/"));

        addRxBusSubscribe(ChooseEstateEvent.class, new Action1<ChooseEstateEvent>() {
            @Override
            public void call(ChooseEstateEvent event) {
                groupListBean = event.getRoomGroupListBean();
                tvAddress.setText(groupListBean.getName());
                tvAddress.setTextColor(getResources().getColor(R.color.text_black));
                isSumbit();
            }
        });

        getData();
        initEnable();
        initListener();
        initConfit();
        initPicker();
        getHouseConfig();
        isSumbit();


    }

    private void getData() {

        editName.setText(bean.getRoom_name());
        editName.setSelection(editName.getText().toString().length());
        if (bean.getRoom_type() != null && bean.getRoom_type().contains("_")) {
            String[] t = bean.getRoom_type().split("_");
            mFirst = Integer.valueOf(t[0]);
            mSecond = Integer.valueOf(t[1]);
            mThird = Integer.valueOf(t[2]);
            tvType.setText(mFirst + " 室 " + mThird + " 厅" + (mSecond == 0 ? "" : " " + mSecond + " 卫"));
        }
        if (!TextUtils.isEmpty(bean.getRoom_group())) {
            tvAddress.setText(bean.getRoom_group());
            editAddressMore.setText(bean.getAddress());
        }
        editFloor.setText(bean.getFloor() + "");
        eEditFloorSum.setText(bean.getTotal_floor() + "");
        editArea.setText(bean.getMeasure_area() + "");
        editIntroduce.setText(TextUtils.isEmpty(bean.getRemark()) ? "无" : bean.getRemark());

        List<String> list = bean.getImage();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String[] files = list.get(i).split("/");
            String fileName = files[files.length - 1];
            UploadPicBean uploadPicBean = new UploadPicBean();
            uploadPicBean.setFilename(fileName);
            uploadPicBean.setUrl(list.get(i));
            uploadPicBeans.add(uploadPicBean);
            MissionEnity missionEnity = new MissionEnity();
            missionEnity.imgpath = list.get(i);
            missionEnity.imgname = fileName;
            missionEnity.isNet = true;
            pic.addImg(missionEnity);
        }

    }

    private void initEnable() {
        editName.setEnabled(true);
        tvType.setEnabled(true);
//                tvAgent.setEnabled(true);
        tvAddress.setEnabled(true);
        editAddressMore.setEnabled(true);
        eEditFloorSum.setEnabled(true);
        editFloor.setEnabled(true);
        editArea.setEnabled(true);
//                editConfit.setEnabled(true);
        editIntroduce.setEnabled(true);
        pic.setEnabled(true);
    }

    private void initListener() {
        editName.addTextChangedListener(textWatcher);
        editAddressMore.addTextChangedListener(textWatcher);
        eEditFloorSum.addTextChangedListener(textWatcher);

        editArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String area = editArea.getText().toString();
                if (area.startsWith(".")) {
                    area = area.replace(".", "");
                    editArea.setText(area);
                    editArea.setSelection(area.length());
                } else if (area.startsWith("0") && area.length() > 1 && !area.startsWith("0.")) {
                    editArea.setText("0");
                    editArea.setSelection(1);
                } else if (area.contains(".")) {
                    int index = area.indexOf(".");
                    if (index < area.length() - 1) {
                        String a = area.substring(index + 1, area.length());
                        if (a.contains(".")) {
                            area = area.substring(0, index) + "." + a.replace(".", "");
                            editArea.setText(area);
                            editArea.setSelection(area.length());
                        }
                    } else if (index == 5) {
                        area = area.substring(0, index);
                        editArea.setText(area);
                        editArea.setSelection(area.length());
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                isSumbit();
            }
        });

        editFloor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String floor = editFloor.getText().toString();
                if (floor.length() > 1) {
                    String str = floor.substring(1, floor.length());
                    if (str.contains("-") || str.contains("b") || str.contains("B")) {
                        floor = floor.charAt(0) + str.replace("-", "").replace("b", "").replace("B", "");
                        editFloor.setText(floor);
                        editFloor.setSelection(floor.length());
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                isSumbit();
            }
        });
        pic.setIPOP(new IPopBack() {
            @Override
            public void callBack(String s) {
                popBack(s);
            }

            @Override
            public void delCallBank(int index) {
                picCurSum--;
                uploadPicBeans.remove(index);
                isSumbit();
            }
        });
    }

    private void initConfit() {
        mRecyclerConfig.setLayoutManager(new GridLayoutManager(this, 6));
        if (confitList == null)
            confitList = new ArrayList<>();
        confitAdapter = new CommonAdapter<HouseConfigBean>(this, confitList, R.layout.adapter_house_config) {
            @Override
            public void convert(ViewHolder viewHolder, final HouseConfigBean houseConfigBean) {
                final ImageView img = viewHolder.getView(R.id.check_config);
                if (houseConfigBean.getStatus() == 0) {
                    if (CacheUtils.checkFileExist(houseConfigBean.getUn_selected_logo())) {
                        Picasso.get().load(CacheUtils.getFile(houseConfigBean.getUn_selected_logo())).into(img);
                    } else if (!TextUtils.isEmpty(houseConfigBean.getUn_selected_logo())) {
                        CacheUtils.saveFile(houseConfigBean.getUn_selected_logo(), mActivity);
                        Picasso.get().load(houseConfigBean.getUn_selected_logo()).into(img);
                    }
                } else {
                    if (CacheUtils.checkFileExist(houseConfigBean.getSelected_logo())) {
                        Picasso.get().load(CacheUtils.getFile(houseConfigBean.getSelected_logo())).into(img);
                    } else if (!TextUtils.isEmpty(houseConfigBean.getSelected_logo())) {
                        CacheUtils.saveFile(houseConfigBean.getSelected_logo(), mActivity);
                        Picasso.get().load(houseConfigBean.getSelected_logo()).into(img);
                    }
                }
//                Picasso.get().load(houseConfigBean.getStatus() == 0 ? houseConfigBean.getUn_selected_logo() : houseConfigBean.getSelected_logo()).into(img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = confitList.size();
                        Picasso.get().load(houseConfigBean.getStatus() == 1 ? houseConfigBean.getUn_selected_logo() : houseConfigBean.getSelected_logo()).into(img);
                        for (int i = 0; i < size; i++) {
                            if (confitList.get(i).getId() == houseConfigBean.getId()) {
                                confitList.get(i).setStatus(houseConfigBean.getStatus() == 0 ? 1 : 0);
                                break;
                            }
                        }
                    }
                });
            }
        };
        mRecyclerConfig.setAdapter(confitAdapter);
    }

    private void getHouseConfig() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getRoomConfigList()
                .compose(RxUtil.<DataInfo<List<HouseConfigBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<HouseConfigBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<List<HouseConfigBean>> dataInfo) {
                        if (dataInfo.success()) {
                            List<HouseConfigBean> list = dataInfo.data();
                            int size = list.size();
                            for (int i = 0; i < size; i++) {
                                HouseConfigBean bean = list.get(i);
                                int position = isHaveConfit(bean);
                                if (position == -1) {
                                    confitList.add(list.get(i));
                                } else {
                                    confitList.get(position).setSelected_logo(bean.getSelected_logo());
                                    confitList.get(position).setUn_selected_logo(bean.getUn_selected_logo());
                                }
                                if (CacheUtils.checkFileExist(bean.getSelected_logo())) {
                                    Picasso.get().load(CacheUtils.getFile(bean.getSelected_logo())).fetch();
                                } else if (!TextUtils.isEmpty(bean.getSelected_logo())) {
                                    CacheUtils.saveFile(bean.getSelected_logo(), mActivity);
                                    Picasso.get().load(bean.getSelected_logo()).fetch();
                                }
                                if (CacheUtils.checkFileExist(bean.getUn_selected_logo())) {
                                    Picasso.get().load(CacheUtils.getFile(bean.getUn_selected_logo())).fetch();
                                } else if (!TextUtils.isEmpty(bean.getUn_selected_logo())) {
                                    CacheUtils.saveFile(bean.getUn_selected_logo(), mActivity);
                                    Picasso.get().load(bean.getUn_selected_logo()).fetch();
                                }
                            }
                            confitAdapter.notifyDataSetChanged();
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private int isHaveConfit(HouseConfigBean bean) {
        int len = confitList.size();
        for (int j = 0; j < len; j++) {
            if (bean.getId() == confitList.get(j).getId())
                return j;
        }
        return -1;
    }

    private void initPicker() {
        for (int i = 0; i < 10; i++) {
            firstList.add(i + " 室");
            secondList.add(i + " 卫");
            thirdList.add(i + " 厅");
        }
        onLinkagePicker();
    }

    public void onLinkagePicker() {
        provider = new ThreeChoosePicker.DataProvider() {

            @Override
            public boolean isOnlyTwo() {
                return false;
            }

            @Override
            public List<String> provideFirstData() {
                return firstList;
            }

            @Override
            public List<String> provideSecondData() {
                return secondList;
            }

            @Override
            public List<String> provideThirdData() {
                return thirdList;
            }

        };
        picker = new ThreeChoosePicker(this, provider);
        picker.setCanLoop(false);
        picker.setCanLinkage(false);
        picker.setOnMoreItemPickListener(new OnMoreItemPickListener<String>() {

            @Override
            public void onItemPicked(String first, String second, String third) {
                mFirst = Integer.valueOf(first.charAt(0) + "");
                mSecond = Integer.valueOf(second.charAt(0) + "");
                mThird = Integer.valueOf(third.charAt(0) + "");
                tvType.setText(first + " " + second + " " + third);
                isSumbit();
            }
        });
        picker.setOnMoreWheelListener(new OnMoreWheelListener() {
            @Override
            public void onFirstWheeled(int index, String item) {

            }

            @Override
            public void onSecondWheeled(int index, String item) {

            }

            @Override
            public void onThirdWheeled(int index, String item) {

            }
        });

    }


    public void popBack(String str) {
        final String s = str;
        rxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (s.equals("相机")) {
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                photoFileName = "JPEG_" + timeStamp + ".png";
                                File f = null;
                                File dir = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    //		获取图片沙盒文件夹
                                    File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                    f = new File(PICTURES.getAbsolutePath() , photoFileName);
                                    dir = new File(PICTURES.getAbsolutePath() );
                                } else {
                                    f = new File(Environment.getExternalStorageDirectory() + "/konka/", photoFileName);
                                    dir = new File(Environment.getExternalStorageDirectory() + "/konka/");
                                }

                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
//                                if (f.exists()) {
//                                    f.delete();
//                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    Uri uri = FileProvider.getUriForFile(HouseEditActivity.this, ImagePickerProvider.getFileProviderName(HouseEditActivity.this), f);
                                    //跳转到相机
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_CAMERA);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    takePhotoBiggerThan7(f.getAbsolutePath());
                                } else {
                                    // 加载路径图片路径
                                    Uri mUri = Uri.fromFile(f);
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    // 指定存储路径，这样就可以保存原图了
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                                    startActivityForResult(takePictureIntent, REQUEST_CODE_CHOOSE_CAMERA);
                                }
                            } else if (s.equals("图库")) {
//                                Intent intent = new Intent(Intent.ACTION_PICK);
//                                intent.setType("image/*");
//                                intent.setAction(Intent.ACTION_GET_CONTENT);
//                                startActivityForResult(intent, 2);
                                selectPhoto();
                            }
                        } else {
                            showToast(getString(R.string.no_permissions));
                        }
                    }
                });
    }

    private void takePhotoBiggerThan7(String absolutePath) {
        Uri mCameraTempUri;
        try {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            values.put(MediaStore.Images.Media.DATA, absolutePath);
            mCameraTempUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (mCameraTempUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraTempUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            }
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSelector.builder()
                    .useCamera(false) // 设置是否使用拍照
                    .setSingle(false)  //设置是否单选
                    .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
                    .canPreview(true) //是否可以预览图片，默认为true
                    .start(this, REQUEST_CODE_CHOOSE_PHOTO); // 打开相册
        } else {
            Matisse.from(this)
                    .choose(MimeType.allOf())
                    .capture(false)
//                .captureStrategy(new CaptureStrategy(true, "com.konka.fileprovider"))
                    .countable(true)
                    .maxSelectable(PHOTO_MAX_SUM - uploadPicBeans.size())
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.dp_130))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
//                .imageEngine(new PicassoEngine())
                    .imageEngine(new PicassoEngine())
                    .forResult(REQUEST_CODE_CHOOSE_PHOTO);
        }
    }


    @OnClick({R.id.iv_back, R.id.tv_right, R.id.activity_addHouse_tv_address, R.id.activity_addHouse_tv_type, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                showBack();
                break;
            case R.id.tv_right://删除
                if (bean == null)
                    return;
                if (bean.getService_status() == 1) {
                    showTipsDel(getString(R.string.no_to_del_house_pay_money));
                } else if (!TextUtils.isEmpty(bean.getDevice_id()) || !TextUtils.isEmpty(bean.getGateway_id())) {
                    showTipsDel(getString(R.string.no_to_del_house_bind_dev));
                } else if (bean.getStatus() > 0) {
                    showTipsDel(getString(R.string.no_to_del_house_rent_order));
                } else {
                    showDel();
                }
                break;
            case R.id.activity_addHouse_tv_address:
                ChooseEstateActivity.toActivity(this, bean.getCity());
                break;
            case R.id.activity_addHouse_tv_type:
                picker.show();
                break;
            case R.id.submit:
                sumbit();
                break;
        }
    }

    private void sumbit() {
        int floor = Integer.valueOf(editFloor.getText().toString());
        int floorSum = getFloor(eEditFloorSum.getText().toString());
        String name = editName.getText().toString();
        String address = editAddressMore.getText().toString();
        if (TextUtils.isEmpty(name.replace(" ", ""))) {
            ShowToastUtil.showNormalToast(this, getString(R.string.error_house_info_name_no));
            return;
        }
        if (TextUtils.isEmpty(address.replace(" ", ""))) {
            ShowToastUtil.showNormalToast(this, getString(R.string.error_house_info_address_no));
            return;
        }
        if (floor == 0) {
            ShowToastUtil.showNormalToast(this, getString(R.string.warm_house_floor_0));
            return;
        } else if (floorSum == 0) {
            ShowToastUtil.showNormalToast(this, getString(R.string.warm_house_floor_sum_0));
            return;
        } else if (floor > floorSum) {
            ShowToastUtil.showNormalToast(this, getString(R.string.warm_house_floor_sum));
            return;
        }
        String img = "";
        int size = uploadPicBeans.size();
        for (int i = 0; i < size; i++) {
            img += uploadPicBeans.get(i).getFilename();
            if (i < size - 1)
                img += ",";
        }
        String config = "";
        int len = confitList.size();
        for (int i = 0; i < len; i++) {
            HouseConfigBean bean = confitList.get(i);
            if (bean.getStatus() == 1) {
                if (!config.equals(""))
                    config += ",";
                config += bean.getId();
            }

        }
        String room_type = mFirst + "_" + mSecond + "_" + mThird;
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance()
                .editRoom3(bean.getRoom_id(),
                        name,
                        room_type,
                        config,
                        groupListBean == null ? bean.getRoom_group_id() : groupListBean.getRoom_group_id() + "",
                        address,
                        floorSum + "",
                        floor + "",
                        editArea.getText().toString(),
                        editIntroduce.getText().toString(),
                        img)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        e.printStackTrace();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new UpdataHouseInfoEvent());
                            RxBus.getDefault().post(new LandlordHouseListEvent(11));
                            doSuccess();
                            finish();
                        } else {
                            ShowToastUtil.showNormalToast(HouseEditActivity.this, dataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void del() {
        showLoadingDialog();
        Subscription subscription = (SecondRetrofitHelper.getInstance().delHouse(bean.getRoom_id())
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new DelHouseEvent(bean.getRoom_id()));
                            RxBus.getDefault().post(new LandlordHouseInfoEvent(11));
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }

    private boolean isSumbit() {
        if (editName.getText().toString().equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (tvAddress.getText().toString().equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (editAddressMore.getText().toString().equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (editFloor.getText().toString().replace("-", "").replace("B", "").replace("b", "").equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (eEditFloorSum.getText().toString().equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (tvType.getText().toString().equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (editArea.getText().toString().equals("")) {
            submit.setEnabled(false);
            return false;
        }
        if (uploadPicBeans.size() <= 0) {
            submit.setEnabled(false);
            return false;
        }
        submit.setEnabled(true);
        return true;
    }

    private int getFloor(String floor) {
        int f;
        if (floor.startsWith("B") || floor.startsWith("b") || floor.startsWith("-")) {
            f = -Integer.valueOf(floor.replace("B", "").replace("b", "").replace("-", ""));
        } else if (floor.contains("B") || floor.contains("b") || floor.contains("-")) {
            f = 0;
        } else {
            f = Integer.valueOf(floor);
        }
        return f;
    }

    private void uploadPic(final File file, final String fileName) {
        showLoadingDialog();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Subscription subscription = SecondRetrofitHelper.getInstance().uploadPic(fullName, body)
                .compose(RxUtil.<DataInfo<UploadPicBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<UploadPicBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        e.printStackTrace();
                        picCurSum--;
                    }

                    @Override
                    public void onNext(DataInfo<UploadPicBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            MissionEnity missionEnity = new MissionEnity();
                            missionEnity.imgpath = file.getAbsolutePath();
                            missionEnity.imgname = fileName;
                            pic.addImg(missionEnity);
                            uploadPicBeans.add(dataInfo.data());
                            isSumbit();
                        } else {
                            picCurSum--;
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 选择图片返回
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_CAMERA) {
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String imageFileName = "JPEG_" + timeStamp + ".png";
//                File f = saveBitmap(imageFileName, imageBitmap);
                picCurSum++;
                File f = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //		获取图片沙盒文件夹
                    File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    f = new File(PICTURES.getAbsolutePath() , photoFileName);
                    uploadPic(f, f.getName());
                } else {
                    f = new File(Environment.getExternalStorageDirectory() , photoFileName);

                    File compressedImageFile;
                    try {
                        // 图片压缩
                        compressedImageFile = new Compressor(this).compressToFile(f);
                        if (!compressedImageFile.exists())
                            compressedImageFile.createNewFile();
                        uploadPic(compressedImageFile, compressedImageFile.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//                uploadPic(compressedImageFile, compressedImageFile.getName());

            } else if (requestCode == REQUEST_CODE_CHOOSE_PHOTO && data != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                    Uri selectedImageUri = data.getData();
//                    if (selectedImageUri != null) {
//                        String s2 = getRealPathFromURI(selectedImageUri);
//                        String s[] = s2.split("/");
//                        uploadPic(new File(s2),s[s.length - 1]);
//                    }
                    List<Uri> list = new ArrayList<>();
                    list.addAll(Matisse.obtainResult(data));
                    int size = list.size();
                    picCurSum += size;
                    for (int i = 0; i < size; i++) {
                        String path = PictureUtils.getPathFromUri(this, list.get(i));
                        File compressedImageFile;
                        try {
                            // 图片压缩
                            compressedImageFile = new Compressor(this).compressToFile(new File(path));
                            if (!compressedImageFile.exists())
                                compressedImageFile.createNewFile();
                            uploadPic(compressedImageFile, compressedImageFile.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //		获取图片沙盒文件夹
                    File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    ArrayList<String> images = data.getStringArrayListExtra(
                            ImageSelector.SELECT_RESULT);

                    int size = images.size();
                    picCurSum += size;
                    for (int i = 0; i < size; i++) {
                        String path = images.get(i);
                        Uri uri = UriUtils.getImageContentUri(this, path);
                        //图片名称
                        String mFileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                        //图片路径
                        String mFilePath = PICTURES.getAbsolutePath() + "/" + mFileName;
                        File file = new File(mFilePath);

                        FileInputStream inputStream = null;
                        FileOutputStream outputStream = null;
                        ParcelFileDescriptor pfd = null;
                        try {
                            pfd = getContentResolver().openFileDescriptor(uri, "r");
                            outputStream = new FileOutputStream(file);
                            if (pfd != null) {
                                inputStream = new FileInputStream(pfd.getFileDescriptor());
                                byte[] bytes = new byte[1024];
                                //得到实际读取的长度  
                                int n = 0;
                                //循环读取  
                                while ((n = inputStream.read(bytes)) != -1) {
                                    outputStream.write(bytes, 0, n);
                                }

                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {

                            try {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        uploadPic(file, file.getName());

                    }


                }
            }
        }
    }


    /**
     * 保存图片到本地
     */
    public File saveBitmap(String picName, Bitmap bm) {
        File f = new File("/sdcard/konka/", picName);
        File dir = new File("/sdcard/konka/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getRealPathFromURI(Uri contentURI) {
        String result = getPath(this, contentURI);
        return result;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    public void onBackPressed() {
        showBack();
    }


    /**************************************弹窗*************************************************************/
    private void showDel() {
        if (delPopupWindow == null)
            delPopupWindow = new CommonPopupWindow.Builder(this)
                    .setTitle(getString(R.string.tips))
                    .setContent(getString(R.string.is_to_del_house))
                    .setRightTextColor("#4A90E1")
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delPopupWindow.dismiss();
                            del();
                        }
                    })
                    .create();
        showPopup(delPopupWindow);
    }

    private void showTipsDel(String content) {
        if (warmDelHousePopup == null)
            warmDelHousePopup = new WarmDelHousePopup(this);
        warmDelHousePopup.setTvContent(content);
        showPopup(warmDelHousePopup);
    }

    private void showBack() {
        if (commonPopupWindow == null)
            commonPopupWindow = new CommonPopupWindow.Builder(this)
                    .setTitle(getString(R.string.tips))
                    .setContent(getString(R.string.warm_house_give_up_edit))
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonPopupWindow.dismiss();
                            finish();
                        }
                    })
                    .create();
        showPopup(commonPopupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) findViewById(R.id.lin_title).getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }
}
