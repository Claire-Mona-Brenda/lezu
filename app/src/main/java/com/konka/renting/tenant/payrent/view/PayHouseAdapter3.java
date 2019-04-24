package com.konka.renting.tenant.payrent.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.tenant.payrent.PayRentPresent;
import com.konka.renting.tenant.payrent.TFRequestCancel;
import com.konka.renting.tenant.payrent.TenantOrderDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PayHouseAdapter3 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	List<RenterOrderListBean> list;
	Context context;
	MissionitemClick mic;
	public PayHouseAdapter3(Context context, List<RenterOrderListBean> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return list.size();
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int arg1) {
		// TODO Auto-generated method stub
		final int index = arg1;
		final RenterOrderListBean missionEnity=list.get(arg1);
		MVHolder arg0=(MVHolder) holder;
		arg0.itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mic!=null){
					mic.missionItemClick(index);
				}
			}
		});

		if (!TextUtils.isEmpty(missionEnity.getThumb_image()))
			Picasso.get().load(missionEnity.getThumb_image()).into(arg0.imageView);
		else
			Picasso.get().load(R.mipmap.fangchan_jiazai).into(arg0.imageView);
		arg0.xh.setText("订单编号：" + missionEnity.getOrder_no());
		arg0.status.setText(getStringStatus(missionEnity.getStatus()));
		arg0.adress.setText(missionEnity.getRoom_name());
		String unit = missionEnity.getType() == 1 ? "/天" : "/月";
		arg0.price.setText("¥" + (int)Float.parseFloat(missionEnity.getHousing_price())+unit);
		int color;
		if (missionEnity.getType() == 1) {
			//短8fc320
			arg0.h_lx.setText("【短租】");
			color = context.getResources().getColor(R.color.color_short);

		} else if (missionEnity.getType() == 2) {
			arg0.h_lx.setText("【长租】");
			color = context.getResources().getColor(R.color.color_long);
			//长
		} else {
			color = context.getResources().getColor(R.color.color_nor);
		}
		arg0.h_lx.setTextColor(color);
		arg0.dis_time.setText(missionEnity.getStart_time());
		arg0.end_time.setText(missionEnity.getEnd_time());

		arg0.sqtf.setVisibility(View.GONE);
		arg0.connect.setVisibility(View.GONE);
		arg0.cancel.setVisibility(View.VISIBLE);

		arg0.cancel.setText("取消退房");

		arg0.cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {//取消退房
				new AlertDialog.Builder(context).setTitle("确定执行此操作！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						TFRequestCancel requestCancel=new TFRequestCancel(context);
						requestCancel.setRefresh(pRefresh);
						requestCancel.orderCancel(missionEnity.getOrder_id());
					}
				}).setNegativeButton("取消",null).show();
			}
		});
		arg0.mLinOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {//详情
				TenantOrderDetailActivity.toActivity(context,missionEnity.getOrder_id(),missionEnity.getType()+"");
			}
		});

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub

		View v = LayoutInflater.from(context).inflate(
				R.layout.lib_payhouse_adapter, arg0,false);
		return new MVHolder(v);

	}

	private String getStringStatus(int status){
		String s="";
		switch (status) {
			case 1:
				s = context.getString(R.string.order_status_1);
				break;
			case 2:
				s = context.getString(R.string.order_status_2);
				break;
			case 3:
				s = context.getString(R.string.order_status_3);
				break;
			case 4:
				s = context.getString(R.string.order_status_4);
				break;
			case 5:
				s = context.getString(R.string.order_status_5);
				break;
			case 6:
				s = context.getString(R.string.order_status_6);
				break;
			case 7:
				s = context.getString(R.string.order_status_7);
				break;
		}
		return s;
	}

	public class MVHolder extends RecyclerView.ViewHolder {

		TextView xh;
		TextView h_lx;
		TextView status;
		TextView adress;
		TextView price;
		TextView people_num;
		TextView dis_time;
		TextView end_time;
		AppCompatImageView imageView;
		LinearLayout mLinOrder;
		TextView cancel,connect,sqtf;
		public MVHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			h_lx=(TextView)itemView.findViewById(R.id.h_lx);
			xh=(TextView)itemView.findViewById(R.id.xh);
			status=(TextView)itemView.findViewById(R.id.status);
			adress=(TextView)itemView.findViewById(R.id.adress);
			price=(TextView)itemView.findViewById(R.id.price);
			status=(TextView)itemView.findViewById(R.id.status);
			dis_time=(TextView)itemView.findViewById(R.id.dis_time);
			end_time=(TextView)itemView.findViewById(R.id.tv_end_time);
			imageView=(AppCompatImageView)itemView.findViewById(R.id.img_house);
			cancel=(TextView) itemView.findViewById(R.id.cancel);
			connect=(TextView) itemView.findViewById(R.id.connect);
			sqtf=(TextView) itemView.findViewById(R.id.sqtf);
			mLinOrder = itemView.findViewById(R.id.lin_order);
		}

	}
	public class SplitMVHolder extends RecyclerView.ViewHolder {
		TextView user;

		public SplitMVHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			user=itemView.findViewById(R.id.textView1);

		}

	}

	public interface MissionitemClick{
		public void missionItemClick(int position);
	}
	public void setItemClickListener(MissionitemClick mic){
		this.mic = mic;
	}
	PayRentPresent.PRefresh pRefresh;
	public void setRefresh(PayRentPresent.PRefresh pRefresh){
		this.pRefresh=pRefresh;
	}
}
