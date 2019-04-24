package com.konka.renting.tenant.findroom;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.MapView;
import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.event.RefreshFindRoomEvent;
import com.konka.renting.tenant.findroom.map.RoomMapActivity;

import rx.functions.Action1;

/**
 */
public class FindRoomFragment extends BaseFragment {
    private MapView mapView;
    public static FindRoomFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FindRoomFragment fragment = new FindRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FindRoomFragment() {
        // Required empty public constructor
    }

    /**
     * 方法必须重写
     */
    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if(roomMapActivity!=null){
            roomMapActivity.getData();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    RoomMapActivity roomMapActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.lib_basicmap_activity, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
         roomMapActivity=new RoomMapActivity();
        roomMapActivity.initView(view,mapView);
        addRxBusSubscribe(RefreshFindRoomEvent.class, new Action1<RefreshFindRoomEvent>() {
            @Override
            public void call(RefreshFindRoomEvent locationRefreshEvent) {
                showLoadingDialog();
                roomMapActivity. getData();
                dismiss();
                //刷新经纬度
            }
        });
        return view;
    }

}
