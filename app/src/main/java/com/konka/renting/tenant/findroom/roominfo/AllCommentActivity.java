package com.konka.renting.tenant.findroom.roominfo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.CommentTemp;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jbl on 2018/5/30.
 */

public class AllCommentActivity extends Activity implements OnLoadmoreListener, OnRefreshListener,View.OnClickListener {
    RecyclerView listView1;
    SmartRefreshLayout refresh;
    String  roomInfo;
    int p=1;
    List<RoomInfo.Comment> list;
    AllCommentAdapter allCommentAdapter;
    private CompositeSubscription mCompositeSubscription;

    public static void toActivity(Context ctx, String id){
            Intent intent=new Intent(ctx,AllCommentActivity.class);
            intent.putExtra("info",id);
            ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.lib_all_comment_activity);
        listView1=findViewById(R.id.listView1);
        refresh=findViewById(R.id.refresh);
        roomInfo=getIntent().getStringExtra("info");
        refresh.setOnLoadmoreListener(this);
        refresh.setOnRefreshListener(this);
        refresh.setEnableLoadmore(false);
        list=new ArrayList<RoomInfo.Comment>();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        listView1.setLayoutManager(lm);
        allCommentAdapter=new AllCommentAdapter(AllCommentActivity.this,list);
        listView1.setAdapter(allCommentAdapter);
//        getData();
        refresh.autoRefresh();
    }
    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
    public void getData(){
        rx.Observable<DataInfo<CommentTemp>> observable = null;
        observable = RetrofitHelper.getInstance().getRoomCommentList(roomInfo,p);
        //    ShowToastUtil.showLoadingDialog(context);
        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<CommentTemp>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<CommentTemp>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<CommentTemp> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            list.addAll(homeInfoDataInfo.data().commentList);

                            allCommentAdapter.notifyDataSetChanged();
                        } else {

//                            showToast(homeInfoDataInfo.msg());
                        }
                        refresh.finishRefresh();
//                        ShowToastUtil.dismiss();
                    }
                }));
        addSubscrebe(subscription);
    }
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        list.clear();
        getData();

    }

    @Override
    public void onClick(View view) {
        this.finish();
    }
}
