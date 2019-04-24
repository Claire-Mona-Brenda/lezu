package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by kaite on 2018/3/18.
 */

public class MySurfaceView extends SurfaceView {

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        //设置裁剪的圆心，半径
        path.addCircle(1000/2, 1000/2, 700/ 2, Path.Direction.CCW);
        //裁剪画布，并设置其填充方式
        canvas.clipPath(path, Region.Op.REPLACE);
        super.draw(canvas);
    }
}
