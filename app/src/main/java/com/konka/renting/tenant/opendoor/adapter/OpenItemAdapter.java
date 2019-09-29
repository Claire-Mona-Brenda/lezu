package com.konka.renting.tenant.opendoor.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.konka.renting.R;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.tenant.opendoor.OpenRotationAnimatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import butterknife.BindView;

public class OpenItemAdapter extends PagerAdapter {
    private final int START_TIMER = 111;
    private final int CANCLE_TIMER = 222;

    private Context mContext;
    private List<OpenDoorListbean> mData;
    List<View> viewList = new ArrayList<>();
    List<String> timerIdList = new ArrayList<>();
    Map<String, Timer> timerMap = new HashMap<>();
    private OnCall onCall;

    public OpenItemAdapter(Context mContext, List<OpenDoorListbean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.adapter_open_item, null);
        ImageView mImgStatus = view.findViewById(R.id.adapter_open_item_img_status);
        ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
        OpenRotationAnimatorView mImgOpenAnimation = view.findViewById(R.id.adapter_open_item_img_open_animation);
        TextView mTvName = view.findViewById(R.id.adapter_open_item_tv_name);
        TextView mTvMoreHistory = view.findViewById(R.id.adapter_open_item_tv_more_history);
        TextView mTvRentTimeStart = view.findViewById(R.id.adapter_open_item_tv_rent_time_start);
        TextView mTvRentTimeEnd = view.findViewById(R.id.adapter_open_item_tv_rent_time_end);

        TextView mTvOpenPwd = view.findViewById(R.id.adapter_open_item_tv_open_pwd);
        TextView mTvAddUser = view.findViewById(R.id.adapter_open_item_tv_add_user);
        TextView mTvGatewaySet = view.findViewById(R.id.adapter_open_item_tv_gateway_set);
        TextView mTvManager = view.findViewById(R.id.adapter_open_item_tv_manager);
        TextView mTvSync = view.findViewById(R.id.adapter_open_item_tv_sync);

        LinearLayout mLlOpenPwd = view.findViewById(R.id.adapter_open_item_ll_open_pwd);
        LinearLayout mLlAddUser = view.findViewById(R.id.adapter_open_item_ll_add_user);
        LinearLayout mLlGatewaySet = view.findViewById(R.id.adapter_open_item_ll_gateway_set);
        LinearLayout mLlManager = view.findViewById(R.id.adapter_open_item_ll_manager);
        LinearLayout mLlSync = view.findViewById(R.id.adapter_open_item_ll_sync);


