package com.example.harshit.playbackspeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by harshit on 5/2/18.
 */

public class PaintCustomView extends View {

    private Context context;
    private DrawListener listener;
    private Paint mPaint;
    private Path mPath;
    private Canvas mExtraCanvas;
    private Bitmap mExtraBitmap;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private float videoDuration;
    private float minDuration = 1;
    private List<float[]> speeds;
    private List<Float> listX;
    private List<Float> listY;
    private float A = 4;

    public PaintCustomView(Context context) {
        super(context);

        init(context);
    }

    public void setDuration(int duration){
        videoDuration = duration;
    }

    private void init(Context context) {
        this.context = context;
        listener = (DrawListener) context;
        speeds = new ArrayList<>();
        listX = new ArrayList<>();
        listY = new ArrayList<>();
        // Holds the path we are currently drawing.
        mPath = new Path();
        // Set up the paint with which to draw.
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        // Smoothens out edges of what is drawn without affecting shape.
        mPaint.setAntiAlias(true);
        // Dithering affects how colors with higher-precision device
        // than the are down-sampled.
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE); // default: FILL
        mPaint.setStrokeJoin(Paint.Join.ROUND); // default: MITER
        mPaint.setStrokeCap(Paint.Cap.ROUND); // default: BUTT
        mPaint.setStrokeWidth(12); // default: Hairline-width (really thin)
    }

    public PaintCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onSizeChanged(int width, int height,
                                 int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        // Create bitmap, create canvas with bitmap, fill canvas with color.
        mExtraBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        mExtraCanvas = new Canvas(mExtraBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the bitmap that stores the path the user has drawn.
        // Initially the user has not drawn anything
        // so we see only the colored bitmap.
        canvas.drawBitmap(mExtraBitmap, 0, 0, null);

        // Draw a frame around the picture.
        int inset = 40;
        @SuppressLint("DrawAllocation") Rect myFrame = new Rect(inset, inset,
                getScreenWidth() - inset, getScreenHeight() - inset);
        canvas.drawRect(myFrame, mPaint);
    }

    public float convertPixelsToDp(float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float X = convertPixelsToDp(event.getX());
        float Y = convertPixelsToDp(event.getY());
        Log.i("XXXXX", Float.toString(Y));
//        if ((X * videoDuration) / 300 % 1 == 0) {
//            // value needs to be saved
//            speeds[(int) ((X * videoDuration) / 300)] = A * ((400 - Y) / 400);
//        }
        listX.add(X);
        listY.add(Y);
        speeds.add(new float[]{X, Y});
        if (X >= 349){
            listener.setXList(listX);
            listener.setYList(listY);
            listener.setSpeedArray(speeds);
        }
        float x = event.getX();
        float y= event.getY();

        // Invalidate() is inside the case statements because there are many
        // other types of motion events passed into this listener,
        // and we don't want to invalidate the view for those.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                // No need to invalidate because we are not drawing anything.
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                // No need to invalidate because we are not drawing anything.
                break;
            default:
                // Do nothing.
        }
        return true;
    }

    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            // Reset mX and mY to the last drawn point.
            mX = x;
            mY = y;
            // Save the path in the extra bitmap,
            // which we access through its canvas.
            mExtraCanvas.drawPath(mPath, mPaint);
        }
    }

    private void touchUp() {
        // Reset the path so it doesn't get drawn again.
        mPath.reset();
    }

//    public float[] getSpeedArray(){
//        return speeds;
//    }

    // Get the width of the screen
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    // Get the height of the screen
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
