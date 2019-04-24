package com.konka.renting.tenant.findroom.map;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.ScreenUtil;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jbl on 2018/3/11.
 */

public class RoomMapWindow implements AMap.InfoWindowAdapter {

    private Context context;
    TextView
            text1,
            text2,
            text3,
            text4,
            text5,
            text6,
            status;
    RoomInfo markInfo;
    RoomInfo roomInfo;
    Button button;
    private CompositeSubscription mCompositeSubscription;

    public RoomMapWindow(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        markInfo = (RoomInfo) marker.getObject();
        View view = LayoutInflater.from(context).inflate(R.layout.lib_house_map_window, null);
        setViewContent(marker, view);
        RelativeLayout linearLayout = (RelativeLayout) view.findViewById(R.id.lib_title_layout);
        linearLayout.getLayoutParams().width = (int) (ScreenUtil.getScreenWidth(context) * 0.8);
        final ImageView close = view.findViewById(R.id.window_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.hideInfoWindow();
            }
        });
         button = view.findViewById(R.id.btn_room_info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomInfo!=null) {
                    markInfo.info=roomInfo.info;
                    Intent intent = new Intent(context, RoomInfoActivity.class);
                    intent.putExtra("roominfo", markInfo);
                    context.startActivity(intent);
                }
            }
        });


        text1 = (TextView) view.findViewById(R.id.text1);
        text2 = (TextView) view.findViewById(R.id.text2);
        text3 = (TextView) view.findViewById(R.id.text3);
        text4 = (TextView) view.findViewById(R.id.text4);
        text5 = (TextView) view.findViewById(R.id.text5);
        text6 = (TextView) view.findViewById(R.id.text6);
        status = (TextView) view.findViewById(R.id.status);
        getData();
        return view;
    }
    public void getData(){
        rx.Observable<DataInfo<RoomInfo>> observable = null;
        observable= RetrofitHelper.getInstance().getRoomInfo(markInfo.id);
        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<RoomInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomInfo>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia","");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<RoomInfo> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            homeInfoDataInfo.data();
                            roomInfo=homeInfoDataInfo.data();
                            bindData(roomInfo.info);
                        } else {
//                            showToast(homeInfoDataInfo.msg());
                        }
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
    public void bindData(RoomInfo.RoomDescription info){
        text1.setText(info.room_type);
        text2.setText(info.measure_area+"平米");
        text3.setText(info.floor+"/"+info.total_floor+"层");
        text4.setText(info.address);
        text5.setText(info.landlord_real_name);
        text6.setText(info.landlord_tel);
        status.setText(info.getType());
        if(info.type.equals("1")){
            //短8fc320
            status.setBackgroundColor(context.getResources().getColor(R.color.color_short));
            button.setBackgroundResource(R.drawable.shape_short);
        } else if(info.type.equals("2")){

            //长
            status.setBackgroundColor(context.getResources().getColor(R.color.color_long));
            button.setBackgroundResource(R.drawable.shape_button);
        }
        if(info.is_lease.equals("1")){
            text1.setText("已出租");
            status.setText("已出租");
            status.setBackgroundColor(context.getResources().getColor(R.color.color_nor));
            button.setBackgroundResource(R.drawable.shape_nor);
        }
    }

    //这个方法根据自己的实体信息来进行相应控件的赋值
    private void setViewContent(Marker marker, View view) {

    }

    //提供了一个给默认信息窗口定制内容的方法。如果用自定义的布局，不用管这个方法。
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
