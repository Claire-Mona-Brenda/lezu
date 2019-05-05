package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.CheckGatewayStatusBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.GatewayDetailBean;
import com.konka.renting.event.ChooseOtherWiFiEvent;
import com.konka.renting.event.DelGatewayEvent;
import com.konka.renting.event.EditGatwayEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.gateway.widget.GatewaySettingPopup;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.TipsPopup;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class GatewaySettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, GatewaySettingPopup.OnClickItemListent {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    @BindView(R.id.activity_gateway_setting_ll_manage_wifi)
    LinearLayout llSetWifi;
    //    @BindView(R.id.activity_gateway_wifi_setting_cb_wifi)
//    CheckBox cbWifi;
    @BindView(R.id.activity_gateway_wifi_setting_edt_wifi)
    EditText edtWifi;
    @BindView(R.id.activity_gateway_wifi_setting_cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.activity_gateway_wifi_setting_edt_pwd)
    EditText edtPwd;
    @BindView(R.id.activity_gateway_wifi_setting_btn_save)
    Button btnSave;

    @BindView(R.id.activity_gateway_setting_btn_restart)
    Button btnRestart;
    @BindView(R.id.activity_gateway_setting_ll_gatewayName)
    LinearLayout llGatewayName;
    @BindView(R.id.activity_gateway_setting_tv_gatewayName)
    TextView tvGatewayName;
    @BindView(R.id.activity_gateway_setting_img_rename)
    ImageView imgRename;
    @BindView(R.id.img_gate)
    ImageView imgGate;
    @BindView(R.id.activity_gateway_setting_tv_gatewayNumber)
    TextView tvGatewayNumber;
    @BindView(R.id.activity_gateway_setting_tv_conn)
    TextView tvConn;
    @BindView(R.id.activity_gateway_setting_img_conn)
    ImageView imgConn;
    @BindView(R.id.activity_gateway_setting_img_conn_loading)
    ImageView imgConnLoading;
    @BindView(R.id.activity_gateway_setting_tv_power)
    TextView tvPower;
    @BindView(R.id.activity_gateway_setting_img_power_loading)
    ImageView imgPowerLoading;
    @BindView(R.id.activity_gateway_wifi_setting_tv_find_wifi)
    TextView tvFindWifi;

    public static final int TYPE_LANDLORD = 11;
    public static final int TYPE_TENANT = 22;

    String room_id;
    private String gatewayId;
    private String gatewayName = "";
    private String gatewaySearil = "";
    private int gatewayVersion;
    private int network;//联网方式 1:2g 2:wifi 3:以太网
    private int type;//身份类型  房东端  租客端
    private String wifiName = "";
    private String wifiPwd = "";

    private AnimationDrawable frameConnAnim, framePowerAnim;

    GatewaySettingPopup gatewaySettingPopup;
    //    ChooseWiFiPopup chooseWifiPopup;
    CommonPopupWindow popupWindow;
    TipsPopup tipsPopup;
    boolean isStartting = true;//是否正在重启网关


    public static void toActivity(Context context, String room_id, String gatewayId, String gatewayVersion, int type) {
        Intent intent = new Intent(context, GatewaySettingActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("gatewayId", gatewayId);
        intent.putExtra("gatewayVersion", gatewayVersion);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_gateway;
    }

    @Override
    public void init() {
        room_id = getIntent().getStringExtra("room_id");
        gatewayId = getIntent().getStringExtra("gatewayId");
        type = getIntent().getIntExtra("type", TYPE_LANDLORD);
        gatewayVersion = Integer.valueOf(getIntent().getStringExtra("gatewayVersion"));

        setTitleText(R.string.gateway_info);

        if (type == TYPE_LANDLORD) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(R.string.common_del);
        } else {
            tvRight.setVisibility(View.GONE);
        }
//        ivRight.setVisibility(View.VISIBLE);
//        ivRight.setImageResource(R.mipmap.caidan);


        llSetWifi.setVisibility(gatewayVersion > 2 ? View.VISIBLE : View.GONE);
        imgGate.setImageResource(gatewayVersion > 3 ? R.mipmap.wangguan_2_116px_png : R.mipmap.wangguan_1_116px_png);

//        cbWifi.setOnCheckedChangeListener(this);
        cbPwd.setOnCheckedChangeListener(this);

        addRxBusSubscribe(ChooseOtherWiFiEvent.class, new Action1<ChooseOtherWiFiEvent>() {
            @Override
            public void call(ChooseOtherWiFiEvent chooseOtherWiFiEvent) {
                if (chooseOtherWiFiEvent.isClick) {
                    ChooseWiFiActivity.toActivity(GatewaySettingActivity.this);
//                    chooseWifiPopup.dismiss();
                } else {
//                    if (btnSave.isSelected()){
                    edtWifi.setText(chooseOtherWiFiEvent.chooseWiFiName);
                    edtWifi.setSelection(edtWifi.getText().toString().length());
                    edtPwd.setText("");
                    edtPwd.setFocusable(true);
                    edtPwd.requestFocus();
//                    }else{
//                        showSetWiFi(chooseOtherWiFiEvent.chooseWiFiName);
//                    }
                }
            }
        });
        addRxBusSubscribe(EditGatwayEvent.class, new Action1<EditGatwayEvent>() {
            @Override
            public void call(EditGatwayEvent editGatwayEvent) {
                tvGatewayName.setText(editGatwayEvent.name);
            }
        });

        isConning(false);
        showLoadingAnimaTion();
        initData();
        checkStatusTime();

    }

    private void showLoadingAnimaTion() {
        imgPowerLoading.setVisibility(View.VISIBLE);
        imgConnLoading.setVisibility(View.VISIBLE);
        frameConnAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_gateway_loading);
        framePowerAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_gateway_loading);
        imgConnLoading.setBackground(frameConnAnim);
        imgPowerLoading.setBackground(framePowerAnim);
        frameConnAnim.start();
        framePowerAnim.start();
    }

    private void hideLoadingAnimaTion() {
        imgPowerLoading.setVisibility(View.GONE);
        imgConnLoading.setVisibility(View.GONE);
        if (frameConnAnim.isRunning())
            frameConnAnim.stop();
        if (framePowerAnim.isRunning())
            framePowerAnim.stop();
    }


    @OnClick({R.id.iv_back, R.id.iv_right, R.id.tv_right,
            R.id.activity_gateway_wifi_setting_btn_save,
            R.id.activity_gateway_setting_btn_restart,
            R.id.activity_gateway_setting_ll_gatewayName,
            R.id.activity_gateway_wifi_setting_tv_find_wifi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.iv_right://菜单
                showSettingDialog();
                break;
            case R.id.tv_right://删除
                showDel();
                break;
            case R.id.activity_gateway_wifi_setting_tv_find_wifi://查看附近WiFi
                ChooseWiFiActivity.toActivity(GatewaySettingActivity.this);
                break;
            case R.id.activity_gateway_wifi_setting_btn_save://设置网关WiFi
                if (isStartting) {
                    showTipsPopup(getString(R.string.please_gateway_restart_success_wait));
                    return;
                }
                if (btnSave.getText().toString().equals(getString(R.string.common_set))) {
                    enableToSetWiFi(true);
                    btnSave.setSelected(true);
                    btnSave.setText(R.string.common_save);
                } else {
                    String name = edtWifi.getText().toString();
                    String pwd = edtPwd.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        showToast(R.string.please_input_wifi_ssid);
                    } else if (pwd.length() > 0 && pwd.length() < 8) {
                        showWiFiPwdTipsPopup(getString(R.string.please_input_wifi_pwd_more));
                    } else if (name.equals(wifiName) && pwd.equals(wifiPwd)) {
//                        showToast(getString(R.string.please_input_wifi_pwd_same));
//                        btnSave.setText(R.string.common_set);
//                        enableToSetWiFi(false);
//                        btnSave.setSelected(false);
                        showSetWiFiSame(name, pwd);
                    } else {
                        setWiFi(name, pwd);
                    }
                }

                break;
            case R.id.activity_gateway_setting_btn_restart://网关重启
                if (isStartting) {
                    showTipsPopup(getString(R.string.please_gateway_restart_success_wait));
                    return;
                }
                showReboot();
                break;
            case R.id.activity_gateway_setting_ll_gatewayName://修改网关别名
                if (type == TYPE_LANDLORD)
                    EditGatewayActivity.toActivity(this, gatewayName, gatewayId);
                break;
        }
    }


    private void showSettingDialog() {
        // 开启 popup 时界面透明
        if (gatewaySettingPopup == null) {
            gatewaySettingPopup = new GatewaySettingPopup(this);
            gatewaySettingPopup.setOnClickItemListent(this);
        }
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        gatewaySettingPopup.showAsDropDown(ivRight);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        gatewaySettingPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

//    private void showChooseWiFiPopup() {
//        // 开启 popup 时界面透明
//        if (chooseWifiPopup == null)
//            chooseWifiPopup = new ChooseWiFiPopup(this);
//        // popupwindow 第一个参数指定popup 显示页面
//        chooseWifiPopup.showAsDropDown(edtWifi, 0, 0, Gravity.CENTER_HORIZONTAL);     // 第一个参数popup显示activity页面
//        chooseWifiPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                cbWifi.setChecked(false);
//            }
//        });
//
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
//            case R.id.activity_gateway_wifi_setting_cb_wifi://是否显示WiFi选项列表
//                if (isChecked)
//                    showChooseWiFiPopup();
//                else
//                    chooseWifiPopup.dismiss();
//                break;
            case R.id.activity_gateway_wifi_setting_cb_pwd://是否显示密码
                if (isChecked) {
                    edtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                if (!TextUtils.isEmpty(edtPwd.getText().toString())) {
                    edtPwd.setSelection(edtPwd.getText().toString().length());
                }
                break;
        }
    }

    /**************************************************弹窗******************************************************/

    private void showReboot() {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.warm_restart_gateway))
                .setRightTextColor("#FF0000")
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reboot();
                        popupWindow.dismiss();
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    private void showDel() {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.warm_del_gateway))
                .setContentTextColor("#FF0000")
                .setRightTextColor("#FF0000")
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delGatway();
                        popupWindow.dismiss();
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    private void showSetWiFi(final String wifiName) {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.popup_sure_change_wifi))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enableToSetWiFi(true);
                        edtWifi.setText(wifiName);
                        edtWifi.setSelection(edtWifi.getText().toString().length());
                        edtPwd.setFocusable(true);
                        edtPwd.requestFocus();
                        popupWindow.dismiss();
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    private void showSetWiFiSame(final String name,final String pwd) {
        popupWindow = new CommonPopupWindow.Builder(this)
                .setTitle(getString(R.string.tips))
                .setContent(getString(R.string.popup_sure_change_same_wifi))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        setWiFi(name, pwd);
                    }
                })
                .create();
        showPopup(popupWindow);
    }

    private void showTipsPopup(String content) {
        if (tipsPopup == null) {
            tipsPopup = new TipsPopup(this);
        }
        tipsPopup.setContent(content);
        if (!tipsPopup.isShowing())
            showPopup(tipsPopup);
    }

    private void showWiFiPwdTipsPopup(String content) {
        if (tipsPopup == null) {
            tipsPopup = new TipsPopup(this);
        }
        tipsPopup.setContent(content);
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        tipsPopup.showAtLocation((View) tvTitle.getParent().getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        tipsPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
                edtPwd.setSelection(edtPwd.getText().toString().length());
                edtPwd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        edtPwd.requestFocus();
                        InputMethodManager inputManager =
                                (InputMethodManager) edtPwd.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(edtPwd, 0);
                    }
                }, 200);
            }
        });
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) findViewById(R.id.lin_title).getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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

    /***************************************************接口**************************************************************/

    private void initData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().detailGateway(gatewayId)
                .compose(RxUtil.<DataInfo<GatewayDetailBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<GatewayDetailBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        showError(e.getMessage());
                        hideLoadingAnimaTion();
                        tvConn.setVisibility(View.VISIBLE);
                        tvPower.setVisibility(View.VISIBLE);
                        tvConn.setText(R.string.disconnect);
                        tvPower.setText(R.string.un_know);
                        isConning(true);
                    }

                    @Override
                    public void onNext(DataInfo<GatewayDetailBean> dataInfo) {
                        if (dataInfo.success()) {
                            network = Integer.valueOf(dataInfo.data().getNetwork());
                            gatewayName = dataInfo.data().getGateway_name();
                            gatewaySearil = dataInfo.data().getGateway_no();
                            tvGatewayName.setText(gatewayName);
                            tvGatewayNumber.setText(gatewaySearil);
                            imgRename.setVisibility(type == TYPE_LANDLORD ? View.VISIBLE : View.GONE);
                            check();
                            imgConnLoading.setVisibility(View.GONE);
                            frameConnAnim.stop();
                            tvConn.setVisibility(View.VISIBLE);
                            wifiName = dataInfo.data().getWifi_name();
                            wifiPwd = dataInfo.data().getWifi_password();
                            edtWifi.setText(dataInfo.data().getWifi_name());
                            edtWifi.setSelection(dataInfo.data().getWifi_name().length());
                            edtPwd.setText(dataInfo.data().getWifi_password());
                        } else {
                            isConning(true);
                            hideLoadingAnimaTion();
                            tvConn.setVisibility(View.VISIBLE);
                            tvPower.setVisibility(View.VISIBLE);
                            tvConn.setText(R.string.disconnect);
                            tvPower.setText(R.string.un_know);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().detailGateway(gatewayId)
                .compose(RxUtil.<DataInfo<GatewayDetailBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<GatewayDetailBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<GatewayDetailBean> dataInfo) {
                        if (dataInfo.success()) {
                            network = Integer.valueOf(dataInfo.data().getNetwork());
                            gatewayName = dataInfo.data().getGateway_name();
                            gatewaySearil = dataInfo.data().getGateway_no();
                            check();
                            imgConnLoading.setVisibility(View.GONE);
                            tvConn.setVisibility(View.VISIBLE);
                            wifiName = dataInfo.data().getWifi_name();
                            wifiPwd = dataInfo.data().getWifi_password();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void delGatway() {
        Subscription subscription = SecondRetrofitHelper.getInstance().unbindGateway(gatewayId)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            RxBus.getDefault().post(new DelGatewayEvent());
                            finish();
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void check() {
        Subscription subscription = SecondRetrofitHelper.getInstance().get_device_information(gatewayId)
                .compose(RxUtil.<DataInfo<CheckGatewayStatusBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<CheckGatewayStatusBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        isConning(true);
                        hideLoadingAnimaTion();
                        tvPower.setVisibility(View.VISIBLE);
                        imgConn.setVisibility(View.GONE);
                        tvPower.setText(R.string.un_know);
                        tvConn.setText(R.string.disconnect);
                    }

                    @Override
                    public void onNext(DataInfo<CheckGatewayStatusBean> info) {
                        hideLoadingAnimaTion();
                        if (info.success()) {
                            isConning(false);
                            tvPower.setVisibility(View.VISIBLE);
                            tvPower.setText(info.data().getPower() == 0 ? getString(R.string.no) : getString(R.string.yes));
                            imgConn.setVisibility(View.VISIBLE);
                            if (network == 1) {
                                int gprs = Integer.valueOf(info.data().getGprs());
                                imgConn.setVisibility(View.VISIBLE);
                                if (gprs <= 15) {
                                    imgConn.setImageResource(R.mipmap.sign_2);
                                } else if (gprs <= 20) {
                                    imgConn.setImageResource(R.mipmap.sign_3);
                                } else if (gprs <= 25) {
                                    imgConn.setImageResource(R.mipmap.sign_4);
                                } else {
                                    imgConn.setImageResource(R.mipmap.sign_5);
                                }
                            }
                            switch (network) {
                                case 1:
                                    tvConn.setText(R.string.gateway_conn_2G);
                                    break;
                                case 2:
                                    tvConn.setText(R.string.gateway_conn_WiFi);
                                    break;
                                case 3:
                                    tvConn.setText(R.string.gateway_conn_Ethernet);
                                    break;
                            }
                            if (tipsPopup != null && tipsPopup.isShowing()) {
                                tipsPopup.dismiss();
                            }
                        } else {
                            isConning(true);
                            imgConn.setVisibility(View.GONE);
                            tvPower.setVisibility(View.VISIBLE);
                            tvPower.setText(R.string.un_know);
                            tvConn.setText(R.string.disconnect);

                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void reboot() {
        isConning(true);
        Subscription subscription = SecondRetrofitHelper.getInstance().reStart(gatewayId)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        isConning(false);
                        showTipsPopup(getString(R.string.please_gateway_restart_fail));
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            isConning(true);
                            imgConn.setVisibility(View.GONE);
                            tvPower.setVisibility(View.VISIBLE);
                            tvPower.setText(R.string.un_know);
                            tvConn.setText(R.string.disconnect);
                        } else {
                            isConning(false);
                            showTipsPopup(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 点击右上角菜单里子项
     */
    @Override
    public void onClick(String type) {
        switch (type) {
            case GatewaySettingPopup.CLICK_DELETE:
                showDel();
                break;
        }
    }

    private void setWiFi(final String name, final String pwd) {
        isConning(true);
        Subscription subscription = SecondRetrofitHelper.getInstance().setWifi(room_id, gatewayId, name, pwd)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        isConning(false);
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        if (dataInfo.success()) {
                            showToast(R.string.success_to_setting_wifi_please_gateway_restart);
                            enableToSetWiFi(false);
                            wifiName = name;
                            wifiPwd = pwd;
                            btnSave.setText(R.string.common_set);
                            btnSave.setSelected(false);
                            reboot();
                        } else {
                            isConning(false);
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    private void isConning(boolean isConning) {
        this.isStartting = isConning;
        btnSave.setEnabled(!isConning);
        btnRestart.setEnabled(!isConning);
        btnRestart.setText(isConning ? R.string.connecting : R.string.restart);
        if ( btnSave.getText().toString().equals(getString(R.string.common_set))) {
            enableToSetWiFi(false);
        } else {
            enableToSetWiFi(true);
        }
    }

    private void enableToSetWiFi(boolean enable) {
        tvFindWifi.setEnabled(enable);
        edtWifi.setEnabled(enable);
        edtPwd.setEnabled(enable);
        if (!enable) {
            cbPwd.setChecked(false);
        }
        cbPwd.setEnabled(enable);
    }

    /****************************************************倒计时处理*************************************************************/
    private void checkStatusTime() {
        Subscription subscription = Observable.timer(10, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        getData();
                        checkStatusTime();
                    }
                });
        addSubscrebe(subscription);
    }

}
