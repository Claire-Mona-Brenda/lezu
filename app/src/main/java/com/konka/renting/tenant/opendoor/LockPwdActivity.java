package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Switch;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class LockPwdActivity extends BaseActivity {


    @BindView(R.id.switch_message)
    Switch mSwitchMessage;
    private String OPEN_AUTH = "1";
    private String CLOSE_AUTH = "0";
    private String type;
    private String is_verification;
    private String has_device_auth_password;

    public static void toActivity(Context context, String type) {

        Intent intent = new Intent(context, LockPwdActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_pwd;
    }

    @Override
    public void init() {

        setTitleText(R.string.open_door_pwd);
        /*is_operate_device_auth = UserBean.getInstance().isIs_operate_device_auth();
        has_device_auth_password = UserBean.getInstance().isHas_device_auth_password();*/
        type = getIntent().getStringExtra("type");
        is_verification = LoginUserBean.getInstance().getIs_verification();
        has_device_auth_password = LoginUserBean.getInstance().getVerification_pwd();
        if (LoginUserBean.getInstance().getIs_verification().equals("1")) {
            mSwitchMessage.setChecked(true);
        } else {
            mSwitchMessage.setChecked(false);
        }

        addRxBusSubscribe(SetPwdEvent.class, new Action1<SetPwdEvent>() {
            @Override
            public void call(SetPwdEvent setPwdEvent) {

                if (setPwdEvent.isEmpty() == true) {
                    showToast(setPwdEvent.getPwd());
                } else if (setPwdEvent.getType() == SetPwdEvent.Type.ADD) {
                    setPwd(setPwdEvent.getPwd());
                } else if (setPwdEvent.getType() == SetPwdEvent.Type.CLOSE && !setPwdEvent.isEmpty())
                    checkPwdAuth(setPwdEvent.getPwd());


            }
        });
        addRxBusSubscribe(CancelSetEvent.class, new Action1<CancelSetEvent>() {
            @Override
            public void call(CancelSetEvent cancelSetEvent) {
                mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    }
                });
                if (cancelSetEvent.getType() == SetPwdEvent.Type.ADD)
                    mSwitchMessage.setChecked(false);
                if (cancelSetEvent.getType() == SetPwdEvent.Type.CLOSE)
                    mSwitchMessage.setChecked(true);
                mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                            showSetPwd();
                        } else {

                            if (isChecked) {
                                updateHasStatus(OPEN_AUTH, true);
                            } else {
                                checkPwd();
                            }

                        }
                    }
                });
            }
        });
        mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                    showSetPwd();
                } else {

                    if (isChecked) {
                        updateHasStatus(OPEN_AUTH, true);
                    } else {
                        //updateHasStatus(CLOSE_AUTH,false);
                        checkPwd();
                    }

                }
            }
        });

    }

    private void checkPwdAuth(String pwd) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().checkPwd(type, pwd)
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
                        Log.e("check", dataInfo.msg());
                        if (dataInfo.success()) {
                            updateHasStatus(CLOSE_AUTH, false);
                        } else {
                            showToast(dataInfo.msg());
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                }
                            });
                            mSwitchMessage.setChecked(true);
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                                        showSetPwd();
                                    } else {

                                        if (isChecked) {
                                            updateHasStatus(OPEN_AUTH, true);
                                        } else {
                                            checkPwd();
                                        }

                                    }
                                }
                            });
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void showSetPwd() {
        // 开启 popup 时界面透明
        SetPwdPopup reviseNeeknamePopup = new SetPwdPopup(this, SetPwdEvent.Type.ADD);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        // popupwindow 第一个参数指定popup 显示页面
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        reviseNeeknamePopup.showAtLocation(this.findViewById(R.id.set_pwd), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        reviseNeeknamePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private void checkPwd() {
        // 开启 popup 时界面透明
        SetPwdPopup reviseNeeknamePopup = new SetPwdPopup(this, SetPwdEvent.Type.CLOSE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        // popupwindow 第一个参数指定popup 显示页面
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        reviseNeeknamePopup.showAtLocation(this.findViewById(R.id.set_pwd), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        reviseNeeknamePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private void setPwd(String pwd) {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().addPwd(type, pwd)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            }
                        });
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                        mSwitchMessage.setChecked(false);
                        mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                                    showSetPwd();
                                } else {

                                    if (isChecked) {
                                        updateHasStatus(OPEN_AUTH, true);
                                    } else {
                                        //updateHasStatus(CLOSE_AUTH,false);
                                        checkPwd();
                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {

                        Log.e("data....", dataInfo.msg());
                        if (dataInfo.success()) {
                            updateStatus(OPEN_AUTH);
                        } else {
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                }
                            });
                            //showToast(dataInfo.msg());
                            mSwitchMessage.setChecked(false);
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                                        showSetPwd();
                                    } else {

                                        if (isChecked) {
                                            updateHasStatus(OPEN_AUTH, true);
                                        } else {
                                            //updateHasStatus(CLOSE_AUTH,false);
                                            checkPwd();
                                        }

                                    }
                                }
                            });
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    public void updateStatus(String status) {
        Subscription subscription = RetrofitHelper.getInstance().updateLockState(type, status)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        showError(e.getMessage());
                        mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            }
                        });
                        mSwitchMessage.setChecked(false);
                        mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                                    showSetPwd();
                                } else {

                                    if (isChecked) {
                                        updateHasStatus(OPEN_AUTH, true);
                                    } else {
                                        checkPwd();
                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        Log.e("xitong....", dataInfo.msg());
                        showToast(dataInfo.msg());
                        if (dataInfo.success()) {
                            has_device_auth_password = "123";
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                }
                            });
                            mSwitchMessage.setChecked(true);
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                                        showSetPwd();
                                    } else {

                                        if (isChecked) {
                                            updateHasStatus(OPEN_AUTH, true);
                                        } else {
                                            checkPwd();
                                        }

                                    }
                                }
                            });
                            LoginUserBean.getInstance().setVerification_pwd("123");
                            LoginUserBean.getInstance().setIs_verification("1");
                            LoginUserBean.getInstance().save();
                        } else {
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                }
                            });
                            mSwitchMessage.setChecked(false);
                            mSwitchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    if (LoginUserBean.getInstance().getVerification_pwd().equals("")) {
                                        showSetPwd();
                                    } else {

                                        if (isChecked) {
                                            updateHasStatus(OPEN_AUTH, true);
                                        } else {
                                            checkPwd();
                                        }

                                    }
                                }
                            });
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    public void updateHasStatus(String status, final boolean b) {
        Subscription subscription = RetrofitHelper.getInstance().updateLockState(type, status)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo dataInfo) {
                        dismiss();
                        showToast(dataInfo.msg());
                        Log.e("check1", dataInfo.msg());
                        if (dataInfo.success()) {
                            if (b == false) {
                                LoginUserBean.getInstance().setIs_verification(CLOSE_AUTH);
                                LoginUserBean.getInstance().save();
                            } else {
                                LoginUserBean.getInstance().setIs_verification(OPEN_AUTH);
                                LoginUserBean.getInstance().save();
                            }

                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @OnClick({R.id.iv_back, R.id.re_revise_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.re_revise_pwd:
                RevisePwdActivity.toActivity(this, type);
                break;
        }
    }

}
