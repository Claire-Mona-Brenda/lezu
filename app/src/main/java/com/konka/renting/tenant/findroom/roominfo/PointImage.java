package com.konka.renting.tenant.findroom.roominfo;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.konka.renting.R;

public class PointImage extends ImageView {
	public PointImage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PointImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PointImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setChoosed(boolean b) {
		if(b){
			setImageResource(R.mipmap.dian2);
		}else{
			setImageResource(R.mipmap.dian1);
		}
	}
}
