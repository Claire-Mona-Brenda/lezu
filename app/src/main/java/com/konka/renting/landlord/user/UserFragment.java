package com.konka.renting.landlord.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.ServiceTelBean;
import com.konka.renting.event.RenZhengSuccessEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.home.bill.BillListActivity;
import com.konka.renting.landlord.user.collection.MyCollectionActivity;
import com.konka.renting.landlord.user.message.MessageActivity;
import com.konka.renting.landlord.user.tenant.HouseTenantListActivity;
import com.konka.renting.landlord.user.userinfo.CertificationActivity;
import com.konka.renting.landlord.user.userinfo.FaceDectectEvent;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;
import com.konka.renting.landlord.user.userinfo.RenZhengInfoActivity;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.landlord.user.userinfo.UserInfoActivity;
import com.konka.renting.landlord.user.withdrawcash.RechargeActivity;
import com.konka.renting.landlord.user.withdrawcash.RechargeEvent;
import com.konka.renting.landlord.user.withdrawcash.WithdrawEvent;
import com.konka.renting.landlord.user.withdrawcash.WithdrawcashActivity;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.konka.renting.widget.RenZhengTipsPopup;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class UserFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_user_nickname)
    TextView mTvUserNickname;
    @BindView(R.id.tv_user_phone)
    TextView mTvUserPhone;
    @BindView(R.id.icon_user_photo)
    CircleImageView mIconUserPhoto;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tv_message_sum)
    TextView mTvMessageSum;

    @BindView(R.id.icon_user_setting)
    ImageView imgUserSetting;
    @BindView(R.id.tv_recharge)
    TextView tvRecharge;
    @BindView(R.id.tv_withdraw)
    TextView tvWithdraw;
    @BindView(R.id.tv_money_unit)
    TextView tvMoneyUnit;
    @BindView(R.id.img_user_isAuthentication)
    ImageView imgUserIsAuthentication;
    @BindView(R.id.img_rent_people)
    ImageView imgRentPeople;
    @BindView(R.id.tv_rent_people)
    RelativeLayout tvRentPeople;
    @BindView(R.id.img_bill)
    ImageView imgBill;
    @BindView(R.id.tv_bill)
    RelativeLayout tvBill;
    @BindView(R.id.img_collection)
    ImageView imgCollection;
    @BindView(R.id.tv_collection)
    RelativeLayout tvCollection;
    @BindView(R.id.img_message)
    ImageView imgMessage;
    @BindView(R.id.tv_message)
    RelativeLayout tvMessage;
    @BindView(R.id.img_face)
    ImageView imgFace;
    @BindView(R.id.img_problem)
    ImageView imgProblem;
    @BindView(R.id.img_call_us)
    ImageView imgCallUs;

    private LandlordUserBean userInfoBean;
    String urlPic;
    RenZhengTipsPopup renZhengTipsPopup;
    CommonPopupWindow callPopupWindow;
    String tel;

    public UserFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        ViewGroup viewGroup = (ViewGroup) mIconUserPhoto.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());

        init();

        return view;
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }


    @Override
    public void init() {
        super.init();
        initData();
        addRxBusSubscribe(UpdateEvent.class, new Action1<UpdateEvent>() {
            @Override
            public void call(UpdateEvent updateEvent) {
                initData();
            }
        });
        addRxBusSubscribe(RechargeEvent.class, new Action1<RechargeEvent>() {
            @Override
            public void call(RechargeEvent rechargeEvent) {
                initData();
            }
        });
        addRxBusSubscribe(FaceDectectEvent.class, new Action1<FaceDectectEvent>() {
            @Override
            public void call(FaceDectectEvent faceDectectEvent) {
                initData();
            }
        });
        addRxBusSubscribe(WithdrawEvent.class, new Action1<WithdrawEvent>() {
            @Override
            public void call(WithdrawEvent withdrawEvent) {
                initData();
            }
        });
        addRxBusSubscribe(RenZhengSuccessEvent.class, new Action1<RenZhengSuccessEvent>() {
            @Override
            public void call(RenZhengSuccessEvent renZhengSuccessEvent) {
                if (userInfoBean != null) {
                    userInfoBean.setIs_auth(1);
                    imgUserIsAuthentication.setVisibility(View.VISIBLE);
                    imgUserIsAuthentication.setImageResource(R.mipmap.attestation_icon);

                }
            }
        });

    }

    private void initData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordUserInfo()
                .compose(RxUtil.<DataInfo<LandlordUserBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<LandlordUserBean>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataInfo<LandlordUserBean> userInfoBeanDataInfo) {
                        if (userInfoBeanDataInfo.success()) {
                            userInfoBean = userInfoBeanDataInfo.data();
                            if (userInfoBeanDataInfo.data() != null) {
                                if (userInfoBeanDataInfo.data().getUnread() > 0)
                                    mTvMessageSum.setText(userInfoBeanDataInfo.data().getUnread() + "");
                                else
                                    mTvMessageSum.setVisibility(View.GONE);
                                mTvUserNickname.setText(userInfoBeanDataInfo.data().getReal_name());
                                String tel = userInfoBeanDataInfo.data().getPhone();
                                if (!tel.equals("")) {
                                    int len = tel.length();
                                    String str = tel.substring(0, 3);
                                    for (int i = 3; i < len - 2; i++) {
                                        str += "*";
                                    }
                                    str += tel.substring(len - 2, len);
                                    tel = str;
                                }
                                mTvUserPhone.setText(tel);

                                imgUserIsAuthentication.setVisibility(View.VISIBLE);
                                imgUserIsAuthentication.setImageResource(userInfoBeanDataInfo.data().getIs_auth() == 1 ? R.mipmap.attestation_icon : R.mipmap.notattestation_icon);

                                if (userInfoBeanDataInfo.data().getThumb_headimgurl() != null
                                        && !userInfoBeanDataInfo.data().getThumb_headimgurl().equals(urlPic == null ? "" : urlPic)) {
                                    if (!TextUtils.isEmpty(userInfoBeanDataInfo.data().getThumb_headimgurl())) {
                                        if (CacheUtils.checkFileExist(userInfoBeanDataInfo.data().getThumb_headimgurl())) {
                                            Picasso.get().load(CacheUtils.getFile(userInfoBeanDataInfo.data().getThumb_headimgurl())).
                                                    placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                        } else {
                                            CacheUtils.saveFile(userInfoBeanDataInfo.data().getThumb_headimgurl(), getActivity());
                                            Picasso.get().load(userInfoBeanDataInfo.data().getThumb_headimgurl()).
                                                    placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                        }
                                    } else {
                                        Picasso.get().load(R.mipmap.touxiang).
                                                placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                    }
                                }
                                urlPic = userInfoBeanDataInfo.data().getThumb_headimgurl();
                                String balance=userInfoBeanDataInfo.data().getBalance();
                                if (TextUtils.isEmpty(balance)){
                                    balance = "";
                                }
                                mTvMoney.setText(balance);

                            }

                        } else {
                            showToast(userInfoBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_bill, R.id.tv_collection, R.id.tv_message, R.id.icon_user_setting,
            R.id.tv_withdraw, R.id.tv_recharge, R.id.img_call_us, R.id.img_face,
            R.id.tv_rent_people, R.id.img_problem})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_withdraw://提现
                if (userInfoBean != null) {
                    WithdrawcashActivity.toActivity(getContext(), userInfoBean.getBalance());
                }
                break;
            case R.id.tv_recharge://充值
                if (userInfoBean != null) {
                    RechargeActivity.toActivity(getContext());
                }
                break;
            case R.id.tv_rent_people://租客
                if (userInfoBean != null)
                    HouseTenantListActivity.toActivity(getContext());
                break;
            case R.id.tv_bill://账单
                if (userInfoBean != null) {
                    BillListActivity.toActivity(getContext());
                }
                break;
            case R.id.tv_collection://催租
                if (userInfoBean != null) {
                    MyCollectionActivity.toActivity(getContext());
                }
                break;
            case R.id.tv_message://消息
                if (userInfoBean != null) {
                    MessageActivity.toActivity(getContext());
                }
                break;
            case R.id.icon_user_setting://个人设置
                UserInfoActivity.toActivity(getContext(), LoginInfo.LANDLORD);
                break;
//            case R.id.tv_change_pwd://修改密码
//                ForgetPasswordActivity.toActivity(getContext(), LoginInfo.LANDLORD, LoginUserBean.getInstance().getMobile());
//                break;
            case R.id.img_call_us://联系客服
                if (userInfoBean != null) {
                    showCalll();

                }

                break;
            case R.id.img_problem://常见问题
//                WebviewActivity.toActivity(getActivity(), WebType.WEB_PROBLEM);
                if (userInfoBean != null) {
                    ProblemActivity.toActivity(getActivity());
                }
                break;
            case R.id.img_face://实名认证
                if (userInfoBean != null && userInfoBean.getIs_auth() == 1) {
                    RenZhengInfoActivity.toActivity(mActivity);
                } else if (userInfoBean != null && userInfoBean.getIs_auth() == 0) {
                    CertificationActivity.toActivity(mActivity, 1);
                }

                break;
//            case R.id.tv_after_the_process://售后流程
////                WebviewActivity.toActivity(getActivity(), WebType.WEB_ABOUT);
//                AfterProcessActivity.toActivity(getContext(), ((MainActivity) getActivity()).mAppConfigBean.getDisclaimer());
//                break;
//            case R.id.tv_login_out://退出登录
//
//                new AlertDialog.Builder(getActivity()).setTitle(R.string.login_out).setMessage("是否退出登录")
//                        .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                landmoreLoginOut();
//                            }
//                        }).setNegativeButton(R.string.warn_cancel, null).create().show();
//                break;
        }
    }

    private void callService() {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().serviceTel()
                .compose(RxUtil.<DataInfo<ServiceTelBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ServiceTelBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<ServiceTelBean> dataInfo) {
                        dismiss();
                        if (dataInfo.success()) {
                            tel = dataInfo.data().getTel();
                            PhoneUtil.call(tel, getContext());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void landmoreLoginOut() {
        LoginUserBean.getInstance().reset();
        LoginUserBean.getInstance().save();
        LoginNewActivity.toLandlordActivity(getContext());
        getActivity().finish();
    }

    /*********************************************弹窗***************************************************/
    private void showRenZhengTips() {
        if (renZhengTipsPopup == null) {
            renZhengTipsPopup = new RenZhengTipsPopup(mActivity);
            renZhengTipsPopup.setOnToClickCall(new RenZhengTipsPopup.OnToClickCall() {
                @Override
                public void onClick() {
                    CertificationActivity.toActivity(mActivity, 1);
                }
            });
        }
        showPopup(renZhengTipsPopup);
    }

    private void showCalll() {
        if (callPopupWindow == null)
            callPopupWindow = new CommonPopupWindow.Builder(mActivity)
                    .setTitle(getString(R.string.tips))
                    .setContent(getString(R.string.if_have_call_question_call))
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callPopupWindow.dismiss();
                            if (TextUtils.isEmpty(tel)) {
                                callService();

                            } else {
                                PhoneUtil.call(tel, getContext());
                            }
                        }
                    })
                    .create();
        showPopup(callPopupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) mIconUserPhoto.getParent().getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
