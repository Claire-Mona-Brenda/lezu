package com.konka.renting.tenant.opendoor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.UploadRecordBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.order.MyGridAdapter;
import com.konka.renting.landlord.user.collection.NoScrollGridView;
import com.konka.renting.utils.PictureUtils;
import com.konka.renting.utils.RxUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;

public class UploadFileActivity extends BaseActivity implements MyGridAdapter.SetDeleteListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_upload_file)
    TextView tvUploadFile;
    @BindView(R.id.grid_upload_file)
    NoScrollGridView gridUploadFile;
    @BindView(R.id.btn_confirm_upload)
    Button btnConfirmUpload;

    private final int REQUEST_CODE_CHOOSE = 101;
    private final int PHOTO_REQUEST_CODE = 111;   //  是否开启相机权限
    private MyGridAdapter adapter;
    List<Uri> list = new ArrayList<Uri>();
    List<String> listFilename = new ArrayList<>();
    private File compressedImageFile;
    String order_no;

    public static void toActivity(Context context, String order_no) {
        Intent intent = new Intent(context, UploadFileActivity.class);
        intent.putExtra("order_no", order_no);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_uploadfile;
    }

    @Override
    public void init() {
        setTitleText(R.string.upload_file);
        order_no = getIntent().getStringExtra("order_no");
        RxPermissions rxPermission = new RxPermissions(mActivity);
        rxPermission.request(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                        }
                    }
                });
        initGrid();
    }

    private void initGrid() {
        adapter = new MyGridAdapter(this);
        adapter.setDeleteListener(this);
        gridUploadFile.setAdapter(adapter);
        gridUploadFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == adapterView.getChildCount() - 1) {
                    if (list.size() != 6) {
                        requestPermission();
                    }
                }
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.btn_confirm_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_confirm_upload:
                confirmUpload();
                break;
        }
    }

    /**
     * 判断是否开启拍照权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PHOTO_REQUEST_CODE);
            } else {
                //addPhotoView();      //获取图片
                selectPhoto();
            }
        } else {
            //系统不高于6.0直接执行
            //addPhotoView();      //获取图片
            selectPhoto();
        }
    }

    @Override     // 相机开启权限判断回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHOTO_REQUEST_CODE) {
            //addPhotoView();   // 添加图片
            selectPhoto();

        } else {
            Toast.makeText(this, "需要授予权限才可拍照", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void selectPhoto() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.konka.fileprovider"))
                .countable(true)
                .maxSelectable(6)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.dp_130))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            list.addAll(Matisse.obtainResult(data));
            List<File> listImg = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                //File file = new File("/sdcard",mSelected.get(i).getPath() + ".png");
                String path = PictureUtils.getPathFromUri(this, list.get(i));
                File file = new File(path);
                try {
                    // 图片压缩
                    compressedImageFile = new Compressor(this).compressToFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listImg.add(compressedImageFile);
            }
            for (int i = 0; i < listImg.size(); i++) {

                if (!listImg.get(i).exists()) {
                    Log.e("file", listImg.get(i) + "");
                    try {
//                    listImg.get(i).mkdirs();
                        listImg.get(i).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", listImg.get(i).getName(), RequestBody.create(null, listImg.get(i)));
                uploadPhoto(part);
            }

        }
    }

    private void uploadPhoto(MultipartBody.Part part) {
        showLoadingDialog();

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
                            listFilename.add(uploadRecordBeanDataInfo.data().filename);
                            adapter.setData(list);
                        } else {
                            showToast(uploadRecordBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void confirmUpload() {
        showLoadingDialog();
        String url = "";
        int size = listFilename.size();
        for (int i = 0; i < size; i++) {
            url += listFilename.get(i);
            if (i < size - 1)
                url += ",";
        }
        Subscription subscription = RetrofitHelper.getInstance().addContract(order_no, url)
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
                            showSuccess();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void showSuccess() {
        // 开启 popup 时界面透明
        UploadFileSuccessPopupwindow uploadSuccess = new UploadFileSuccessPopupwindow(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
       getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        uploadSuccess.showAtLocation(findViewById(R.id.activity_uploadfile_ll), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        uploadSuccess.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
                finish();
            }
        });
    }

    @Override
    public void deleteImage(int posistion) {
        list.remove(posistion);
        listFilename.remove(posistion);
        adapter.setData(list);
    }
}
