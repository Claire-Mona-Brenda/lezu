package com.konka.renting.tenant.user.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.TenantRenterListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

public class TenantManagerActivity extends BaseActivity {

    @BindView(R.id.list_tenant_manager)
    ListView mListTenantManager;
    private List<TenantRenterListBean.ListBean> mData = new ArrayList<>();
    private CommonAdapter<TenantRenterListBean.ListBean> mAdapter;
    private TextView tvName;
    private ImageView ivPhoto;
    private TextView tvPhone;
    String AGREE = "1";
    String REFUSE = "2";
    String order_id;

    public static void toActivity(Context context, String order_id) {
        Intent intent = new Intent(context, TenantManagerActivity.class);
        intent.putExtra("order_id", order_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tenant_manager;
    }

    @Override
    public void init() {
        setTitleText(R.string.rent_manager);
        initList();

        initManager();

        initData();
    }

    private void initManager() {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tenant_manager, mListTenantManager, false);
        mListTenantManager.addHeaderView(view);
        tvName = view.findViewById(R.id.tv_name);
        ivPhoto = view.findViewById(R.id.iv_header);
        tvPhone = view.findViewById(R.id.tv_phone);
    }

    private void initData() {

        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().getShareRentingList(order_id)
                .compose(RxUtil.<DataInfo<TenantRenterListBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<TenantRenterListBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<TenantRenterListBean> dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            mData.clear();
                            mData.addAll(dataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                            tvName.setText(dataInfo.data().getMember().getReal_name());
                            tvPhone.setText(dataInfo.data().getMember().getPhone());
                            if (!dataInfo.data().getMember().getHeadimgurl().equals(""))
                                Picasso.get().load(dataInfo.data().getMember().getHeadimgurl()).into(ivPhoto);
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void initList() {
        mAdapter = new CommonAdapter<TenantRenterListBean.ListBean>(this, mData, R.layout.item_tenant_manager) {
            @Override
            public void convert(ViewHolder viewHolder, final TenantRenterListBean.ListBean s, int i) {
                viewHolder.setText(R.id.tv_name, s.getReal_name());
                TextView tvStatus = viewHolder.getView(R.id.tv_satus);
                TextView tvAgree = viewHolder.getView(R.id.tv_agree);
                TextView tvRefuse = viewHolder.getView(R.id.tv_refuse);
                TextView tvManager = viewHolder.getView(R.id.tv_manager);
                TextView tvRemove = viewHolder.getView(R.id.tv_remove);
                TextView tvPhone = viewHolder.getView(R.id.tv_phone);
                tvPhone.setText(s.getPhone());
                if (s.getStatus() == 0) {
                    tvStatus.setVisibility(View.GONE);
                    tvPhone.setVisibility(View.VISIBLE);
                    tvAgree.setVisibility(View.VISIBLE);
                    tvManager.setVisibility(View.GONE);
                    tvRefuse.setVisibility(View.VISIBLE);
                    tvRemove.setVisibility(View.GONE);
                } else if (s.getStatus() == 1) {
                    tvStatus.setVisibility(View.GONE);
                    tvPhone.setVisibility(View.VISIBLE);
                    tvAgree.setVisibility(View.GONE);
                    tvManager.setVisibility(View.GONE);
                    tvRefuse.setVisibility(View.GONE);
                    tvRemove.setVisibility(View.VISIBLE);
                }
                tvAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//同意合租
                        new AlertDialog.Builder(mActivity).setTitle(R.string.dialog_renter_manage_apply_title).setMessage(R.string.dialog_renter_manage_agree)
                                .setNegativeButton(R.string.warn_cancel,null)
                                .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateRenter(s.getId(), AGREE);
                                    }
                                }).create().show();

                    }
                });
                tvRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//拒绝合租
                        new AlertDialog.Builder(mActivity).setTitle(R.string.dialog_renter_manage_apply_title).setMessage(R.string.dialog_renter_manage_cancel)
                                .setNegativeButton(R.string.warn_cancel,null)
                                .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateRenter(s.getId(), REFUSE);
                                    }
                                }).create().show();

                    }
                });
                tvRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//移除合租租客
                        new AlertDialog.Builder(mActivity).setTitle(R.string.dialog_renter_manage_remove_title).setMessage(R.string.dialog_renter_manage_remove)
                                .setNegativeButton(R.string.warn_cancel,null)
                                .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeRenter(s.getId());
                                    }
                                }).create().show();

                    }
                });

            }
        };
        mListTenantManager.setAdapter(mAdapter);
    }

    private void removeRenter(String id) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().jointRentApplyRemove(id, order_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            initData();
                            showToast(dataInfo.msg());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void updateRenter(String id, String state) {
        showLoadingDialog();
        Observable observable;
        if (state.equals(AGREE)) {
            observable = SecondRetrofitHelper.getInstance().jointRentApplyConfirm(id, order_id);
        } else {
            observable = SecondRetrofitHelper.getInstance().jointRentApplyCancel(id, order_id);
        }
        Subscription subscription = observable
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        dismiss();
                        if (dataInfo.success()) {
                            initData();
                            showToast(dataInfo.msg());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
