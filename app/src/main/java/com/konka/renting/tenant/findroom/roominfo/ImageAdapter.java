package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends PagerAdapter {
	int mGalleryItemBackground;
	Context mContext;
	private List<Topic> list;
	LayoutInflater layoutInflater;

	public ImageAdapter(Context c, List<Topic> list) {
		mContext = c;
		this.list = list;
		layoutInflater = LayoutInflater.from(c);
	}

	

	class ViewHolder {
		ImageView imageview;
	}

	public int getCount() {
		return list.size();
	}

	public boolean isViewFromObject(View view, Object obj) {
		return view == (View) obj;
	}

	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewGroup) arg0).removeView((View) arg2);

	}

	public void finishUpdate(View arg0) {

	}

	@Override
	public Object instantiateItem(View arg0, final int arg1) {
		Topic topic=list.get(arg1);
		ImageView iv = new ImageView(mContext);
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		Picasso.get().load(topic.path).into(iv);
//		iv.setImageResource(R.drawable.m);
		((ViewPager) arg0).addView(iv, 0);
		return iv;
	}

	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	public Parcelable saveState() {

		return null;
	}

	public void startUpdate(View arg0) {

	}
}
