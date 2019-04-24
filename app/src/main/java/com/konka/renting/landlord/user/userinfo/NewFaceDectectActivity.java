package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.utils.APIService;
import com.konka.renting.utils.AccessToken;
import com.konka.renting.utils.Config;
import com.konka.renting.utils.FaceException;
import com.konka.renting.utils.OnResultListener;

import butterknife.OnClick;
import rx.functions.Action1;

public class NewFaceDectectActivity extends BaseActivity {

    private int type;

    public static void toActivity(Context context, int type) {
        Intent intent = new Intent(context, NewFaceDectectActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    private boolean hasGotToken = false;
    private AlertDialog.Builder alertDialog;
    private static final int REQUEST_CODE_CAMERA = 102;

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_face_dectect;
    }

    @Override
    public void init() {
        setTitleText("实名认证");
        type = getIntent().getIntExtra("type",1);
        alertDialog = new AlertDialog.Builder(this);
        addRxBusSubscribe(FaceDectectEvent.class, new Action1<FaceDectectEvent>() {
            @Override
            public void call(FaceDectectEvent faceDectectEvent) {
                finish();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.iv_back, R.id.tv_start_face})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_start_face:
               /* if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(NewFaceDectectActivity.this, CameraActivity.class);
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
                startActivityForResult(intent, REQUEST_CODE_CAMERA);*/
               //initAccessToken();
                //FaceOnlineVerifyActivity.toActivity(this);
                //CertificationActivity.toActivity(this);
                FaceDetectActivity.toActivity(this,type);
                finish();
                break;
        }
    }

    // 在线活体检测和公安核实需要使用该token，为了防止ak、sk泄露，建议在线活体检测和公安接口在您的服务端请求
    private void initAccessToken() {

        APIService.getInstance().init(getApplicationContext());
        APIService.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                showError(result.getAccessToken());
            }

            @Override
            public void onError(FaceException error) {
                showError(error.getErrorMessage());
            }
        }, Config.apiKey, Config.secretKey);
    }

}
