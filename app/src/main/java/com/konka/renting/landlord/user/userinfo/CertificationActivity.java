package com.konka.renting.landlord.user.userinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.IdCardFrontbean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.event.RenZhengSuccessEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.APIService;
import com.konka.renting.utils.Config;
import com.konka.renting.utils.FaceException;
import com.konka.renting.utils.FileUtil;
import com.konka.renting.utils.LivenessVsIdcardResult;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;

public class CertificationActivity extends BaseActivity {

    @BindView(R.id.iv_idcard_head)
    ImageView mIvIdcardHead;
    @BindView(R.id.iv_idcard_back)
    ImageView mIvIdcardBack;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_idcard)
    EditText mEtIdcard;
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
    private String photoUrlName;
    private String frontName;
    private String backName;
    private String start_time;
    private String end_time;
    private final int PHOTO_REQUEST_CODE = 111;                    // 是否开启相机权限
    private Intent intent;
    private boolean hasGotToken = false;
    private AlertDialog.Builder alertDialog;
    private static final int REQUEST_CODE_CAMERA = 102;
    private File file;
    private String filePath = "";
    //    private String bestImage;
    private String idnumber;
    private String username;
    private int type1;
    private String sex = "";
    private String birthDay = "";


    public static void toActivity(Context context, int type) {

        Intent intent = new Intent(context, CertificationActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putString("bestimage_path", bestImagepath);
        bundle.putInt("type", type);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_certification;
    }

    @Override
    public void init() {
        setTitleText(R.string.certification);

        alertDialog = new AlertDialog.Builder(this);
        Bundle bundle = getIntent().getExtras();
//        bestImage = bundle.getString("bestimage_path");
        type1 = bundle.getInt("type");
//        if (bestImage != null)
//            uploadPic(new File(bestImage));
        initAccessTokenLicenseFile();
        //  初始化本地质量控制模型,释放代码在onDestory中
        //  调用身份证扫描必须加上 intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true); 关闭自动初始化和释放本地模型
        CameraNativeHelper.init(this, OCR.getInstance(this).getLicense(),
                new CameraNativeHelper.CameraNativeInitCallback() {
                    @Override
                    public void onError(int errorCode, Throwable e) {
                        String msg;
                        switch (errorCode) {
                            case CameraView.NATIVE_SOLOAD_FAIL:
                                msg = "加载so失败，请确保apk中存在ui部分的so";
                                break;
                            case CameraView.NATIVE_AUTH_FAIL:
                                msg = "授权本地质量控制token获取失败";
                                break;
                            case CameraView.NATIVE_INIT_FAIL:
                                msg = e.getMessage();
                                break;
                            default:
                                msg = String.valueOf(errorCode);
                        }
                        alertText("本地质量控制初始化错误，错误原因： ", msg);
                    }
                });
        addRxBusSubscribe(RenZhengSuccessEvent.class, new Action1<RenZhengSuccessEvent>() {
            @Override
            public void call(RenZhengSuccessEvent renZhengSuccessEvent) {
                finish();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.iv_idcard_head, R.id.iv_idcard_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_idcard_head:
                if (!checkTokenStatus()) {
                    return;
                }
                intent = new Intent(CertificationActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE,
                        true);
                // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                // 请手动使用CameraNativeHelper初始化和释放模型
                // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL,
                        true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                Log.e("filepath1", FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case R.id.iv_idcard_back:
                intent = new Intent(CertificationActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        "/data/user/0/com.konka.renting/files/pic1.jpg");
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE,
                        true);
                // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                // 请手动使用CameraNativeHelper初始化和释放模型
                // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL,
                        true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case R.id.btn_commit:
                /* // 调转到活体识别界面*/
                username = mEtName.getText().toString();
                idnumber = mEtIdcard.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(idnumber)) {
                    showToast(R.string.warm_fail_up_face_f);
                } else if (TextUtils.isEmpty(backName)) {
                    showToast(R.string.warm_fail_up_face_b);
                } else {
                    FaceDetectActivity.toActivity(this, type1, username, idnumber, frontName, backName, sex, birthDay, start_time, end_time);
//                    initAccessToken();
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);

                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                        type = 2;
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        filePath = "/data/user/0/com.konka.renting/files/pic1.jpg";
                        type = 3;
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
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
        //uploadPhoto(photoFile);
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
                            if (type == 2) {
                                frontName = uploadRecordBeanDataInfo.data().getFilename();
                                Picasso.get().load(uploadRecordBeanDataInfo.data().getUrl()).into(mIvIdcardHead);
                            }
                            if (type == 3) {
                                backName = uploadRecordBeanDataInfo.data().getFilename();
                                Picasso.get().load(uploadRecordBeanDataInfo.data().getUrl()).into(mIvIdcardBack);
                            }

                        } else {
                            showToast(uploadRecordBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void idCardfrontDectect(String frontName) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().idCardFrontDectect(frontName)
                .compose(RxUtil.<DataInfo<IdCardFrontbean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<IdCardFrontbean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<IdCardFrontbean> idCardFrontbeanDataInfo) {

                        dismiss();
                        showToast(idCardFrontbeanDataInfo.msg());
                        if (idCardFrontbeanDataInfo.success()) {
                            mEtName.setText(idCardFrontbeanDataInfo.data().name);
                            mEtIdcard.setText(idCardFrontbeanDataInfo.data().id);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void identyFacedect() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().identityAuth(mEtName.getText().toString(), mEtIdcard.getText().toString(),
                frontName, backName, photoUrlName, sex, birthDay, start_time, end_time)
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
                            LoginUserBean.getInstance().setIs_lodge_identity("1");
                            LoginUserBean.getInstance().setRealname(mEtName.getText().toString());
                            LoginUserBean.getInstance().setIdentity(mEtIdcard.getText().toString());
                            IdentyActivity.toActivity(mActivity, "2", type1);
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                            IdentyActivity.toActivity(mActivity, "3", type1);
                            finish();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void identyTentFacedect() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().identityIdent(mEtName.getText().toString(), mEtIdcard.getText().toString())
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
                            identyFacedect();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void recIDCard(String idCardSide, final String filePath) {
        file = new File(filePath);
        final IDCardParams param = new IDCardParams();
        param.setImageFile(file);
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);
       /* if (param.getIdCardSide().equals("front"))
            Picasso.with(this).load(param.getImageFile()).into(mIvIdcardHead);
        if (param.getIdCardSide().equals("back"))
            Picasso.with(this).load(param.getImageFile()).into(mIvIdcardBack);*/
        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    //alertText("", result.toString());
                    if (result.getIdCardSide().equals(IDCardParams.ID_CARD_SIDE_FRONT)) {
                        if (result.getIdNumber() != null && !TextUtils.isEmpty(result.getIdNumber().toString()))
                            mEtIdcard.setText(result.getIdNumber().toString());
                        if (result.getName() != null && !TextUtils.isEmpty(result.getName().toString()))
                            mEtName.setText(result.getName().toString());
                        if (result.getGender() != null && !TextUtils.isEmpty(result.getGender().toString()))
                            sex = result.getGender().toString().equals("男") ? "1" : "2";
                        if (result.getBirthday() != null && !TextUtils.isEmpty(result.getBirthday().toString()))
                            birthDay = result.getBirthday().toString();
                    } else {
                        start_time = result.getSignDate().toString();
                        end_time = result.getExpiryDate().toString();
                    }
                    uploadPhoto(file);
                } else {
                    showToast(idCardSide.equals(IDCardParams.ID_CARD_SIDE_FRONT) ? R.string.warm_fail_face_f : R.string.warm_fail_face_b);
                }
            }

            @Override
            public void onError(OCRError error) {
                alertText("", error.getMessage());
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
                alertText("自定义文件路径licence方式获取token失败", error.getMessage());
            }
        }, "aip.license", getApplicationContext());
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        CameraNativeHelper.release();
        OCR.getInstance(this).release();

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
        showLoadingDialog();
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        //displayTip(resultTipTV, "公安身份核实中...");
        String username = mEtName.getText().toString();
        String idnumber = mEtIdcard.getText().toString();
        APIService.getInstance().policeVerify(username, idnumber, filePath, new
                com.konka.renting.utils.OnResultListener<LivenessVsIdcardResult>() {
                    @Override
                    public void onResult(LivenessVsIdcardResult result) {
                        dismiss();
                        delete(filePath);
                        if (result != null && result.getResult().getScoreX() >= 80) {
                            if (type1 == 1)
                                identyFacedect();
                            else {
//                                    BindMobileActivity.toActivity(mActivity);
                                RxBus.getDefault().post(new FaceDectectEvent());
                                finish();
                            }
                        } else {
                            showToast("核身失败,公安验证分数过低");

                        }
                    }

                    @Override
                    public void onError(FaceException error) {
                        dismiss();
                        delete(filePath);
                        // TODO 错误处理
                        // 如返回错误码为：216600，则核身失败，提示信息为：身份证号码错误
                        // 如返回错误码为：216601，则核身失败，提示信息为：身份证号码与姓名不匹配
                        Toast.makeText(CertificationActivity.this, "公安身份核实失败:" + error.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }


    // 在线活体检测和公安核实需要使用该token，为了防止ak、sk泄露，建议在线活体检测和公安接口在您的服务端请求
    private void initAccessToken() {

        showLoadingDialog();
        mProgressDialog.setCanceledOnTouchOutside(false);
        APIService.getInstance().init(getApplicationContext());
        APIService.getInstance().initAccessTokenWithAkSk(new com.konka.renting.utils.OnResultListener<com.konka.renting.utils.AccessToken>() {
            @Override
            public void onResult(com.konka.renting.utils.AccessToken result) {
                dismiss();
                if (result != null && !TextUtils.isEmpty(result.getAccessToken())) {
//                    if (type1 == 1)
//                        policeVerify(bestImage);
                } else if (result != null) {
                    showToast("在线活体token获取失败");
                } else {
                    showToast("在线活体token获取失败");
                }
            }

            @Override
            public void onError(FaceException error) {
                // TODO 错误处理
                dismiss();
                showToast("在线活体token获取失败");
            }
        }, Config.apiKey, Config.secretKey);
    }

    private void delete(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void displayTip(final TextView textView, final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) {
                    textView.setText(tip);
                }
            }
        });
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
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<UploadPicBean> uploadRecordBeanDataInfo) {

                        dismiss();
                        if (uploadRecordBeanDataInfo.success()) {
                            photoUrlName = uploadRecordBeanDataInfo.data().getFilename();
                        } else {
                            showToast(uploadRecordBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
