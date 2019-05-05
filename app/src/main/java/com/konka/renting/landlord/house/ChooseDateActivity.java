package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.henry.calendarview.DatePickerController;
import com.henry.calendarview.DayPickerView;
import com.henry.calendarview.SimpleMonthAdapter;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.event.ChooseDateEvent;
import com.konka.renting.utils.RxBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ChooseDateActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.activity_choose_date_day)
    DayPickerView mDayPickerView;

    boolean isChoose = false;
    String startDate, endDate,beforeEndDate;
    List<String> rentingList;

    public static void toActivity(Context context, ArrayList<String> rentingList) {
        Intent intent = new Intent(context, ChooseDateActivity.class);
        intent.putStringArrayListExtra("rentingList", rentingList);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_date;
    }

    @Override
    public void init() {
        tvTitle.setText(getString(R.string.choose_date));
        tvRight.setText(getString(R.string.confirm));
        tvRight.setVisibility(View.VISIBLE);

        rentingList = getIntent().getStringArrayListExtra("rentingList");
        Calendar cal = Calendar.getInstance();
        DayPickerView.DataModel dataModel = new DayPickerView.DataModel();
        dataModel.yearStart = cal.get(Calendar.YEAR);
        dataModel.monthStart = cal.get(Calendar.MONTH);
        dataModel.monthCount = 36;
        dataModel.defTag = "";
        dataModel.leastDaysNum = 1;
        dataModel.mostDaysNum = 2000;
//        SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> ss = new SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay>();
//        ss.setFirst(new SimpleMonthAdapter.CalendarDay(cal, "123"));
//        dataModel.selectedDays = ss;
        List<SimpleMonthAdapter.CalendarDay> busyDays = new ArrayList<>();
        int size = rentingList.size();
        for (int i = 0; i < size; i++) {
            String[] date = rentingList.get(i).split("-");
            SimpleMonthAdapter.CalendarDay cd = new SimpleMonthAdapter.CalendarDay(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[2]));
            busyDays.add(cd);
        }
        dataModel.busyDays = busyDays;
        mDayPickerView.setParameter(dataModel, new DatePickerController() {
            @Override
            public void onDayOfMonthSelected(SimpleMonthAdapter.CalendarDay calendarDay) {
                isChoose = false;
            }

            @Override
            public void onDateRangeSelected(List<SimpleMonthAdapter.CalendarDay> selectedDays) {
                if (selectedDays.size() <= 1)
                    return;
                isChoose = true;
                SimpleMonthAdapter.CalendarDay startCal = selectedDays.get(0);
                startDate = startCal.year + "-" + (startCal.month + 1) + "-" + startCal.day;
                SimpleMonthAdapter.CalendarDay endCal = selectedDays.get(selectedDays.size() - 1);
                endDate = endCal.year + "-" + (endCal.month + 1) + "-" + endCal.day;
                SimpleMonthAdapter.CalendarDay beforeEndCal = selectedDays.get(selectedDays.size() - 2);
                beforeEndDate = beforeEndCal.year + "-" + (beforeEndCal.month + 1) + "-" + beforeEndCal.day;

            }

            @Override
            public void alertSelectedFail(FailEven even) {

            }
        });
    }


    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if (isChoose) {
                    if (checkLastDay()) {
                        RxBus.getDefault().post(new ChooseDateEvent(startDate, endDate));
                        finish();
                    }else
                        showToast(R.string.warn_end_time_not_same);
                } else {
                    showToast(R.string.please_choose_date);
                }
                break;
        }
    }

    private boolean checkLastDay() {
        int size = rentingList.size();
        for (int i = 0; i < size; i++) {
            if(beforeEndDate.equals(rentingList.get(i)))
                return false;
        }
        return true;
    }


}
