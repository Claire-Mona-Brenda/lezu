package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.ClockSetManageRenameBean;
import com.konka.renting.bean.ClockSetManagerItemBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class RenameActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.activity_rename_edt_name)
    EditText edtName;

    String room_id;
    String item_id;
    String name;

    public static void toActivity(Context context, String room_id, String item_id, String name) {
        Intent intent = new Intent(context, RenameActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("item_id", item_id);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rename;
    }


    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");
        item_id = getIntent().getStringExtra("item_id");
        name = getIntent().getStringExtra("name");

        if (!TextUtils.isEmpty(name)) {
            edtName.setText(name);
            edtName.setSelection(name.length());
        }
        edtName.requestFocus();
        edtName.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtName.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) edtName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edtName, 0);
            }
        }, 300);

    }


    @OnClick({R.id.tv_cancel, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:
                String rename=edtName.getText().toString();
                if (TextUtils.isEmpty(rename)){
                    showToast(R.string.please_input_name);
                }else{
                    renameFingerIc(rename);
                }
                break;
        }
    }

    /**
     * 重命名
     */
    private void renameFingerIc(final String rename) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().renameFingerIc(item_id, rename)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        dismiss();
                        if (info.success()) {
                            doSuccess();
                            RxBus.getDefault().post(new ClockSetManageRenameBean(rename, item_id));
                            finish();
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }
}
