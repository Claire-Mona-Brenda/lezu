package com.konka.renting.landlord.gateway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.event.RefreshGatewayDataEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class BindGatewayActivity extends BaseActivity {


    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.edit_pwd)
    EditText mEditPassword;
    @BindView(R.id.edit_serial)
    EditText editSerial;
    @BindView(R.id.activity_bind_img_scan)
    ImageView imgScan;

    final int REQUEST_CODE = 12;

    private String mRoomId;


    public static void toActivity(Context context, String room_id) {
        Intent intent = new Intent(context, BindGatewayActivity.class);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bind_gateway;
    }

    @Override
    public void init() {
        setTitleText(R.string.device_edit_bind_gateway);


        mRoomId = getIntent().getStringExtra("room_id");


//        addRxBusSubscribe(ScanEvent.class, new Action1<ScanEvent>() {
//            @Override
//            public void call(ScanEvent scanEvent) {
//                String code = scanEvent.code;
//                String[] spllitResult = code.split("&&");
//                if (spllitResult.length >= 2) {
//                    code = spllitResult[1];
//                }
//
//                if (TextUtils.isEmpty(code)) {
//                    code = scanEvent.code;
//                }
//                editSerial.setText(code);
//                editSerial.setSelection(editSerial.getText().toString().length());
//            }
//        });
    }


    @OnClick({R.id.iv_back, R.id.btn_sure, R.id.activity_bind_img_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_sure:
                addGateway();
                break;
            case R.id.activity_bind_img_scan:
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.
                        request(Manifest.permission.CAMERA)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) { // Always true pre-M
                                    // I can control the camera now
                                    Intent intent = new Intent(BindGatewayActivity.this, CaptureActivity.class);
                                    startActivityForResult(intent, REQUEST_CODE);
                                } else {
                                    // Oups permission denied
                                    doFailed();
                                }
                            }
                        });
                break;
        }
    }

    private void addGateway() {
        if (UIUtils.showHint(mEditPassword, editSerial)) {
            return;
        }

        showLoadingDialog();

        String gateway_no = editSerial.getText().toString();
        String password = mEditPassword.getText().toString();

        Subscription subscription = SecondRetrofitHelper.getInstance().bindGateway(mRoomId, gateway_no, password)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        BindGatewayFaildedActivity.toActivity(mActivity);
                        finish();

                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
//                            BindGatewaySuccessActivity.toActivity(mActivity);
                            RxBus.getDefault().post(new RefreshGatewayDataEvent());
                            finish();
                        } else {
//                            BindGatewayFaildedActivity.toActivity(mActivity,dataInfo.msg());
                            showToast(dataInfo.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String code = bundle.getString(CodeUtils.RESULT_STRING);
                    String[] spllitResult = code.split("&&");
                    if (spllitResult.length >= 2) {
                        code = spllitResult[1];
                    }

                    if (TextUtils.isEmpty(code)) {
                        code = bundle.getString(CodeUtils.RESULT_STRING);
                    }
                    editSerial.setText(code);
                    editSerial.setSelection(editSerial.getText().toString().length());
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    showToast("解析二维码失败");
                }
            }
        }
    }
}
