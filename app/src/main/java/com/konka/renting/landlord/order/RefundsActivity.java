package com.konka.renting.landlord.order;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.OrderBean;
import com.konka.renting.bean.OrderDetailBean;
import com.konka.renting.bean.UploadRecordBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.user.collection.NoScrollGridView;
import com.konka.renting.utils.PictureUtils;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;
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

public class RefundsActivity extends BaseActivity implements MyGridAdapter.SetDeleteListener {


    @BindView(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @BindView(R.id.icon_room)
    ImageView mIconRoom;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    List<Uri> list = new ArrayList<android.net.Uri>();
    @BindView(R.id.grid_upload_certificate)
    NoScrollGridView mGridUploadCertificate;
    private int REQUEST_CODE_CHOOSE = 101;
    private final int PHOTO_REQUEST_CODE = 111;   //  是否开启相机权限
    private MyGridAdapter adapter;
    List<String> listFilename = new ArrayList<>();
    private OrderBean orderBean;
    private File compressedImageFile;

    public static void toActivity(Context context, OrderBean orderBean, String type) {
        Intent intent = new Intent(context, RefundsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("orderbean", orderBean);
        bundle.putString("type", type);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void toDetailActivity(Context context, OrderDetailBean orderBean, String type) {
        Intent intent = new Intent(context, RefundsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("orderdetail", orderBean);
        bundle.putString("type", type);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_refunds;
    }

    @Override
    public void init() {

        setTitleText(R.string.confirm_refund);
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
//        initData();
        initGrid();
        setData();
    }

    private void setData() {

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

    private void initGrid() {
        adapter = new MyGridAdapter(this);
        adapter.setDeleteListener(this);
        mGridUploadCertificate.setAdapter(adapter);
        mGridUploadCertificate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private void selectPhoto() {
        Matisse.from(RefundsActivity.this)
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

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String type = bundle.getString("type");
        if (type.equals("list")) {
            orderBean = (OrderBean) bundle.getSerializable("orderbean");
            mTvOrderNumber.setText(orderBean.getMerge_order_no());
            Picasso.get().load(orderBean.getRoomInfo().getImage()).into(mIconRoom);
            mTvAddress.setText(orderBean.getRoomInfo().getRoom_name());
            mTvTime.setText(orderBean.getStart_time() + "-" + orderBean.getEnd_time());
            mTvMoney.setText("￥" + orderBean.getRoomInfo().getHousing_price());
        } else {

            OrderDetailBean orderDetailBean = (OrderDetailBean) bundle.getSerializable("orderdetail");
            mTvOrderNumber.setText(orderDetailBean.getInfo().getMerge_order_no());
            Picasso.get().load(orderDetailBean.getInfo().getRoomInfo().getImage()).into(mIconRoom);
            mTvAddress.setText(orderDetailBean.getInfo().getRoomInfo().getRoom_name());
            mTvTime.setText(orderDetailBean.getInfo().getStart_time() + "-" + orderDetailBean.getInfo().getEnd_time());
            mTvMoney.setText("￥" + orderDetailBean.getInfo().getHousing_price());

        }

    }

    @OnClick({R.id.iv_back, R.id.btn_confirm_refunds})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_confirm_refunds:
                confirmRefund();
                break;
        }
    }

    private void confirmRefund() {
        String image = listToString(listFilename);
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().updateCheckout(image, 1, mTvOrderNumber.getText().toString())
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

                            RxBus.getDefault().post(new UpdateCheckOutEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(","); // 分隔符
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
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

    @Override
    public void deleteImage(int posistion) {
        list.remove(posistion);
        listFilename.remove(posistion);
        adapter.setData(list);
    }
}
