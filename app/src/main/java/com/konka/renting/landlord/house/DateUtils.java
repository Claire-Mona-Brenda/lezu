package com.konka.renting.landlord.house;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 处理所有和日期相关的处理
 */
public class DateUtils extends Object {
	/** 系统总的失效日期 */
	public static final String DATE_FOREVER = "9999-12-31";
	/** 时间格式 */
	private static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	/** 全时间格式 */
	private static final String FORMAT_FULLTIME = "yyMMddHHmmssSSS";
	/** 日期格式 */
	private static final String FORMAT_DATE = "yyyy-MM-dd";
	private static final String FORMAT_DATE2 = "yyyy年MM月dd日HH时";
	private static final String FORMAT_DATE3 = "yyyy年MM月dd日";
	/** 日期格式 */
	public static final String FORMATDATE = "yyyyMMdd";
	/** 日期格式 */
	private static final String FORMAT_YEARMONTH = "yyyy-MM";
	/** 纯时间格式 */
	private static final String FORMAT_TIME = "HH:mm:ss";
	/** 纯时间格式 */
	private static final String FORMAT_TIME2 = "HH:mm";
	/** 整点时间格式 */
	private static final String FORMAT_DATETIMEZD = "yyyy-MM-dd HH:00:00";
	/** 不带秒的时间格式 */
	private static final String FORMAT_DATEMINUTE = "yyyy-MM-dd HH:mm";
	private static final String FORMAT_MT = "MM-dd HH:mm";
	private static final String FORMAT_Month = "MM";
	private static final String FORMAT_YearMonth = "yyyy-MM";
	private static final String FORMAT_MonthDay = "MM-dd";

	private static Calendar cal = Calendar.getInstance();

	public static double getIntervalDate(String small, String big) {
		double smallMi = DateStrConvertMills(small);
		double bigMi = DateStrConvertMills(big);

		double interval = (bigMi - smallMi) / 1000 / 60 / 60;
		double b = Math.round(interval * 10) / 10.0;// 保留1位小数
		return b;
	}

	/**
	 * 看哪天是第几周
	 * 
	 * @param testDate
	 *            要测试的日期
	 * @return int 该日期是第几周
	 */
	public static String testWhichWeek(String testDateStr) {

		String testYear = testDateStr.substring(0, 4);
		Date testDate = getDateFromString(testDateStr);
		int testYear1 = Integer.parseInt(testYear);
		return testWhichWeek1(testDate, testYear1);
	}

	/**
	 * 看哪天是第几周
	 * 
	 * @param testDate
	 *            要测试的日期
	 * @return int 该日期是第几周
	 */
	public static String testWhichWeek1(Date testDate, int year) {
		// 如果第一周周一是上一年的，从第二周开始
		int finish = 52;
		String lastday = getDayByWeek(year, 53, true);
		if (lastday.substring(0, 4).equals("" + year))
			finish = 53;

		for (int j = 1; j <= finish; j++) {
			Date testDate1 = getDateFromString(getDayByWeek(year, j, true));
			Date testDate7 = getDateFromString(getDayByWeek(year, j, false));
			if (testDate.equals(testDate1)
					|| testDate.equals(testDate7)
					|| (testDate.after(testDate1) && testDate.before(testDate7))) {
				return year + "-" + j;
			}
		}
		return testWhichWeek1(testDate, year - 1);
	}

