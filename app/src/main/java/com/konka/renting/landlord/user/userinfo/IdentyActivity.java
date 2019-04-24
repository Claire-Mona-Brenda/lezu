package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.utils.RxBus;

import butterknife.BindView;
import butterknife.OnClick;
/*
* 审核结果
* status
* 0，未审核1，审核中2，审核通过3，审核拒绝
* */
public class IdentyActivity extends BaseActivity {


    @BindView(R.id.iv_check)
    ImageView mIvCheck;
    @BindView(R.id.tv_check_result)
    TextView mTvCheckResult;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    private String status;

    public static void toActivity(Context context, String status, int type1){
        Intent intent = new Intent(context,IdentyActivity.class);
        intent.putExtra("status",status);
        context.startActivity(intent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_identy;
    }

    @Override
    public void init() {

        setTitleText("审核结果");
        status = getIntent().getStringExtra("status");
        if (status.equals("1")){
            return;
        }else if (status.equals("2")){
            mIvCheck.setImageResource(R.mipmap.icon_success);
            mTvCheckResult.setText("审核通过");
        }else if (status.equals("3")){
            mIvCheck.setImageResource(R.mipmap.icon_failer);
            mTvCheckResult.setText("审核未通过");
            mBtnConfirm.setText("去认证");
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                RxBus.getDefault().post(new FaceDectectEvent());
                if (status.equals("1")){
                    finish();
                }else if (status.equals("2")){
                    finish();
                }else if (status.equals("3")){
                    NewFaceDectectActivity.toActivity(this,1);
                    finish();
                }
                break;
            case R.id.btn_confirm:
                RxBus.getDefault().post(new FaceDectectEvent());
                if (status.equals("1")){
                    finish();
                }else if (status.equals("2")){
                   finish();
                }else if (status.equals("3")){
                    NewFaceDectectActivity.toActivity(this,1);
                    finish();
                }
                break;
        }
    }
}
