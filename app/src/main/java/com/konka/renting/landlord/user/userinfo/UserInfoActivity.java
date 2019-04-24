package com.konka.renting.landlord.user.userinfo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class UserInfoActivity extends BaseActivity implements CustompopupWindow.OnPopItemClickListener, OnDateSetListener {

    @BindView(R.id.iv_header)
    CircleImageView mIvHeader;
    @BindView(R.id.et_name)
    TextView mEtName;
    @BindView(R.id.tv_sex_detail)
    TextView mTvSexDetail;
    @BindView(R.id.tv_age_detail)
    TextView mTvAgeDetail;
    @BindView(R.id.et_idcard)
    TextView mEtIdcard;
    @BindView(R.id.iv_front)
    ImageView mIvFront;
    @BindView(R.id.tv_front)
    TextView mTvFront;
    @BindView(R.id.iv_idcard_back)
    ImageView mIvBack;
    @BindView(R.id.tv_back)
    TextView mTvBack;
    @BindView(R.id.tv_right)
    TextView mTvRight;
    @BindView(R.id.lin_user_info)
    LinearLayout mLinUserInfo;
    @BindView(R.id.tv_mobile)
    TextView mTvMobile;
    @BindView(R.id.tv_face_right)
    TextView tvFaceRight;

    private TimePickerDialog mDialogAll;        // 日期选择器控件
    private String time = null;
    private final int STORAGE_PERMISSIONS_REQUEST_CODE = 222;    //  获取SDK权限
    private static final int RESULT_CODE_CAMEA = 001;           //拍照
    private static final int RESULT_CODE_PHOTO = 002;           //相册
    private CustompopupWindow pop;          // popupWindow 弹出框
    private File path = new File(Environment.getExternalStorageDirectory().getPath());
    private File outputImage = new File(path + "/photo.jpg");
    private Uri imageUri;                  //定义的图片路径
    private Uri cropImageUri;             // 拍照、相册选择定义最终路
    private File filepath = new File(Environment.getExternalStorageDirectory().getPath() + "/touxiang.jpg");  // 最终转换路径
    private int type;
    private String headName;
    private String picPath;
    private String frontName;
    private String backName;
    private String birthday;
    private String sex;
    private LandlordUserDetailsInfoBean bean;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    private final int PHOTO_REQUEST_CODE = 111;                    // 是否开启相机权限
    private PhotoBitmapSelect selectPhoto;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void init() {
        setTitleText(R.string.user_setting);
//        setRightText(R.string.common_save);
        mTvRight.setVisibility(View.GONE);
        requestPermission();      // 6.0申请拍照权限
        autoObtainStoragePermission();        //  自动获取sdk权限
        getData();
        addRxBusSubscribe(BindMobileEvent.class, new Action1<BindMobileEvent>() {
            @Override
            public void call(BindMobileEvent bindMobileEvent) {
                mTvMobile.setText(bindMobileEvent.getPhone());
                RxBus.getDefault().post(new UpdateEvent());
            }
        });
        addRxBusSubscribe(FaceDectectEvent.class, new Action1<FaceDectectEvent>() {
            @Override
            public void call(FaceDectectEvent faceDectectEvent) {
                finish();
            }
        });

    }

    private void getData() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordUserDetailsInfo()
                .compose(RxUtil.<DataInfo<LandlordUserDetailsInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<LandlordUserDetailsInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismiss();
                        doFailed();
                        finish();
                    }

                    @Override
                    public void onNext(DataInfo<LandlordUserDetailsInfoBean> dataInfo) {
                        super.onNext(dataInfo);
                        dismiss();
                        if (dataInfo.success()) {
                            bean = dataInfo.data();
                            initData(dataInfo.data());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void initData(LandlordUserDetailsInfoBean data) {
        if (data.getHeadimgurl() != null)
            if (!data.getHeadimgurl().equals(""))
                Picasso.get().load(data.getHeadimgurl()).placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIvHeader);
        mEtName.setText(data.getReal_name());
        mTvSexDetail.setText(data.getSex());
        mTvAgeDetail.setText(data.getAge());
        if (data.getIs_identity() == 1) {
            tvFaceRight.setText(R.string.setting_user_real_ok);
        } else {
            tvFaceRight.setText(R.string.setting_user_real_no);
        }
        String tel = data.getPhone();
        if (!tel.equals("")) {
            int len = tel.length();
            String str = tel.substring(0, 3);
            for (int i = 3; i < len - 2; i++) {
                str += "*";
            }
            str += tel.substring(len - 2, len);
            tel = str;
        }
        mTvMobile.setText(tel);
        String idCard = data.getIdentity();
        if (!idCard.equals("")) {
            int len = idCard.length();
            String str = idCard.substring(0, 1);
            for (int i = 1; i < len - 4; i++) {
                str += "*";
            }
            str += idCard.substring(len - 4, len);
            idCard = str;
        }
        mEtIdcard.setText(idCard);
        if (!data.getIdentity_just().equals("")) {
            mTvFront.setVisibility(View.GONE);
            mIvFront.setVisibility(View.VISIBLE);
            Picasso.get().load(data.getIdentity_just()).placeholder(R.mipmap.icon_photo).error(R.mipmap.icon_photo).into(mIvFront);
        }
        if (!data.getIdentity_back().equals("")) {
            mTvBack.setVisibility(View.GONE);
            mIvBack.setVisibility(View.VISIBLE);
            Picasso.get().load(data.getIdentity_back()).placeholder(R.mipmap.icon_photo).error(R.mipmap.icon_photo).into(mIvBack);
        }

    }


    @OnClick({R.id.iv_back, R.id.re_revise_photo, R.id.rl_name, R.id.re_sex, R.id.re_age, R.id.re_bind, R.id.re_idcard,
            R.id.rl_idcard_photo, R.id.re_front, R.id.re_back, R.id.re_face, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.re_revise_photo://头像
                setPhoto(1);
                type = 1;
                break;
            case R.id.re_sex://性别
//            change_sex();
            case R.id.re_age://年龄
//            showDatePicker();
            case R.id.rl_name://姓名
            case R.id.re_idcard://身份证号码
            case R.id.rl_idcard_photo://身份证照片
            case R.id.re_face://实名制认证
                changeReal();
                break;
            case R.id.re_bind://修改手机
                if (bean.getIs_identity() != 1)
                    NewFaceDectectActivity.toActivity(this, 1);
                else
                    showWarmDialog(3);
                break;
            case R.id.tv_right://保存
//                updateInfo();
                break;
        }
    }

    public void change_sex() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //定义一个AlertDialog
        final String[] strarr = {"男", "女"};
        builder.setItems(strarr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // 自动生成的方法存根
                if (arg1 == 0) {//男
                    sex = "1";
                    mTvSexDetail.setText("男");
                } else {//女
                    sex = "2";
                    mTvSexDetail.setText("女");
                }
            }
        });
        builder.show();
    }

    //展示时间选择器
    private void showDatePicker() {
        long tenYears = 100L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId(time)
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - tenYears)
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(19)
                .build();
        mDialogAll.show(getSupportFragmentManager(), "YEAR_MONTH_DAY");
    }

    private void changeReal() {
        if (bean.getIs_identity() != 1)
            NewFaceDectectActivity.toActivity(this, 1);
        else showWarmDialog(2);
    }

    /**
     * 判断是否开启拍照权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            /*if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                //请求获取录音权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PHOTO_REQUEST_CODE);
            } else {
                if (hasSdcard()) {
                    initPhoto();   //  调用拍照弹出框获取图片
                } else {
                    Toast.makeText(this, "设备没有SD卡", Toast.LENGTH_SHORT).show();
                }
            }*/
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_REQUEST_CODE);
        } else {
            //系统不高于6.0直接执行
            initPhoto();   //获取图片
        }
    }

    /**
     * 自动获取sdk权限
     */
    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            initPhoto();   //  调用拍照弹出框获取图片
        }
    }

    // 相机开启权限判断回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case PHOTO_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        initPhoto();   //获取图片
                    } else {
                        Toast.makeText(this, "设备没有SD卡", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请允许打开相机", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        initPhoto();   //获取图片
                    } else {
                        Toast.makeText(this, "设备没有SD卡", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请允许打操作SDCard", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            default:
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // 拍照、相册弹出框
    private void initPhoto() {
        // 选择图片——拍照、相册
        pop = new CustompopupWindow(this);
        pop.setPopItemClickListener(this);
    }

    //设置头像
    private void setPhoto(int i) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.7f;
//                if (bgAlpha == 1) {
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
//                } else {
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
//                }
        this.getWindow().setAttributes(lp);
        // popupwindow 第一个参数指定popup 显示页面
        pop.showAtLocation(this.findViewById(R.id.lin_user_info), Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    // （调用拍照弹出框）选择图片方式
    @Override
    public void setPopItemClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn_take_photo:
                imageUri = Uri.fromFile(outputImage);    //将file 转化为uri路径
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this, "com.konka.fileprovider", outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);   //指定图片输出地址   将拍照结果保存至photo_file的Uri中，不保留在相册中
                startActivityForResult(intent, RESULT_CODE_CAMEA);   //启动相机  startActivityForResult() 结果返回onActivityResult()函数   第2个参数——拍照返回参数
                pop.dismiss();
                break;
            case R.id.id_btn_select:
                //调用相册
                Intent photo = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photo.setType("image/*");
                startActivityForResult(photo, RESULT_CODE_PHOTO);
                pop.dismiss();
                break;
            case R.id.id_btn_cancel:
                pop.dismiss();
                break;
        }
    }

    /**
     * 拍照、调用相册回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CODE_CAMEA:      // 拍照后裁剪图片

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserInfoActivity.this.getContentResolver(), imageUri);
                    if (bitmap != null) {
                        //userInfoPhoto.setImageBitmap(bitmap);              //  显示头像
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 上传头像
                try {
                    Bitmap bitmap_up = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));

                    try {             //保存头像到本地
                        saveBitmap(bitmap_up);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                break;
            case RESULT_CODE_PHOTO:    // 相册裁剪图片
                if (data != null) {
                    cropImageUri = Uri.fromFile(filepath);
                    Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                    // 内容提供者标示
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(this, "com.konka.fileprovider", new File(newUri.getPath()));
                    }
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserInfoActivity.this.getContentResolver(), newUri);
                        if (bitmap != null) {
                            //userInfoPhoto.setImageBitmap(bitmap);              //  显示头像
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 上传头像
                    try {
                        Bitmap bitmap_up = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(newUri));

                        try {             //保存头像到本地
                            saveBitmap(bitmap_up);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
        }
    }

    /**
     * 保存头像到本地
     * 上传头像图片
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    private File saveBitmap(Bitmap bitmap) throws IOException {
        String path_head = Environment.getExternalStorageDirectory().toString();   // 生成头像路径
        File dirFile = new File(path_head);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File photoFile = new File(path_head + "/photo.jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(photoFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        uploadPhoto(photoFile);
        return photoFile;
    }

    private void uploadPhoto(final File file) {
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
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<UploadPicBean> uploadRecordBeanDataInfo) {

                        dismiss();
                        if (uploadRecordBeanDataInfo.success()) {
                            if (type == 1) {
                                headName = uploadRecordBeanDataInfo.data().getFilename();
                                picPath=uploadRecordBeanDataInfo.data().getThumb_url();
                                updateInfo();
                            }
                            if (type == 2) {
                                frontName = uploadRecordBeanDataInfo.data().getFilename();
                                Picasso.get().load(file).into(mIvFront);
                                mTvFront.setVisibility(View.GONE);
                                mIvFront.setVisibility(View.VISIBLE);
                            }
                            if (type == 3) {
                                backName = uploadRecordBeanDataInfo.data().getFilename();
                                Picasso.get().load(file).into(mIvBack);
                                mTvBack.setVisibility(View.GONE);
                                mIvBack.setVisibility(View.VISIBLE);
                            }

                        } else {
                            showToast(uploadRecordBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void updateInfo() {
        showLoadingDialog();
        Observable observable = null;
        Subscription subscription = SecondRetrofitHelper.getInstance().updateHead(headName)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            Picasso.get().load(picPath).placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIvHeader);
                            showToast(dataInfo.msg());
                            RxBus.getDefault().post(new UpdateEvent());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //  日期选择器
    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {

        birthday = getDateToString(millseconds);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy");

        int nowYear = Integer.parseInt(formate.format(new Date()));
        int year = Integer.parseInt(formate.format(millseconds));
        String age = String.valueOf(nowYear - year);
        mTvAgeDetail.setText(age);


    }

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void showWarmDialog(final int type) {
        if (type == 3) {
            new AlertDialog.Builder(this).setTitle(R.string.setting_user_title_real)
                    .setMessage(R.string.setting_user_title_phone_msg)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FaceDetectActivity.toActivity(UserInfoActivity.this, type);
                        }
                    })
                    .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.setting_user_title_real)
                    .setMessage(R.string.setting_user_title_real_msg)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FaceDetectActivity.toActivity(UserInfoActivity.this, type);
                        }
                    })
                    .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

}
