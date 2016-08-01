package com.vidumanszky.imagemaskingsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by VBalazs on 2016-07-26.
 */
public class HexagonView extends View {

    private Paint hexagonPaint;

    private Path hexagonPath = new Path();

    private float centerX;
    private float centerY;
    private float radius;

    public HexagonView(Context context) {
        super(context);

        init();
    }

    public HexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        hexagonPaint = new Paint();
        hexagonPaint.setColor(Color.BLUE);
        hexagonPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        centerX = Math.round(canvasWidth / 2);
        centerY = Math.round(canvasHeight / 2);

        radius = (canvasWidth > canvasHeight ? canvasWidth : canvasHeight) / 4;

        calculatePath();

        canvas.drawPath(hexagonPath, hexagonPaint);
    }

    private void calculatePath() {
        float corner = 50;
        CornerPathEffect cornerPathEffect = new CornerPathEffect(corner);
        hexagonPaint.setPathEffect(cornerPathEffect);

        float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
        hexagonPath.moveTo(centerX, centerY + radius);
        hexagonPath.lineTo(centerX - triangleHeight, centerY + radius/2);
        hexagonPath.lineTo(centerX - triangleHeight, centerY - radius/2);
        hexagonPath.lineTo(centerX, centerY - radius);
        hexagonPath.lineTo(centerX + triangleHeight, centerY - radius/2);
        hexagonPath.lineTo(centerX + triangleHeight, centerY + radius/2);
        hexagonPath.close();
    }
}
