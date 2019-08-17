package com.konka.renting.landlord.house.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;


public class OnTouchMapView extends RelativeLayout {
    private NestedScrollView scrollView;
    public OnTouchMapView(Context context) {
        super(context);
    }

    public OnTouchMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OnTouchMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setScrollView(NestedScrollView scrollView) {
        this.scrollView = scrollView;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
