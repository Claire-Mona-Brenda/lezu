package com.konka.renting.landlord.house.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.entity.DicEntity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.utils.ScreenUtil;
import com.yyh.window.LBWindow;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class AutoChooseWidget implements OnClickListener,
		OnItemClickListener, OnTouchListener {

	TextView chose_widegt;
	Context context;
	List<DicEntity.Content> list;
	DicEntity.Content cur_item;
	AutoChooseAdapter adapter;
//	LBWindow jiaWindow;
	ListView listview;
	String type;
	Dialog dialog;
	ShowToastUtil showToastUtil;
	public static final  String  HOUSE_TYPE="house_type";
	public static final  String  CITY="city";
	public static final  String  ROOM_TYPE="room_type";
	public static final  String  ROOM_ORI="room_ori";
	public static final  String  ROOM_CONFIG="room_config";
	public static final  String  PAY_TYPE="pay_type";//交租方式
	public static final  String  RENT_TYPE="rent_type";//租金类型
	public static final  String  LONG_SHORT="long_short";//租金类型

	protected CompositeSubscription mCompositeSubscription;
	public AutoChooseWidget(Context context, String type, TextView tar) {
		this.context = context;
		chose_widegt=tar;
		list=new ArrayList<>();
		this.type=type;
		showToastUtil=new ShowToastUtil();
		// TODO Auto-generated constructor stub
	}
	ItemSelect itemSelect;
	ItemSelect2 itemSelect2;
	public AutoChooseWidget setItemSelect(ItemSelect itemSelect){
			this.itemSelect=itemSelect;
			return this;
	}
	public AutoChooseWidget setItemSelect2(ItemSelect2 itemSelect){
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

		rx.Observable<DataInfo<DicEntity>> observable = null;
		if(type.equals(HOUSE_TYPE)){
			observable=RetrofitHelper.getInstance().getLeaseType();
		}else if(type.equals(CITY)){
			observable=RetrofitHelper.getInstance().getAllLetterCity();
		}else if(type.equals(ROOM_TYPE)){
			observable=RetrofitHelper.getInstance().getRoomType();
		}else if(type.equals(ROOM_ORI)){
			observable=RetrofitHelper.getInstance().getOrientation();
		}else if(type.equals(ROOM_CONFIG)){
			observable=RetrofitHelper.getInstance().getConfig();
		}else if(type.equals(RENT_TYPE)){
			observable=RetrofitHelper.getInstance().getRenterOrderLeaseType();
		}else if(type.equals(PAY_TYPE)){
			DicEntity dicEntity=new DicEntity();
			dicEntity.lists=new ArrayList<>();
			DicEntity.Content content=new DicEntity.Content();
			content.id="0";
			content.name="线下支付";
			dicEntity.lists.add(content);

			DicEntity.Content content2=new DicEntity.Content();
			content2.id="1";
			content2.name="线上支付";
			dicEntity.lists.add(content2);
			bindData(dicEntity);
			return;
		}else if(type.equals(LONG_SHORT)){
			DicEntity dicEntity=new DicEntity();
			dicEntity.lists=new ArrayList<>();
			DicEntity.Content content=new DicEntity.Content();
			content.id="1";
			content.name="短租";
			dicEntity.lists.add(content);

			DicEntity.Content content2=new DicEntity.Content();
			content2.id="2";
			content2.name="长租";
			dicEntity.lists.add(content2);
			bindData(dicEntity);
			return;
		}

		Subscription subscription = (observable
				.compose(RxUtil.<DataInfo<DicEntity>>rxSchedulerHelper())
				.subscribe(new CommonSubscriber<DataInfo<DicEntity>>() {
					@Override
					public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
						Log.d("jia","");
						e.printStackTrace();
					}

					@Override
					public void onNext(DataInfo<DicEntity> homeInfoDataInfo) {
//                        dismiss();

						if (homeInfoDataInfo.success()) {
							homeInfoDataInfo.data();
							bindData(homeInfoDataInfo.data());
						} else {
//                            showToast(homeInfoDataInfo.msg());
						}
					}
				}));
		addSubscrebe(subscription);
	}

	public void bindData(DicEntity dicEntity){
	list=dicEntity.lists;
		adapter = new AutoChooseAdapter(context, list);
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
			itemSelect.itemSelect(cur_item.id);
		}
		if(itemSelect2!=null){
			itemSelect2.itemSelect2(cur_item.name);
		}
		dialog.dismiss();
		if(chose_widegt!=null){
			chose_widegt.setText(cur_item.name);
			chose_widegt.setTag(cur_item.id);
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
	IForAdd iForAdd;
	public void setIfor(IForAdd ifor){
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
