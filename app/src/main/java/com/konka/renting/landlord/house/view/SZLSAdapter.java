package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.landlord.house.entity.DicEntity;

import java.util.List;

public class SZLSAdapter extends BaseAdapter {
	Context context;
	List<RoomInfo.HouseSzHis> list;
	LayoutInflater layoutInflater;
	int curPosition=-1;

	public SZLSAdapter(Context context, List<RoomInfo.HouseSzHis> list) {
		this.list = list;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = layoutInflater.inflate(
					R.layout.lib_szls_adapter, null);
		}


//		if (position==curPosition) {
//			convertView.setBackgroundColor(Color.YELLOW);
//		} else {
//			convertView.setBackgroundColor(Color.argb(0, 0, 0, 0));
//		}
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView date = (TextView) convertView.findViewById(R.id.date);
		TextView total = (TextView) convertView.findViewById(R.id.total);
		RoomInfo.HouseSzHis houseSzHis=list.get(position);
		date.setText(houseSzHis.start_time+"-"+houseSzHis.end_time);
		name.setText(houseSzHis.nickname);
		total.setText("+￥"+houseSzHis.housing_price);
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
