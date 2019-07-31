package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.AddBankInfo;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GetIssueBankBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class AddBankCardActivity extends BaseActivity {

    @BindView(R.id.et_holder)
    EditText mEtHolder;
    @BindView(R.id.et_card_num)
    EditText mEtCardNum;
    @BindView(R.id.tv_type_select)
    TextView mTvTypeSelect;
    @BindView(R.id.et_mobile)
    EditText mEtMobile;
    private String id;
    private String name;
    private String cardnumber;
    private String mobile;

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

    @OnClick({R.id.iv_back, R.id.tv_type_select, R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_type_select:
                String number = mEtCardNum.getText().toString();
                if (!TextUtils.isEmpty(number)) {
                    getIssueBank(number);
                } else {
                    showToast("请输入银行卡号");
                }

                break;
            case R.id.btn_complete:
                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(cardnumber) || !TextUtils.isEmpty(mobile) || !TextUtils.isEmpty(mTvTypeSelect.getText().toString()))
                    comfirmAdd();
                else
                    showToast("请输入完整信息");
                break;
        }
    }

    private void comfirmAdd() {
        name = mEtHolder.getText().toString();
        cardnumber = mEtCardNum.getText().toString();
        mobile = mEtMobile.getText().toString();
        showError(name+cardnumber+id+mobile);
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

}
