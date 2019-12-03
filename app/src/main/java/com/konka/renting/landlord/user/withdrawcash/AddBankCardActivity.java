package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GetIssueBankBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.WarmPopupwindow;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class AddBankCardActivity extends BaseActivity {

    @BindView(R.id.et_holder)
    EditText mEtHolder;
    @BindView(R.id.et_card_num)
    EditText mEtCardNum;
//    @BindView(R.id.img_type_select)
//    ImageView mImgTypeSelect;
    @BindView(R.id.et_type_select)
    EditText mEtTypeSelect;
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
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.et_phone)
    EditText etPhone;

    private String name;
    private String cardnumber;
    private String bankName;
    private String phone;

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

        mEtCardNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().length() >= 16) {
//                    getIssueBank(charSequence.toString());
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtCardNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                textView.getApplicationWindowToken(), 0);
                    }
//                    if (mEtCardNum.getText().toString().length() >= 16) {
//                        getIssueBank(mEtCardNum.getText().toString());
//                    }
                    return true;
                }
                return false;
            }
        });

    }

    @OnClick({R.id.iv_back, R.id.et_type_select, R.id.btn_complete})
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
//                break;
            case R.id.btn_complete://提交
                name = mEtHolder.getText().toString();
                cardnumber = mEtCardNum.getText().toString();
                bankName = mEtTypeSelect.getText().toString();
                phone = etPhone.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(cardnumber)&& !TextUtils.isEmpty(bankName) && phone.length()==11) {
                    comfirmAdd();
                }else if (phone.length()<11){
                    showToast(R.string.please_input_phone);
                }else {
                    showToast(R.string.please_input_all_info);
                }
                break;
        }
    }

    private void comfirmAdd() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().addBankCard(cardnumber, name,bankName, phone)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new AddBankEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

//    private void getIssueBank(String card_no) {
//        Subscription subscription = SecondRetrofitHelper.getInstance().getIssueBank(card_no)
//                .compose(RxUtil.<DataInfo<GetIssueBankBean>>rxSchedulerHelper())
//                .subscribe(new CommonSubscriber<DataInfo<GetIssueBankBean>>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(DataInfo<GetIssueBankBean> dataInfo) {
//                        if (dataInfo.success()) {
//                            mEtTypeSelect.setText(dataInfo.data().getBank_name());
//                            mImgTypeSelect.setVisibility(View.VISIBLE);
//                            if (!TextUtils.isEmpty(dataInfo.data().getBank_image())) {
//                                Picasso.get().load(dataInfo.data().getBank_image()).into(mImgTypeSelect);
//                            } else {
//                                Picasso.get().load(R.mipmap.bank_other).into(mImgTypeSelect);
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

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
