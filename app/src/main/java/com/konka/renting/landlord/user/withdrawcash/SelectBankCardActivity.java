package com.konka.renting.landlord.user.withdrawcash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.MyBankBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, SelectBankCardActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_bank_card;
    }

    @Override
    public void init() {

        setTitleText(R.string.select_bank_card);
        setRightText(R.string.add_bank);
        initData();
        initList();
        addRxBusSubscribe(AddBankEvent.class, new Action1<AddBankEvent>() {
            @Override
            public void call(AddBankEvent addBankEvent) {
                initData();
            }
        });
    }

    private void initList() {
        //str=str.Substring(str.Length-i);
        mAdapter = new CommonAdapter<MyBankBean>(this, mData, R.layout.item_bank_card) {
            @Override
            public void convert(ViewHolder viewHolder, MyBankBean myBankBean) {
                if (myBankBean.number.length() > 0) {
                    String number = myBankBean.number;
                    String band = "(" + number.substring(number.length() - 4) + ")";
                    viewHolder.setText(R.id.tv_bank_card, myBankBean.bank_card + band);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.getDefault().post(new SelectCardEvent(myBankBean.id, myBankBean.number, myBankBean.bank_card));
                        finish();
                    }
                });
            }

        };
        mRvCard.setLayoutManager(new LinearLayoutManager(this));
        mRvCard.setAdapter(mAdapter);
    }

    private void initData() {
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().getBankCard()
                .compose(RxUtil.<DataInfo<ListInfo<MyBankBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ListInfo<MyBankBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<ListInfo<MyBankBean>> listInfoDataInfo) {

                        dismiss();
                        if (listInfoDataInfo.success()) {
                            mData.addAll(listInfoDataInfo.data().lists);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast(listInfoDataInfo.msg());
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

}
