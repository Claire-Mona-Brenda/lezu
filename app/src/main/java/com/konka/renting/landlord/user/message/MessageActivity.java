package com.konka.renting.landlord.user.message;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.MessageBean;
import com.konka.renting.bean.MessageListBean;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.list_message)
    ListView mListMessage;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefresh;
    private List<MessageListBean> mData = new ArrayList<>();
    private int page = 1;
    private CommonAdapter<MessageListBean> mAdapter;

    private boolean mIsLandlord;


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public void init() {

        setTitleText(R.string.message);
        initList();
        mIsLandlord = LoginInfo.isLandlord(LoginUserBean.getInstance().getType());
        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (mIsLandlord)
                    initData();
                else
                    mRefresh.finishRefresh();
            }
        });
        mRefresh.autoRefresh();
    }

    private void initList() {
        mAdapter = new CommonAdapter<MessageListBean>(this, mData, R.layout.item_message) {
            @Override
            public void convert(ViewHolder viewHolder, MessageListBean s, int i) {
                viewHolder.setText(R.id.tv_message_content, s.getIntro());
                viewHolder.setText(R.id.tv_message_time, s.getTime());
                viewHolder.setText(R.id.tv_message_title, s.getTitle());
                viewHolder.setVisible(R.id.adapter_message_view_point, s.getIs_read() == 0);
                notifyDataSetChanged();
            }
        };
        mListMessage.setAdapter(mAdapter);
        mListMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageDetailActivity.toActivity(MessageActivity.this, mData.get(i).getId() + "");
            }
        });
    }

    private void initData() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getMessageList(page + "")
                .compose(RxUtil.<DataInfo<PageDataBean<MessageListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<MessageListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        mRefresh.finishRefresh();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<MessageListBean>> dataInfo) {
                        mRefresh.finishRefresh();
                        if (dataInfo.success()) {
                            mData.clear();
                            mData.addAll(dataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast(dataInfo.msg());
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
