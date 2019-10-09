package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenPwdWayActivity extends BaseActivity {
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
    @BindView(R.id.activity_open_pwd_way_rl_short_pwd)
    RelativeLayout mRlShortPwd;
    @BindView(R.id.activity_open_pwd_way_rl_self_pwd)
    RelativeLayout mRlSelfPwd;
    @BindView(R.id.activity_open_pwd_way_rl_once_pwd)
    RelativeLayout mRlOncePwd;
    @BindView(R.id.activity_open_pwd_way_rl_timer_pwd)
    RelativeLayout mRlTimerPwd;
    @BindView(R.id.activity_open_pwd_way_rl_times_pwd)
    RelativeLayout mRlTimesPwd;

    final String KEY_DEVICE_ID = "device_id";
    final String KEY_ROOM_ID = "room_id";

    String deviceId;
    String room_id;
    int is_generate_password;

    public static void toActivity(Context context, String device_id, String room_id,int is_generate_password) {
        Intent intent = new Intent(context, OpenPwdWayActivity.class);
        intent.putExtra("device_id", device_id);
        intent.putExtra("room_id", room_id);
        intent.putExtra("is_generate_password", is_generate_password);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_open_pwd_way;
    }

    @Override
    public void init() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        room_id = getIntent().getStringExtra(KEY_ROOM_ID);
        is_generate_password = getIntent().getIntExtra("is_generate_password",1);

        tvTitle.setText(R.string.open_pwd);

//        if (is_generate_password==0){
//            mRlOncePwd.setVisibility(View.GONE);
//            mRlTimerPwd.setVisibility(View.GONE);
//            mRlTimesPwd.setVisibility(View.GONE);
//        }else{
            mRlOncePwd.setVisibility(View.VISIBLE);
            mRlTimerPwd.setVisibility(View.VISIBLE);
            mRlTimesPwd.setVisibility(View.VISIBLE);
//        }


    }

    @OnClick({R.id.iv_back, R.id.activity_open_pwd_way_rl_short_pwd, R.id.activity_open_pwd_way_rl_self_pwd,R.id.activity_open_pwd_way_rl_once_pwd, R.id.activity_open_pwd_way_rl_timer_pwd, R.id.activity_open_pwd_way_rl_times_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_open_pwd_way_rl_short_pwd://临时密码
                ShortPwdActivity.toActivity(this, deviceId, room_id);
                break;
            case R.id.activity_open_pwd_way_rl_self_pwd://自定义密码
                SelfPwdActivity.toActivity(this, deviceId, room_id);
                break;
            case  R.id.activity_open_pwd_way_rl_once_pwd://一次性密码
                OncePwdActivity.toActivity(this, deviceId, room_id);
                break;
            case R.id.activity_open_pwd_way_rl_timer_pwd://计时密码
                SetTimerPwdActivity.toActivity(this,deviceId,room_id);
                break;
            case R.id.activity_open_pwd_way_rl_times_pwd://计次密码
                SetTimesPwdActivity.toActivity(this,deviceId,room_id);
                break;
        }
    }
}
