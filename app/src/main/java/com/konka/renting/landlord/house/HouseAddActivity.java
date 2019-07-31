package com.konka.renting.landlord.house;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CityBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.view.AgentChooseWidget;
import com.konka.renting.landlord.house.view.RoomTypeChooseWidget;
import com.konka.renting.landlord.house.widget.IPopBack;
import com.konka.renting.landlord.house.widget.PicRecordWidget;
import com.konka.renting.landlord.house.widget.PicassoEngine;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.PictureUtils;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.listeners.OnMoreWheelListener;
import cn.addapp.pickers.picker.LinkagePicker;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;

public class HouseAddActivity extends BaseActivity {
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
    @BindView(R.id.activity_addHouse_edit_agent)
    TextView tvAgent;
    @BindView(R.id.activity_addHouse_edit_config)
    EditText mEditConfig;
    @BindView(R.id.activity_addHouse_recylerview_Config)
    RecyclerView mRecyclerConfig;
    @BindView(R.id.activity_addHouse_edit_introduce)
    EditText editIntroduce;
    @BindView(R.id.pic)
    PicRecordWidget pic;
    @BindView(R.id.submit)
    Button submit;

    private final int REQUEST_CODE_CHOOSE_CAMERA = 101;//图片来源选择相机
    private final int REQUEST_CODE_CHOOSE_PHOTO = 102;//图片来源选择图库
    private final int PHOTO_REQUEST_CODE = 111;   //  是否开启相机权限
    private final int PHOTO_MAX_SUM = 5;   //  添加图片的最大数量
    private final int CITY_PROVINCE = 201;//获取省
    private final int CITY_CITY = 202;//获取市
    private final int CITY_DISTRICT = 203;//获取区

    private List<CityBean> provinceList = new ArrayList<>();
    private List<CityBean> cityList = new ArrayList<>();
    private List<CityBean> districtList = new ArrayList<>();

    private List<String> provinceStrings = new ArrayList<>();
    private List<String> cityStrings = new ArrayList<>();
    private List<String> districtStrings = new ArrayList<>();


    private int picCurSum = 0;

    RxPermissions rxPermissions;
    LinkagePicker picker;
    LinkagePicker.DataProvider provider;
    CommonPopupWindow commonPopupWindow;

    private CityBean provinceBean = new CityBean();//选择的省份
    private CityBean cityBean = new CityBean();//选择的城市
    private CityBean districtBean = new CityBean();//选择的区镇


    private List<UploadPicBean> uploadPicBeans = new ArrayList<>();
    private RoomTypeChooseWidget roomTypeChooseWidget;
    private AgentChooseWidget agentChooseWidget;
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

    @Override
    public int getLayoutId() {
        rxPermissions = new RxPermissions(this);
        return R.layout.activity_add_house;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.add_house_title);

        roomTypeChooseWidget = new RoomTypeChooseWidget(this, RoomTypeChooseWidget.ROOM_TYPE, tvType);
        agentChooseWidget = new AgentChooseWidget(this, AgentChooseWidget.AGENT_TYPE, tvAgent);

        tvAreaTips.setText(getArea(tvAreaTips.getText().toString() + "/"));

