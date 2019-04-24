package com.konka.renting.guide;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.utils.SharedPreferenceUtil;
import com.konka.renting.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


public class GuideActivity extends BaseActivity {

    private ViewPager root_vip;
    private TextView tvGuide1, tvGuide2, tvGuide3;
    public static final String IS_NEED_SHOW_GUIDE = "is_need_show_guide";
    private GuideFragmentsAdapter mGuideFragmentsAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private GestureDetector gestureDetector; // 用户滑动
    private int flaggingWidth;// 滑动关闭引导页所需滚动的长度
    private int currentItem = 0; // 当前图片的位置

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void init() {

        gestureDetector = new GestureDetector(new GuideViewTouch());
        flaggingWidth = UIUtils.dip2px(20);

        root_vip = (ViewPager) findViewById(R.id.root_vip);
//        tvGuide1 = (TextView) findViewById(R.id.tv_guide_1);
//        tvGuide2 = (TextView)findViewById(R.id.tv_guide_2);
//        tvGuide3 = (TextView)findViewById(R.id.tv_guide_3);

        mFragments.add(GuideFragment.newInstance(0));
        mFragments.add(GuideFragment.newInstance(1));
        mFragments.add(GuideFragment.newInstance(2));

//        selectGuide(0);

        mGuideFragmentsAdapter = new GuideFragmentsAdapter(getSupportFragmentManager(), mFragments);
        root_vip.setAdapter(mGuideFragmentsAdapter);
        root_vip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
//                selectGuide(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void selectGuide(int position) {
        tvGuide1.setSelected(position == 0);
        tvGuide2.setSelected(position == 1);
        tvGuide3.setSelected(position == 2);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 当滑动到最后一页时，继续滑动将进入到联赛选择列表页
     */
    private class GuideViewTouch extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            if (currentItem == 2) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()
                        - e2.getY()) && (e1.getX() - e2.getX() <= (-flaggingWidth)
                        || e1.getX() - e2.getX() >= flaggingWidth)) {
                    if (e1.getX() - e2.getX() >= flaggingWidth) {
                        SharedPreferenceUtil.setBoolean(getApplicationContext(), GuideActivity.IS_NEED_SHOW_GUIDE, false);
                        Intent intent = new Intent(GuideActivity.this, LoginNewActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
