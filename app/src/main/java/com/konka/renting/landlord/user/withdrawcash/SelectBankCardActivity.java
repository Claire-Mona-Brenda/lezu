package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.MyBankBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.event.DelBankEvent;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class SelectBankCardActivity extends BaseActivity {


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
    @BindView(R.id.activity_select_bank_card_rv_card)
    RecyclerView mRvCard;
    @BindView(R.id.activity_select_bank_card_srl_card)
    SmartRefreshLayout mSrlCard;

    private List<MyBankBean> mData = new ArrayList<>();
    private CommonAdapter<MyBankBean> mAdapter;

    CommonPopupWindow commonPopupWindow;

    String del_card_id;
    boolean isChoose;//是否选择银行卡返回

    int page = 1;

    public static void toActivity(Context context, boolean isChoose) {
        Intent intent = new Intent(context, SelectBankCardActivity.class);
        intent.putExtra("isChoose", isChoose);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_bank_card;
    }

    @Override
    public void init() {
        isChoose = getIntent().getBooleanExtra("isChoose", false);

        setTitleText(isChoose ? R.string.select_bank_card : R.string.my_bank_card_icon);
        setRightText(R.string.add_bank);
        getBankCardList(true);
        initList();
        addRxBusSubscribe(AddBankEvent.class, new Action1<AddBankEvent>() {
            @Override
            public void call(AddBankEvent addBankEvent) {
                mSrlCard.autoRefresh();
            }
        });
    }

    private void initList() {
        //str=str.Substring(str.Length-i);
        mAdapter = new CommonAdapter<MyBankBean>(this, mData, R.layout.item_bank_card) {
            @Override
            public void convert(ViewHolder viewHolder, MyBankBean myBankBean) {
                viewHolder.setText(R.id.tv_bank_card, myBankBean.getCard_no());
                viewHolder.setText(R.id.tv_bank_type, myBankBean.getBank_name());
                ImageView imgPic = viewHolder.getView(R.id.img_bank_icon);
                if (!TextUtils.isEmpty(myBankBean.getBank_image())) {
                    Picasso.get().load(myBankBean.getBank_image()).into(imgPic);
                } else {
                    Picasso.get().load(R.mipmap.bank_other).into(imgPic);
                }
                viewHolder.setOnClickListener(R.id.adapter_bank_card_tv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDelPopup(myBankBean.getCard_id() + "");
                    }
                });
                viewHolder.setOnClickListener(R.id.rl_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isChoose) {
                            RxBus.getDefault().post(new SelectCardEvent(myBankBean.getCard_id() + "", myBankBean.getCard_no(), myBankBean.getBank_name(), myBankBean.getBank_image()));
                            finish();
                        }
                    }
                });
            }

        };
        mRvCard.setLayoutManager(new LinearLayoutManager(this));
        mRvCard.setAdapter(mAdapter);

        mSrlCard.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getBankCardList(true);
            }
        });
        mSrlCard.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getBankCardList(false);
            }
        });
    }

    private void getBankCardList(boolean isResh) {
        if (isResh) {
            page = 1;
        } else {
            page++;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getBankCardList(page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<MyBankBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<MyBankBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        doFailed();
                        if (isResh) {
                            mSrlCard.finishRefresh();
                        } else {
                            mSrlCard.finishLoadmore();
                            page--;
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<MyBankBean>> listInfoDataInfo) {
                        if (isResh) {
                            mData.clear();
                            mSrlCard.finishRefresh();
                        } else {
                            mSrlCard.finishLoadmore();
                        }
                        if (listInfoDataInfo.success()) {
                            if (isResh) {
                                mData.clear();
                            }
                            mData.addAll(listInfoDataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (!isResh)
                                page--;
                            showToast(listInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    private void delBank(String card_id) {
        showLoadingDialog();
        Subscription subscription = SecondRetrofitHelper.getInstance().delBankBean(card_id)
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo info) {
                        dismiss();
                        if (info.success()) {
                            int size = mData.size();
                            for (int i = 0; i < size; i++) {
                                if (card_id.equals(mData.get(i).getCard_id() + "")) {
                                    mData.remove(i);
                                    break;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            RxBus.getDefault().post(new DelBankEvent(card_id));
                        } else {
                            showToast(info.msg());
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                AddBankCardActivity.toActivity(this);
                break;
        }
    }


    /**************************************************弹窗****************************************************************/


    /**
     *
     */
    private void showDelPopup(String card_id) {
        del_card_id = card_id;
        if (commonPopupWindow == null) {
            commonPopupWindow = new CommonPopupWindow.Builder(mActivity)
                    .setTitle(getString(R.string.tips))
                    .setContent(getString(R.string.tips_del_bank_card))
                    .setRightBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonPopupWindow.dismiss();
                            if (!TextUtils.isEmpty(del_card_id)) {
                                delBank(del_card_id);
                            }
                        }
                    })
                    .create();
        }

        showPopup(commonPopupWindow);

    }


    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mActivity.getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) mSrlCard.getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
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
