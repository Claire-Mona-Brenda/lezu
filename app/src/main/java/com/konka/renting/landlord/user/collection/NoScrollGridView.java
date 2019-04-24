package com.konka.renting.landlord.user.collection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 自定义重写 gridview
 * 原磊
 */
public class NoScrollGridView extends GridView {

	public NoScrollGridView(Context context) {
		super(context);
	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	// 禁止gridview 滑动
	@Override
	protected boolean dispatchHoverEvent(MotionEvent event) {
		if (event.getAction()== MotionEvent.ACTION_MOVE){
			return true;   //禁止Gridview进行滑动
		}
		return super.dispatchHoverEvent(event);
	}
}
