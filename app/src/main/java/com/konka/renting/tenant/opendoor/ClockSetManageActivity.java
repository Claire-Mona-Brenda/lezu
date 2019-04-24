package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.ClockSetManageRenameBean;
import com.konka.renting.bean.ClockSetManagerItemBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.HowToSettingPopup;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonWarmPopup;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ClockSetManageActivity extends BaseActivity {
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
    @BindView(R.id.activity_fingerprint_ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.activity_fingerprint_manager_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_fingerprint_manager_refreshLayout)
    SmartRefreshLayout mRefreshLayout;


    public static final int TYPE_REMOTE = 1;//遥控器
    public static final int TYPE_IC_CARD = 2;//IC卡
    public static final int TYPE_FINGERPRINT = 3;//指纹

    private CommonAdapter commonAdapter;
    private List<ClockSetManagerItemBean> dataList = new ArrayList<>();
    private HowToSettingPopup howToSettingPopup;//录入指纹或添加IC卡提示
    private CommonWarmPopup warmPopup;//添加提示

    private int type;
    private String room_id;

    private int page = 1;

    public static void toActivity(Context context, int type, String room_id) {
        Intent intent = new Intent(context, ClockSetManageActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_clock_set_manager;
    }

    @Override
    public void init() {
        type = getIntent().getIntExtra("type", TYPE_FINGERPRINT);
        room_id = getIntent().getStringExtra("room_id");

        tvTitle.setText(initTitle());
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.device_icon_add);

        commonAdapter = new CommonAdapter<ClockSetManagerItemBean>(this, dataList, R.layout.adapter_clock_set_manage) {
            @Override
            public void convert(ViewHolder viewHolder, final ClockSetManagerItemBean bean) {
                viewHolder.setText(R.id.adapter_clock_manage_tv_name, bean.getName());
                //移除
                viewHolder.setOnClickListener(R.id.adapter_clock_manage_tv_remove, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFingerIc(bean);
                    }
                });
                //重命名
                viewHolder.setOnClickListener(R.id.adapter_clock_manage_tv_rename, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RenameActivity.toActivity(mActivity,room_id,bean.getId(),bean.getName());
                    }
                });
            }
        };

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getDataList();
            }
        });

        addRxBusSubscribe(ClockSetManageRenameBean.class, new Action1<ClockSetManageRenameBean>() {
            @Override
            public void call(ClockSetManageRenameBean bean) {
                int size=dataList.size();
                for (int i = 0; i < size; i++) {
                    if (dataList.get(i).getId().equals(bean.getId())){
                        dataList.get(i).setName(bean.getName());
                        commonAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });

        getDataList();
    }

    private String initTitle() {
        String title = "";
        switch (type) {
            case TYPE_FINGERPRINT:
                title = getString(R.string.open_fingerprint);
                break;
            case TYPE_IC_CARD:
                title = getString(R.string.open_ic_card);
                break;
            case TYPE_REMOTE:
                title = getString(R.string.open_remote);
                break;
        }
        return title;
    }

    private String getPopupConten(int type) {
        String content = "";
        switch (type) {
            case TYPE_FINGERPRINT:
                content = getString(R.string.tips_popup_add_fingerprint);
                break;
            case TYPE_IC_CARD:
                content = getString(R.string.tips_popup_add_ic);
                break;
            case TYPE_REMOTE:
                content = getString(R.string.tips_popup_add_remote);
                break;
        }
        return content;
    }

    private String getPopupWarm(int type) {
        String content = "";
        switch (type) {
            case TYPE_FINGERPRINT:
                content = getString(R.string.warm_popup_fingerprint);
                break;
            case TYPE_IC_CARD:
                content = getString(R.string.warm_popup_ic);
                break;
            case TYPE_REMOTE:
                content = getString(R.string.warm_popup_remote);
                break;
        }
        return content;
    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                showAddWarmPopup(type);
                break;
        }
    }

    private void add(int type) {
        switch (type) {
            case TYPE_FINGERPRINT:
                addFingerprint();
                break;
            case TYPE_IC_CARD:
                addIcCard();
                break;
        }
    }


    /*********************************************************接口*********************************************************************/

    /**
     * 添加指纹
     */
    private void addFingerprint() {
        Subscription subscription = SecondRetrofitHelper.getInstance().addFingerprint(room_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        if (info.success()) {
                            showHowSettingPopup(0);
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 添加IC卡
     */
    private void addIcCard() {
        Subscription subscription = SecondRetrofitHelper.getInstance().addIcCard(room_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        if (info.success()) {
                            showHowSettingPopup(1);
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 删除
     */
    private void removeFingerIc(final ClockSetManagerItemBean bean) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().removeFingerIc(bean.getId())
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
                            dataList.remove(bean);
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 指纹 ic卡 列表
     */
    private void getDataList() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getFingerIcList(room_id, type + "", page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<ClockSetManagerItemBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<ClockSetManagerItemBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<ClockSetManagerItemBean>> info) {
                        if (info.success()) {
                            dataList.clear();
                            dataList.addAll(info.data().getList());
                            commonAdapter.notifyDataSetChanged();
                        } else {
                            showToast(info.msg());
                        }

                    }
                });
        addSubscrebe(subscription);
    }

/***********************************************************弹窗******************************************************************/
    /**
     * 添加确定提示框
     */
    private void showAddWarmPopup(final int type) {

        if (warmPopup == null) {
            warmPopup = new CommonWarmPopup.Builder(this)
                    .setContent(getPopupConten(type))
                    .setWarmContent(getPopupWarm(type))
                    .setWarmContentTextColor("#FF4707")
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            warmPopup.dismiss();
                            add(type);

                        }
                    })
                    .create();
        }
        showPopup(warmPopup);
    }

    /**
     * 如何操作设备录入指纹和ic卡
     */
    private void showHowSettingPopup(int position) {
        if (howToSettingPopup == null)
            howToSettingPopup = new HowToSettingPopup(this);
        howToSettingPopup.chooseItem(position);
        showPopup(howToSettingPopup);
    }

    private void showPopup(PopupWindow popupWindow) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) tvTitle.getParent(), Gravity.CENTER, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (howToSettingPopup != null)
            howToSettingPopup.onDestroy();
    }
}
