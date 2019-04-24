package com.konka.renting.landlord.house.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

@SuppressLint("NewApi")
public class LruBitmap {
@SuppressLint("NewApi")
private static LruCache<String, Bitmap>lru=new LruCache<String, Bitmap>(1024*1024*30){
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getByteCount();
	}
};
public static void  addBitmap(String key,Bitmap bitmap){
	if(key!=null&&bitmap!=null){
		lru.put(key, bitmap);
	}
	
}
public static Bitmap getBitmap(String key){
	return lru.get(key);
}
public static boolean hasBitMap(String key){
	Log.d("jia==","="+lru.size()/1024/1024);
	Bitmap b= lru.get(key);
	if(b==null){
		return false;
	}else{
		return true;
	}
}
public static int getCount(){
	return lru.size();
}
}
