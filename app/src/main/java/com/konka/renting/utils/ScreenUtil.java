package com.konka.renting.utils;

import android.content.Context;

public class ScreenUtil {	
	public static   int dp2Px(Context ctx,float dp){
		float scale=ctx.getResources().getDisplayMetrics().density;
		
		return (int) (dp*scale+0.5f);
				
	}
	public static   int dpScaleWidth(Context ctx,float dp){
		double scale=(double)ctx.getResources().getDisplayMetrics().widthPixels/(double)600;
		
		return (int) (dp*scale+0.5f);
		
	}
	public static   int dpScaleHeight(Context ctx,float dp){
		double scale=(double)ctx.getResources().getDisplayMetrics().heightPixels/(double)1024;
		
		return (int) (dp*scale+0.5f);
		
	}
	public static int px2Dp(Context ctx,float dp){
		float scale=ctx.getResources().getDisplayMetrics().density;
		
		return (int) (dp/scale+0.5f);
		
	}
	public static int getMaxAlign(Context ctx){
		float heightPixels=ctx.getResources().getDisplayMetrics().heightPixels;
		float widthPixels=ctx.getResources().getDisplayMetrics().widthPixels;
		
		return (int) Math.max(heightPixels, widthPixels);
		
	}
	public static int getScreenWidth(Context ctx){
		float widthPixels=ctx.getResources().getDisplayMetrics().widthPixels;
		
		return (int)widthPixels;
		
	}
	public static int getScreenHeight(Context ctx){
		float widthPixels=ctx.getResources().getDisplayMetrics().heightPixels;
		
		return (int)widthPixels;
		
	}
	public static int getScreenDpi(Context ctx){
		float widthPixels=ctx.getResources().getDisplayMetrics().densityDpi;
		
		return (int)widthPixels;
		
	}
}
