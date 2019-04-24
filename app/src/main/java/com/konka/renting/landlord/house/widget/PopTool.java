package com.konka.renting.landlord.house.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopTool {
	 Dialog d = null;
	  public  void showPopupWindow(Context c,String[]s,final IPopBack popBack) {
		   final PopupWindow popupWindow = new PopupWindow(c);
//ScrollView scrollView=new ScrollView(c);

	        // 一个自定义的布局，作为显示的内容
	      LinearLayout view=new LinearLayout(c);
	      view.setOrientation(LinearLayout.VERTICAL);
	      for(int i=0;i<s.length;i++){
	    	  final String bas=s[i];
	    	  Button b=new Button(c);
	    	  b.setText(s[i]);
	    	  b.setTextColor(Color.WHITE);
	    	  b.setWidth(300);
	    	  b.setHeight(100);
	    	  b.setBackgroundColor(Color.argb(50, 0, 0, 0));
	    	  b.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					popBack.callBack(bas);
				close();
//					popupWindow.dismiss();
				}
			});
	    	  view.addView(b);
	      }
	      d=    new AlertDialog.Builder(c).setView(view).show();
//	      scrollView.addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	     
//	        popupWindow.setOutsideTouchable(true);
//	        popupWindow.setContentView(view);
//	        popupWindow.setWidth(200);
//	        popupWindow.setHeight(500);
//	        
//
//	        // 设置好参数之后再show
//	        popupWindow.showAsDropDown(baseview);

	    }
	  private void close(){
			d.cancel();
	  }
}
