package com.konka.renting.landlord.house.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseDetailsInfoBean;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.event.LandlordHouseInfoEvent;
import com.konka.renting.event.UpdataHouseInfoEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.DeviceListActivity;
import com.konka.renting.landlord.gateway.UserGatewayListActivity;
import com.konka.renting.landlord.house.HouseEditActivity;
import com.konka.renting.landlord.house.OpenManageActivity;
import com.konka.renting.landlord.house.TemporaryPwdActivity;
import com.konka.renting.landlord.house.activity.DevListActivity;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class HouseInfoSettingPopupwindow extends PopupWindow implements View.OnClickListener {
    HouseDetailsInfoBean2 bean;
    Context mContext;
    private View mView;
    private RelativeLayout rlPay;//安装付费
    private RelativeLayout rlEdit;//编辑房产
    private RelativeLayout rlBind;//绑定设备
    private RelativeLayout rlGateway;//网关列表
    private RelativeLayout rlOpenManage;//开门管理
    private RelativeLayout rlDel;//申请删除
    private RelativeLayout rlPwd;//临时密码

    protected CompositeSubscription mCompositeSubscription;

    public HouseInfoSettingPopupwindow(Context context, HouseDetailsInfoBean2 infoBean) {
        super(context);
        this.bean = infoBean;
        this.mContext = context;
        init(context);   //布局
        setPopupWindow();   //布局属性
    }

    private void init(Context context) {
        mView = View.inflate(context, R.layout.popup_house_info_setting, null);
        rlPay = mView.findViewById(R.id.popup_rl_pay_setting);
        rlEdit = mView.findViewById(R.id.popup_rl_edit);
        rlBind = mView.findViewById(R.id.popup_rl_binddevice);
        rlGateway = mView.findViewById(R.id.popup_rl_gateway);
        rlOpenManage = mView.findViewById(R.id.popup_rl_open_manage);
        rlDel = mView.findViewById(R.id.popup_rl_del);
        rlPwd = mView.findViewById(R.id.popup_rl_pwd);

        rlPay.setOnClickListener(this);
        rlEdit.setOnClickListener(this);
        rlBind.setOnClickListener(this);
        rlGateway.setOnClickListener(this);
        rlOpenManage.setOnClickListener(this);
        rlDel.setOnClickListener(this);
        rlPwd.setOnClickListener(this);


    }

    public void setBean(HouseDetailsInfoBean2 bean) {
        this.bean = bean;
    }

    public void initStatus(int status) {
        switch (status) {
            case 0:
            case 1://未缴纳安装费
//                rlPay.setVisibility(View.VISIBLE);
//                rlEdit.setVisibility(View.VISIBLE);
//                rlBind.setVisibility(View.VISIBLE);
//                rlGateway.setVisibility(View.GONE);
//                rlOpenManage.setVisibility(View.GONE);
//                rlDel.setVisibility(View.VISIBLE);
//                rlPwd.setVisibility(View.GONE);
//                break;
            case 2://待安装认证
//                rlPay.setVisibility(View.GONE);
//                rlEdit.setVisibility(View.VISIBLE);
//                rlBind.setVisibility(View.VISIBLE);
//                rlGateway.setVisibility( View.GONE);
//                rlOpenManage.setVisibility(View.VISIBLE);
//                rlDel.setVisibility(View.VISIBLE);
//                rlPwd.setVisibility(View.GONE);
//                break;
            case 3://待发布

                rlEdit.setVisibility(View.VISIBLE);
                rlBind.setVisibility(View.VISIBLE);
                rlGateway.setVisibility(View.GONE);

                rlDel.setVisibility(View.VISIBLE);
                rlPwd.setVisibility(View.GONE);
                if (TextUtils.isEmpty(bean.getDevice_id())) {
                    rlOpenManage.setVisibility(View.GONE);
                    rlPay.setVisibility(View.GONE);
                } else if (bean.getIs_install() == 0) {
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.GONE);
                }else{
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.VISIBLE);
                }
                break;
            case 4://已发布
                if (TextUtils.isEmpty(bean.getDevice_id())) {
                    rlOpenManage.setVisibility(View.GONE);
                    rlPay.setVisibility(View.GONE);
                } else if (bean.getIs_install() == 0) {
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.GONE);
                }else{
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.VISIBLE);
                }
                rlEdit.setVisibility(View.GONE);
                rlBind.setVisibility(View.VISIBLE);
                rlGateway.setVisibility(View.GONE);
                rlDel.setVisibility(View.GONE);
                rlPwd.setVisibility(View.GONE);
                break;
            case 5://已确定
                if (TextUtils.isEmpty(bean.getDevice_id())) {
                    rlOpenManage.setVisibility(View.GONE);
                    rlPay.setVisibility(View.GONE);
                } else if (bean.getIs_install() == 0) {
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.GONE);
                }else{
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.VISIBLE);
                }
                rlEdit.setVisibility(View.GONE);
                rlBind.setVisibility(View.VISIBLE);
                rlGateway.setVisibility( View.GONE);
                rlDel.setVisibility(View.GONE);
                rlPwd.setVisibility(View.GONE);
                break;
            case 6://已出租
                if (TextUtils.isEmpty(bean.getDevice_id())) {
                    rlOpenManage.setVisibility(View.GONE);
                    rlPay.setVisibility(View.GONE);
                } else if (bean.getIs_install() == 0) {
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.GONE);
                }else{
                    rlOpenManage.setVisibility(View.VISIBLE);
                    rlPay.setVisibility(View.VISIBLE);
                }
                rlEdit.setVisibility(View.GONE);
                rlBind.setVisibility(View.VISIBLE);
                rlGateway.setVisibility( View.GONE);
                rlDel.setVisibility(View.GONE);
                rlPwd.setVisibility(View.GONE);
                break;
        }
