package com.konka.renting.start;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.guide.GuideActivity;
import com.konka.renting.login.SplashActivity;
import com.konka.renting.utils.SharedPreferenceUtil;


/**
 * @author Nate Robinson
 * @Time 2017/12/24
 */

public class StartActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public void init() {
        handler.sendMessageDelayed(handler.obtainMessage(), 1000 * 2);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isNeedShowGuide = SharedPreferenceUtil.getBoolean(getApplicationContext(), GuideActivity.IS_NEED_SHOW_GUIDE, true);
            if (isNeedShowGuide) {
                startActivity(new Intent(StartActivity.this, GuideActivity.class));
            } else {
                startActivity(new Intent(StartActivity.this, SplashActivity.class));

            }
            finish();
        }
    };
}
