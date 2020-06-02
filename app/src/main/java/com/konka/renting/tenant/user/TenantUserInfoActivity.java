package com.konka.renting.tenant.user;

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
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.UriUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.TenantUserinfoBean;
import com.konka.renting.bean.UploadRecordBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.user.userinfo.BindMobileEvent;
import com.konka.renting.landlord.user.userinfo.CertificationActivity;
import com.konka.renting.landlord.user.userinfo.CustompopupWindow;
import com.konka.renting.landlord.user.userinfo.FaceDectectEvent;
import com.konka.renting.landlord.user.userinfo.FaceDetectActivity;
import com.konka.renting.landlord.user.userinfo.IdentyActivity;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.landlord.user.userinfo.PhotoBitmapSelect;
import com.konka.renting.landlord.user.userinfo.PhotoUtils;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.landlord.user.userinfo.UpdatePhotoActivity;
import com.konka.renting.login.ForgetPasswordActivity;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.utils.ImagePickerProvider;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;

public class TenantUserInfoActivity extends BaseActivity implements CustompopupWindow.OnPopItemClickListener {

    @BindView(R.id.iv_header)
    CircleImageView mIvHeader;
    @BindView(R.id.iv_idcard_back)
    ImageView mIvBack;
    @BindView(R.id.lin_user_info)
    LinearLayout mLinUserInfo;
    @BindView(R.id.tv_mobile)
    TextView mTvMobile;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.ic_right)
    ImageView icRight;
    @BindView(R.id.re_revise_photo)
    RelativeLayout reRevisePhoto;
    @BindView(R.id.tv_pwd)
    TextView tvPwd;
    @BindView(R.id.pwd_right)
    ImageView pwdRight;
    @BindView(R.id.re_pwd)
    RelativeLayout rePwd;
    @BindView(R.id.tv_bind)
    TextView tvBind;
    @BindView(R.id.bind_right)
    ImageView bindRight;
    @BindView(R.id.re_bind)
    RelativeLayout reBind;
    @BindView(R.id.activity_user_info_tv_login_out)
    TextView tvLoginOut;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.re_version)
    RelativeLayout reVersion;

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
    private TenantUserinfoBean userInfoBean;

    public static void toActivity(Context context, TenantUserinfoBean userInfoBean) {
        Intent intent = new Intent(context, TenantUserInfoActivity.class);
        intent.putExtra("userinfo", userInfoBean);
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
        setRightText(R.string.common_save);
        requestPermission();      // 6.0申请拍照权限
        autoObtainStoragePermission();        //  自动获取sdk权限
        initData();
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

    private void initData() {
        Intent intent = getIntent();
        userInfoBean = (TenantUserinfoBean) intent.getSerializableExtra("userinfo");

        if (userInfoBean.getHeadimgurl() != null)
            if (!userInfoBean.getHeadimgurl().equals(""))
                Picasso.get().load(userInfoBean.getHeadimgurl()).placeholder(R.mipmap.icon_photo).error(R.mipmap.icon_photo).into(mIvHeader);
        String tel = userInfoBean.getTel();
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
        tvVersion.setText(getVerName(this));

    }


    @OnClick({R.id.iv_back, R.id.re_revise_photo, R.id.re_bind, R.id.tv_right, R.id.re_pwd, R.id.activity_user_info_tv_login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.tv_right://保存
                break;
            case R.id.re_revise_photo://头像
                if (userInfoBean != null)
                    new RxPermissions(mActivity).request(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        setPhoto(1);
                                        type = 1;
                                    } else {
                                        showToast(getString(R.string.no_permissions));
                                    }
                                }
                            });
                break;
            case R.id.re_pwd://修改密码
                if (userInfoBean != null)
                    ForgetPasswordActivity.toActivity(this, LoginInfo.TENANT, userInfoBean.getTel());
                break;
            case R.id.re_bind://修改手机
                if (userInfoBean != null)
                    UpdatePhotoActivity.toActivity(this, LoginInfo.TENANT, userInfoBean.getTel());
                break;
            case R.id.activity_user_info_tv_login_out://退出登录
                new AlertDialog.Builder(this).setTitle(R.string.login_out).setMessage("是否退出登录")
                        .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                landmoreLoginOut();
                            }
                        }).setNegativeButton(R.string.warn_cancel, null).create().show();
                break;

        }
    }

    private final int PHOTO_REQUEST_TAKEPHOTO = 1;

    /**
     * 判断是否开启拍照权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_REQUEST_TAKEPHOTO);
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
            case PHOTO_REQUEST_TAKEPHOTO:
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //		获取图片沙盒文件夹
                    File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File f = new File(PICTURES.getAbsolutePath(), "photo.jpg");
                    Uri uri = FileProvider.getUriForFile(mActivity, ImagePickerProvider.getFileProviderName(mActivity), f);
                    //跳转到相机
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, RESULT_CODE_CAMEA);
                } else {
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
                }
                pop.dismiss();
                break;
            case R.id.id_btn_select:
                //调用相册
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ImageSelector.builder()
                            .useCamera(false) // 设置是否使用拍照
                            .setSingle(true)  //设置是否单选
                            .canPreview(true) //是否可以预览图片，默认为true
                            .start(this, RESULT_CODE_PHOTO); // 打开相册
                } else {
                    //调用相册
                    Intent photo = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photo.setType("image/*");
                    startActivityForResult(photo, RESULT_CODE_PHOTO);
                }
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(TenantUserInfoActivity.this.getContentResolver(), imageUri);
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
                }else{
                    //		获取图片沙盒文件夹
                    File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File f = new File(PICTURES.getAbsolutePath() , "photo.jpg");
                    uploadPhoto(f);
                }

                break;
            case RESULT_CODE_PHOTO:    // 相册裁剪图片
                if (data != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        cropImageUri = Uri.fromFile(filepath);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        // 内容提供者标示
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(this, "com.konka.fileprovider", new File(newUri.getPath()));
                        }
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(TenantUserInfoActivity.this.getContentResolver(), newUri);
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
                    }else {
                        File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        ArrayList<String> images = data.getStringArrayListExtra(
                                ImageSelector.SELECT_RESULT);

                        int size = images.size();
                        for (int i = 0; i < size; i++) {
                            String path = images.get(i);
                            Uri uri = UriUtils.getImageContentUri(this, path);
                            //图片路径
                            String mFilePath = PICTURES.getAbsolutePath() + "/photo.jpg";
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
                            uploadPhoto(file);

                        }
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

    private void uploadPhoto(final File f) {
        showLoadingDialog();
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", f.getName(), RequestBody.create(null, f));
        Subscription subscription = RetrofitHelper.getInstance().uploadPhoto(part)
                .compose(RxUtil.<DataInfo<UploadRecordBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<UploadRecordBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<UploadRecordBean> uploadRecordBeanDataInfo) {

                        dismiss();
                        if (uploadRecordBeanDataInfo.success()) {
                            if (type == 1) {
                                headName = uploadRecordBeanDataInfo.data().filename;
                                Picasso.get().load(f).placeholder(R.mipmap.icon_photo).error(R.mipmap.icon_photo).into(mIvHeader);
                            }

                        } else {
                            showToast(uploadRecordBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void landmoreLoginOut() {
        LoginUserBean.getInstance().reset();
        LoginUserBean.getInstance().save();
        LoginNewActivity.toTenantActivity(mActivity);
        finish();
    }

    private String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

}
