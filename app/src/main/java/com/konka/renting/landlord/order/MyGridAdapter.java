package com.konka.renting.landlord.order;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.konka.renting.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaite on 2018/4/8.
 */

public class MyGridAdapter extends BaseAdapter {
    List<Uri> mData = new ArrayList<>();
    Context context;

    public MyGridAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Uri> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = mData == null ? 1 : mData.size() + 1;
        if (count > 6) {
            return 6;
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.refunds_grid_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.ivGrid = view.findViewById(R.id.grid_image);
            viewHolder.ivDelete = view.findViewById(R.id.icon_close);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (i < mData.size()) {
            Picasso.get().load(mData.get(i)).into(viewHolder.ivGrid);
            viewHolder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivDelete.setVisibility(View.GONE);
            //viewHolder.ivGrid.setImageResource(R.mipmap.lib_house_add);
            Picasso.get().load(R.mipmap.lib_house_add).into(viewHolder.ivGrid);
        }
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.deleteImage(i);
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView ivGrid, ivDelete;
    }
    public interface SetDeleteListener{
        void deleteImage(int posistion);
    }
    SetDeleteListener listener;
    public void setDeleteListener(SetDeleteListener listener){
        this.listener = listener;
    }
}
