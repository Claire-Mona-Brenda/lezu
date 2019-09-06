package com.konka.renting.tenant.user;

import android.content.Intent;
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
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.ServiceTelBean;
import com.konka.renting.bean.ShareBean;
import com.konka.renting.event.RenZhengSuccessEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.ShareDialog;
import com.konka.renting.landlord.user.ProblemActivity;
import com.konka.renting.landlord.user.message.MessageActivity;
import com.konka.renting.landlord.user.other.AfterProcessActivity;
import com.konka.renting.landlord.user.userinfo.CertificationActivity;
import com.konka.renting.landlord.user.userinfo.FaceDectectEvent;
import com.konka.renting.landlord.user.userinfo.RenZhengInfoActivity;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.landlord.user.userinfo.UserInfoActivity;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.tenant.TenantMainActivity;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.konka.renting.widget.RenZhengTipsPopup;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class TenantUserFragment extends BaseFragment {

    @BindView(R.id.tv_user_nickname)
    TextView mTvUserNickname;
    @BindView(R.id.icon_user_photo)
    CircleImageView mIconUserPhoto;
    @BindView(R.id.icon_user_setting)
    ImageView imgUserSetting;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_user_phone)
    TextView tvUserPhone;
    @BindView(R.id.img_user_isAuthentication)
    ImageView imgUserIsAuthentication;
    @BindView(R.id.re_user)
    RelativeLayout reUser;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_authentication)
    TextView tvAuthentication;
    @BindView(R.id.tv_problem)
    TextView tvProblem;
    @BindView(R.id.tv_serviceCall)
    TextView tvServiceCall;
    @BindView(R.id.tv_after_the_process)
    TextView tvAfterTheProcess;

    Unbinder unbinder;

    RenZhengTipsPopup renZhengTipsPopup;
    private LandlordUserDetailsInfoBean infoBean;

    public TenantUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TenantUserFragment newInstance() {
        TenantUserFragment fragment = new TenantUserFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_tenant, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        ViewGroup viewGroup = (ViewGroup) tvTitle.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
        return view;
    }

    @Override
    public void init() {
        super.init();
        tvTitle.setText(R.string.main_user);
        imgUserSetting.setImageResource(R.mipmap.set_black);
        imgUserSetting.setVisibility(View.VISIBLE);

        addRxBusSubscribe(UpdateEvent.class, new Action1<UpdateEvent>() {
            @Override
            public void call(UpdateEvent updateEvent) {
                initData();
            }
        });
        addRxBusSubscribe(FaceDectectEvent.class, new Action1<FaceDectectEvent>() {
            @Override
            public void call(FaceDectectEvent faceDectectEvent) {
                initData();
            }
        });
        addRxBusSubscribe(RenZhengSuccessEvent.class, new Action1<RenZhengSuccessEvent>() {
            @Override
            public void call(RenZhengSuccessEvent renZhengSuccessEvent) {
                if (infoBean != null) {
                    infoBean.setIs_identity(1);
                    imgUserIsAuthentication.setVisibility(View.VISIBLE);
                    imgUserIsAuthentication.setImageResource(R.mipmap.attestation_icon);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (infoBean == null) {
            initData();
        }
    }

    private void initData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getLandlordUserDetailsInfo()
                .compose(RxUtil.<DataInfo<LandlordUserDetailsInfoBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<LandlordUserDetailsInfoBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        showToast("请先登录");
                        LoginUserBean.getInstance().reset();
                        LoginUserBean.getInstance().save();
                        startActivity(new Intent(mActivity, LoginNewActivity.class));
                        mActivity.finish();
                    }

                    @Override
                    public void onNext(DataInfo<LandlordUserDetailsInfoBean> dataInfo) {
                        if (dataInfo.success()) {

                            if (dataInfo.data() != null) {
                                infoBean = dataInfo.data();

                                LoginUserBean.getInstance().setIs_lodge_identity(dataInfo.data().getIs_identity() + "");
                                LoginUserBean.getInstance().setRealname(dataInfo.data().getReal_name());
                                LoginUserBean.getInstance().setIdentity(dataInfo.data().getIdentity());
                                LoginUserBean.getInstance().save();

                                imgUserIsAuthentication.setImageResource(LoginUserBean.getInstance().is_tenant_identity() ? R.mipmap.attestation_icon : R.mipmap.notattestation_icon);
                                mTvUserNickname.setText(dataInfo.data().getReal_name());
                                String tel = dataInfo.data().getPhone();
                                if (!tel.equals("")) {
                                    int len = tel.length();
                                    String str = tel.substring(0, 3);
                                    for (int i = 3; i < len - 2; i++) {
                                        str += "*";
                                    }
                                    str += tel.substring(len - 2, len);
                                    tel = str;
                                }
                                tvUserPhone.setText(tel);
                                if (CacheUtils.checkFileExist(dataInfo.data().getThumb_headimgurl())) {
                                    Picasso.get().load(CacheUtils.getFile(dataInfo.data().getThumb_headimgurl())).
                                            placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                } else if (!TextUtils.isEmpty(dataInfo.data().getThumb_headimgurl())) {
                                    CacheUtils.saveFile(dataInfo.data().getThumb_headimgurl(), getActivity());
                                    Picasso.get().load(dataInfo.data().getThumb_headimgurl()).
                                            placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                } else {
                                    Picasso.get().load(R.mipmap.touxiang).
                                            placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                }
                            }
                        } else {
                            showToast(dataInfo.msg());
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

    @OnClick({R.id.tv_message, R.id.tv_authentication, R.id.tv_problem, R.id.tv_serviceCall, R.id.tv_after_the_process, R.id.icon_user_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.icon_user_setting://个人设置
                UserInfoActivity.toActivity(getContext(), LoginInfo.TENANT);
                break;
            case R.id.tv_message://我的消息
                if (infoBean != null && infoBean.getIs_identity() == 1) {
                    MessageActivity.toActivity(getActivity());
                } else if (infoBean != null && infoBean.getIs_identity() == 0) {
                    showRenZhengTips();
                }
                break;
            case R.id.tv_authentication://实名认证
                if (infoBean != null && infoBean.getIs_identity() == 1) {
                    RenZhengInfoActivity.toActivity(mActivity);
                } else if (infoBean != null && infoBean.getIs_identity() == 0) {
                    CertificationActivity.toActivity(mActivity, 1);
                }
                break;
            case R.id.tv_problem://常见问题
                if (infoBean != null && infoBean.getIs_identity() == 1) {
                    ProblemActivity.toActivity(getActivity());
                } else if (infoBean != null && infoBean.getIs_identity() == 0) {
                    showRenZhengTips();
                }
//                WebviewActivity.toActivity(getContext(), WebType.WEB_PROBLEM);
                break;
            case R.id.tv_serviceCall://联系客服
                callService();
                break;
            case R.id.tv_after_the_process://售后流程
//                WebviewActivity.toActivity(getContext(), WebType.WEB_ABOUT);
                if (infoBean != null && infoBean.getIs_identity() == 1) {
                    AfterProcessActivity.toActivity(getContext(), ((TenantMainActivity) getActivity()).mAppConfigBean.getDisclaimer());
                } else if (infoBean != null && infoBean.getIs_identity() == 0) {
                    showRenZhengTips();
                }
                break;
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
                            PhoneUtil.call(dataInfo.data().getTel(), getContext());
                        } else {
                            showToast(dataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    private void getShare() {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().getShare()
                .compose(RxUtil.<DataInfo<ShareBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ShareBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                    }

                    @Override
                    public void onNext(DataInfo<ShareBean> shareBeanDataInfo) {

                        dismiss();
                        if (shareBeanDataInfo.success()) {
                            ShareDialog.shareData(mActivity, shareBeanDataInfo.data().title, shareBeanDataInfo.data().content, shareBeanDataInfo.data().imageUrl, shareBeanDataInfo.data().url);
                        } else {
                            showToast(shareBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
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
