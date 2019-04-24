package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.konka.renting.R;
import com.konka.renting.utils.Util;


public class AutoGridView extends LinearLayout implements OnClickListener {
	Context context;
	LinearLayout parent;
	int col=1;
	AutoItemClick autoItemClick;
	AutoAdapter adapter;
	int dheight;
	public AutoGridView(Context context) {
		super(context);
		this.context = context;
		init();
		// TODO Auto-generated constructor stub
	}

	public AutoGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
		// TODO Auto-generated constructor stub
	}

	public AutoGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		this.context = context;
		init();
		// init(attrs);

	}

	private void init() {
		LayoutInflater.from(context).inflate(R.layout.autogrid, this, true);
		parent = (LinearLayout) findViewById(R.id.parent);

	}
	public void setAdapter(AutoAdapter adapter){
		this.adapter=adapter;
		creatView(adapter, col);
	}
	public void setCol(int col2){
		this.col = col2;
	}
	public void clear(){
		parent.removeAllViews();
	}
	public void setDiverHeight(int dheight){
		this.dheight=dheight;
	}
	private void creatView(AutoAdapter adapter, int col) {
		LayoutParams lp = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		lp.leftMargin = Util.dip2px(context, 4);
		
		int size = adapter.getConunt();
		int row;
		if (size % col == 0) {
			row = size / col;
		} else {
			row = size / col + 1;
		}
		if(col>1){
			for (int i = 0; i < row; i++) {
				LinearLayout lay = new LinearLayout(context);
				lay.setOrientation(LinearLayout.HORIZONTAL);
				for (int j = 0; j < col; j++) {
					if (j == row - 1) {
						lp = new LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						lp.weight = 1;
						lp.topMargin= Util.dip2px(context, 1);
						lp.leftMargin = Util.dip2px(context, 1);
					} else if (j == 0) {
						lp = new LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						lp.weight = 1;
						lp.topMargin= Util.dip2px(context, 1);
						lp.leftMargin = Util.dip2px(context, 1);
					} else {

						lp = new LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						lp.weight = 1;
						lp.topMargin= Util.dip2px(context, 1);
						lp.leftMargin = Util.dip2px(context, 1);
					}
					if(i * col + j>=size){
						return;
					}else{
						View v = adapter.getView(i * col + j);
						v.setOnClickListener(this);
						v.setTag(i * col + j);
						lay.addView(v, lp);
					}
					

				}
//				List <Integer>l=new ArrayList<Integer>();
//				for(int m=0;i<lay.getChildCount();i++){
//					l.add(lay.getChildAt(m).getMeasuredHeight());
//					
//				}
//				Collections.sort(l);
//				for(int m=0;i<lay.getChildCount();i++){
//					lay.getChildAt(m).getLayoutParams().height=l.get(l.size()-1);
//					
//				}
				parent.addView(lay, lp);

			}
		}else{
			for (int i = 0; i < size; i++) {
				LinearLayout lay = new LinearLayout(context);
				lay.setOrientation(LinearLayout.HORIZONTAL);
				
						lp = new LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						if(dheight>0){
							lp.topMargin=dheight;
						}
						
//						lp.weight = 1;
//						lp.topMargin= Util.dip2px(context, 1);
//						lp.leftMargin = Util.dip2px(context, 1);
		
					View v = adapter.getView(i);
					v.setOnClickListener(this);
					v.setTag(i);
					lay.addView(v, lp);
					parent.addView(lay, lp);
				}
				

			}
		}
		

	

	public void setOnItemClick(AutoItemClick autoItemClick) {
		this.autoItemClick = autoItemClick;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public interface AutoItemClick {
		public void autoItemClick(View par, int posi);
	}

	@Override
	public void onClick(View arg0) {
		int pos = (Integer) arg0.getTag();
		if (autoItemClick != null) {
			autoItemClick.autoItemClick(AutoGridView.this, pos);
		}

	}
}
