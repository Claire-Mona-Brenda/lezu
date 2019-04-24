package com.konka.renting.landlord.house.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.IPopBack;
import com.konka.renting.landlord.house.widget.PopTool;
import com.konka.renting.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PicstandardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	List<MissionEnity> list;
	Context context;
	MissionitemClick mic;
	IPopBack iPopBack;
	int gray_color=Color.parseColor("#999b9a");
	public PicstandardAdapter(Context context, List<MissionEnity> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	public void setIPOP(IPopBack ipop) {
		iPopBack=ipop;
	}


		@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int arg1) {
		// TODO Auto-generated method stub
		final int index = arg1;
		MVHolder arg0=(MVHolder) holder;


			arg0.del.setVisibility(View.GONE);
			MissionEnity missionEnity=list.get(arg1);
			//if(missionEnity.type==MissionEnity.TYPE_NORMAL){
			arg0.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(mic!=null){
						mic.missionItemClick(index);
					}
				}
			});
			arg0.add.setVisibility(View.GONE);
			arg0.del.setVisibility(View.GONE);
			arg0.content_img.setVisibility(View.VISIBLE);
			Picasso.get().load(missionEnity.imgpath).transform(new RoundedTransformation(15,0)).into(arg0.content_img);



	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub

				View v = LayoutInflater.from(context).inflate(
						R.layout.lib_picstantard_adapter, arg0,false);
				return new MVHolder(v);


	}

	public class MVHolder extends RecyclerView.ViewHolder {


		ImageView add;
		ImageView del;
		ImageView content_img;
		public MVHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub

			add=(ImageView)itemView.findViewById(R.id.content_add);
			content_img=(ImageView)itemView.findViewById(R.id.content_img);
			del=(ImageView)itemView.findViewById(R.id.del);
		}

	}

	
	public interface MissionitemClick{
		public void missionItemClick(int position);
	}
	public void setItemClickListener(MissionitemClick mic){
		this.mic = mic;
	}
	public void addImg(){
		String[] s2 = { "相机", "图库" };
		new PopTool().showPopupWindow(context, s2, iPopBack);
	}

}
