package com.konka.renting.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import okio.Buffer;

public class Util {

	/**
	 * get hex string stands for byte[]
	 */
	protected static final String HEXES = "0123456789ABCDEF";
	private static final int SERVICE_TYPE_DIAMOND = 40;
	private static final int SERVICE_TYPE_READ_MONTH = 38;
	public static final int SERVICE_TYPE_SENIOR = 2;
	private static final int SERVICE_TYPE_NEW_REG = 999;
	private static final int SERVICE_TYPE_NONE = -1;
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
		}
		 
		public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
		}
	public static String getHexString(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	/**
	 * gen full file path based on given file name
	 * 
	 * @param fileName
	 * @param context
	 * @return
	 */
	public static String genFilePath(String fileName, Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getFilesDir().getAbsolutePath()).append("/")
				.append(fileName);
		return sb.toString();
	}

	/**
	 * get package version
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager pkgMgr = context.getPackageManager();
			PackageInfo info = pkgMgr.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// should never comes here
		}

		return null;
	}

	/**
	 * device id generated by the very fist device boot
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		return deviceId;
		// return Secure.getString(context.getContentResolver(),
		// Secure.ANDROID_ID);
	}

	/**
	 * get device IMEI, for cell phone only
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * convert bytes to UTF-8 string
	 * 
	 * @param data
	 * @return
	 */
	public static String toString(byte[] data)
			throws UnsupportedEncodingException {
		return new String(data, "UTF-8");
	}

	/**
	 * get system version
	 * 
	 * @return
	 */
	public static String getSysVer() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * get Screen Width
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * get Screen height
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * get clipBitmap
	 * 
	 * @param bitmap
	 * @param resizeWidth
	 * @param resizeHeight
	 * @return
	 */
	public static Bitmap clipBitmap(Bitmap bitmap, int resizeWidth,
			int resizeHeight) {
		Bitmap bmp = null;
		if (bitmap != null) {
			float resizeProportion = ((float) resizeWidth) / resizeHeight;
			int bmpWidth = bitmap.getWidth();
			int bmpHeight = bitmap.getHeight();
			float bmpProportion = ((float) bmpWidth) / bmpHeight;
			int clipWidth = 0;
			int clipHeight = 0;
			int offsetX = 0;
			int offsetY = 0;
			if (resizeProportion < bmpProportion) {
				clipWidth = (int) (((float) resizeWidth * bmpHeight) / resizeHeight);
				clipHeight = bmpHeight;
				offsetX = (bmpWidth - clipWidth) / 2;
			} else {
				clipHeight = (int) ((float) resizeHeight * bmpWidth / resizeWidth);
				clipWidth = bmpWidth;
				offsetY = (bmpHeight - clipHeight) / 2;
			}
			float scaleWidth = ((float) resizeWidth) / clipWidth;
			float scaleHeight = ((float) resizeHeight) / clipHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bmp = Bitmap.createBitmap(bitmap, offsetX, offsetY, clipWidth,
					clipHeight, matrix, true);
			if (null != bitmap && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}
		return bmp;

	}

	public static boolean isSystemMail(String uid) {
		// system mail uid:
		// $msg->from_uid != '3' && $msg->from_uid != '7508' && $msg->from_uid
		// != '2894338' && $msg->from_uid != '3286106' && $msg->from_uid !=
		// '20631499' && $msg->from_uid !='22910713')

		if (uid.equals("3")) {
			return true;
		} else if (uid.equals("7508")) {
			return true;
		} else if (uid.equals("2894338")) {
			return true;
		} else if (uid.equals("3286106")) {
			return true;
		} else if (uid.equals("20631499")) {
			return true;
		} else if (uid.equals("22910713")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param is
	 */
	public static void close(InputStream is) {
		try {
			is.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param os
	 */
	public static void close(OutputStream os) {
		try {
			os.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Get rounded bitmap
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return bmp;
	}

	public static int getAge(int birthYear, String birthDay) {
		if (null == birthDay)
			return 18;

		Date ageDate = new Date();
		ageDate.setMonth(Integer.parseInt(birthDay.substring(0, 2)) - 1);
		ageDate.setDate(Integer.parseInt(birthDay.substring(2, 4)));

		if (!ageDate.before(new Date())) {
			return (Util.getYear() - birthYear - 1);
		} else {
			return (Util.getYear() - birthYear);
		}
	}

	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static JSONObject getRawJsonArray(Context mContext, String filename) {
		try {
			InputStream is = mContext.getAssets().open(filename);
			StringBuffer strbuf = new StringBuffer();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader bufReader = new BufferedReader(reader);
			String line;
			while ((line = bufReader.readLine()) != null) {
				strbuf.append(line);
			}
			JSONObject obj = null;
			try {
				obj = new JSONObject(strbuf.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return obj;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * read data from stream.
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] readDataFromIS(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] data = new byte[50];
		int readLen = 0;
		while ((readLen = is.read(data)) > 0)
			os.write(data, 0, readLen);
		return os.toByteArray();
	}

	public static boolean checkSenior(JSONArray serviceType) {
		boolean isSenior = false;
		try {
			if (null != serviceType
					&& !serviceType.getString(0).equalsIgnoreCase("")) {
				for (int i = 0; i < serviceType.length(); i++) {
					// Check if the user is senior user
					if (SERVICE_TYPE_SENIOR == serviceType.getInt(i)) {
						isSenior = true;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return isSenior;
	}

	/**
	 * get last section in the URL. Normally this is to get the file name.
	 * 
	 * @param url
	 * @return the last section string
	 */
	public static String lastSectionInURL(URL url) {
		String str = url.getPath();
		String strs[] = str.split("/");
		return strs[strs.length - 1];
	}

	/**
	 * get ver of current app
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
		}
		return versionName;
	}

	/**
	 * get verion code of current app
	 * 
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		return verCode;
	}

	/**
	 * check if there is key for given value in the Map
	 * 
	 * @param map
	 * @param value
	 * @return key for the given value or 'null' if no that key
	 */
	public static Object keyForValue(Map<?, ?> map, Object value) {
		for (Object key : map.keySet())
			if (map.get(key) == value)
				return key;

		return null;
	}

	/**
	 * today is or not checkUpdate, one day check once;
	 */

	public static boolean versionCheckEachDay(Context mContext) {
		boolean rtn = false;
		return rtn;

	}

	/**
	 * check network status
	 */
	public static boolean checkNetworkStatus(Context mContext) {
		boolean rtn = true;
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(mContext.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			rtn = false;
		}
		return rtn;
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int resizeWidth,
			int resizeHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix(); //
		float scaleWidth = ((float) resizeWidth / width); //
		float scaleHeight = ((float) resizeHeight / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	public static int getRotateDegree(String imgPath) {
		int degree = 0;
		try {
			ExifInterface exifIntf = new ExifInterface(imgPath);
			String orientation = exifIntf
					.getAttribute(ExifInterface.TAG_ORIENTATION);
			int intOrentation = Integer.parseInt(orientation);
			switch (intOrentation) {
			case ExifInterface.ORIENTATION_NORMAL:
				degree = 0;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				degree = 0;
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				degree = 0;
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				degree = 0;
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				degree = 0;
				break;
			case ExifInterface.ORIENTATION_UNDEFINED:
				degree = 0;
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	private static final char[] HEX_DIGITS = {'0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static boolean percentEncoded(String encoded, int pos, int limit) {
		return pos + 2 < limit
				&& encoded.charAt(pos) == '%'
				&& decodeHexDigit(encoded.charAt(pos + 1)) != -1
				&& decodeHexDigit(encoded.charAt(pos + 2)) != -1;
	}

	private static int decodeHexDigit(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		}
		if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		}
		return -1;
	}

	/**
	 * Returns a substring of {@code input} on the range {@code [pos..limit)} with the following
	 * transformations:
	 * <ul>
	 * <li>Tabs, newlines, form feeds and carriage returns are skipped.
	 * <li>In queries, ' ' is encoded to '+' and '+' is encoded to "%2B".
	 * <li>Characters in {@code encodeSet} are percent-encoded.
	 * <li>Control characters and non-ASCII characters are percent-encoded.
	 * <li>All other characters are copied without transformation.
	 * </ul>
	 *
	 * @param alreadyEncoded true to leave '%' as-is; false to convert it to '%25'.
	 * @param strict         true to encode '%' if it is not the prefix of a valid percent encoding.
	 * @param plusIsSpace    true to encode '+' as "%2B" if it is not already encoded.
	 * @param asciiOnly      true to encode all non-ASCII codepoints.
	 */
	private static String canonicalize(String input, int pos, int limit, String encodeSet,
									   boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
		int codePoint;
		for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
			codePoint = input.codePointAt(i);
			if (codePoint < 0x20
					|| codePoint == 0x7f
					|| codePoint >= 0x80 && asciiOnly
					|| encodeSet.indexOf(codePoint) != -1
					|| codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
					|| codePoint == '+' && plusIsSpace) {
				// Slow path: the character at i requires encoding!
				Buffer out = new Buffer();
				out.writeUtf8(input, pos, i);
				canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
						asciiOnly);
				return out.readUtf8();
			}
		}

		// Fast path: no characters in [pos..limit) required encoding.
		return input.substring(pos, limit);
	}

	private static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet,
									 boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
		Buffer utf8Buffer = null; // Lazily allocated.
		int codePoint;
		for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
			codePoint = input.codePointAt(i);
			if (alreadyEncoded
					&& (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
				// TODO Skip this character.
				Log.d(Util.class.getName(), "codePoint:" + codePoint);
				// TODO delete this
			} else if (codePoint == '+' && plusIsSpace) {
				// Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
				out.writeUtf8(alreadyEncoded ? "+" : "%2B");
			} else if (codePoint < 0x20
					|| codePoint == 0x7f
					|| codePoint >= 0x80 && asciiOnly
					|| encodeSet.indexOf(codePoint) != -1
					|| codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
				// Percent encode this character.
				if (utf8Buffer == null) {
					utf8Buffer = new Buffer();
				}
				utf8Buffer.writeUtf8CodePoint(codePoint);
				while (!utf8Buffer.exhausted()) {
					int b = utf8Buffer.readByte() & 0xff;
					out.writeByte('%');
					out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
					out.writeByte(HEX_DIGITS[b & 0xf]);
				}
			} else {
				// This character doesn't need encoding. Just copy it over.
				out.writeUtf8CodePoint(codePoint);
			}
		}
	}

	static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict) {
		return canonicalize(
				input, 0, input.length(), encodeSet, alreadyEncoded, strict, true, true);
	}

	/**
	 * 对字符串md5加密
	 *
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
