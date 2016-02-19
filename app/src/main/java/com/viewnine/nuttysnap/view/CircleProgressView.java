package com.viewnine.nuttysnap.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.activity.MainActivity;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2/2/16.
 */
public class CircleProgressView extends View {
    private RectF rectF;
    Paint paintStrokeInactive, paintStrokeActivated, paintBgActivated, paintClear, paintBgInactivated, paintText;
    private RectF rectF2;
    boolean isRecording = false;
    private int positionCircleX;
    private int radiusCircle;
    private OnProgressListener circleTouchListener;

    private static final int MAX_CLICK_DURATION = 300;
    private long startClickTime;
    private long clickDuration = 0;
    Handler handler = new Handler();
    Timer timer;

    private static int UPDATE_PROGRESS_INTERVAL = 10; //millisecond
    private static int CIRCLE_DEGREE = 360;
    private int maxTimeToFillFullCircle = 5 * 1000; //Max time to fill all progress
    private float rangeDegree = 0;
    private final int STROCK_WIDTH = 10;

    public CircleProgressView(Context context) {
        super(context);
//        initRect();
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        initRect();
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initRect();
    }

    public interface OnProgressListener {
        public void singleTouch();
        public void startLongTouch();
        public void stopLongTouch();
        public void progressIsReset();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw bg
        canvas.drawCircle(positionCircleX, positionCircleX, getMeasuredWidth() / 2, paintBgInactivated);

        //Draw circle stroke inactivated
        canvas.drawArc(rectF, 0, CIRCLE_DEGREE, false, paintStrokeInactive);

        //Draw stroke progress
        canvas.drawArc(rectF2, 270, value, false, paintStrokeActivated);

        if(isRecording){
//            Log.d(CircleProgressView.class.getSimpleName(), "draw circle view");
            canvas.drawCircle(positionCircleX, positionCircleX, radiusCircle, paintBgActivated);
        }else {
//            Log.d(CircleProgressView.class.getSimpleName(), "Clear circle view");
            canvas.drawCircle(positionCircleX, positionCircleX, radiusCircle, paintClear);
        }

        //Draw percent
        String percent = String.valueOf((int)(value / CIRCLE_DEGREE * 100)) + " %";
        canvas.drawText(percent, getMeasuredWidth() / 2 - 10, getMeasuredWidth() / 2, paintText);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        checkClickEvent();
                        break;
                    case MotionEvent.ACTION_UP:
                        long duration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(duration < MAX_CLICK_DURATION){
                            Log.d(MainActivity.class.getSimpleName(), "Remove checkClickEvent() callbacks");
                            handler.removeCallbacks(clickEventRunnable);
                           singleTouch();
                        }else {

                            if(isRecording){
                                notifyStopRecording();
                            }
                        }
                        break;
                }
                return true;
    }

    public void setOnCircleTouchListener(OnProgressListener onCircleTouchListener){
        this.circleTouchListener = onCircleTouchListener;
    }

    private void updateRangeDegree(){
        rangeDegree = ((float)UPDATE_PROGRESS_INTERVAL * CIRCLE_DEGREE) / maxTimeToFillFullCircle;
    }

    /**
     *
     * @param maxTime: millisecond
     */
    public void setMaxTimeToFillFullCircle(int maxTime){
        maxTimeToFillFullCircle = maxTime;
        updateRangeDegree();
    }

    private void initData(){
        updateRangeDegree();
        initRect();
    }

    private void initRect(){
        int strokeWidth = STROCK_WIDTH;
        int diameter = getMeasuredWidth() - strokeWidth;
        int leftPosition = getMeasuredWidth() / 2 - diameter/2 ;
        positionCircleX = getMeasuredWidth() / 2;
//        radiusCircle = diameter / 2  - strokeWidth;
        radiusCircle = diameter / 2 - strokeWidth / 2;
        rectF = new RectF(leftPosition,leftPosition, diameter + leftPosition , diameter + leftPosition);
        paintStrokeInactive = new Paint();
        paintStrokeInactive.setColor(getContext().getResources().getColor(R.color.bg_record_stroke_inactive));
        paintStrokeInactive.setStrokeWidth(strokeWidth);
        paintStrokeInactive.setStyle(Paint.Style.STROKE);
        paintStrokeInactive.setFlags(Paint.ANTI_ALIAS_FLAG);

        rectF2 = new RectF(leftPosition,leftPosition, diameter + leftPosition , diameter + leftPosition);

        paintStrokeActivated = new Paint();
        paintStrokeActivated.setColor(getResources().getColor(R.color.bg_record_stroke_activated));
        paintStrokeActivated.setStrokeWidth((int) (strokeWidth));
        paintStrokeActivated.setStyle(Paint.Style.STROKE);
        paintStrokeActivated.setFlags(Paint.ANTI_ALIAS_FLAG);


        paintBgActivated = new Paint();
        paintBgActivated.setColor(getResources().getColor(R.color.bg_record_button_activated));
        paintBgActivated.setStyle(Paint.Style.FILL);
        paintBgActivated.setFlags(Paint.ANTI_ALIAS_FLAG);

        paintClear = new Paint();
        paintClear.setColor(Color.TRANSPARENT);
        paintClear.setStyle(Paint.Style.FILL);
        paintClear.setFlags(Paint.ANTI_ALIAS_FLAG);

        paintBgInactivated = new Paint();
        paintBgInactivated.setColor(getContext().getResources().getColor(R.color.bg_record_button_inactive));
        paintBgInactivated.setStyle(Paint.Style.FILL);
        paintBgInactivated.setFlags(Paint.ANTI_ALIAS_FLAG);

        paintText = new Paint();
        paintText.setColor(Color.YELLOW);
        paintText.setTextSize(35);
    }

    float value = 0;
    public void updateProgress(float value){
        this.value = value;
        isRecording = true;
        invalidate();
    }

    public void stopLongTouch(){
        if(circleTouchListener != null){
            circleTouchListener.stopLongTouch();
        }
        value = 0;
        isRecording = false;
        invalidate();
    }

    private void startLongTouch(){
        if(circleTouchListener != null){
            circleTouchListener.startLongTouch();
        }


    }

    private void singleTouch(){

        if(circleTouchListener != null){
            circleTouchListener.singleTouch();
        }
    }

    private void checkClickEvent(){
        handler.postDelayed(clickEventRunnable, MAX_CLICK_DURATION);
    }

    private Runnable clickEventRunnable = new Runnable() {
        @Override
        public void run() {
            clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
            if(clickDuration < MAX_CLICK_DURATION){
                singleTouch();
            }else {
                notifyStartProgress();
            }
        }
    };



    private void notifyStartProgress(){

        if(timer != null){
            timer.cancel();
        }
        startLongTouch();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                checkToUpdateProgress();
            }
        }, 100, UPDATE_PROGRESS_INTERVAL);
    }

    private void notifyStopRecording(){

        if(timer != null){
            timer.cancel();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopLongTouch();
                    }
                });

            }
        }, 100);
    }


    private void checkToUpdateProgress(){
        if (value >= CIRCLE_DEGREE) {
//            if(circleTouchListener != null){
//                circleTouchListener.stopLongTouch();
//            }
//
//            value = 0;
            notifyStopRecording();
        }

        value += rangeDegree;
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProgress(value);
            }
        });

    }
}
