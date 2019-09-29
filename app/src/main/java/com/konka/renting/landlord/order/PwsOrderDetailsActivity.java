package com.konka.renting.landlord.order;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PwsOrderDetailsBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class PwsOrderDetailsActivity extends BaseActivity {

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
    @BindView(R.id.activity_pws_order_details_tv_houes_type)
    TextView tvHouesType;
    @BindView(R.id.activity_pws_order_details_tv_address)
    TextView tvAddress;
    @BindView(R.id.activity_pws_order_details_tv_cardId)
    TextView tvCardId;
    @BindView(R.id.activity_pws_order_details_tv_copy)
    TextView tvCopy;
    @BindView(R.id.activity_pws_order_details_tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.activity_pws_order_details_tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.activity_pws_order_details_tv_houesId)
    TextView tvHouesId;
    @BindView(R.id.activity_pws_order_details_tv_sure)
    TextView tvSure;
    @BindView(R.id.tv_tips_id)
    TextView tvTipsId;

    String room_id, order_id, room_name, startDate, endDate;


    public static void toActivity(Context context, String order_id) {
        Intent intent = new Intent(context, PwsOrderDetailsActivity.class);
        intent.putExtra("order_id", order_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pws_order_details;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_pwd_order_details);

        order_id = getIntent().getStringExtra("order_id");
        queryPassword();
    }

    private void queryPassword() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().queryPassword2(order_id)
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
                                if (!TextUtils.isEmpty(dataInfo.data().getAccount())) {
                                    tvTipsId.setVisibility(View.VISIBLE);
                                    tvCopy.setVisibility(View.VISIBLE);
                                    tvCardId.setText(dataInfo.data().getAccount());
                                } else {
                                    tvTipsId.setVisibility(View.GONE);
                                    tvCopy.setVisibility(View.INVISIBLE);
                                    tvCardId.setText(R.string.rent_in_code_status_yes);
                                }
                                tvStartTime.setText(dataInfo.data().getStart_time());
                                tvEndTime.setText(dataInfo.data().getEnd_time());
                                tvHouesId.setText(dataInfo.data().getRoom_name());
                                tvAddress.setText(dataInfo.data().getAddress());
                                String room_type;
                                if (dataInfo.data().getRoom_type().contains("_")) {
                                    String[] t = dataInfo.data().getRoom_type().split("_");
                                    room_type = t[0] + "室" + t[2] + "厅" + (t[1].equals("0") ? "" : t[1] + "卫");
                                } else {
                                    room_type = dataInfo.data().getRoom_type();
                                }
                                tvHouesType.setText(room_type);
                            }
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @OnClick({R.id.iv_back, R.id.activity_pws_order_details_tv_copy, R.id.activity_pws_order_details_tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.activity_pws_order_details_tv_sure:
                finish();
                break;
            case R.id.activity_pws_order_details_tv_copy:
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", tvCardId.getText().toString());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                showToast(R.string.toach_copy_to_clipboard);
                break;
        }
    }

}
