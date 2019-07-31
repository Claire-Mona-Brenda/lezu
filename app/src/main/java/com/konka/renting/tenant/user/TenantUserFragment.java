package com.konka.renting.tenant.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserBean;
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.ServiceTelBean;
import com.konka.renting.bean.ShareBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.ShareDialog;
import com.konka.renting.landlord.user.ProblemActivity;
import com.konka.renting.landlord.user.message.MessageActivity;
import com.konka.renting.landlord.user.other.AfterProcessActivity;
import com.konka.renting.landlord.user.userinfo.FaceDectectEvent;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.landlord.user.userinfo.UserInfoActivity;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.tenant.TenantMainActivity;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.PhoneUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.UIUtils;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class TenantUserFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_user_nickname)
    TextView mTvUserNickname;
    @BindView(R.id.icon_user_photo)
    CircleImageView mIconUserPhoto;
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
    @BindView(R.id.tv_quit)
    TextView tvQuit;
    private LandlordUserBean userinfoBean;

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
        initData();
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
                            LoginUserBean.getInstance().setIs_lodge_identity(dataInfo.data().getIs_identity() + "");
                            LoginUserBean.getInstance().setRealname(dataInfo.data().getReal_name());
                            LoginUserBean.getInstance().setIdentity(dataInfo.data().getIdentity());
                            LoginUserBean.getInstance().save();

                            if (dataInfo.data() != null) {
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

    @OnClick({R.id.tv_message, R.id.tv_authentication, R.id.tv_problem, R.id.tv_serviceCall, R.id.tv_after_the_process, R.id.tv_quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_message://我的消息
                MessageActivity.toActivity(getActivity());
                break;
            case R.id.tv_authentication://实名认证
                UserInfoActivity.toActivity(getContext());
                break;
            case R.id.tv_problem://常见问题
//                WebviewActivity.toActivity(getContext(), WebType.WEB_PROBLEM);
                ProblemActivity.toActivity(getActivity());
                break;
            case R.id.tv_serviceCall://联系客服
                callService();
                break;
            case R.id.tv_after_the_process://售后流程
//                WebviewActivity.toActivity(getContext(), WebType.WEB_ABOUT);
                AfterProcessActivity.toActivity(getContext(), ((TenantMainActivity) getActivity()).mAppConfigBean.getDisclaimer());
                break;
            case R.id.tv_quit://退出登录
                new AlertDialog.Builder(getActivity()).setTitle(R.string.login_out).setMessage("是否退出登录")
                        .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loginOut();
                            }
                        }).setNegativeButton(R.string.warn_cancel, null).create().show();
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

    private void loginOut() {
        LoginUserBean.getInstance().reset();
        LoginUserBean.getInstance().save();
        LoginNewActivity.toTenantActivity(getContext());
        getActivity().finish();
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


}
