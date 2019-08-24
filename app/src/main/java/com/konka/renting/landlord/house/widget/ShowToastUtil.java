package com.konka.renting.landlord.house.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.konka.renting.R;


/**
 * 提示工具类
 * 
 * @author 杨瑞鹏
 * @date 2012-11-16 上午10:22:17
 */
public class ShowToastUtil {

	private static ProgressDialog mProgressDialog;

	//
	public static void showSuccessToast(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		LayoutInflater viewInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = viewInflater.inflate(R.layout.lib_toast_ok, null);
		TextView tv = (TextView) toastView.findViewById(R.id.successmsg);
		tv.setText(msg);
		toast.setView(toastView);
		toast.setGravity(Gravity.CENTER, 0, -200);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}


	public static void showWarningToast(Context context, String warningMsg) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		LayoutInflater viewInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = viewInflater.inflate(R.layout.lib_toast_problem, null);
		TextView tv = toastView.findViewById(R.id.warningmsg);
		tv.setText(warningMsg);
		toast.setView(toastView);
		toast.setGravity(Gravity.TOP, 0, 300);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	
	public static void showWarningLongToast(Context context, String warningMsg) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		LayoutInflater viewInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = viewInflater.inflate(R.layout.lib_toast_problem, null);
		TextView tv =  toastView.findViewById(R.id.warningmsg);
		tv.setText(warningMsg);
		toast.setView(toastView);
		toast.setGravity(Gravity.TOP, 0, 300);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
	
	public static void showNormalToast(Context context, String warningMsg){
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		LayoutInflater viewInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = viewInflater.inflate(R.layout.lib_toast_problem, null);
		TextView tv = (TextView) toastView.findViewById(R.id.warningmsg);
		ImageView iv = (ImageView) toastView.findViewById(R.id.warning_img);
		iv.setVisibility(View.GONE);
		tv.setText(warningMsg);
		toast.setView(toastView);
		toast.setGravity(Gravity.CENTER, 0,0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	public static  void sendAlertDiaLog(Context c,String s){
		AlertDialog dialog=new AlertDialog.Builder(c).setTitle("提示")
				.setMessage(s).setNegativeButton("确定", null).create();
				dialog.show();
				dialog.getWindow().setLayout(400, 350);
	}
	public  synchronized  static void showLoadingDialog(Context c) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(c);
			mProgressDialog.setMessage(c.getString(R.string.loading));
			mProgressDialog.show();
		}

	}

	public synchronized static void dismiss() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog=null;
	}
}
