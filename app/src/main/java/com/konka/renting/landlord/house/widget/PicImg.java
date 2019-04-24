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
import android.widget.ImageView;

/**
 * Created by ATI on 2017/8/2.
 */
public class PicImg extends ImageView{
    boolean isrun=true;
    int y=0;
    int x=10;
    Paint paint,paint2;
    Path path=new Path();
int rectcolor;
    public PicImg(Context context) {
        super(context);
        init();
    }
    public PicImg(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }
    public PicImg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        RectF rectF=new RectF(0,0,this.getWidth(),this.getHeight());
        canvas.drawRect(rectF, paint2);


//        canvas.drawPath(path,paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        isrun=false;
        super.onDetachedFromWindow();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void init(){
        rectcolor=Color.BLACK;
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        paint2=new Paint();
        paint2.setColor(rectcolor);
        paint2.setStrokeWidth(20);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
        //paint2.setShadowLayer(7, 0, 0, Color.YELLOW);

    }
    public void setRectColor(int color){
        rectcolor=color;
        paint2.setColor(rectcolor);
        postInvalidate();
    }
    public void setRectColor(String color){
        rectcolor=Color.parseColor(color);
        paint2.setColor(rectcolor);
        postInvalidate();
    }
}
