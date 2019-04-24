package com.konka.renting.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.guide.GuideActivity;
import com.konka.renting.landlord.MainActivity;
import com.konka.renting.utils.SharedPreferenceUtil;

public class SplashActivity extends BaseActivity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isNeedShowGuide = SharedPreferenceUtil.getBoolean(getApplicationContext(), GuideActivity.IS_NEED_SHOW_GUIDE, true);
            if (isNeedShowGuide) {
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            }else {
                if (LoginUserBean.getInstance().isLogin()) {
                    MainActivity.toMainActivity(mActivity, LoginUserBean.getInstance().getType());
                    finish();
                }else {
                    LoginNewActivity.toTenantActivity(mActivity);
                    finish();
                }
            }

        }
    };
    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
        mHandler.sendEmptyMessageDelayed(0,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }
}
