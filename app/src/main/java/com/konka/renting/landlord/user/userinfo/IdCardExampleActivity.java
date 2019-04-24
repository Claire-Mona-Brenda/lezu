package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.OnClick;
import rx.functions.Action1;

public class IdCardExampleActivity extends BaseActivity {

    private String bestImagePath;

    public static void toActivity(Context context, String bestImagePath) {
        Intent intent = new Intent(context, IdCardExampleActivity.class);
        intent.putExtra("bestimage_path", bestImagePath);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_id_card_example;
    }

    @Override
    public void init() {

        setTitleText(R.string.certification);
        bestImagePath = getIntent().getStringExtra("bestimage_path");
        addRxBusSubscribe(FaceDectectEvent.class, new Action1<FaceDectectEvent>() {
            @Override
            public void call(FaceDectectEvent faceDectectEvent) {
                finish();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_commit:
                //CertificationActivity.toActivity(this,bestImagePath, type);
                break;
        }
    }
}
