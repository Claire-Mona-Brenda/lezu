package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.landlord.house.widget.CircleImageView;
import com.konka.renting.utils.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 超市热卖推荐
 * */
public class RoomComAdapter extends AutoAdapter {
	Context context;
	LayoutInflater layoutInflater;
	public List<RoomInfo.Comment> list;


	public RoomComAdapter(Context context, List<RoomInfo.Comment> list) {
		this.context = context;
		this.list = list;

	}

	@Override
	public int getConunt() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public View getView(int position) {
		// TODO Auto-generated method stub

		View convertView = LayoutInflater.from(context).inflate(
				R.layout.room_comment, null);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView date = (TextView) convertView.findViewById(R.id.date);
		TextView content = (TextView) convertView.findViewById(R.id.content);
		LinearLayout user_group = (LinearLayout) convertView.findViewById(R.id.user_group);
		CircleImageView tx = (CircleImageView) convertView.findViewById(R.id.tx);
		final RoomInfo.Comment h = list.get(position);
		user_group.removeAllViews();
		int wid = ScreenUtil.dp2Px(context, 100);
		name.setText(h.memberInfo.real_name);
		date.setText(h.time);
		content.setText(h.content);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wid, wid);
		lp.leftMargin=10;
		for (int i = 0; i < h.image_list.length; i++) {
			ImageView circleImageView = new ImageView(context);
			user_group.addView(circleImageView, lp);
			String path = h.image_list[i];
			Picasso.get().load(path).into(circleImageView);
		}
		Picasso.get().load(h.memberInfo.headimgurl).into(tx);

		return convertView;
	}

}
