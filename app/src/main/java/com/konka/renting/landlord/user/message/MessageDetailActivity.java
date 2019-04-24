package com.konka.renting.landlord.user.message;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.MessageListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class MessageDetailActivity extends BaseActivity {

    @BindView(R.id.tv_message_title)
    TextView mTvMessageTitle;
    @BindView(R.id.iv_message)
    ImageView mIvMessage;
    @BindView(R.id.tv_message_content)
    TextView mTvMessageContent;
    private String id;

    private boolean mIsLandlord;


    public static void toActivity(Context context, String messageId) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("id", messageId);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_detail;
    }

    @Override
    public void init() {
        mIsLandlord = LoginInfo.isLandlord(LoginUserBean.getInstance().getType());
        setTitleText(R.string.message_detail);
        id = getIntent().getStringExtra("id");
        initData();
    }

    private void initData() {

        showLoadingDialog();
        Subscription subscription =
//                mIsLandlord ?
//                SecondRetrofitHelper.getInstance().getMessageDetail(id) :
//                RetrofitHelper.getInstance().getRenterMessageDetail(id))
                SecondRetrofitHelper.getInstance().getMessageDetail(id)
                        .compose(RxUtil.<DataInfo<MessageListBean>>rxSchedulerHelper())
                        .subscribe(new CommonSubscriber<DataInfo<MessageListBean>>() {
                            @Override
                            public void onError(Throwable e) {
                                dismiss();
                                doFailed();
                                showError(e.getMessage());
                            }

                            @Override
                            public void onNext(DataInfo<MessageListBean> messageDetailBeanDataInfo) {
                                dismiss();
                                if (messageDetailBeanDataInfo.success()) {
                                    if (messageDetailBeanDataInfo.data() != null) {
                                        mTvMessageTitle.setText(messageDetailBeanDataInfo.data().getTitle());
                                        if (messageDetailBeanDataInfo.data().getImage() != null)
                                            if (!messageDetailBeanDataInfo.data().getImage().equals(""))
                                                Picasso.get().load(messageDetailBeanDataInfo.data().getImage()).into(mIvMessage);
                                        mTvMessageContent.setText(messageDetailBeanDataInfo.data().getContent());
                                    }
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
