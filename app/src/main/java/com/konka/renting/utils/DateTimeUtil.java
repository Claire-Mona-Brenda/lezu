package com.konka.renting.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jzxiang on 14/01/2018.
 */

public class DateTimeUtil {

    /**
     * 一些时间格式
     */
    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_MONTH_DAY_TIME = "MM-dd HH:mm";
    public final static String FORMAT_DATE = "yyyy-MM-dd";

    public final static String[] WEEK_DAYS = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    public static String getFormatToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(currentTime);
    }

    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    public static long getTimeMills(String time) {
        Date date = stringToDate(time, FORMAT_DATE_TIME_SECOND);
        if (date == null)
            return 0;
        return date.getTime();
    }

    /**
     * 类似QQ/微信 聊天消息的时间
     */
    public static String getChatTime(long timesamp) {
        long clearTime = timesamp;
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(clearTime);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = "今天 " /*+ getHourAndMin(clearTime)*/;
                break;
            case 1:
                result = "昨天 " /*+ getHourAndMin(clearTime)*/;
                break;
            case 2:
                result = "前天 " /*+ getHourAndMin(clearTime)*/;
                break;
            default:
                result = getTime(clearTime);
                break;
        }
        return result;
    }

    private static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATE);
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_TIME);
        return format.format(new Date(time));
    }

    /**
     * 获取当前日期是星期几
     *
     * @param time
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(long time) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getWeekFromIndex(int index) {
        if (index < 0 || index > 6)
            return String.valueOf(index);
        return WEEK_DAYS[index];
    }

    /**
     * 对日期进行增加操作
     *
     * @param target
     *            需要进行运算的日期
     * @param hour
     *            小时
     * @return
     */
    public static Date addDateTime(Date target, double hour) {
        if (null == target || hour < 0) {
            return target;
        }

        return new Date(target.getTime() + (long) (hour * 60 * 60 * 1000));
    }

    /**
     * 对日期进行相减操作
     *
     * @param target
     *            需要进行运算的日期
     * @param hour
     *            小时
     * @return
     */
    public static Date subDateTime(Date target, double hour) {
        if (null == target || hour < 0) {
            return target;
        }

        return new Date(target.getTime() - (long) (hour * 60 * 60 * 1000));
    }
}
