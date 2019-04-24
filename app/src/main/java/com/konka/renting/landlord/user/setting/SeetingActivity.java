package com.konka.renting.landlord.user.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.igexin.sdk.PushManager;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.WebType;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.ChoiceIdentityActivity;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.utils.EncryptUtils;
import com.konka.renting.utils.RxUtil;

import butterknife.OnClick;
import rx.Subscription;

public class SeetingActivity extends BaseActivity {

    private int id;
    private int LANDMORE_ID = 1;
    private int TENANT_ID = 2;

    public static void toActivity(Context context, int id) {
        Intent intent = new Intent(context, SeetingActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("id", id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_seeting;
    }

    @Override
    public void init() {
        setTitleText(R.string.setting);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getInt("id");

    }


    @OnClick({R.id.iv_back, R.id.tv_about, R.id.tv_probem, R.id.btn_loginout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_about:
                WebviewActivity.toActivity(this, WebType.WEB_ABOUT);
                break;
            case R.id.tv_probem:
                WebviewActivity.toActivity(this, WebType.WEB_PROBLEM);
                break;
            case R.id.btn_loginout:
                if (id == LANDMORE_ID)
                    landmoreLoginOut();
                else
                    tenantLoginOut();
                break;
        }
    }

    private void landmoreLoginOut() {
        LoginUserBean.getInstance().reset();
        LoginUserBean.getInstance().save();
        startActivity(new Intent(SeetingActivity.this, LoginNewActivity.class));
        for (int i = 0; i < mActivities.size(); i++) {
            if (mActivities.get(i) != null && !mActivities.get(i).isFinishing()) {
                mActivities.get(i).finish();
            }
        }
    }

    private void tenantLoginOut() {
        PushManager.getInstance().unBindAlias(mActivity, EncryptUtils.encryptMD5ToString(LoginUserBean.getInstance().getMobile()).toLowerCase(), true);
        LoginUserBean.getInstance().reset();
        LoginUserBean.getInstance().save();

        startActivity(new Intent(SeetingActivity.this, LoginNewActivity.class));
        for (int i = 0; i < mActivities.size(); i++) {
            if (mActivities.get(i) != null && !mActivities.get(i).isFinishing()) {
                mActivities.get(i).finish();
            }
        }
    }

}
