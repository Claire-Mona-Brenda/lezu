package com.konka.renting.tenant.payrent;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.bean.ServicePackageBean;

import java.util.ArrayList;
import java.util.List;


public class ServiceGridAdapter extends BaseAdapter {
    private List<ServicePackageBean.ListsBean> mdatas = new ArrayList<>();
    private LayoutInflater inflater;
    Context context;
    private int selector = 0;

    public ServiceGridAdapter(Context context) {

        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<ServicePackageBean.ListsBean> mdatas) {
        this.mdatas = mdatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mdatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mdatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.service_package_item, null);
            vh = new ViewHolder();
            vh.tvPackage = convertView.findViewById(R.id.flow_card_item);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvPackage.setText(mdatas.get(position).getText());
        if (selector == position) {
            listener.setConfirmText(selector);
            vh.tvPackage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_stroke_red));
            Resources resource = context.getResources();
            ColorStateList csl = resource.getColorStateList(R.color.color_ffffff);
            vh.tvPackage.setTextColor(csl);

        } else {
            vh.tvPackage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_stroke_white));
            Resources resource = context.getResources();
            ColorStateList csl = resource.getColorStateList(R.color.color_black);
            vh.tvPackage.setTextColor(csl);

        }
        return convertView;
    }

    class ViewHolder {
        public TextView tvPackage;
    }

    public void changeState(int posistion) {
        selector = posistion;
        notifyDataSetChanged();
    }
    Listener listener;
    public void setListener(Listener listener){
        this.listener = listener;
    }
    public interface Listener{
        void setConfirmText(int posistion);
    }
}
