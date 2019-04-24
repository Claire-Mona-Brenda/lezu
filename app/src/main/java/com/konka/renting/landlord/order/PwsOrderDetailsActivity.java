package com.konka.renting.landlord.order;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.AddRentingBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PwsOrderDetailsBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class PwsOrderDetailsActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.activity_pws_order_details_tv_cardId)
    TextView tvCardId;
    @BindView(R.id.activity_pws_order_details_tv_pwd)
    TextView tvPwd;
    @BindView(R.id.activity_pws_order_details_tv_copy)
    TextView tvCopy;
    @BindView(R.id.activity_pws_order_details_tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.activity_pws_order_details_tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.activity_pws_order_details_tv_houesId)
    TextView tvHouesId;

    String room_id, order_id, room_name;

    public static void toActivity(Context context, String room_id, String order_id, String room_name) {
        Intent intent = new Intent(context, PwsOrderDetailsActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("order_id", order_id);
        intent.putExtra("room_name", room_name);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pws_order_details;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_pwd_order_details);

        room_id = getIntent().getStringExtra("room_id");
        order_id = getIntent().getStringExtra("order_id");
        room_name = getIntent().getStringExtra("room_name");

        tvHouesId.setText(room_name);
        queryPassword();
    }

    private void queryPassword() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().landlordShortRentAccount(order_id)
                .compose(RxUtil.<DataInfo<PwsOrderDetailsBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PwsOrderDetailsBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo<PwsOrderDetailsBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            if (dataInfo.success()) {
                                tvCardId.setText(dataInfo.data().getAccount());
                                tvPwd.setText(dataInfo.data().getPassword());
                                tvStartTime.setText(dataInfo.data().getStart_time());
                                tvEndTime.setText(dataInfo.data().getEnd_time());
                            }
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick({R.id.iv_back, R.id.activity_pws_order_details_tv_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_pws_order_details_tv_copy:
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", tvCardId.getText().toString()+" "+tvPwd.getText().toString());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                showToast(R.string.toach_copy_to_clipboard);

                break;
        }
    }
}
