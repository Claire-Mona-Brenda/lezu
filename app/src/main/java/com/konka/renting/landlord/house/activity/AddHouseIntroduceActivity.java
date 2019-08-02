package com.konka.renting.landlord.house.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.event.AddHouseCompleteEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.HouseAddActivity;
import com.konka.renting.landlord.house.PicViewPagerActivity;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.IPopBack;
import com.konka.renting.landlord.house.widget.PicassoEngine;
import com.konka.renting.landlord.house.widget.PopTool;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.CircleTransform;
import com.konka.renting.utils.PictureUtils;
import com.konka.renting.utils.RoundedTransformation;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;

public class AddHouseIntroduceActivity extends BaseActivity {

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
    @BindView(R.id.activity_add_house_introduce_edt_introduce)
    EditText mEdtIntroduce;
    @BindView(R.id.activity_add_house_introduce_ll_pic)
    LinearLayout mLlPic;
    @BindView(R.id.activity_add_house_introduce_rv_pic)
    RecyclerView mRvPic;

    private final int REQUEST_CODE_CHOOSE_CAMERA = 101;//图片来源选择相机
    private final int REQUEST_CODE_CHOOSE_PHOTO = 102;//图片来源选择图库
    private final int PHOTO_MAX_SUM = 5;   //  添加图片的最大数量


    RxPermissions rxPermissions;

    PoiItem mPoiItem;
    String address;
    String room_name;
    String room_type;
    String room_config_id;
    String total_floor;
    String floor;
    String measure_area;

    CommonAdapter<UploadPicBean> commonAdapter;
    private List<UploadPicBean> uploadPicBeans = new ArrayList<>();

    IPopBack iPopBack;

    public static void toActivity(Context context, PoiItem mPoiItem, String address, String room_name, String room_type, String room_config_id,
                                  String total_floor, String floor, String measure_area) {
        Intent intent = new Intent(context, AddHouseIntroduceActivity.class);
        intent.putExtra(PoiItem.class.getSimpleName(), mPoiItem);
        intent.putExtra("address", address);
        intent.putExtra("room_name", room_name);
        intent.putExtra("room_type", room_type);
        intent.putExtra("room_config_id", room_config_id);
        intent.putExtra("total_floor", total_floor);
        intent.putExtra("floor", floor);
        intent.putExtra("measure_area", measure_area);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_house_introduce;
    }

    @Override
    public void init() {
        mPoiItem = getIntent().getParcelableExtra(PoiItem.class.getSimpleName());
        address = getIntent().getStringExtra("address");
        room_name = getIntent().getStringExtra("room_name");
        room_type = getIntent().getStringExtra("room_type");
        room_config_id = getIntent().getStringExtra("room_config_id");
        total_floor = getIntent().getStringExtra("total_floor");
        floor = getIntent().getStringExtra("floor");
        measure_area = getIntent().getStringExtra("measure_area");

        tvTitle.setText(R.string.add_house);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);
        tvRight.setText(R.string.common_complete);
        tvRight.setTextColor(getResources().getColor(R.color.text_blue));
        tvRight.setVisibility(View.VISIBLE);

        rxPermissions = new RxPermissions(this);

        commonAdapter = new CommonAdapter<UploadPicBean>(this, uploadPicBeans, R.layout.adapter_add_house_introduce) {
            @Override
            public void convert(ViewHolder viewHolder, UploadPicBean uploadPicBean) {
                ImageView picImg = viewHolder.getView(R.id.adapter_add_house_introduce_img_pic);
                if (!TextUtils.isEmpty(uploadPicBean.getThumb_url())) {
                    Picasso.get().load(uploadPicBean.getThumb_url()).error(R.mipmap.fangchan_jiazai).into(picImg);
                } else {
                    Picasso.get().load(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(picImg);
                }
                viewHolder.setOnClickListener(R.id.adapter_add_house_introduce_img_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadPicBeans.remove(uploadPicBean);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = 0;
                        int size = uploadPicBeans.size();
                        ArrayList<String> picList = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            picList.add(uploadPicBeans.get(i).getUrl());
                            if (uploadPicBeans.get(i).getFilename().equals(uploadPicBean.getFilename())) {
                                position = i;
                            }
                        }
                        PicViewPagerActivity.toActivity(mActivity, picList, position);
                    }
                });

            }
        };
        mRvPic.setLayoutManager(new GridLayoutManager(this, 3));
        mRvPic.setAdapter(commonAdapter);

        initListen();
    }

    private void initListen() {
        iPopBack = new IPopBack() {
            @Override
            public void callBack(String s) {
                popBack(s);
            }

            @Override
            public void delCallBank(int id) {
            }
        };
    }


    @OnClick({R.id.iv_back, R.id.tv_right, R.id.activity_add_house_introduce_ll_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if (uploadPicBeans.size() <= 0) {
                    showToast(R.string.please_add_pic);
                } else {
                    sumbit();
                }
                break;
            case R.id.activity_add_house_introduce_ll_pic:
                if (uploadPicBeans.size() < 5) {
                    addImg();
                } else {
                    showToast(String.format(getString(R.string.tips_add_pic_num_mast), PHOTO_MAX_SUM + ""));
                }
                break;
        }
    }

    public void addImg() {
        String[] s2 = {"相机", "图库"};
        new PopTool().showPopupWindow(this, s2, iPopBack);
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
                .maxSelectable(PHOTO_MAX_SUM - uploadPicBeans.size())
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.dp_130))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE_PHOTO);
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

    /**********************************************接口***************************************************/
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
                    }

                    @Override
                    public void onNext(DataInfo<UploadPicBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            uploadPicBeans.add(dataInfo.data());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    private void sumbit() {


        String img = "";
        int size = uploadPicBeans.size();
        for (int i = 0; i < size; i++) {
            img += uploadPicBeans.get(i).getFilename();
            if (i < size - 1)
                img += ",";
        }
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance()
                .addRoom2(room_name,
                        room_type,
                        room_config_id,
                        mPoiItem.getProvinceName(),
                        mPoiItem.getCityName(),
                        mPoiItem.getAdName(),
                        mPoiItem.getSnippet() + address,
                        total_floor,
                        floor,
                        measure_area,
                        mEdtIntroduce.getText().toString(),
                        img,
                        mPoiItem.getLatLonPoint().getLongitude() + "",
                        mPoiItem.getLatLonPoint().getLatitude() + "")
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
                            AddHouseCompleteActivity.toActivity(mActivity);
                            RxBus.getDefault().post(new AddHouseCompleteEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }
}