	/**
	 * 把时间转换成long
	 * 
	 * @return long
	 */
	public static long convertUponGet(Object arg0) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATEMINUTE);
		long longTime = 0;
		try {
			cal.setTime(sdf.parse((String) arg0));
			longTime = cal.getTimeInMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longTime;
	}

	/**
	 * 得到当前的日期时间字符串
	 * 
	 * @return 日期时间字符串
	 */
	public static String getDatetime() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_DATETIME);
	}
	
	/**
	 * 得到当前的日期时间字符串
	 * 
	 * @return 日期时间字符串 格式 年月日
	 */
	public static String getDatetime1() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_DATE);
	}
	public static String getDatetime2() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_DATE2);
	}
	public static String getDatetime3() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_DATE3);
	}
	/**
	 * 得到当前的全时间字符串，包含毫秒
	 * 
	 * @return 日期时间字符串
	 */
	public static String getFulltime() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_FULLTIME);
	}

	/**
	 * 得到当前的日期时间字符串
	 * 
	 * @return 日期时间字符串
	 */
	public static String getDatetimeW3C() {
		return getDate() + "T" + getTime();
	}

	/**
	 * 得到当前的日期时间字符串
	 * 
	 * @return 日期时间字符串
	 */
	public static String getDatetimeZd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return getStringFromDate(calendar.getTime(), FORMAT_DATETIME);
	}

	/**
	 * 得到当前的年月日期字符串
	 * 
	 * @return 年月日期字符串
	 */
	public static String getYearMonth() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_YEARMONTH);
	}

	/**
	 * 得到当前的日期字符串
	 * 
	 * @return 日期字符串
	 */
	public static String getDate() {
		return getDate(Calendar.getInstance());
	}

	/**
	 * 得到指定日期的字符串
	 * 
	 * @param calendar
	 *            指定的日期
	 * @return 日期字符串
	 */
	public static String getDate(Calendar calendar) {
		return getStringFromDate(calendar.getTime(), FORMAT_DATE);
	}

	/**
	 * 得到当前的纯时间字符串
	 * 
	 * @return 时间字符串
	 */
	public static String getTime() {
		Calendar calendar = Calendar.getInstance();
		return getStringFromDate(calendar.getTime(), FORMAT_TIME2);
	}

	/**
	 * 根据数字得到中文数字。
	 * 
	 * @param number
	 *            数字
	 * @return 中文数字
	 */
	public static String getChineseNum(String number) {
		String chinese = "";
		int x = Integer.parseInt(number);

		switch (x) {
		case 0:
			chinese = "○";
			break;
		case 1:
			chinese = "一";
			break;
		case 2:
			chinese = "二";
			break;
		case 3:
			chinese = "三";
			break;
		case 4:
			chinese = "四";
			break;
		case 5:
			chinese = "五";
			break;
		case 6:
			chinese = "六";
			break;
		case 7:
			chinese = "七";
			break;
		case 8:
			chinese = "八";
			break;
		case 9:
			chinese = "九";
			break;
		default:
		}
		return chinese;
	}

	/**
	 * 得到当前日期的中文日期字符串
	 * 
	 * @return 中文日期字符串
	 */
	public static String getChineseDate() {
		return getChineseDate(getDate());
	}

	/**
	 * 根据日期获取当前日期为第几周
	 */

	public static int getWeekOfYear(String datestr) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		int week = 1;
		try {
			date = format.parse(datestr);
			Calendar calendar = Calendar.getInstance();
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.setTime(date);
			week = calendar.get(Calendar.WEEK_OF_YEAR);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return week;
	}

	/**
	 * 根据日期值得到中文日期字符串
	 * 
	 * @param date
	 *            日期值
	 * @return 中文日期字符串
	 */
	public static String getChineseDate(String date) {
		if (date.length() < 10) {
			return "";
		} else {
			String year = date.substring(0, 4); // 年
			String month = date.substring(5, 7); // 月
			String day = date.substring(8, 10); // 日
			String y1 = year.substring(0, 1); // 年 字符1
			String y2 = year.substring(1, 2); // 年 字符1
			String y3 = year.substring(2, 3); // 年 字符3
			String y4 = year.substring(3, 4); // 年 字符4
			String m2 = month.substring(1, 2); // 月 字符2
			String d1 = day.substring(0, 1); // 日 1
			String d2 = day.substring(1, 2); // 日 2
			String cy1 = getChineseNum(y1);
			String cy2 = getChineseNum(y2);
			String cy3 = getChineseNum(y3);
			String cy4 = getChineseNum(y4);
			String cm2 = getChineseNum(m2);
			String cd1 = getChineseNum(d1);
			String cd2 = getChineseNum(d2);
			String cYear = cy1 + cy2 + cy3 + cy4 + "年";
			String cMonth = "月";

			if (Integer.parseInt(month) > 9) {
				cMonth = "十" + cm2 + cMonth;
			} else {
				cMonth = cm2 + cMonth;
			}

			String cDay = "日";

			if (Integer.parseInt(day) > 9) {
				cDay = cd1 + "十" + cd2 + cDay;
			} else {
				cDay = cd2 + cDay;
			}

			String chineseday = cYear + cMonth + cDay;
			return chineseday;
		}
	}

	/**
	 * 根据日期值得到中文日期字符串
	 * 
	 * @param date
	 *            给定日期
	 * @return 2005年09月23日格式的日期
	 */
	public static String getChineseTwoDate(String date) {
		if (date.length() < 10) {
			return "";
		} else {
			String year = date.substring(0, 4); // 年
			String month = date.substring(5, 7); // 月
			String day = date.substring(8, 10); // 日

			return year + "年" + month + "月" + day + "日";
		}
	}

	/**
	 * 得到当前日期的星期数 : 例如 '星期一', '星期二'等
	 * 
	 * @return 当前日期的星期数
	 */
	public static String getChineseDayOfWeek() {
		return getChineseDayOfWeek(getDate());
	}

	/**
	 * 得到指定日期的星期数
	 * 
	 * @param strDate
	 *            指定日期字符串
	 * @return 指定日期的星期数
	 */
	public static String getChineseDayOfWeek(String strDate) {
		Calendar calendar = getCalendar(strDate);

		int week = calendar.get(Calendar.DAY_OF_WEEK);
		String strWeek = "";

		switch (week) {
		case Calendar.SUNDAY:
			strWeek = "星期日";
			break;
		case Calendar.MONDAY:
			strWeek = "星期一";
			break;
		case Calendar.TUESDAY:
			strWeek = "星期二";
			break;
		case Calendar.WEDNESDAY:
			strWeek = "星期三";
			break;
		case Calendar.THURSDAY:
			strWeek = "星期四";
			break;
		case Calendar.FRIDAY:
			strWeek = "星期五";
			break;
		case Calendar.SATURDAY:
			strWeek = "星期六";
			break;
		default:
			strWeek = "星期一";
			break;
		}

		return strWeek;
	}

	/**
	 * compare two kinds String with format : 12:00 , 08:00; or 12:00:00,
	 * 08:00:00
	 * 
	 * @param firstTime
	 *            the first time string
	 * @param secondTime
	 *            the second time string
	 * @return 0 -- same 1 -- first bigger than second -1 -- first smaller than
	 *         second -2 -- invalid time format
	 */
	public static int compareOnlyByTime(String firstTime, String secondTime) {
		try {
			String timeDelm = ":";

			// calculate the first time to integer
			int pos = firstTime.indexOf(timeDelm);
			int iFirst = Integer.parseInt(firstTime.substring(0, pos)) * 10000;
			firstTime = firstTime.substring(pos + 1);
			pos = firstTime.indexOf(timeDelm);

			if (pos > 0) {
				iFirst = iFirst
						+ (Integer.parseInt(firstTime.substring(0, pos)) * 100)
						+ Integer.parseInt(firstTime.substring(pos + 1));
			} else {
				iFirst = iFirst + (Integer.parseInt(firstTime) * 100);
			}

			// calculate the second time string to integer
			pos = secondTime.indexOf(timeDelm);
			int iSecond = Integer.parseInt(secondTime.substring(0, pos)) * 10000;
			secondTime = secondTime.substring(pos + 1);
			pos = secondTime.indexOf(timeDelm);

			if (pos > 0) {
				iSecond = iSecond
						+ (Integer.parseInt(secondTime.substring(0, pos)) * 100)
						+ Integer.parseInt(secondTime.substring(pos + 1));
			} else {
				iSecond = iSecond + (Integer.parseInt(secondTime) * 100);
			}

			// compare two
			if (iFirst == iSecond) {
				return 0;
			} else if (iFirst > iSecond) {
				return 1;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -2;
		}
	}

	/**
	 * 得到与当前日期相差指定天数的日期字符串
	 * 
	 * @param days
	 *            前后的天数，正值为后， 负值为前
	 * @return 日期字符串
	 */
	public static String getCertainDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days);
		return getStringFromDate(calendar.getTime(), FORMAT_DATE);
	}

	/**
	 * 得到与当前日期相差指定分钟的日期字符串
	 * 
	 * @param days
	 *            前后的天数，正值为后， 负值为前
	 * @return 日期字符串
	 */
	public static String getCertainDateByMinute(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minute);
		return getStringFromDate(calendar.getTime(), FORMAT_DATETIME);
	}

	/**
	 * 得到与当前日期相差指定月数的日期字符串
	 * 
	 * @param dif
	 *            前后的月数，正值为后， 负值为前
	 * @return 日期字符串
	 */
	public static String getCertainMonth(int dif) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, dif);
		return getStringFromDate(calendar.getTime(), FORMAT_DATE);
	}

	/**
	 * 得到与指定日期相差指定天数的日期字符串
	 * 
	 * @param dateString
	 *            指定的日期
	 * @param days
	 *            前后的天数，正值为后， 负值为前
	 * @return 日期字符串
	 */
	public static String getCertainDate(String dateString, int days) {
		Calendar calendar = getCalendar(dateString);
		calendar.add(Calendar.DATE, days);
		return getStringFromDate(calendar.getTime(), FORMAT_DATE);
	}

	/**
	 * 得到与指定日期相差指定天数的日期字符串
	 * 
	 * @param dateString
	 *            指定的日期
	 * @param period
	 *            前后的天数，正值为后， 负值为前
	 * @param periodType
	 *            周期类别 可以是天、月、年
	 * @return 日期字符串
	 */
	public static String getCertainDate(String dateString, int period,
			int periodType) {
		Calendar calendar = getCalendar(dateString);

		switch (periodType) {
		case 1: // 天
			calendar.add(Calendar.DATE, period);
			break;
		case 2: // 月
			calendar.add(Calendar.MONTH, period);
			break;
		case 3: // 年
			calendar.add(Calendar.MONTH, period * 12);
			break;
		default:
		}
		return getStringFromDate(calendar.getTime(), FORMAT_DATE);
	}

	/**
	 * 根据规定格式的字符串得到Calendar
	 * 
	 * @param dateString
	 *            日期串
	 * @return 对应Calendar
	 */
	public static Calendar getCalendar(String dateString) {
		Calendar calendar = Calendar.getInstance();
		String[] items = dateString.split("-");
		calendar.set(Integer.parseInt(items[0]),
				Integer.parseInt(items[1]) - 1, Integer.parseInt(items[2]));
		return calendar;
	}

	/**
	 * 得到本周星期一的日期
	 * 
	 * @return 日期字符串
	 */
	public static String getFirstDateOfWeek() {
		return getFirstDateOfWeek(getDate());
	}

	/**
	 * 得到指定日期的星期一的日期
	 * 
	 * @param dateString
	 *            日期字符串
	 * @return 本周星期一的日期
	 */
	public static String getFirstDateOfWeek(String dateString) {
		Calendar calendar = getCalendar(dateString);
		int iCount;
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			iCount = -6;
		} else {
			iCount = Calendar.MONDAY - calendar.get(Calendar.DAY_OF_WEEK);
		}

		return getCertainDate(dateString, iCount);
	}

	/**
	 * 得到指定日期所在星期的所有日期的拼接串:格式：'2012-10-08'，'2012-10-09'，'2012-10-10'......
	 * 
	 * @param dateString
	 *            日期字符串
	 * @return 本周星期一的日期
	 */
	public static String getDaysOfWeek(String dateString) {
		StringBuilder sb = new StringBuilder("");
		sb.append("'").append(dateString).append("'");
		String mondayDate = getFirstDateOfWeek(dateString); // 得到星期一的日期
		for (int i = 0; i < 7; i++) {
			sb.append(",").append("'").append(getDateAdded(i, mondayDate))
					.append("'");
		}

		return sb.toString();
	}

	/**
	 * 将指定格式的字符串格式化为日期
	 * 
	 * @param s
	 *            字符串内容
	 * @return 日期
	 */
	public static Date getDateFromString(String s) {
		return getDateFromString(s, FORMAT_DATE);
	}

	/**
	 * 将指定格式的字符串格式化为日期
	 * 
	 * @param s
	 *            字符串内容
	 * @param format
	 *            字符串格式
	 * @return 日期
	 */
	public static Date getDateFromString(String s, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 根据长度判断日期字符串的格式，通过该格式来转换日期
	 * 
	 * @param s
	 *            日期字符串内容
	 * @return 日期
	 */
	public static Date getDateFromString2(String s) {
		try {
			String format = "";
			if(s!=null&&s.trim().length()!=0&&s.trim().length()==10){
				format = FORMAT_DATE;
			}else if(s!=null&&s.trim().length()!=0&&s.trim().length()==5){
				if (s.contains(":")) {
					format="HH:mm";
				}else {
					format ="MM-dd";
				}
			}else{
				if(s!=null&&s.trim().length()!=0&&s.trim().length()==19){
					format = FORMAT_DATETIME;
				}else{
					format="yyyy-MM-dd HH:mm";
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(s);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将日期格式化为指定的字符串
	 * 
	 * @param d
	 *            日期
	 * @param format
	 *            输出字符串格式
	 * @return 日期字符串
	 */
	public static String getStringFromDate(Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 将字符串日期格式化为指定的格式
	 * 
	 * @param d
	 *            字符串日期
	 * @param curFormat
	 *            传进来的日期的格式
	 * @param toFormat
	 *            输出字符串格式
	 * @return 日期字符串
	 */
	public static String getFormatDateFromStringDate(String d,
			String curFormat, String toFormat) {
		if (d != null && d.trim().length() != 0
				&& !d.equalsIgnoreCase("null")) {
			Date temp = getDateFromString(d, curFormat);
			SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
			if(temp!=null){
				return sdf.format(temp);
			}
		}
		return "";
	}

	/**
	 * 得到当前的年份
	 * 
	 * @return 当前年份
	 */
	public static int getYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 得到当前的月份
	 * 
	 * @return 当前月份
	 */
	public static int getMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 得到当前的日期
	 * 
	 * @return 当前日期
	 */
	public static int getDay() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 当得到两个日期相差天数。
	 * 
	 * @param first
	 *            第一个日期
	 * @param second
	 *            第二个日期
	 * @return 相差的天数
	 */
	public static int selectDateDiff(String first, String second) {
		int dif = 0;
		try {
			Date fDate = getDateFromString(first, FORMAT_DATE);
			Date sDate = getDateFromString(second, FORMAT_DATE);
			dif = (int) ((sDate.getTime() - fDate.getTime()) / 86400000);
		} catch (Exception e) {
			dif = 0;
		}
		return dif;
	}

	/**
	 * 当前日期与参数传递的日期的相差天数
	 * 
	 * @param dateinfo
	 *            指定的日期
	 * @return 相差的天数
	 */
	public static int selectDateDiff(String dateinfo) {
		return selectDateDiff(dateinfo, getDate());
	}

	/**
	 * 某日期加上几天得到另外一个日期
	 * 
	 * @param addNum
	 *            要增加的天数
	 * @param getDate
	 *            某日期
	 * @return 与某日期相差addNum天的日期
	 */
	public static String getDateAdded(int addNum, String getDate) {
		return getCertainDate(getDate, addNum);
	}

	/**
	 * 某日期（带时间）加上几天得到另外一个日期 （带时间）
	 * 
	 * @param datetime
	 *            需要调整的日期（带时间）
	 * @param days
	 *            调整天数
	 * @return 调整后的日期（带时间）
	 */
	public static String getCertainDatetime(String datetime, int days) {
		Date curDate = getDateFromString(datetime, FORMAT_DATETIME);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.add(Calendar.DATE, days);
		return getStringFromDate(calendar.getTime(), FORMAT_DATETIME);
	}

	/**
	 * 得到当前日期的所在月的第一天的日期
	 * 
	 * @param date
	 *            当前日期
	 * @return String 返回的日期
	 * 
	 */
	public static String getMonthFirstDay(String date) {
		Calendar cal = getCalendar(date);
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String year = String.valueOf(cal.get(Calendar.YEAR));
		if (Integer.parseInt(month) < 10) {
			month = "0" + month;
		}
		return year + "-" + month + "-01";
	}
	
	public static String getFirstDayByYear(){
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		String str = year+"-01-01 00:00:00";
		return str;
	}
	
	public static String getLastDayByYear(){
		String first_day = getFirstDayByYear();
		Date date = getDateFromString(first_day, FORMAT_DATETIME);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.SECOND, -1);
		return getStringFromDate(cal.getTime(),FORMAT_DATETIME );
		
	}

	/**
	 * 得到当前日期的所在月的最后一天的日期
	 * 
	 * @param date
	 *            当前日期
	 * @return String 返回的日期
	 * 
	 */
	public static String getMonthLastDay(String date) {
		Calendar cal = getCalendar(date);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		int nextMonth = month + 1;
		int nextYear = year;
		if (nextMonth == 13) {
			nextMonth = 1;
			nextYear = nextYear + 1;
		}
		String nextMonthFirstDay = nextYear + "-" + nextMonth + "-01";
		return getCertainDate(nextMonthFirstDay, -1) + " 23:59:59";
	}

	/**
	 * 得到当前日期的所在月的最后一天的日期(带时分秒)
	 * 
	 * @param date
	 *            当前日期
	 * @return String 返回的日期
	 * 
	 */
	public static String getFullMonthLastDay(String date) {
		Calendar cal = getCalendar(date);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		int nextMonth = month + 1;
		int nextYear = year;
		if (nextMonth == 13) {
			nextMonth = 1;
			nextYear = nextYear + 1;
		}
		String nextMonthFirstDay = nextYear + "-" + nextMonth + "-01";
		return getCertainDate(nextMonthFirstDay, -1) + " 23:59:59";
	}

	/**
	 * 取得两日期间的月份差数
	 * 
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 月份差数
	 */
	public static int getMonthDiff(String startDate, String endDate) {
		String[] startArray = startDate.split("-");
		String[] endArray = endDate.split("-");
		int startYear = Integer.parseInt(startArray[0]);
		int startMonth = Integer.parseInt(startArray[1]);
		int endYear = Integer.parseInt(endArray[0]);
		int endMonth = Integer.parseInt(endArray[1]);
		return Math.abs((endYear - startYear) * 12 + endMonth - startMonth);
	}

	/**
	 * 如果当前日期是周六或者周日，则返回下周一的日期
	 * 
	 * @param date
	 *            当前日期
	 * @return String 下周一日期
	 */
	public static String getWorkDate(String date) {
		Date curDate = getDateFromString(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		int week = calendar.get(Calendar.DAY_OF_WEEK);

		if (week == Calendar.SATURDAY) {
			calendar.add(Calendar.DATE, 2);
		} else if (week == Calendar.SUNDAY) {
			calendar.add(Calendar.DATE, 1);
		}
		return getDate(calendar);

	}

	/**
	 * 返回两个时间相差的小时数
	 * 
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 返回值
	 * @throws HDException
	 *             发生错误（系统）
	 * @throws TipException
	 *             发生错误（业务）
	 */
	public static double getDiffHoure(String beginTime, String endTime)
			throws Exception {
		//
		double dif = 0;
		try {
			Date eDatetime = getDateFromString(endTime, FORMAT_DATE);
			Date bDatetime = getDateFromString(beginTime, FORMAT_DATE);
			dif = (eDatetime.getTime() - bDatetime.getTime()) / 1000 / 3600;
		} catch (Exception e) {
			dif = 0;
		}
		return dif;
	}

	/**
	 * 获取相差时间，精确到分钟
	 * 
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 获取相差时间
	 */
	public static String getDiffTime(String beginTime, String endTime) {
		try {
			if (endTime == null || endTime.length() == 0) {
				endTime = getDatetime();
			}
			Date eTime = getDateFromString(endTime, FORMAT_DATETIME);
			Date bTime = getDateFromString(beginTime, FORMAT_DATETIME);
			long time = eTime.getTime() - bTime.getTime();
			StringBuffer sb = new StringBuffer();
			int day = (int) Math.floor(time / (24 * 3600000));
			if (day > 0) {
				sb.append(day).append("天");
			}
			time = time % (24 * 3600000);
			int hour = (int) Math.floor(time / 3600000);
			if (hour > 0) {
				sb.append(hour).append("小时");
			}
			time = time % 3600000;
			int minute = (int) Math.ceil(time / 60000);
			if (minute > 0) {
				sb.append(minute).append("分钟");
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取相差时间的分钟数
	 * 
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 获取相差时间
	 */
	public static String getDiffMinutes(String beginTime, String endTime) {
		try {
			if (endTime == null || endTime.length() == 0) {
				endTime = getDatetime();
			}
			Date eTime = getDateFromString(endTime, FORMAT_DATETIME);
			Date bTime = getDateFromString(beginTime, FORMAT_DATETIME);
			long time = eTime.getTime() - bTime.getTime();

			if (time >= 0) {
				return time / 60000 + "";
			} else {
				return "0";
			}

		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 获取两个时间之间的秒差
	 * 
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 获取相差时间
	 */
	public static String getDiffMiao(String beginTime, String endTime) {
		try {
			if (endTime == null || endTime.length() == 0) {
				endTime = getDatetime();
			}
			Date eTime = getDateFromString(endTime, FORMAT_DATETIME);
			Date bTime = getDateFromString(beginTime, FORMAT_DATETIME);
			long time = eTime.getTime() - bTime.getTime();
			StringBuffer sb = new StringBuffer();
			int day = (int) Math.floor(time / (24 * 3600000));
			if (day > 0) {
				sb.append(day).append("天");
			}
			time = time % (24 * 3600000);
			int hour = (int) Math.floor(time / 3600000);
			if (hour > 0) {
				sb.append(hour).append("小时");
			}
			time = time % 3600000;
			int minute = (int) Math.ceil(time / 60000);
			if (minute > 0) {
				sb.append(minute).append("分钟");
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 取得时间差描述,默认 yyyy-MM-dd HH:mm:ss
	 */
	public static String getDiffDisc(String sStartTime) {
		return getDiffDisc(sStartTime, FORMAT_DATETIME);
	}

	/**
	 * @param beginTime
	 * @return
	 */
	public static String getDiffDisc(String sStartTime, String dateFormat) {
		String disc = "";

		Calendar startCal = Calendar.getInstance();
		// Date startDate=getDateFromString(sStartTime,dateFormat);
		startCal.setTime(getDateFromString(sStartTime, dateFormat));
		Calendar cal = Calendar.getInstance();

		int timeDiff = (int) Math.floor((cal.getTimeInMillis() - startCal
				.getTimeInMillis()) / 1000);// 相差多少秒
		int minuteDiff = (int) Math.floor(timeDiff / 60);
		if (minuteDiff == 0) {
			disc = "刚刚";
			return disc;
		}

		int hourDiff = (int) Math.floor(timeDiff / 3600);
		if (hourDiff == 0) {
			disc = minuteDiff + "分钟前";
			return disc;
		}

		boolean sameDay = getDate(cal).equals(getDate(startCal));
		if (sameDay) {
			disc = "今天 "
					+ preBqChar("" + startCal.get(Calendar.HOUR_OF_DAY), '0', 2)
					+ ":"
					+ preBqChar("" + startCal.get(Calendar.MINUTE), '0', 2);
			return disc;
		}

		Calendar ztCal = cal;
		ztCal.add(Calendar.DAY_OF_MONTH, -1);
		boolean isZt = getDate(ztCal).equals(getDate(startCal));
		if (isZt) {
			disc = "昨天 "
					+ preBqChar("" + startCal.get(Calendar.HOUR_OF_DAY), '0', 2)
					+ ":"
					+ preBqChar("" + startCal.get(Calendar.MINUTE), '0', 2);
			return disc;
		}

		boolean sameYear = cal.get(Calendar.YEAR) == startCal
				.get(Calendar.YEAR);
		if (sameYear) {// 同年
			disc = (startCal.get(Calendar.MONTH) + 1)
					+ "月"
					+ startCal.get(Calendar.DAY_OF_MONTH)
					+ "日 "
					+ preBqChar("" + startCal.get(Calendar.HOUR_OF_DAY), '0', 2)
					+ ":"
					+ preBqChar("" + startCal.get(Calendar.MINUTE), '0', 2);
			return disc;
		}

		disc = startCal.get(Calendar.YEAR) + "年"
				+ (startCal.get(Calendar.MONTH) + 1) + "月"
				+ startCal.get(Calendar.DAY_OF_MONTH) + "日 "
				+ preBqChar("" + startCal.get(Calendar.HOUR_OF_DAY), '0', 2)
				+ ":" + preBqChar("" + startCal.get(Calendar.MINUTE), '0', 2);
		return disc;
	}

	private static String preBqChar(String source, char ch, int length) {
		if (source == null) {
			return null;
		}
		String rStr = source;
		if (source.length() < length) {
			for (int i = 0; i < (length - source.length()); i++) {
				rStr = ch + rStr;
			}
		}
		return rStr;
	}

	/**
	 * get current date's quater's first day
	 * 
	 * @param date
	 * @return
	 */
	public static String getQuaterInFirstDay(String date) {
		String[] arr = date.split("-");
		Calendar calendar = Calendar.getInstance();
		// calendar.setTime(new Date());
		// int month = getQuarterInMonth(calendar.get(Calendar.MONTH), true);
		int month = getQuarterInMonth(Integer.valueOf(arr[1]), true);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return getStringFromDate(calendar.getTime(), FORMAT_DATETIME);

	}

	/**
	 * get current date's quater's last day
	 * 
	 * @param date
	 * @return
	 */
	public static String getQuaterInLastDay(String date) {
		String[] arr = date.split("-");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		// int month = getQuarterInMonth(calendar.get(Calendar.MONTH), false);
		int month = getQuarterInMonth(Integer.valueOf(arr[1]), false);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		return getStringFromDate(calendar.getTime(), FORMAT_DATETIME);
	}

	// 返回第几个月份，不是几月
	// 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
	private static int getQuarterInMonth(int month, boolean isQuarterStart) {
		int months[] = { 1, 4, 7, 10 };
		if (!isQuarterStart) {
			months = new int[] { 3, 6, 9, 12 };
		}
		if (month >= 1 && month <= 3)
			return months[0];
		else if (month >= 4 && month <= 6)
			return months[1];
		else if (month >= 7 && month <= 9)
			return months[2];
		else
			return months[3];
	}

	public static double getDiffDays(String start, String end) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		Date beginDate = sdf.parse(start);
		Date endDate = sdf.parse(end);
		double days = (endDate.getTime() - beginDate.getTime())
				/ (24 * 60 * 60 * 1000);

		return days;
	}

	public static boolean isExteed7Days(String createDate) {

		String curDate = getDate();
		boolean isExteed = false;
		try {
			if (getDiffDays(createDate, curDate) > 7) {
				isExteed = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExteed;
	}

	/**
	 * 求某年下的第几个星期的日期 返回java.uilt.Date 类型日期 时间time为当前机器时间
	 * 
	 * @param year
	 *            要获得的年
	 * @param week
	 *            第几个星期
	 * @param flag
	 *            是否是第一天还是最后一天,当为true时返回第一天,false则返回最后一天
	 * @return java.uilt.Date 类型日期
	 * @例如 getDayByWeek(2002,2,true) 返回Tue Jan 08 14:11:57 CST 2002
	 */
	public static String getDayByWeek(int year, int week, boolean flag) {

		// 如果第一周周一是上一年的，从第二周开始
		int increase = 0;
		String year1 = "" + year;
		String firstday = DateUtils.getDayByWeekReal(Integer.valueOf(year), 1,
				true);
		if (!year1.equals(firstday.substring(0, 4)))
			increase = 1;

		return DateUtils.getDayByWeekReal(Integer.valueOf(year), week
				+ increase, flag);
	}

	/**
	 * 求某年下的第几个星期的日期 返回java.uilt.Date 类型日期 时间time为当前机器时间
	 * 
	 * @param year
	 *            要获得的年
	 * @param week
	 *            第几个星期
	 * @param flag
	 *            是否是第一天还是最后一天,当为true时返回第一天,false则返回最后一天
	 * @return java.uilt.Date 类型日期
	 * @例如 getDayByWeek(2002,2,true) 返回Tue Jan 08 14:11:57 CST 2002
	 */
	public static String getDayByWeekReal(int year, int week, boolean flag) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.setTimeInMillis(cal.getTimeInMillis() + 1 * 24 * 60 * 60 * 1000);
		if (!flag)
			cal.setTimeInMillis(cal.getTimeInMillis() + 6 * 24 * 60 * 60 * 1000);
		return getStringFromDate(cal.getTime(), FORMAT_DATE);
	}

	/**
	 * 将字符串数据转化为毫秒数
	 */

	public static long DateStrConvertMills(String dateTime) {
		long timeMills = 0;
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(dateTime);
			timeMills = d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return timeMills;
	}

	/**
	 * 将毫秒数转化为时间
	 */

	public static String MillsConvertDateStr(long mills) {
		Date date = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(date);
	}

	public static String formatDateStrMDHM(String date) {
		String mdhm = date;
		if (date.length() == 8) {
			String month = date.substring(0, 2) + "月"; // 月
			String day = date.substring(2, 4) + "日";// 日
			String hour = date.substring(4, 6) + "时";
			String minute = date.substring(6, 8) + "分";

			mdhm = month.concat(day).concat(hour).concat(minute);
		}

		return mdhm;
	}
	public static String getCurrentTime(){
		  Date dt = new Date();   
		    //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制   
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");   
		    String temp_str=sdf.format(dt);   
//		Time t=new Time("GMT+8:00");
//		t.setToNow();
//		String time =t.year+""+t.month+""+t.monthDay+""+t.hour+""+t.minute+""+t.second+"";
		return temp_str;
	}
	
	public static String getYearStrByDate(String dateStr){
		try{
			Date d = getDateFromString(dateStr,"yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int y = calendar.get(Calendar.YEAR);
			return y+"";
		}catch (Exception e) {
			return "";
		}
		
	}
	
	public static String getMonthStrByDate(String dateStr){
		try{
			Date d = getDateFromString(dateStr,"yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int m = calendar.get(Calendar.MONTH) + 1;
			return m+"";
		}catch (Exception e) {
			return "";
		}
		
	}
	public static String getDayStrByDate(String dateStr){
		try{
			Date d = getDateFromString(dateStr,"yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int day = calendar.get(Calendar.DATE);
			return day+"";
		}catch (Exception e) {
			return "";
		}
		
	}
	
	public static String getHourStrByDate(String dateStr){
		try{
			Date d = getDateFromString(dateStr,"yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int h = calendar.get(Calendar.HOUR_OF_DAY);
			String hStr = null;
			if(h<10){
				hStr = "0"+h;
			}else{
				hStr = h+"";
			}
			return hStr;
		}catch (Exception e) {
			return "";
		}
		
	}
	
	public static String getMinutesStrByDate(String dateStr){
		try{
			Date d = getDateFromString(dateStr,"yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			int m = calendar.get(Calendar.MINUTE);
			String mStr = null;
			if(m<10){
				mStr = "0"+m;
			}else{
				mStr = m+"";
			}
			return mStr;
		}catch (Exception e) {
			return "";
		}
		
	}
	
	public static String getTimeByDateStr(String dateStr,String fromStr){
		try{
			Date d = getDateFromString(dateStr,fromStr);
			return getStringFromDate(d,fromStr);
		}catch (Exception e) {
			return "";
		}
	}
	
	public static String getTimeByDateStr2(String dateStr,String fromStr){
		try{
			Date d = getDateFromString2(dateStr);
			return getStringFromDate(d,fromStr);
		}catch (Exception e) {
			return "";
		}
	}
	
	public static String getBNStart(String dateStr){
		Date d = getDateFromString(dateStr,"yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int m = calendar.get(Calendar.MONTH) + 1;
		int y = calendar.get(Calendar.YEAR);
		if(m>=7){
			return y+"-07-01";
		}else{
			return y+"-01-01";
		}
		
	}
	
	public static String getBNEnd(String dateStr){
		Date d = getDateFromString(dateStr,"yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int m = calendar.get(Calendar.MONTH) + 1;
		int y = calendar.get(Calendar.YEAR);
		if(m>=7){
			return y+"-12-31";
		}else{
			return y+"-06-30";
		}
		
	}
	
	public static String getQNStart(String dateStr){
		Date d = getDateFromString(dateStr,"yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int y = calendar.get(Calendar.YEAR);
		return y+"-01-01";
	}
	
	public static String getQNEnd(String dateStr){
		Date d = getDateFromString(dateStr,"yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int y = calendar.get(Calendar.YEAR);
		return y+"-12-31";
		
	}
	public static String getCurrentYear(){
		  Date dt = new Date();   
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");   
		    String temp_str=sdf.format(dt);   
		return temp_str;
	}
	public static String getCurrentTimeNoMM(){
		  Date dt = new Date();   
		    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATEMINUTE);   
		    String temp_str=sdf.format(dt);   
		return temp_str;
	}
	public static String getMonthTime(){
		  Date dt = new Date();   
		    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_MT);   
		    String temp_str=sdf.format(dt);   
		return temp_str;
	}
	public static String getCurMonth(){
		Date dt = new Date();   
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_Month);   
		String temp_str=sdf.format(dt);   
		return temp_str;
	}
	public static String getMonthDay(){
		Date dt = new Date();   
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_MonthDay);   
		String temp_str=sdf.format(dt);   
		return temp_str;
	}
	public static Date StringToDate(String dateStr) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public  static int TOADY=0;
	public  static int YESTERDAY=1;
	public  static int BEFOREYESTERDAY=2;
	public static String getDateByType(int type){
	Calendar c=Calendar.getInstance();
	c.set(Calendar.DATE, c.get(Calendar.DATE)-type);
	
	Date date=c.getTime();
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	System.out.println(simpleDateFormat.format(date));
	return simpleDateFormat.format(date);
	}
	public static String formatDate(String format,String datestr) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String stringdate="";
		Date date = null;
		try {
			date = sdf.parse(datestr);
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			 stringdate=dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stringdate;
	}
	public static String formatDate2(String format,String datestr) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String stringdate="";
		Date date = null;
		try {
			date = sdf.parse(datestr);
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			 stringdate=dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stringdate;
	}
	 /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        String format;
        Object[] array;
        String html ;
        if(day>0){
            format="%1$,d天%2$,d小时%3$,d分钟";
            array=new Object[]{day,hour,min};
           }else if(hour>0){
            format="%1$,d小时%2$,d分钟";
            array=new Object[]{hour,min};
           }else{
            format="%1$,d分钟";
            array=new Object[]{min};
           }
           html= String.format(format, array);
        return html;
        //return day + "天" + hour + "小时" + min + "分" ;
    }
	public static String getCurrentNoMM(){
		Date dt = new Date();   
		//最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制   
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");   
		String temp_str=sdf.format(dt);   
//		Time t=new Time("GMT+8:00");
//		t.setToNow();
//		String time =t.year+""+t.month+""+t.monthDay+""+t.hour+""+t.minute+""+t.second+"";
		return temp_str;
	}
	
	/**
	 *  1  date1>date2
	 * -1 date1<date2
	 *  0  date1=date2
	 * **/
	public static int compare_date(String date1,String date2){
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		try{
			Date dt1=df.parse(date1);
			Date dt2=df.parse(date2);
			if(dt1.getTime()>dt2.getTime()){
				return 1;
			}else if(dt1.getTime()<dt2.getTime()){
				return -1;
			}else{
				return 0;
			}
		}catch(Exception e){
			
		}
		return 0;
	}
}
