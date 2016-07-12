package com.vidumanszky.imagemaskingsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by VBalazs on 2016-07-12.
 */
public class PathRendererView extends View {

    private Paint fillPaint;
    private Paint strokePaint;

    private Bitmap fillBmp;
    private BitmapShader fillBmpShader;

    private Matrix m = new Matrix();

    private float posX = 150;
    private float posY = 150;

    private float radius = 100;

    public PathRendererView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);

        strokePaint = new Paint();
        strokePaint.setDither(true);
        strokePaint.setColor(0xFFFFFF00);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(3);

        fillBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bricks);

        fillBmpShader = new BitmapShader(fillBmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        fillPaint = new Paint();
        fillPaint.setColor(0xFFFFFFFF);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setShader(fillBmpShader);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                posX = event.getX();
                posY = event.getY();
                break;
        }

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getMatrix().invert(m);
        fillBmpShader.setLocalMatrix(m);

        canvas.drawCircle(posX, posY, radius, fillPaint);
        canvas.drawCircle(posX, posY, radius, strokePaint);
    }
}
