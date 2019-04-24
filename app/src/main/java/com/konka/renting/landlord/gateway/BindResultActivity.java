package com.konka.renting.landlord.gateway;

import android.view.View;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BindResultActivity extends BaseActivity {

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bind_result;
    }

    @Override
    public void init() {
        mTvResult.setText(getContentTextId());
        mTvResult.setCompoundDrawablesWithIntrinsicBounds(0, getIconId(), 0, 0);
        setTitleText(getTitleTextId());
    }

    @OnClick({R.id.iv_back, R.id.btn_red_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.btn_red_back:
                finish();
                break;
        }
    }

    public abstract int getTitleTextId();

    public abstract int getIconId();

    public abstract int getContentTextId();

    public void setContentText(String contentText){
        mTvResult.setText(contentText);
    }

}
