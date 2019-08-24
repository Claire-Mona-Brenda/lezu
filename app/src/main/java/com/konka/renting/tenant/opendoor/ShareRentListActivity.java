package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RentListBean;
import com.konka.renting.bean.ShareRentListBean;
import com.konka.renting.event.AddShareRentEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ShareRentListActivity extends BaseActivity {
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
    @BindView(R.id.activity_share_rent_list_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.activity_share_rent_list_ll_add)
    LinearLayout mListLlAdd;

    String order_id;
    List<ShareRentListBean> rentListBeans;
    CommonAdapter<ShareRentListBean> commonAdapter;
    CommonPopupWindow commonPopupWindow;


    public static void toActivity(Context context, String order_id) {
        Intent intent = new Intent(context, ShareRentListActivity.class);
        intent.putExtra("order_id", order_id);
        context.startActivity(intent);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_share_rent_list;
    }

    @Override
    public void init() {
        order_id = getIntent().getStringExtra("order_id");


       rentListBeans = new ArrayList<>();
        commonAdapter = new CommonAdapter<ShareRentListBean>(this, rentListBeans, R.layout.adapter_share_rent_list) {
            @Override
            public void convert(ViewHolder viewHolder, ShareRentListBean shareRentListBean) {
                viewHolder.setText(R.id.adapter_share_ren_list_tv_phone, shareRentListBean.getPhone());
                viewHolder.setOnClickListener(R.id.adapter_share_ren_list_tv_remove, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(getString(R.string.dialog_dissmiss_rent), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonPopupWindow.dismiss();
                                cancle(shareRentListBean.getMember_id() + "");
                            }
                        });
                    }
                });
            }
        };

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(commonAdapter);

        addRxBusSubscribe(AddShareRentEvent.class, new Action1<AddShareRentEvent>() {
            @Override
            public void call(AddShareRentEvent addShareRentEvent) {
                getRentDate();
            }
        });

        getRentDate();

    }

    @OnClick({R.id.iv_back, R.id.activity_share_rent_list_ll_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_share_rent_list_ll_add:
                AddRentPeopleActivity.toActivity(this, order_id, false);
                break;
        }
    }

    private void removeBean(String member_id) {
        int size = rentListBeans.size();
        for (int i = 0; i < size; i++) {
            if (member_id.equals(rentListBeans.get(i).getMember_id() + "")) {
                rentListBeans.remove(i);
                commonAdapter.notifyDataSetChanged();
                break;
            }
        }
        if (rentListBeans.size() <= 0) {
            RxBus.getDefault().post(new AddShareRentEvent(0, order_id));
        }
    }

    /*******************************************************接口********************************************/

    private void getRentDate() {

        Subscription subscription = SecondRetrofitHelper.getInstance().shareList(order_id)
                .compose(RxUtil.<DataInfo<List<ShareRentListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<ShareRentListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<List<ShareRentListBean>> dataInfo) {

                        if (dataInfo.success()) {
                            rentListBeans.clear();
                            rentListBeans.addAll(dataInfo.data());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void cancle(final String member_id) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().removeShareOrder(order_id, member_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        dismiss();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            removeBean(member_id);
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**************************************************弹窗****************************************************************/


    /**
     *
     */
    private void showPopup(String content, View.OnClickListener onClickListener) {
        commonPopupWindow = new CommonPopupWindow.Builder(mActivity)
                .setTitle(getString(R.string.tips))
                .setContent(content)
                .setRightBtnClickListener(onClickListener)
                .create();
        showPopup(commonPopupWindow);

    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) mRecycler.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
