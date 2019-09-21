package com.konka.renting.tenant.opendoor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.konka.renting.R;
import com.konka.renting.utils.UIUtils;


public class OpenRotationAnimatorView extends View {
    private float startAngle = 0;
    private float endAngle = 0;
    float w, h;
    float strokeWidth;

    Paint anglePaint;
    Context mContext;

    public OpenRotationAnimatorView(Context context) {
        super(context);
        init(context);
    }

    public OpenRotationAnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OpenRotationAnimatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;

        strokeWidth = context.getResources().getDimensionPixelSize(R.dimen.dp_4);
        anglePaint = new Paint();
        anglePaint.setColor(Color.parseColor("#FFB97D"));
        anglePaint.setStyle(Paint.Style.STROKE);
        anglePaint.setStrokeWidth(strokeWidth);
        anglePaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        w = getMeasuredWidth();
        h = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF oval = new RectF(strokeWidth, strokeWidth, w-strokeWidth, h-strokeWidth);
        canvas.drawArc(oval, startAngle, endAngle, false, anglePaint);
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        invalidate();

    }

    public float getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
        invalidate();

    }
}
