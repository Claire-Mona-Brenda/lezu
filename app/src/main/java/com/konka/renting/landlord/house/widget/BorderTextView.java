package com.konka.renting.landlord.house.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ATI on 2017/8/2.
 */
public class BorderTextView extends TextView{
    boolean isrun=true;
    int y=0;
    int x=10;
    Paint paint2;
    Path path=new Path();

    public BorderTextView(Context context) {
        super(context);
        init();
    }
    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }
    public BorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        RectF rectF=new RectF(2,2,this.getWidth()-2,this.getHeight()-2);
        canvas.drawRoundRect(rectF,10,10,paint2);


//        canvas.drawPath(path,paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        isrun=false;
        super.onDetachedFromWindow();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void init(){
        setLayerType(LAYER_TYPE_SOFTWARE, paint2);
        paint2=new Paint();
        paint2.setColor(Color.parseColor("#55864A"));
        paint2.setStrokeWidth(5);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
        //paint2.setShadowLayer(7, 0, 0, Color.YELLOW);

    }

}
