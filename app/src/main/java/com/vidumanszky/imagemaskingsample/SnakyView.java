package com.vidumanszky.imagemaskingsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
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

    private static final int SNAKE_LENGTH_DEFAULT = 15;

    //TODO: change it to speed!
    private static final int SNAKE_SPEED = 155550;

    private static final int SNAKE_RIGHT = 0;
    private static final int SNAKE_UP = 1;
    private static final int SNAKE_LEFT = 2;
    private static final int SNAKE_DOWN = 3;

    private Context context;

    private Paint borderPaint;
    private Paint snakePaint;
    private Paint foodPaint;

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
    private int snakeDirection;

    private boolean isRunning;
    private int speedIndex;

    public SnakyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();

        reset();

        start();
    }

    public void start() {
        isRunning = true;
        loop();
        reset();
    }

    public void stop() {
        isRunning = false;

        Log.d("SNAKY_REF", "removeCallbacks");
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);

        snakePaint = new Paint();
        snakePaint.setColor(Color.BLACK);

        //food has to be have a hole
        foodPaint = new Paint();
        foodPaint.setColor(Color.BLACK);

        detector = new GestureDetectorCompat(context, this);
    }

    private void reset() {
        borderList = new ArrayList<>();
        snakeList = new ArrayList<>();

        snakeLeft = 0;
        snakeTop = 0;
        snakeRight = 0;
        snakeBottom = 0;

        snakeDirection = SNAKE_RIGHT;

        snakeLength = SNAKE_LENGTH_DEFAULT;
    }

    //TODO: if its dimensions changing in runningTime
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBorder(canvas);

        drawSnake(canvas);

        drawFood(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return true;
    }

    //TODO: onMeasure!!
    private void drawBorder(Canvas canvas) {
        if (borderList.size() == 0) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            rectWidth = canvasWidth / 40;

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

            initCenter();
        }

        drawRects(borderList, canvas, borderPaint);
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

    private void loop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO: why is that?
                SystemClock.sleep(200);

                while (isRunning) {
                    if (speedIndex % SNAKE_SPEED == 0) {
                        calSnakePos();

                        if (isRunning) {
                            snakeList.add(currentSnakeRect);

                            if (snakeList.size() >= snakeLength) {
                                snakeList.remove(0);
                            }
                        }

                        Log.d("SNAKY_REF", "postInvalidate");
                        postInvalidate();

                    }

                    if (speedIndex > 200000) {
                        speedIndex = 0;
                    }
                    ++speedIndex;
                }
            }
        }).start();
    }

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
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //nothing here
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
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
            if (Math.abs(velocityX) >= 1.2 * Math.abs(velocityY)) {

                Log.d("SNAKY_SWIPE", "velocityX > 1.2");

                if (velocityX < 0.0) {

                    Log.d("SNAKY_SWIPE", "velocityX < 0");

                    snakeDirection = SNAKE_LEFT;
                } else {

                    Log.d("SNAKY_SWIPE", "velocityX >= 0");

                    snakeDirection = SNAKE_RIGHT;
                }
            } else {

                Log.d("SNAKY_SWIPE", "velocityX <= 1.2");

                if (velocityY < 0.0) {

                    Log.d("SNAKY_SWIPE", "velocityY < 0");

                    snakeDirection = SNAKE_UP;
                } else {

                    Log.d("SNAKY_SWIPE", "velocityY >= 0");

                    snakeDirection = SNAKE_DOWN;
                }
            }

            Log.d("SNAKY_SWIPE", "snakeDirection: "+snakeDirection);

            if (this.snakeDirection == SNAKE_LEFT && snakeDirection == SNAKE_UP || this.snakeDirection == SNAKE_LEFT && snakeDirection == SNAKE_DOWN) {
                this.snakeDirection = snakeDirection;
                speedIndex = SNAKE_SPEED;
            }

            if (this.snakeDirection == SNAKE_RIGHT && snakeDirection == SNAKE_UP || this.snakeDirection == SNAKE_RIGHT && snakeDirection == SNAKE_DOWN) {
                this.snakeDirection = snakeDirection;
                speedIndex = SNAKE_SPEED;
            }

            if (this.snakeDirection == SNAKE_UP && snakeDirection == SNAKE_LEFT || this.snakeDirection == SNAKE_UP && snakeDirection == SNAKE_RIGHT) {
                this.snakeDirection = snakeDirection;
                speedIndex = SNAKE_SPEED;
            }

            if (this.snakeDirection == SNAKE_DOWN && snakeDirection == SNAKE_LEFT || this.snakeDirection == SNAKE_DOWN && snakeDirection == SNAKE_RIGHT) {
                this.snakeDirection = snakeDirection;
                speedIndex = SNAKE_SPEED;
            }
            return true;
        }
        return false;
    }
}
