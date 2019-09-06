package com.konka.renting.landlord.user.userinfo;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.ui.FaceLivenessActivity;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.idl.facesdk.FaceSDK;
import com.baidu.idl.facesdk.FaceTracker;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.PermissionCallback;
import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.event.RenZhengSuccessEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.APIService;
import com.konka.renting.utils.Config;
import com.konka.renting.utils.FaceCropper;
import com.konka.renting.utils.FaceException;
import com.konka.renting.utils.LivenessVsIdcardResult;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class FaceDetectActivity extends FaceLivenessActivity {

    private int type;
    String name;
    String idCard;
    String faceImg;
    String backImg;
    String sex;
    String birthDay;
    String photoUrlName;
    private String start_time;
    private String end_time;

    public static void toActivity(Context context, int type, String name, String idCard, String faceImg, String backImg, String sex, String birthDay, String start_time, String end_time) {
        Intent intent = new Intent(context, FaceDetectActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("idCard", idCard);
        intent.putExtra("faceImg", faceImg);
        intent.putExtra("backImg", backImg);
        intent.putExtra("sex", sex);
        intent.putExtra("birthDay", birthDay);
        intent.putExtra("start_time", start_time);
        intent.putExtra("end_time", end_time);
        context.startActivity(intent);
    }

    private String bestImagePath;
    private AlertDialog.Builder alertDialog;
    private boolean hasGotToken = false;
    private AlertDialog.Builder alertlllDialog;

    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int PERMISSIONS_REQUEST_CAMERA = 800;
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;

    private PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public boolean onRequestPermission() {
            ActivityCompat.requestPermissions(FaceDetectActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFaceSDK();
        initAccessTokenLicenseFile();
        type = getIntent().getIntExtra("type", 1);
        name = getIntent().getStringExtra("name");
        idCard = getIntent().getStringExtra("idCard");
        faceImg = getIntent().getStringExtra("faceImg");
        backImg = getIntent().getStringExtra("backImg");
        sex = getIntent().getStringExtra("sex");
        birthDay = getIntent().getStringExtra("birthDay");
        start_time = getIntent().getStringExtra("start_time");
        end_time = getIntent().getStringExtra("end_time");


        alertDialog = new AlertDialog.Builder(this);
        alertlllDialog = new AlertDialog.Builder(this);
        RxBus.getDefault().toDefaultObservable(FaceDetectActivity.class, new Action1<FaceDetectActivity>() {
            @Override
            public void call(FaceDetectActivity faceDetectActivity) {
                finish();
            }
        });
    }

    /**
     * 初始化SDK
     */
    private void initFaceSDK() {
        // 第一个参数 应用上下文
        // 第二个参数 licenseID license申请界面查看
        // 第三个参数 assets目录下的License文件名
        FaceSDKManager.getInstance().initialize(this, Config.licenseID, Config.licenseFileName);
        setFaceConfig();
    }

    private void setFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行调整。如果没有指定动作，将使用所有的动作
        List<LivenessTypeEnum> livenessList = new ArrayList<>();
        livenessList.add(LivenessTypeEnum.Mouth);
        livenessList.add(LivenessTypeEnum.Eye);
        livenessList.add(LivenessTypeEnum.HeadUp);
        livenessList.add(LivenessTypeEnum.HeadDown);
        livenessList.add(LivenessTypeEnum.HeadLeft);
        livenessList.add(LivenessTypeEnum.HeadRight);
        config.setLivenessTypeList(livenessList);

        // 设置 活体动作是否随机 boolean
        config.setLivenessRandom(true);
        config.setLivenessRandomCount(2);
        // 模糊度范围 (0-1) 推荐小于0.7
        config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
        // 光照范围 (0-1) 推荐大于40
        config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
        // 裁剪人脸大小
        config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
        config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL);
        config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // 人脸置信度（0-1）推荐大于0.6
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
        // 是否进行质量检测
        config.setCheckFaceQuality(true);
        // 人脸检测使用线程数
        // config.setFaceDecodeNumberOfThreads(4);
        // 是否开启提示音
        config.setSound(true);

        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    @Override
    public void onLivenessCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap) {
        super.onLivenessCompletion(status, message, base64ImageMap);
        if (status == FaceStatusEnum.OK && mIsCompletion) {
            // Toast.makeText(this, "活体检测成功", Toast.LENGTH_SHORT).show();
            saveImage(base64ImageMap);
            alertText("检测结果", "活体检测成功", true);
        } else if (status == FaceStatusEnum.Error_DetectTimeout ||
                status == FaceStatusEnum.Error_LivenessTimeout ||
                status == FaceStatusEnum.Error_Timeout) {
            // Toast.makeText(this, "活体检测采集超时", Toast.LENGTH_SHORT).show();
            alertText("检测结果", "活体检测采集超时", false);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void saveImage(HashMap<String, String> imageMap) {
        // imageMap 里保存了最佳人脸和各个动作的图片，若对安全要求比较高，可以传多张图片进行在线活体，目前只用最佳人脸进行了在线活体检测
        //        Set<Map.Entry<String, String>> sets = imageMap.entrySet();
        //
        //        Bundle bundle = new Bundle();
        //        Bitmap bmp;
        //        List<File> fileList = new ArrayList<>();
        //        for (Map.Entry<String, String> entry : sets) {
        //            bmp = base64ToBitmap(entry.getValue());
        //            ImageView iv = new ImageView(this);
        //            iv.setImageBitmap(bmp);
        //
        //            try {
        //                File file = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
        //                FileOutputStream outputStream = new FileOutputStream(file);
        //                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        //                outputStream.close();
        //                fileList.add(new File(file.getAbsolutePath()));
        //
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //        onlineLiveness(fileList);

        String bestimageBase64 = imageMap.get("bestImage0");
        Bitmap bmp = base64ToBitmap(bestimageBase64);

//        Bitmap newBmp = detect(bmp);
//        if (newBmp == null) {
//            newBmp = bmp;
//        }

        // 如果觉的在线校验慢，可以压缩图片的分辨率，目前没有压缩分辨率，压缩质量置80，在neuxs5上大概30k，后面版本我们将截出人脸部分，大小应该小于10k
        try {
            File file = File.createTempFile("face", ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();

            bestImagePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap detect(Bitmap bitmap) {

        FaceSDKManager.getInstance().getFaceTracker().clearTrackedFaces();

        int[] argb = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int value = FaceSDKManager.getInstance().getFaceTracker().prepare_data_for_verify(
                argb, bitmap.getHeight(), bitmap.getWidth(), FaceSDK.ImgType.ARGB.ordinal(),
                FaceTracker.ActionType.RECOGNIZE.ordinal());

        FaceInfo[] faces = FaceSDKManager.getInstance().getFaceTracker().get_TrackedFaceInfo();
        Log.i("detect", value + " faces->" + faces);
        if (faces != null) {

            FaceInfo faceInfo = faces[0];
            int faceWith = faceInfo.mWidth;
            int centerX = faceInfo.mCenter_x;
            int centerY = faceInfo.mCenter_y;

            int left = centerX - faceWith / 2;
            int top = centerY - faceWith / 2;
            // int left = 0;
            int right = centerX + faceWith / 2;
            // int right = bitmap.getWidth();
            // int top = centerY - faceWith;
            if (left < 0) {
                left = 0;
            }
            if (right > bitmap.getWidth()) {
                right = bitmap.getWidth();
            }
            if (top < 0) {
                top = 0;
            }
            int bottom = centerY + faceWith / 2;
            if (bottom > bitmap.getHeight()) {
                bottom = bitmap.getHeight();
            }

            Rect cropRect = new Rect(left, top, right, bottom);
            int[] cropArgb = FaceCropper.crop(argb, bitmap.getWidth(), cropRect);
            Bitmap cropBitmap =
                    Bitmap.createBitmap(cropArgb, 0, cropRect.width(), cropRect.width(), cropRect.height(),
                            Bitmap.Config.ARGB_8888);

            FaceSDKManager.getInstance().getFaceTracker().clearTrackedFaces();
            return cropBitmap;
        }

        return bitmap;
    }

    private void alertText(final String title, final String message, final boolean isLive) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                               /* Intent intent = new Intent();
                                intent.putExtra("bestimage_path", bestImagePath);
                                setResult(Activity.RESULT_OK, intent);*/
                                if (isLive) {
                                    if (type == 3) {
                                        initAccessToken();
                                    } else {
//                                        CertificationActivity.toActivity(FaceDetectActivity.this, type);
                                        uploadPic(new File(bestImagePath));
                                    }
                                }
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * 自定义license的文件路径和文件名称，以license文件方式初始化
     */
    private void initAccessTokenLicenseFile() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;

            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertlllText("自定义文件路径licence方式获取token失败", error.getMessage());
            }
        }, "aip.license", getApplicationContext());
    }

    private void alertlllText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertlllDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    // 在线活体检测和公安核实需要使用该token，为了防止ak、sk泄露，建议在线活体检测和公安接口在您的服务端请求
    private void initAccessToken() {
        APIService.getInstance().init(getApplicationContext());
        APIService.getInstance().initAccessTokenWithAkSk(new com.konka.renting.utils.OnResultListener<com.konka.renting.utils.AccessToken>() {
            @Override
            public void onResult(com.konka.renting.utils.AccessToken result) {
                Log.e("加载中", result.getAccessToken());
                if (result != null && !TextUtils.isEmpty(result.getAccessToken())) {
                    policeVerify(bestImagePath);

                } else if (result != null) {
                    /*displayTip("获取token", "在线活体token获取失败");
                    retBtn.setVisibility(View.VISIBLE);*/
                    ShowToastUtil.showNormalToast(FaceDetectActivity.this, "在线活体token获取失败");
                    finish();
                } else {
                    /*displayTip(resultTipTV, "在线活体token获取失败");
                    retBtn.setVisibility(View.VISIBLE);*/
                    ShowToastUtil.showNormalToast(FaceDetectActivity.this, "在线活体token获取失败");
                    finish();
                }
            }

            @Override
            public void onError(FaceException error) {
                // TODO 错误处理
               /* displayTip(resultTipTV, "在线活体token获取失败");
                retBtn.setVisibility(View.VISIBLE);*/
                ShowToastUtil.showNormalToast(FaceDetectActivity.this, "在线活体token获取失败");
                finish();
            }
        }, Config.apiKey, Config.secretKey);
    }

    /**
     * 公安接口合并在线活体，调用公安验证接口进行最后的核身比对；公安权限需要在官网控制台提交工单开启
     * 接口地址：https://aip.baidubce.com/rest/2.0/face/v2/person/verify
     * 入参为「姓名」「身份证号」「bestimage」
     * ext_fields 扩展功能。如 faceliveness 表示返回活体值, qualities 表示返回质检测结果
     * quality string 判断质 是否达标。“use” 表示做质 控制,质  好的照 会 直接拒绝
     * faceliveness string 判断活体值是否达标。 use 表示做活体控制,低于活体阈值的 照 会直接拒绝
     * quality_conf和faceliveness_conf 用于指定阈值，超过此分数才调用公安验证，
     *
     * @param filePath
     */
    private void policeVerify(final String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            showToast(R.string.warm_fail_file);
            finish();
            return;
        }
        APIService.getInstance().policeVerify(name, idCard, filePath, new
                com.konka.renting.utils.OnResultListener<LivenessVsIdcardResult>() {
                    @Override
                    public void onResult(LivenessVsIdcardResult result) {
                        if (result != null && result.getResult().getScoreX() >= 80) {
                            sure();
                        } else {
                            ShowToastUtil.showNormalToast(FaceDetectActivity.this, "核身失败");
                            finish();
                        }
                    }

                    @Override
                    public void onError(FaceException error) {
                        //displayTip(resultTipTV, "核身失败：" + error.getErrorMessage());
                        //showToast("核身失败:"+ error.getErrorMessage());
                        // TODO 错误处理
                        // 如返回错误码为：216600，则核身失败，提示信息为：身份证号码错误
                        // 如返回错误码为：216601，则核身失败，提示信息为：身份证号码与姓名不匹配
                        Toast.makeText(FaceDetectActivity.this, "公安身份核实失败:" + error.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                });
    }


    /**
     * 提交认证
     */
    private void sure() {
        Subscription subscription = SecondRetrofitHelper.getInstance().identityAuth(name, idCard,
                faceImg, backImg, photoUrlName, sex, birthDay, start_time, end_time)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        showToast(R.string.warm_fail_face);
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismissLoadingDialog();
                        if (dataInfo.success()) {
                            LoginUserBean.getInstance().setIs_lodge_identity("1");
                            LoginUserBean.getInstance().setRealname(name);
                            LoginUserBean.getInstance().setIdentity(idCard);
                            RxBus.getDefault().post(new RenZhengSuccessEvent());
                            FaceSuccessActivity.toActivity(FaceDetectActivity.this);
                            finish();
                        } else {
                            showToast(dataInfo.msg() + "," + getString(R.string.warm_fail_face));
                            finish();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void uploadPic(final File file) {
        showLoadingDialog();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Subscription subscription = SecondRetrofitHelper.getInstance().uploadPic(fullName, body)
                .compose(RxUtil.<DataInfo<UploadPicBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<UploadPicBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<UploadPicBean> uploadRecordBeanDataInfo) {


                        if (uploadRecordBeanDataInfo.success()) {
                            photoUrlName = uploadRecordBeanDataInfo.data().getFilename();
                            initAccessToken();
                        } else {
                            dismissLoadingDialog();
                            showToast(uploadRecordBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }

    /******************************************BaseActivity*******************************************************/
    protected CompositeSubscription mCompositeSubscription;
    protected ProgressDialog mPDialog;

    public void showLoadingDialog() {
        if (mPDialog == null) {
            mPDialog = new ProgressDialog(this);
            mPDialog.setMessage(getString(R.string.loading));
        }
        mPDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mPDialog != null)
            mPDialog.dismiss();
    }

    public void showToast(String string) {
        UIUtils.displayToast(string);
    }

    public void showToast(int id) {
        UIUtils.displayToast(getString(id));
    }

    public void doFailed() {
        UIUtils.showFailed();
    }

    public void doSuccess() {
        UIUtils.showSuccess();
    }

    /**
     * 取消订阅
     */
    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

    /**
     * 添加订阅
     *
     * @param subscription
     */
    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }


    /**
     * 管理各组件之间的通信
     *
     * @param eventType
     * @param act
     * @param <U>
     */
    protected <U> void addRxBusSubscribe(Class<U> eventType, Action1<U> act) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(eventType, act));
    }

}

