package com.konka.renting.landlord.user.incomeprofile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.ProfileBean;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.utils.RxUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;


public class IncomeProfileActivity extends BaseActivity {

    @BindView(R.id.income_chart)
    PieChart mChart;
    protected String[] mParties = new String[]{
            "", "", "", "", "", ""
    };
    @BindView(R.id.tv_rent)
    TextView mTvRent;
    @BindView(R.id.tv_property_fee)
    TextView mTvPropertyFee;
    @BindView(R.id.tv_water_fee)
    TextView mTvWaterFee;
    @BindView(R.id.tv_litter)
    TextView mTvLitter;
    @BindView(R.id.tv_ele)
    TextView mTvEle;
    @BindView(R.id.tv_cost)
    TextView mTvCost;
    @BindView(R.id.tv_time)
    TextView mTvTime;

    private int total;
    private int rentfee;
    private int waterFee;
    private int eleFee;
    private int propertyFee;
    private int lifterFee;
    private int costFee;
    private Integer[] values = new Integer[6];

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, IncomeProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_income_profile;
    }

    @Override
    public void init() {
        setTitleText(R.string.income_profile);

        //initChart();
        initData();
    }

    private void initData() {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM");
        final String yearMonth = formate.format(new Date());
        showLoadingDialog();
        Subscription subscription = RetrofitHelper.getInstance().getMyprofile(yearMonth)
                .compose(RxUtil.<DataInfo<ProfileBean>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<ProfileBean>>() {
                    @Override
                    public void onError(Throwable e) {
                        dismiss();
                        doFailed();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataInfo<ProfileBean> profileBeanDataInfo) {

                        dismiss();
                        if (profileBeanDataInfo.success()) {
                            if (profileBeanDataInfo.data() != null){
                                total = profileBeanDataInfo.data().totalIncome;
                                rentfee = profileBeanDataInfo.data().housingPrice;
                                waterFee = profileBeanDataInfo.data().waterAmount;
                                eleFee = profileBeanDataInfo.data().electricAmount;
                                propertyFee = profileBeanDataInfo.data().propertyAmount;
                                lifterFee = profileBeanDataInfo.data().litterAmount;
                                costFee = profileBeanDataInfo.data().costAmount;
                                //values = new Integer[]{rentfee, waterFee, eleFee, propertyFee, lifterFee, costFee};

                                values[0] = rentfee;
                                values[1] = waterFee;
                                values[2] = eleFee;
                                values[3] = propertyFee;
                                values[4] = lifterFee;
                                values[5] = costFee;

                                mTvRent.setText("￥"+rentfee);
                                mTvWaterFee.setText("￥"+waterFee);
                                mTvEle.setText("￥"+eleFee);
                                mTvPropertyFee.setText("￥"+propertyFee);
                                mTvLitter.setText("￥"+lifterFee);
                                mTvCost.setText("￥"+costFee);
                                mTvTime.setText(yearMonth);
                                initChart(total);

                            }else {
                                showToast(profileBeanDataInfo.msg());
                            }

                        }else {
                            showToast(profileBeanDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void initChart(int mTotal) {
       /* ArrayList<PieEntry> mData = new ArrayList<>();
        mData.add(new PieEntry(18800f,"房租"));
        mData.add(new PieEntry(500f,"水费"));
        mData.add(new PieEntry(500f,"电费"));
        mData.add(new PieEntry(1500f,"物业管理费"));
        mData.add(new PieEntry(1500f,"垃圾处理费"));
        mData.add(new PieEntry(200f,"网费"));
        setData(mData);*/
        //mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(77f);
        mChart.setTransparentCircleRadius(77f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);
        setData(6,mTotal);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("￥"+total+"\n本期总收入");
        /*s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);*/
        return s;
    }

    private void setData(int count,int sum) {


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < values.length; i++) {
            entries.add(new PieEntry((float)values[i]/(float)sum, mParties[i % mParties.length]));
        }

       /* for (int i = 0; i < count; i++) {
            entries.add(new PieEntry((float) (Math.random() * 100) + 100 / 5, mParties[i % mParties.length]));
        }*/
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);*/
        colors.add(0, this.getResources().getColor(R.color.color_rent));
        colors.add(1, this.getResources().getColor(R.color.color_water));
        colors.add(2, this.getResources().getColor(R.color.color_ele));
        colors.add(3, this.getResources().getColor(R.color.color_property));
        colors.add(4, this.getResources().getColor(R.color.color_lj));
        colors.add(5, this.getResources().getColor(R.color.color_net));

        //colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        // data.setValueTypeface(tf);
        mChart.setData(data);
        //设置是否显示区域文字内容
        //设置是否显示区域百分比的值
        for (IDataSet<?> set : mChart.getData().getDataSets()) {
            set.setDrawValues(!set.isDrawValuesEnabled());
        }
        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
