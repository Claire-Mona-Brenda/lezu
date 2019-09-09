package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.widget.CommonInputPopupWindow;
import com.konka.renting.widget.WarmPopupwindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBankCardActivity extends BaseActivity {

    @BindView(R.id.et_holder)
    EditText mEtHolder;
    @BindView(R.id.et_card_num)
    EditText mEtCardNum;
    @BindView(R.id.tv_type_select)
    TextView mTvTypeSelect;
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
    @BindView(R.id.img_tips)
    ImageView imgTips;
    @BindView(R.id.ll_warm)
    LinearLayout llWarm;
    @BindView(R.id.btn_complete)
    Button btnComplete;

    private String id;
    private String name;
    private String cardnumber;

    WarmPopupwindow warmPopupwindow;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, AddBankCardActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_bank_card;
    }

    @Override
    public void init() {
        setTitleText(R.string.add_bank_title);

    }

    @OnClick({R.id.iv_back, R.id.tv_type_select,R.id.img_tips, R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
//            case R.id.tv_type_select://
//                String number = mEtCardNum.getText().toString();
//                if (!TextUtils.isEmpty(number)) {
//                    getIssueBank(number);
//                } else {
//                    showToast(R.string.please_input_card_num);
//                }
//
//                break;
            case R.id.img_tips://提示
                showTipsPop();
                break;
            case R.id.btn_complete://提交
                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(cardnumber))
                    comfirmAdd();
                else
                    showToast("请输入完整信息");
                break;
        }
    }

    private void comfirmAdd() {
        name = mEtHolder.getText().toString();
        cardnumber = mEtCardNum.getText().toString();
        showError(name + cardnumber + id);
        showLoadingDialog();
//        Subscription subscription = RetrofitHelper.getInstance().addBankCard(name, cardnumber, id, mobile)
//                .compose(RxUtil.<AddBankInfo>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<AddBankInfo>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
//                        showError(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(AddBankInfo addBankBeanDataInfo) {
//
//                        dismiss();
//                        if (addBankBeanDataInfo.getStatus() == 1) {
//                            showToast(addBankBeanDataInfo.getInfo());
//                            RxBus.getDefault().post(new AddBankEvent());
//                            finish();
//                        } else {
//                            showToast(addBankBeanDataInfo.getInfo());
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
    }

    private void getIssueBank(String number) {
//        showLoadingDialog();
//        Subscription subscription = RetrofitHelper.getInstance().getIssueBank(number)
//                .compose(RxUtil.<DataInfo<GetIssueBankBean>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<GetIssueBankBean>>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
//                        showError(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<GetIssueBankBean> getIssueBankBeanDataInfo) {
//
//                        dismiss();
//                        if (getIssueBankBeanDataInfo.success()) {
//                            mTvTypeSelect.setText(getIssueBankBeanDataInfo.data().name);
//                            id = getIssueBankBeanDataInfo.data().id;
//                        } else {
//                            showToast(getIssueBankBeanDataInfo.msg());
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
    }

    /*******************************************弹窗***********************************************************/
    private void showTipsPop() {
        if (warmPopupwindow == null) {
            warmPopupwindow = new WarmPopupwindow(mActivity);
        }
        showPopup(warmPopupwindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }
}