//        rlPwd.setVisibility(View.GONE);
//        rlOpenManage.setVisibility(TextUtils.isEmpty(bean.getDevice_id()) ? View.GONE : View.VISIBLE);

    }

    private void setPopupWindow() {
        this.setContentView(mView); //设置View
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
//        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_rl_pay_setting://安装付费
                dismiss();
                new AlertDialog.Builder(mContext).setTitle(R.string.house_info_setting_paysetting).setMessage(R.string.house_info_warm_setting_paysetting)
                        .setPositiveButton(R.string.warm_comfit_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, BailPayActivity.class);
                                intent.putExtra("room_id", bean.getRoom_id());
                                intent.putExtra("address", bean.getAddress());
                                mContext.startActivity(intent);
                            }
                        }).setNegativeButton(R.string.warn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

                break;
            case R.id.popup_rl_edit://编辑房产
                HouseEditActivity.toActivity(mContext, bean);
                dismiss();
                break;
            case R.id.popup_rl_binddevice://绑定设备
//                DeviceListActivity.toActivity(mContext, bean.getRoom_id() + "", bean.getRoom_status());
                DevListActivity.toActivity(mContext, bean.getRoom_id() + "", bean.getRoom_status(), bean.getIs_install() == 0, false);
                dismiss();
                break;
            case R.id.popup_rl_gateway://网关列表
//                UserGatewayListActivity.toActivity(mContext, bean.getRoom_id() + "");
                DevListActivity.toActivity(mContext, bean.getRoom_id() + "", bean.getRoom_status(), bean.getIs_install() == 0, false);
                dismiss();
                break;
            case R.id.popup_rl_open_manage://开门管理
//                RxBus.getDefault().post(new HouseInfoSettingPopupEvent(HouseInfoSettingPopupEvent.TYPE_KEYPWD));
                OpenManageActivity.toActivity(mContext, bean);
                dismiss();
                break;
            case R.id.popup_rl_del://申请删除
                dismiss();
                if (bean.getIs_del() == 1) {
                    new AlertDialog.Builder(mContext).setTitle(R.string.house_info_warm_del_title).setMessage(R.string.house_info_warm_del_context)
                            .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    del();
                                }
                            }).setNegativeButton(R.string.warn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                } else {
                    new AlertDialog.Builder(mContext).setTitle(R.string.house_info_warm_del_title).setMessage(R.string.house_info_warm_del_context_no)
                            .setNegativeButton(R.string.warn_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }
                break;
            case R.id.popup_rl_pwd://临时密码
                TemporaryPwdActivity.toActivity(mContext, bean.getRoom_id());
                dismiss();
                break;
        }
    }

    private void del() {
        Subscription subscription = (SecondRetrofitHelper.getInstance().applyDelHouse(bean.getRoom_id() + "")
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, R.string.house_info_warm_del_toast_no, Toast.LENGTH_SHORT).show();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new UpdataHouseInfoEvent());
                            RxBus.getDefault().post(new LandlordHouseInfoEvent(-11));
                            Toast.makeText(mContext, R.string.house_info_warm_del_toast_ok, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, R.string.house_info_warm_del_toast_no, Toast.LENGTH_SHORT).show();
                        }

                    }
                }));
        addSubscrebe(subscription);
    }


    /**
     * 取消订阅
     */
    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

    /**
     * 添加订阅
     *
     * @param subscription
     */
    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
}
