package com.konka.renting.widget;

import android.app.Activity;

import cn.addapp.pickers.picker.DatePicker;
import cn.addapp.pickers.picker.DateTimePicker;

public class DateHourPicker extends DateTimePicker {
    public DateHourPicker(Activity activity) {
        this(activity, YEAR_MONTH_DAY_HOUR);
    }

    public DateHourPicker(Activity activity, @DateMode int mode) {
        super(activity, mode, HOUR_24);
    }

    public DateHourPicker(Activity activity, @DateMode int mode, @TimeMode int timeMode) {
        super(activity, mode, timeMode);
    }

    /**
     * 设置年月日时的单位
     */
    public void setLabel(String yearLabel, String monthLabel, String dayLabel, String hourLabel) {
        super.setLabel(yearLabel, monthLabel, dayLabel, hourLabel, "");
    }

    /**
     * 设置范围：开始的年月日时
     */
    public void setRangeStart(int startYear, int startMonth, int startDay) {
        super.setDateRangeStart(startYear, startMonth, startDay);
    }

    /**
     * 设置范围：结束的年月日时
     */
    public void setRangeEnd(int endYear, int endMonth, int endDay) {
        super.setDateRangeEnd(endYear, endMonth, endDay);
    }

    /**
     * 设置范围：开始的年月日时
     */
    public void setRangeStart(int startYearOrMonth, int startMonthOrDay) {
        super.setDateRangeStart(startYearOrMonth, startMonthOrDay);
    }

    /**
     * 设置范围：结束的年月日时
     */
    public void setRangeEnd(int endYearOrMonth, int endMonthOrDay) {
        super.setDateRangeEnd(endYearOrMonth, endMonthOrDay);
    }


    /**
     * 设置默认选中的年月日时
     */
    public void setSelectedItem(int year, int month, int day,int hour) {
        super.setSelectedItem(year, month, day, hour);
    }

    /**
     * 设置默认选中的年月或者月日
     */
    public void setSelectedItem(int yearOrMonth, int monthOrDay) {
        super.setSelectedItem(yearOrMonth, monthOrDay, 0, 0);
    }


    public void setOnWheelListener(final OnWheelListener listener) {
        if (null == listener) {
            return;
        }
        super.setOnWheelListener(new DateTimePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                listener.onYearWheeled(index, year);
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                listener.onMonthWheeled(index, month);
            }

            @Override
            public void onDayWheeled(int index, String day) {
                listener.onDayWheeled(index, day);
            }

            @Override
            public void onHourWheeled(int index, String hour) {
                listener.onHourWheeled(index, hour);
            }

            @Override
            public void onMinuteWheeled(int index, String minute) {

            }
        });
    }

    public void setOnDatePickListener(final OnDatePickListener listener) {
        if (null == listener) {
            return;
        }
        if (listener instanceof OnYearMonthDayPickListener) {
            super.setOnDateTimePickListener(new OnYearMonthDayTimePickListener() {
                @Override
                public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                    ((OnYearMonthDayPickListener) listener).onDatePicked(year, month, day);
                }
            });
        } else if (listener instanceof OnYearMonthPickListener) {
            super.setOnDateTimePickListener(new OnYearMonthTimePickListener() {
                @Override
                public void onDateTimePicked(String year, String month, String hour, String minute) {
                    ((OnYearMonthPickListener) listener).onDatePicked(year, month);
                }
            });
        } else if (listener instanceof OnMonthDayPickListener) {
            super.setOnDateTimePickListener(new OnMonthDayTimePickListener() {
                @Override
                public void onDateTimePicked(String month, String day, String hour, String minute) {
                    ((OnMonthDayPickListener) listener).onDatePicked(month, day);
                }
            });
        }else if (listener instanceof OnYearMonthDayHourPickListener) {
            super.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayHourPickListener() {
                @Override
                public void onDateTimePicked(String year, String month, String day, String hour) {
                    ((OnYearMonthDayHourPickListener) listener).onDatePicked(year, month, day, hour);
                }
            });
        }
    }

    protected interface OnDatePickListener {

    }

    public interface OnYearMonthDayHourPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month, String day, String hour);

    }

    public interface OnYearMonthDayPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month, String day);

    }

    public interface OnYearMonthPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month);

    }

    public interface OnMonthDayPickListener extends OnDatePickListener {

        void onDatePicked(String month, String day);

    }

    public interface OnWheelListener {

        void onYearWheeled(int index, String year);

        void onMonthWheeled(int index, String month);

        void onDayWheeled(int index, String day);

        void onHourWheeled(int index, String hour);

    }
}
