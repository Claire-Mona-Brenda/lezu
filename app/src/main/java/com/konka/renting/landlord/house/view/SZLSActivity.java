package com.konka.renting.landlord.house.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RoomDes2;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.CircleImageView;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.ScreenUtil;
import com.squareup.picasso.Picasso;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jbl on 2018/4/26.
 */

public class SZLSActivity extends Activity implements View.OnClickListener{
    TextView xh;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    TextView total_price;
    Button edit;
    AppCompatImageView imageView;
    LinearLayout user_group;
    Button delRoom;
    Button start_end;
    Button tz;
    RoomInfo roomInfo;
    SZLSAdapter szlsAdapter;
    private CompositeSubscription mCompositeSubscription;
    int page=1;
    ListView recyclerview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_house_szls);
        roomInfo=getIntent().getParcelableExtra("info");
        recyclerview=(ListView)findViewById(R.id.recyclerview);
            xh=(TextView)findViewById(R.id.xh);
        total_price=(TextView)findViewById(R.id.total_price);
            status=(TextView)findViewById(R.id.status);
            adress=(TextView)findViewById(R.id.adress);
            price=(TextView)findViewById(R.id.price);
            status=(TextView)findViewById(R.id.status);
            dis_time=(TextView)findViewById(R.id.dis_time);
            people_num=(TextView)findViewById(R.id.people_num);
            edit=(Button)findViewById(R.id.edit);
            imageView=(AppCompatImageView)findViewById(R.id.img_house);
            user_group=(LinearLayout)findViewById(R.id.user_group);
            delRoom=findViewById(R.id.delroom);
            start_end=findViewById(R.id.start_end);
            tz=findViewById(R.id.tz);
//            bindData(roomInfo);
            getRefreshData();
    }

    public void bindData(RoomDes2 missionEnity){
        //if(missionEnity.type==MissionEnity.TYPE_NORMAL){
       xh.setText("房间号："+missionEnity.door_number);
        if(missionEnity.is_lease.equals("1")){
           status.setText("已租出");
        }else if(missionEnity.is_lease.equals("0")){
           status.setText("未出租");

        }
        if(missionEnity.total_price!=null){
            total_price.setText("￥"+missionEnity.total_price);
        }

       adress.setText(missionEnity.room_name);
       price.setText("￥"+missionEnity.housing_price);
       people_num.setText(missionEnity.memberCount);
       dis_time.setText("租期："+missionEnity.lease_start_time+"-"+missionEnity.lease_end_time);
        Picasso.get().load(missionEnity.image).into(imageView);
        user_group.removeAllViews();
        int wid= ScreenUtil.dp2Px(this,50);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(wid,wid);
        for(int i=0;i<missionEnity.memberList.size();i++){
            CircleImageView circleImageView=new CircleImageView(this);
           user_group.addView(circleImageView,lp);
            RoomInfo.Member member=(RoomInfo.Member) missionEnity.memberList.get(i);
            String path= member.headimgurl;
            Picasso.get().load(path).into(circleImageView);
        }

//        if(missionEnity.is_on_sale.equals("1")){
//           start_end.setText("停止出租");
//        }else if(missionEnity.is_on_sale.equals("0")){
//           start_end.setText("开始出租");
//        }
//		是否缴纳保证金0否1是
//        if(missionEnity.is_boon.equals("1")){
//           tz.setClickable(true);
//        }else if(missionEnity.is_boon.equals("0")){
//           tz.setClickable(false);
//        }
//		}else{
//			SplitMVHolder mSplitMVHolder=(SplitMVHolder) holder;
//			mSplitMVHolder.user.setText(missionEnity.time_type);
//		}

    }
    public void getRefreshData(){
        ShowToastUtil.showLoadingDialog(this);
        rx.Observable<DataInfo<RoomDes2>> observable = null;
        observable= RetrofitHelper.getInstance().getHistoryRoomList(roomInfo.id,page);
        ShowToastUtil.showLoadingDialog(SZLSActivity.this);
        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<RoomDes2>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomDes2>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia","");
                        e.printStackTrace();
                        ShowToastUtil.showWarningToast(SZLSActivity.this,"数据请求失败");
//
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onNext(DataInfo<RoomDes2> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            homeInfoDataInfo.data();
                            bindData(homeInfoDataInfo.data().roomInfo);
//                            total_price.setText( homeInfoDataInfo.data()..get(RoomInfo.TOTAL_PRICE).toString());
                            szlsAdapter = new SZLSAdapter(SZLSActivity.this,homeInfoDataInfo.data().lists);
                            recyclerview.setAdapter(szlsAdapter);
                        } else {
//                            showToast(homeInfoDataInfo.msg());
                        }
                        ShowToastUtil.dismiss();
                    }
                }));
        addSubscrebe(subscription);
    }
    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }
}
