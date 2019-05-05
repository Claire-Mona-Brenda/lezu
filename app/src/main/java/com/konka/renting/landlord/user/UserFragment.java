package com.konka.renting.landlord.user;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LandlordUserBean;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.ServiceTelBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.MainActivity;
import com.konka.renting.landlord.home.bill.BillListActivity;
import com.konka.renting.landlord.user.collection.MyCollectionActivity;
import com.konka.renting.landlord.user.message.MessageActivity;
import com.konka.renting.landlord.user.other.AfterProcessActivity;
import com.konka.renting.landlord.user.tenant.HouseTenantListActivity;
import com.konka.renting.landlord.user.userinfo.FaceDectectEvent;
import com.konka.renting.landlord.user.userinfo.UpdateEvent;
import com.konka.renting.landlord.user.userinfo.UserInfoActivity;
import com.konka.renting.landlord.user.withdrawcash.RechargeActivity;
import com.konka.renting.landlord.user.withdrawcash.RechargeEvent;
import com.konka.renting.landlord.user.withdrawcash.WithdrawEvent;
import com.konka.renting.landlord.user.withdrawcash.WithdrawcashActivity;
import com.konka.renting.login.ForgetPasswordActivity;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
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
    private LandlordUserBean userInfoBean;

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
        init();
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        ViewGroup viewGroup = (ViewGroup) mIconUserPhoto.getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
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
                                if (userInfoBeanDataInfo.data().getHeadimgurl() != null)
                                    if (!userInfoBeanDataInfo.data().getThumb_headimgurl().isEmpty()) {
                                        Picasso.get().load(userInfoBeanDataInfo.data().getThumb_headimgurl()).
                                                placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                    } else {
                                        Picasso.get().load(R.mipmap.touxiang).
                                                placeholder(R.mipmap.touxiang).error(R.mipmap.touxiang).into(mIconUserPhoto);
                                    }
                                mTvMoney.setText((int)Float.parseFloat(userInfoBeanDataInfo.data().getBalance())+"");
                            }
                            userInfoBean = userInfoBeanDataInfo.data();
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

    @OnClick({R.id.tv_bill, R.id.tv_collection, R.id.tv_message, R.id.tv_setting,
            R.id.tv_withdraw, R.id.tv_recharge, R.id.tv_call_us,
            R.id.tv_rent_people, R.id.tv_change_pwd, R.id.tv_problem, R.id.tv_after_the_process, R.id.tv_login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_withdraw://提现
                WithdrawcashActivity.toActivity(getContext(), userInfoBean.getBalance());
                break;
            case R.id.tv_recharge://充值
                RechargeActivity.toActivity(getContext());
                break;
            case R.id.tv_rent_people://租客
                HouseTenantListActivity.toActivity(getContext());
                break;
            case R.id.tv_bill://账单
                BillListActivity.toActivity(getContext());
                break;
            case R.id.tv_collection://催租
                MyCollectionActivity.toActivity(getContext());
                break;
            case R.id.tv_message://消息
                MessageActivity.toActivity(getContext());
                break;
            case R.id.tv_setting://个人设置
                UserInfoActivity.toActivity(getContext());
                break;
            case R.id.tv_change_pwd://修改密码
                ForgetPasswordActivity.toActivity(getContext(), LoginInfo.LANDLORD, LoginUserBean.getInstance().getMobile());
                break;
            case R.id.tv_call_us://联系客服
                callService();
                break;
            case R.id.tv_problem://常见问题
//                WebviewActivity.toActivity(getActivity(), WebType.WEB_PROBLEM);
                ProblemActivity.toActivity(getActivity());
                break;
            case R.id.tv_after_the_process://售后流程
//                WebviewActivity.toActivity(getActivity(), WebType.WEB_ABOUT);
                AfterProcessActivity.toActivity(getContext(),((MainActivity)getActivity()).mAppConfigBean.getDisclaimer());
                break;
            case R.id.tv_login_out://退出登录

                new AlertDialog.Builder(getActivity()).setTitle(R.string.login_out).setMessage("是否退出登录")
                        .setPositiveButton(R.string.warn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                landmoreLoginOut();
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

    private void landmoreLoginOut() {
        LoginUserBean.getInstance().reset();
        LoginUserBean.getInstance().save();
        LoginNewActivity.toLandlordActivity(getContext());
        getActivity().finish();
    }
}
