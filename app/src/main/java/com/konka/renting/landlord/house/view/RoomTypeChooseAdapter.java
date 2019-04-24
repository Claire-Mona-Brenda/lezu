package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.RoomTypeBean;

import java.util.List;

public class RoomTypeChooseAdapter extends BaseAdapter {
    Context context;
    List<RoomTypeBean> list;
    LayoutInflater layoutInflater;
    int curPosition=-1;

    public RoomTypeChooseAdapter(Context context, List<RoomTypeBean> list) {
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

        convertView = layoutInflater.inflate(
                R.layout.lib_auto_dictionary_choose_adapter, null);
        if (position==curPosition) {
            convertView.setBackgroundColor(Color.YELLOW);
        } else {
            convertView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        TextView name = (TextView) convertView.findViewById(R.id.dir_name);

        name.setText(list.get(position).getName());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