        OpenDoorListbean doorListbean = mData.get(position);
        mImgStatus.setImageResource(doorListbean.getStatus() == 2 ? R.mipmap.opendoor_notcheckin_icon : R.mipmap.opendoor_checkin_icon);
        mTvName.setText(doorListbean.getRoom_name() == null ? "- -" : doorListbean.getRoom_name());
        mTvRentTimeStart.setText(TextUtils.isEmpty(doorListbean.getStart_time()) ? " - - " : doorListbean.getStart_time().split(" ")[0]);
        mTvRentTimeEnd.setText(TextUtils.isEmpty(doorListbean.getEnd_time()) ? " - - " : doorListbean.getEnd_time().split(" ")[0]);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.adapter_open_item_img_open://开门
                        if (onCall != null) {
                            onCall.onClickOpen(position);
                        }
                        break;
                    case R.id.adapter_open_item_tv_more_history://更多
                        if (onCall != null) {
                            onCall.onClickHistoryMore(position);
                        }
                        break;
                    case R.id.adapter_open_item_ll_open_pwd://开门密码
                        if (onCall != null) {
                            onCall.onClickOpenPwd(position);
                        }
                        break;
                    case R.id.adapter_open_item_ll_add_user://添加使用者
                        if (onCall != null) {
                            onCall.onClickAddUser(position);
                        }
                        break;
                    case R.id.adapter_open_item_ll_gateway_set://网关设置
                        if (onCall != null) {
                            onCall.onClickGateway(position);
                        }
                        break;
                    case R.id.adapter_open_item_ll_manager://管理员密码
                        if (onCall != null) {
                            onCall.onClickManager(position);
                        }
                        break;
                    case R.id.adapter_open_item_ll_sync://同步服务费
                        if (onCall != null) {
                            onCall.onClickSync(position);
                        }
                        break;
                }

            }
        };

        mImgOpen.setOnClickListener(clickListener);
        mTvMoreHistory.setOnClickListener(clickListener);
        mLlOpenPwd.setOnClickListener(clickListener);
        mLlAddUser.setOnClickListener(clickListener);
        mLlGatewaySet.setOnClickListener(clickListener);
        mLlManager.setOnClickListener(clickListener);
        mLlSync.setOnClickListener(clickListener);

        view.setTag(position);
        viewList.add(view);
        container.addView(view);

        mImgOpen.setEnabled(true);
        int size = timerIdList.size();
        for (int i = 0; i < size; i++) {
            if (timerIdList.get(i).equals(doorListbean.getOrder_id())) {
                startOpenAnimation(position);
                break;
            }
        }

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        int size = timerIdList.size();
        for (int i = 0; i < size; i++) {
            if (timerIdList.get(i).equals(mData.get(position).getOrder_id())) {
                cancelOpenAnimation(position);
                break;
            }
        }
        viewList.remove(object);
        container.removeView((View) object);
    }

    public void startOpenAnimation(int position) {
        if (position >= getCount())
            return;
        OpenDoorListbean doorListbean = mData.get(position);
        if (doorListbean.getOrder_id() == null)
            return;
        View view = null;
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            int tag = (int) viewList.get(i).getTag();
            if (tag == position) {
                view = viewList.get(i);
                break;
            }
        }
        if (view == null)
            return;
        ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
        OpenRotationAnimatorView mImgOpenAnimation = view.findViewById(R.id.adapter_open_item_img_open_animation);

        if (mImgOpenAnimation.getTag() != null) {
            AnimatorSet animatorSet = (AnimatorSet) mImgOpenAnimation.getTag();
            animatorSet.cancel();
            mImgOpenAnimation.setTag(null);
        }

        mImgOpen.setEnabled(false);
        mImgOpenAnimation.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", 0f, 360f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", -360f, 0f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", 0f, 360f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", -360f, 0f);
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", 0f, 360f);
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", -360f, 0f);
        ObjectAnimator objectAnimator7 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", 0f, 360f);
        ObjectAnimator objectAnimator8 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", -360f, 0f);
        ObjectAnimator objectAnimator9 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", 0f, 360f);
        ObjectAnimator objectAnimator10 = ObjectAnimator.ofFloat(mImgOpenAnimation, "endAngle", -360f, 0f);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mImgOpenAnimation.setVisibility(View.GONE);
                mImgOpenAnimation.setTag(null);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mImgOpenAnimation.setVisibility(View.GONE);
                mImgOpenAnimation.setTag(null);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mImgOpenAnimation.setTag(animatorSet);
        animatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3, objectAnimator4,
                objectAnimator5, objectAnimator6, objectAnimator7, objectAnimator8,
                objectAnimator9, objectAnimator10);
        animatorSet.start();

    }

    public void cancelOpenAnimation(int position) {
        if (position >= getCount())
            return;
        OpenDoorListbean doorListbean = mData.get(position);
        if (doorListbean.getOrder_id() == null)
            return;

        View view = null;
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            int tag = (int) viewList.get(i).getTag();
            if (tag == position) {
                view = viewList.get(i);
                break;
            }
        }
        if (view == null)
            return;
        ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
        OpenRotationAnimatorView mImgOpenAnimation = view.findViewById(R.id.adapter_open_item_img_open_animation);

        if (mImgOpenAnimation.getTag() != null) {
            AnimatorSet animatorSet = (AnimatorSet) mImgOpenAnimation.getTag();
            animatorSet.cancel();
            mImgOpenAnimation.setTag(null);
        }
    }

    public void startTimer(int position) {
        if (position >= getCount())
            return;
        OpenDoorListbean doorListbean = mData.get(position);
        if (doorListbean.getOrder_id() == null)
            return;
        View view = null;
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            int tag = (int) viewList.get(i).getTag();
            if (tag == position) {
                view = viewList.get(i);
                break;
            }
        }
        if (view == null)
            return;
        ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
        mImgOpen.setEnabled(false);

        String order_id = doorListbean.getOrder_id();
        timerIdList.add(order_id);
        Timer timer = new Timer();
        timerMap.put(order_id, timer);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //要推迟执行的方法
                timerIdList.remove(order_id);
                timerMap.remove(order_id);
                Message message = new Message();
                message.what = START_TIMER;
                message.arg1 = position;
                handler.sendMessage(message);

            }
        };
        timer.schedule(task, 10000);
    }

    public void cancelTimer(int position) {
        if (position >= getCount())
            return;
        OpenDoorListbean doorListbean = mData.get(position);
        if (doorListbean.getOrder_id() == null)
            return;

        String order_id = doorListbean.getOrder_id();
        timerIdList.remove(order_id);
        Timer timer = timerMap.remove(order_id);
        if (timer != null) {
            timer.cancel();
        }
        View view = null;
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            int tag = (int) viewList.get(i).getTag();
            if (tag == position) {
                view = viewList.get(i);
                break;
            }
        }
        if (view == null)
            return;
        ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
        mImgOpen.setEnabled(true);

        OpenRotationAnimatorView mImgOpenAnimation = view.findViewById(R.id.adapter_open_item_img_open_animation);
        if (mImgOpenAnimation.getTag() != null) {
            AnimatorSet animatorSet = (AnimatorSet) mImgOpenAnimation.getTag();
            animatorSet.cancel();
            mImgOpenAnimation.setTag(null);
        }
        Log.e("12313","==========cancelTimer=====GONE");
        mImgOpenAnimation.setVisibility(View.GONE);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_TIMER:
                    View view = null;
                    int size = viewList.size();
                    for (int i = 0; i < size; i++) {
                        int tag = (int) viewList.get(i).getTag();
                        if (tag == msg.arg1) {
                            view = viewList.get(i);
                            break;
                        }
                    }
                    if (view == null)
                        return;
                    ImageView mImgOpen = view.findViewById(R.id.adapter_open_item_img_open);
                    mImgOpen.setEnabled(true);
                    break;
            }
        }
    };

    public void setOnCall(OnCall onCall) {
        this.onCall = onCall;
    }

    public interface OnCall {
        void onClickOpen(int position);

        void onClickHistoryMore(int position);

        void onClickOpenPwd(int position);

        void onClickGateway(int position);

        void onClickSync(int position);

        void onClickAddUser(int position);

        void onClickManager(int position);
    }
}
