package com.vidumanszky.imagemaskingsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by VBalazs on 2016-08-01.
 */
public class SnakyView extends View implements GestureDetector.OnGestureListener {

    private static final int SNAKE_LENGTH_DEFAULT = 5;

    //TODO: change it to speed!
    private static final int SNAKE_SLEEP = 75;

    private static final int SNAKE_RIGHT = 0;
    private static final int SNAKE_UP = 1;
    private static final int SNAKE_LEFT = 2;
    private static final int SNAKE_DOWN = 3;

    private Context context;

    private Paint borderPaint;
    private Paint snakePaint;
    private Paint foodPaint;
    private Paint clearPaint;

    private int rectWidth;
    private int snakeLength;
    private int numWidth;
    private int numHeight;

    private int areaLeft;
    private int areaTop;
    private int areaRight;
    private int areaBottom;
    private List<Rect> borderList;

    private List<Rect> snakeList;

    private Rect currentSnakeRect;

    private int snakeLeft;
    private int snakeTop;
    private int snakeRight;
    private int snakeBottom;

    private int centerX;
    private int centerY;

    private Rect foodRect;
    private boolean isFoodEaten = true;

    private GestureDetectorCompat detector;
    private float clickX;
    private float clickY;
    private int snakeDirection;

    private Handler snakeHandler;
    private boolean isRunning;
    private boolean isStopped = true;

    public SnakyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    public void resume() {
        snakeHandler = new Handler();
        if (isStopped) {
            start();
        }

        snakeHandler.postDelayed(runnable, 0);
        isRunning = true;
    }

    public void pause() {
        snakeHandler.removeCallbacks(runnable);
        isRunning = false;
    }

    public void start() {
        init();
        isStopped = false;
    }

    public void stop() {
        pause();
        isStopped = true;
    }

    private void init() {
        snakeHandler = new Handler();

        clearPaint = new Paint();
        clearPaint.setColor(Color.WHITE);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);

        snakePaint = new Paint();
        snakePaint.setColor(Color.BLACK);

        foodPaint = new Paint();
        foodPaint.setColor(Color.BLACK);

        detector = new GestureDetectorCompat(context, this);

        snakeList = new ArrayList<>();

        snakeLeft = 0;
        snakeTop = 0;
        snakeRight = 0;
        snakeBottom = 0;

        snakeDirection = SNAKE_RIGHT;

        snakeLength = SNAKE_LENGTH_DEFAULT;