        getCity("1", CITY_PROVINCE);
        initListener();
        initConfit();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (confitList.size() <= 0)
            getHouseConfig();
    }

    private void initConfit() {
        mRecyclerConfig.setLayoutManager(new GridLayoutManager(this, 6));
        confitList = new ArrayList<>();
        confitAdapter = new CommonAdapter<HouseConfigBean>(this, confitList, R.layout.adapter_house_config) {
            @Override
            public void convert(ViewHolder viewHolder, final HouseConfigBean houseConfigBean) {
                final ImageView img = viewHolder.getView(R.id.check_config);
                Picasso.get().load(houseConfigBean.getStatus() == 0 ? houseConfigBean.getUn_selected_logo() : houseConfigBean.getSelected_logo()).into(img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = confitList.size();
                        if (houseConfigBean.getStatus() == 1) {
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
//                        Picasso.get().load(houseConfigBean.getStatus() == 1 ? houseConfigBean.getUn_selected_logo() : houseConfigBean.getSelected_logo()).into(img);
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
                        } else if (index == 5) {
                            area = area.substring(0, index);
                            editArea.setText(area);
                            editArea.setSelection(area.length());
                        }

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

        roomTypeChooseWidget.setItemSelect(new RoomTypeChooseWidget.ItemSelect() {
            @Override
            public void itemSelect(String type) {
                isSumbit();
            }
        });
        agentChooseWidget.setItemSelect(new AgentChooseWidget.ItemSelect() {
            @Override
            public void itemSelect(String type) {
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

    private void initCityPicker() {
        if (provider == null) {
            provider = new LinkagePicker.DataProvider() {

                @Override
                public boolean isOnlyTwo() {
                    return false;
                }

                @Override
                public List<String> provideFirstData() {
                    if (provinceStrings.size() == 0)
                        provinceStrings.add("");
                    return provinceStrings;
                }

                @Override
                public List<String> provideSecondData(int firstIndex) {
                    if (cityStrings.size() == 0)
                        cityStrings.add("");
                    return cityStrings;
                }

                @Override
                public List<String> provideThirdData(int firstIndex, int secondIndex) {
                    if (districtStrings.size() == 0)
                        districtStrings.add("");
                    return districtStrings;
                }

            };
        }
        if (picker == null) {
            picker = new LinkagePicker(this, provider);
            picker.setCanLoop(false);
            picker.setTitleText("选择地区");
            picker.setOnMoreItemPickListener(new OnMoreItemPickListener<String>() {

                @Override
                public void onItemPicked(String first, String second, String third) {
                    tvAddress.setText(first + " " + second + " " + third);
                    tvAgent.setText("");
                    int p = provinceStrings.indexOf(first);
                    provinceBean.setName(provinceList.get(p).getName());
                    provinceBean.setRegion_id(provinceList.get(p).getRegion_id());

                    int c = cityStrings.indexOf(second);
                    cityBean.setName(cityList.get(c).getName());
                    cityBean.setRegion_id(cityList.get(c).getRegion_id());

                    int d = districtStrings.indexOf(third);
                    districtBean.setName(districtList.get(d).getName());
                    districtBean.setRegion_id(districtList.get(d).getRegion_id());
                    isSumbit();
                }
            });
            picker.setOnMoreWheelListener(new OnMoreWheelListener() {
                @Override
                public void onFirstWheeled(int index, String item) {
                    if (provinceList.size() > 0)
                        getCity(provinceList.get(index).getRegion_id(), CITY_CITY);
                }

                @Override
                public void onSecondWheeled(int index, String item) {
                    if (cityList.size() > 0)
                        getCity(cityList.get(index).getRegion_id(), CITY_DISTRICT);
                }

                @Override
                public void onThirdWheeled(int index, String item) {

                }
            });
        }
        picker.show();
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
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePictureIntent, REQUEST_CODE_CHOOSE_CAMERA);
                            } else if (s.equals("图库")) {
//                                Intent intent = new Intent(Intent.ACTION_PICK);
//                                intent.setType("image/*");
//                                intent.setAction(Intent.ACTION_GET_CONTENT);
//                                startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                                selectPhoto();
                            }
                        } else {
                            showToast(getString(R.string.no_permissions));
                        }
                    }
                });
    }

    private void selectPhoto() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .capture(false)
//                .captureStrategy(new CaptureStrategy(true, "com.konka.fileprovider"))
                .countable(true)
                .maxSelectable(PHOTO_MAX_SUM - picCurSum)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.dp_130))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE_PHOTO);
    }


    @OnClick({R.id.iv_back, R.id.activity_addHouse_tv_address, R.id.activity_addHouse_tv_type, R.id.activity_addHouse_edit_agent, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                showBack();
                break;
            case R.id.activity_addHouse_tv_address://地址
                initCityPicker();
                break;
            case R.id.activity_addHouse_tv_type:
                roomTypeChooseWidget.showPopWindow();
                break;
            case R.id.activity_addHouse_edit_agent:
                if (cityBean.getRegion_id() != null) {
                    agentChooseWidget.setCity_id(cityBean.getRegion_id() == null ? "" : cityBean.getRegion_id());
                    agentChooseWidget.showPopWindow();
                } else {
                    showToast(R.string.add_house_edit_address);
                }
                break;
            case R.id.submit:
                sumbit();
                break;
        }
    }

    private void getCity(String region_id, final int type) {
        String str = "1";
        switch (type) {
            case CITY_PROVINCE:
                str = "1";
                break;
            case CITY_CITY:
                str = "2";
                break;
            case CITY_DISTRICT:
                str = "3";
                break;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getRegionList(region_id, str)
                .compose(RxUtil.<DataInfo<List<CityBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<CityBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<List<CityBean>> dataInfo) {
                        if (dataInfo.success()) {
                            switch (type) {
                                case CITY_PROVINCE:
                                    provinceList.clear();
                                    provinceList.addAll(dataInfo.data());
                                    provinceStrings.clear();
                                    int size1 = provinceList.size();
                                    for (int i = 0; i < size1; i++) {
                                        provinceStrings.add(provinceList.get(i).getName());
                                    }
                                    break;
                                case CITY_CITY:
                                    cityList.clear();
                                    cityList.addAll(dataInfo.data());
                                    cityStrings.clear();
                                    int size2 = cityList.size();
                                    for (int i = 0; i < size2; i++) {
                                        cityStrings.add(cityList.get(i).getName());
                                    }
                                    picker.updataSecond();
                                    break;
                                case CITY_DISTRICT:
                                    districtList.clear();
                                    districtList.addAll(dataInfo.data());
                                    districtStrings.clear();
                                    int size3 = districtList.size();
                                    for (int i = 0; i < size3; i++) {
                                        districtStrings.add(districtList.get(i).getName());
                                    }
                                    picker.updataThirdView();
                                    break;
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

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
                            int size = dataInfo.data().size();
                            for (int i = 0; i < size; i++) {
                                HouseConfigBean bean = dataInfo.data().get(i);
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
                            confitList.clear();
                            confitList.addAll(dataInfo.data());
                            confitAdapter.notifyDataSetChanged();
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void sumbit() {
        int floor = Integer.valueOf(editFloor.getText().toString());
        int floorSum = getFloor(eEditFloorSum.getText().toString());
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
        String img = "";
        int size = uploadPicBeans.size();
        for (int i = 0; i < size; i++) {
            img += uploadPicBeans.get(i).getFilename();
            if (i < size - 1)
                img += ",";
        }
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance()
                .addRoom(editName.getText().toString(),
                        tvType.getTag().toString(),
                        config,
                        tvAgent.getTag().toString(),
                        provinceBean.getRegion_id(),
                        cityBean.getRegion_id(),
                        districtBean.getRegion_id(),
                        editAddressMore.getText().toString(),
                        eEditFloorSum.getText().toString(),
                        editFloor.getText().toString(),
                        editArea.getText().toString(),
                        mEditConfig.getText().toString(),
                        editIntroduce.getText().toString(),
                        img)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            finish();
                        } else {
                            ShowToastUtil.showNormalToast(HouseAddActivity.this, dataInfo.msg());
                        }

                    }
                });
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
        if (tvAgent.getText().equals("")) {
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
                            showToast(dataInfo.msg());
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_CAMERA) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + ".jpg";
                File f = saveBitmap(imageFileName, imageBitmap);
                picCurSum++;
                uploadPic(f, imageFileName);
            } else if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                if (data != null) {
//                    Uri selectedImageUri = data.getData();
//                    if (selectedImageUri != null) {
//                        String s2 = getRealPathFromURI(selectedImageUri);
//                        String s[] = s2.split("/");
//                        uploadPic(new File(s2), s[s.length - 1]);
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
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
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

    /*************************************弹窗**********************************************************************/

    private void showBack() {
        commonPopupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.warm_house_give_up_add))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        commonPopupWindow.dismiss();
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
