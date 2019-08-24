package com.konka.renting.tenant.opendoor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.ActivateBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.event.AddCodeSuccessEvent;
import com.konka.renting.event.TentantOpenDoorEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.PicViewPagerActivity;
import com.konka.renting.landlord.house.activity.HouseInfoActivity2;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.PicstandardWidget;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class AddCodeActivity extends BaseActivity {


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
    @BindView(R.id.activity_add_code_edt_code)
    EditText mEdtCode;
    @BindView(R.id.activity_add_code_tv_sure)
    TextView mTvSure;

    AddCodeSuccessPopup addCodeSuccessPopup;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, AddCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_code;
    }

    @Override
    public void init() {

    }


    @OnClick({R.id.iv_back, R.id.activity_add_code_tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_add_code_tv_sure:
                String code = mEdtCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    showToast(R.string.please_input_code);
                } else {
                    List<View> list = new ArrayList<>();
                    list.add(mEdtCode);
                    hideSoftKeyboard(this, list);
                    activate(code);
                }
                break;
        }
    }

    /**
     * 隐藏软键盘(可用于Activity，Fragment)
     */
    private void hideSoftKeyboard(Context context, List<View> viewList) {
        if (viewList == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /*******************************************接口********************************************************/
    private void activate(String code) {
        showLoadingDialog();
        Subscription subscription = (SecondRetrofitHelper.getInstance().activate(code)
                .compose(RxUtil.<DataInfo<ActivateBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ActivateBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<ActivateBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new TentantOpenDoorEvent(11));
                            RxBus.getDefault().post(new AddCodeSuccessEvent());
                            showSuccessPopwindow(dataInfo.data().getRoom_name());
                        } else {
                            showToast(dataInfo.msg());
                        }

                    }
                }));
        addSubscrebe(subscription);
    }


    private void showSuccessPopwindow(String name) {
        // 开启 popup 时界面透明
        if (addCodeSuccessPopup == null) {
            addCodeSuccessPopup = new AddCodeSuccessPopup(this);
            addCodeSuccessPopup.setmTvName(name);
            addCodeSuccessPopup.setmTvSure(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCodeSuccessPopup.dismiss();
                    finish();
                }
            });
        }

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        addCodeSuccessPopup.showAtLocation(linTitle, Gravity.CENTER, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        addCodeSuccessPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
                finish();
            }
        });
    }
}
