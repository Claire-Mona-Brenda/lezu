package com.konka.renting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LongClickLinearLayout extends LinearLayout {
    private Context mContext;
    private View view;
    private OnTouchEventCall mTouchEventCall;

    public LongClickLinearLayout(Context context) {
        super(context);
        mContext = context;
        view = this;
        init();
    }

    public LongClickLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        view = this;
        init();
    }

    public LongClickLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        view = this;
        init();
    }

    private void init() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                if (mTouchEventCall!=null)
                    mTouchEventCall.onUpEvent();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setmTouchEventCall(OnTouchEventCall mTouchEventCall) {
        this.mTouchEventCall = mTouchEventCall;
    }

    public interface OnTouchEventCall{
        public void onUpEvent();
    }
}