        invalidate();
    }

    //TODO: if its dimensions changing in runningTime
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBorder(canvas);

        clearCanvas(canvas);

        drawSnake(canvas);

        drawFood(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return true;
    }

    private void clearCanvas(Canvas canvas) {
        //TODO: canvas clear
        canvas.drawRect(areaLeft, areaTop, areaRight, areaBottom, clearPaint);
    }

    //TODO: onMeasure!!
    private void drawBorder(Canvas canvas) {
        if (borderList == null) {
            borderList = new ArrayList<>();
        }

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        rectWidth = canvasWidth / 20;

        int borderLeft = 0;
        int borderTop = 0;
        int borderRight = rectWidth;
        int borderBottom = rectWidth;

        for (int i = 0; i<4; i++) {

            while(true) {
                Rect currentBorderItem = new Rect(borderLeft, borderTop, borderRight, borderBottom);

                borderList.add(currentBorderItem);

                if (i == 0) {
                    if (borderBottom + rectWidth < canvasHeight) {
                        borderTop = borderTop + rectWidth;
                        borderBottom = borderBottom + rectWidth;
                    } else {
                        areaBottom = borderTop;
                        break;
                    }
                } else if (i == 1) {
                    if (borderRight + rectWidth < canvasWidth) {
                        borderRight = borderRight + rectWidth;
                        borderLeft = borderLeft + rectWidth;
                    } else {
                        areaRight = borderLeft;
                        break;
                    }
                } else if (i == 2) {
                    if (borderTop - rectWidth >= 0) {
                        borderTop = borderTop - rectWidth;
                        borderBottom = borderBottom - rectWidth;
                    } else {
                        areaTop = borderBottom;
                        break;
                    }
                } else if (i == 3) {
                    if (borderLeft - rectWidth >= 0) {
                        borderRight = borderRight - rectWidth;
                        borderLeft = borderLeft - rectWidth;
                    } else {
                        areaLeft = borderRight;
                        break;
                    }
                }
            }
        }

        drawRects(borderList, canvas, borderPaint);

        initCenter();
    }

    private void initCenter() {
        int areaWidth = areaRight - areaLeft;
        int areaHeight = areaBottom - areaTop;

        numWidth = (areaWidth / rectWidth);
        numHeight = (areaHeight / rectWidth);

        centerX = (numWidth / 2)*rectWidth + areaLeft;
        centerY = (numHeight / 2)*rectWidth + areaTop;
    }

    private void drawSnake(Canvas canvas) {
        if (snakeList != null) {
            for (Rect r : snakeList) {
                canvas.drawRect(r, snakePaint);
            }
        }
    }

    private void drawFood(Canvas canvas) {
        if (isFoodEaten) {
            int foodTop;
            int foodLeft;

            while (true) {
                foodTop = areaTop + getRandom(0, numHeight) * rectWidth;
                foodLeft = areaLeft + getRandom(0, numWidth) * rectWidth;

                foodRect = new Rect(foodLeft, foodTop, foodLeft + rectWidth, foodTop + rectWidth);

                if (!snakeList.contains(foodRect)) {
                    break;
                }
            }
        }

        canvas.drawRect(foodRect, foodPaint);

        isFoodEaten = false;
    }

    private void drawRects(List<Rect> rects, Canvas canvas, Paint paint) {
        for (Rect r : rects) {
            canvas.drawRect(r, paint);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            calSnakePos();

            if (isRunning) {
                snakeList.add(currentSnakeRect);

                if (snakeList.size() >= snakeLength) {
                    snakeList.remove(0);
                }

                snakeHandler.postDelayed(this, SNAKE_SLEEP);
            }

            invalidate();
        }
    };

    private void calSnakePos() {
        if (snakeLeft == 0 && snakeTop == 0 && snakeRight == 0 && snakeBottom == 0) {
            snakeLeft = centerX;
            snakeTop = centerY;
            snakeRight = centerX + rectWidth;
            snakeBottom = centerY + rectWidth;

        } else {
            switch (snakeDirection) {
                case SNAKE_UP:
                    snakeBottom = snakeBottom - rectWidth;
                    snakeTop = snakeTop - rectWidth;
                    break;
                case SNAKE_LEFT:
                    snakeLeft = snakeLeft - rectWidth;
                    snakeRight = snakeRight - rectWidth;
                    break;
                case SNAKE_DOWN:
                    snakeBottom = snakeBottom + rectWidth;
                    snakeTop = snakeTop + rectWidth;
                    break;
                case SNAKE_RIGHT:
                    snakeLeft = snakeLeft + rectWidth;
                    snakeRight = snakeRight + rectWidth;
                    break;
            }
        }

        currentSnakeRect = new Rect(snakeLeft, snakeTop, snakeRight, snakeBottom);

        checkSnakeSelf();
        checkBorder();
        checkFood();
    }

    private void checkSnakeSelf() {
        if (snakeList.contains(currentSnakeRect)) {
            snakePaint.setColor(Color.RED);
            stop();
        }
    }

    private void checkBorder() {
        if (borderList.contains(currentSnakeRect)) {
            borderPaint.setColor(Color.RED);
            stop();
        }
    }

    private void checkFood() {
        if (snakeList.contains(foodRect)) {
            ++snakeLength;
            isFoodEaten = true;
        }
    }

    private int getRandom(int min, int max) {
        Random r = new Random();
        int random = r.nextInt(max - min) + min;
        return random;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        clickX = e.getX();
        clickY = e.getY();

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //nothing here
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        float endX = e.getX();
        float endY = e.getY();

        if (isAClick(endX, endY)) {

            if (isRunning) {
                pause();
            } else {
                resume();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //nothing here
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //nothing here
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (isRunning) {
            int snakeDirection;
            if (Math.abs(velocityX) >= 1.5 * Math.abs(velocityY)) {
                if (velocityX < 0.0) {
                    snakeDirection = SNAKE_LEFT;
                } else {
                    snakeDirection = SNAKE_RIGHT;
                }
            } else {
                if (velocityY < 0.0) {
                    snakeDirection = SNAKE_UP;
                } else {
                    snakeDirection = SNAKE_DOWN;
                }
            }

            if (this.snakeDirection == SNAKE_LEFT && snakeDirection == SNAKE_UP || this.snakeDirection == SNAKE_LEFT && snakeDirection == SNAKE_DOWN) {
                this.snakeDirection = snakeDirection;
            }

            if (this.snakeDirection == SNAKE_RIGHT && snakeDirection == SNAKE_UP || this.snakeDirection == SNAKE_RIGHT && snakeDirection == SNAKE_DOWN) {
                this.snakeDirection = snakeDirection;
            }

            if (this.snakeDirection == SNAKE_UP && snakeDirection == SNAKE_LEFT || this.snakeDirection == SNAKE_UP && snakeDirection == SNAKE_RIGHT) {
                this.snakeDirection = snakeDirection;
            }

            if (this.snakeDirection == SNAKE_DOWN && snakeDirection == SNAKE_LEFT || this.snakeDirection == SNAKE_DOWN && snakeDirection == SNAKE_RIGHT) {
                this.snakeDirection = snakeDirection;
            }
            return true;
        }
        return false;
    }

    private boolean isAClick(float endX, float endY) {
        float differenceX = Math.abs(clickX - endX);
        float differenceY = Math.abs(clickY - endY);

        if (differenceX > 5 ||differenceY > 5) {
            return false;
        }
        return true;
    }
}
