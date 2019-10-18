package com.konka.renting.tenant.opendoor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PwdResultActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activity_pwd_result_tv_title)
    TextView mTvTitle;
    @BindView(R.id.activity_pwd_result_tv_pwd)
    TextView mTvPwd;
    @BindView(R.id.activity_pwd_result_tv_timer_tips_start)
    TextView mTipsStart;
    @BindView(R.id.activity_pwd_result_tv_timer_start)
    TextView mTvTimerStart;
    @BindView(R.id.activity_pwd_result_tv_timer_tips_end)
    TextView mTvTimerTipsEnd;
    @BindView(R.id.activity_pwd_result_tv_timer_end)
    TextView mTvTimerEnd;
    @BindView(R.id.activity_pwd_result_rl_timer)
    RelativeLayout mRlTimer;
    @BindView(R.id.activity_pwd_result_tv_times_tips_start)
    TextView mTvTimesTipsStart;
    @BindView(R.id.activity_pwd_result_tv_times_start)
    TextView mTvTimesStart;
    @BindView(R.id.activity_pwd_result_tv_times_tips_end)
    TextView mTvTimesTipsEnd;
    @BindView(R.id.activity_pwd_result_tv_times_end)
    TextView mTvTimesEnd;
    @BindView(R.id.activity_pwd_result_rl_times)
    RelativeLayout mRlTimes;
    @BindView(R.id.activity_pwd_result_tv_copy)
    TextView mTvCopy;

    String pwd;
    String start;
    String end;
    String times;
    String day;
    boolean isTimer;

    public static void toActivity(Context context, String pwd,String start, String end, String times, String day, boolean isTimer) {
        Intent intent = new Intent(context, PwdResultActivity.class);
        intent.putExtra("pwd", pwd);
        intent.putExtra("start", start);
        intent.putExtra("end", end);
        intent.putExtra("times", times);
        intent.putExtra("day", day);
        intent.putExtra("isTimer", isTimer);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pwd_result;
    }

    @Override
    public void init() {
        pwd = getIntent().getStringExtra("pwd");
        start = getIntent().getStringExtra("start");
        end = getIntent().getStringExtra("end");
        times = getIntent().getStringExtra("times");
        day = getIntent().getStringExtra("day");
        isTimer = getIntent().getBooleanExtra("isTimer", true);

        mTvPwd.setText(pwd);
        if (isTimer){
            mTvTitle.setText(R.string.timer_pwd);

            mRlTimer.setVisibility(View.VISIBLE);
            mRlTimes.setVisibility(View.GONE);

            mTvTimerStart.setText(start);
            mTvTimerEnd.setText(end);
        }else{
            mTvTitle.setText(R.string.times_pwd);

            mRlTimer.setVisibility(View.GONE);
            mRlTimes.setVisibility(View.VISIBLE);

            mTvTimesStart.setText(times);
            mTvTimesEnd.setText(day);
        }
    }


    @OnClick({R.id.iv_back, R.id.activity_pwd_result_tv_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_pwd_result_tv_copy:
                if (!TextUtils.isEmpty(pwd)) {
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", pwd);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    showToast(R.string.toach_copy_to_clipboard);
                }
                break;
        }
    }
}
