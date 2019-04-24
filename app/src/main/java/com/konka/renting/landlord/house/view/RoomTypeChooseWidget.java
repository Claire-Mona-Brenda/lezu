package com.konka.renting.landlord.house.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.AgentBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.RoomTypeBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RoomTypeChooseWidget implements View.OnClickListener,
        AdapterView.OnItemClickListener, View.OnTouchListener {

    TextView chose_widegt;
    Context context;
    List<RoomTypeBean> list;
    RoomTypeBean cur_item;
    RoomTypeChooseAdapter adapter;
    //	LBWindow jiaWindow;
    ListView listview;
    String type;
    Dialog dialog;
    ShowToastUtil showToastUtil;

    public static final  String  ROOM_TYPE="room_type";//代理商类型
    protected CompositeSubscription mCompositeSubscription;
    public RoomTypeChooseWidget(Context context, String type, TextView tar) {
        this.context = context;
        chose_widegt=tar;
        list=new ArrayList<>();
        this.type=type;
        showToastUtil=new ShowToastUtil();
        // TODO Auto-generated constructor stub
    }
    RoomTypeChooseWidget.ItemSelect itemSelect;
    RoomTypeChooseWidget.ItemSelect2 itemSelect2;
    public RoomTypeChooseWidget setItemSelect(RoomTypeChooseWidget.ItemSelect itemSelect){
        this.itemSelect=itemSelect;
        return this;
    }
    public RoomTypeChooseWidget setItemSelect2(RoomTypeChooseWidget.ItemSelect2 itemSelect){
        this.itemSelect2=itemSelect;
        return this;
    }
    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
    //
    public void setText(String s) {
        if(s.equals("")){
            chose_widegt.setText("");
        }else{
            chose_widegt.setText(s);
        }


    }

    public void getData(){

        rx.Observable<DataInfo<List<RoomTypeBean>>> observable = null;
        if (type.equals(ROOM_TYPE)){
            observable=SecondRetrofitHelper.getInstance().getRoomTypeList();
        }

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<List<RoomTypeBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<RoomTypeBean>>>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataInfo<List<RoomTypeBean>> dataInfo) {
                        if (dataInfo.success()) {
                            bindData(dataInfo.data());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    public void bindData(List<RoomTypeBean> beanList){
        list=beanList;
        adapter = new RoomTypeChooseAdapter(context, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        //chose_widegt.setText(cur_item.content);
//			value=cur_item.value;

//		jiaWindow.close();
        dialog.dismiss();
        if(iForAdd!=null){
            iForAdd.remove();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        cur_item = list.get(arg2);
        adapter.curPosition=arg2;
        adapter.notifyDataSetChanged();
        //	chose_widegt.setText(cur_item.content);
//			value=cur_item.value;

//		jiaWindow.close();
        if(itemSelect!=null){
            itemSelect.itemSelect(cur_item.getRoom_type_id());
        }
        if(itemSelect2!=null){
            itemSelect2.itemSelect2(cur_item.getName());
        }
        dialog.dismiss();
        if(chose_widegt!=null){
            chose_widegt.setText(cur_item.getName());
            chose_widegt.setTag(cur_item.getRoom_type_id());
        }



    }

    public void showPopWindow() {
        dialog=new Dialog(context);
//		 mPopupWindow = new PopupWindow(context);
        View popview = LayoutInflater.from(context).inflate(
                R.layout.lib_auto_dictionary_choose, null);
        ImageView sure = (ImageView) popview.findViewById(R.id.dir_sure);
        TextView t=(TextView) popview.findViewById(R.id.dir_title);
//			t.setText("");

        sure.setOnClickListener(this);
        listview = (ListView) popview.findViewById(R.id.listview_dir);
        getData();
        listview.setOnItemClickListener(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(popview);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp=dialogWindow.getAttributes();
        lp.width=ScreenUtil.dp2Px(context, 220);
        lp.height=ScreenUtil.dp2Px(context, 340);
//		jiaWindow=new LBWindow(context);
//
//			jiaWindow.setWidth(ScreenUtil.dp2Px(context, 290));
//			jiaWindow.setHeight(ScreenUtil.dp2Px(context, 400));
//
//		jiaWindow.setView(popview);
//		jiaWindow.show();
        dialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("jia", "触摸");
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showPopWindow();
        }
        return true;
    }
    AutoChooseWidget.IForAdd iForAdd;
    public void setIfor(AutoChooseWidget.IForAdd ifor){
        iForAdd=ifor;
    }
    public interface  IForAdd{
        public void remove();
    }
    public interface ItemSelect{
        public void itemSelect(String type);
    }
    public interface ItemSelect2{
        public void itemSelect2(String type);
    }
}
