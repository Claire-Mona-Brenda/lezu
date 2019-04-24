package com.konka.renting.tenant.payrent.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.PayOrder;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PayHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	List<PayOrder> list;
	Context context;
	MissionitemClick mic;
	int gray_color=Color.parseColor("#999b9a");
	public PayHistoryAdapter(Context context, List<PayOrder> list) {
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
		final PayOrder missionEnity=list.get(arg1);
		//if(missionEnity.type==MissionEnity.TYPE_NORMAL){
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

		Picasso.get().load(missionEnity.roomInfo.image).into(arg0.imageView);
		arg0.xh.setText("订单编号："+missionEnity.merge_order_no);
		arg0.data_status.setText(missionEnity.order_status);
		if(missionEnity.pay_time.equals("0")){
			arg0.data_status.setVisibility(View.INVISIBLE);
		}else{
			arg0.data_status.setText("支付时间："+missionEnity.pay_time);

		}


		arg0.adress.setText(missionEnity.roomInfo.address);
		arg0.price.setText("￥"+missionEnity.housing_price);
		arg0.status.setText(missionEnity.status_name);
		arg0.dis_time.setText(missionEnity.start_time+"-"+missionEnity.end_time);
		arg0.connect.setBackgroundResource(R.drawable.shape_house);
		arg0.connect.setTextColor(context.getResources().getColor(R.color.common_title));
		arg0.connect.setText("查看详情");
		arg0.cancel.setText("支付房租");
		arg0.cancel.setVisibility(View.INVISIBLE);
		if(missionEnity.order_status.equals("未付款")){
			arg0.cancel.setVisibility(View.VISIBLE);
		}else{
			arg0.cancel.setVisibility(View.VISIBLE);
		}
		arg0.cancel.setVisibility(View.INVISIBLE);
		arg0.cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(context,PaySubmitActivity.class);
				intent.putExtra("pay",missionEnity);
				context.startActivity(intent);
			}
		});
		arg0.connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(context,RoomRentDetailActivity.class);
				intent.putExtra("pay",missionEnity);
				intent.putExtra("notshow",true);
				context.startActivity(intent);
			}
		});
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub

			View v = LayoutInflater.from(context).inflate(
					R.layout.lib_payhouse_adapter, arg0,false);
			return new MVHolder(v);
//		}else{
//			View v = LayoutInflater.from(context).inflate(
//					R.layout.lib_missionl_split_adapter, arg0,false);
//			return new SplitMVHolder(v);
//		}
	
	}

	public class MVHolder extends RecyclerView.ViewHolder {

		TextView xh;
		TextView status;
		TextView adress;
		TextView price;
		TextView people_num;
		TextView dis_time;
		TextView data_status;
		AppCompatImageView imageView;
		Button cancel,connect;
		public MVHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			xh=(TextView)itemView.findViewById(R.id.xh);
			status=(TextView)itemView.findViewById(R.id.status);
			adress=(TextView)itemView.findViewById(R.id.adress);
			price=(TextView)itemView.findViewById(R.id.price);
			status=(TextView)itemView.findViewById(R.id.status);
			dis_time=(TextView)itemView.findViewById(R.id.dis_time);
			data_status=(TextView)itemView.findViewById(R.id.data_status);
			imageView=(AppCompatImageView)itemView.findViewById(R.id.img_house);
			cancel=(Button) itemView.findViewById(R.id.cancel);
			connect=(Button) itemView.findViewById(R.id.connect);
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
	
}
