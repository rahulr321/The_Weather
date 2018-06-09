package com.orbot.theweather.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Rahul on 29/11/2015.
 */
public class Compass extends View {

    private float direction;
    private int color;

    public Compass(Context context) {
        super(context);
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Compass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                View.MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int r;
        if (w > h) {
            r = h / 2;
        } else {
            r = w / 2;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);

        canvas.drawCircle(w / 2, h / 2, r, paint);

        paint.setColor(Color.RED);


        canvas.drawLine(
                w/2,
                h/2,
                (float)(w/2 + r * Math.sin(-direction)),
                (float)(h/2 - r * Math.cos(-direction)),
                paint);

    }

    public void setBearing(float dir) {
        direction = dir;
        invalidate();
    }
   /*private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width = 0;
    private int height = 0;
    private Matrix matrix; // to manage rotation of the compass view
    private Bitmap bitmap;
    private float bearing; // rotation angle to North

    public Compass(Context context) {
        super(context);
        initialize();
    }

    public Compass(Context context, AttributeSet attr) {
        super(context, attr);
        initialize();
    }
    public Compass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        matrix = new Matrix();
        // create bitmap for compass icon
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_expand_less_24dp);
    }

    public void setBearing(float b) {
        bearing = b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        *//*int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();*//*

//        if (bitmapWidth > canvasWidth || bitmapHeight > canvasHeight) {
//            // resize bitmap to fit in canvas
//            bitmap = Bitmap.createScaledBitmap(bitmap,
//                    (int) (bitmapWidth * 0.85), (int) (bitmapHeight * 0.85), true);
//        }

        // center
        int bitmapX = bitmap.getWidth() / 2;
        int bitmapY = bitmap.getHeight() / 2;
        int parentX = width / 2;
        int parentY = height / 2;
        int centerX = parentX - bitmapX;
        int centerY = parentY - bitmapY;

        // calculate rotation angle
        //int rotation = (int) (360 - bearing);

        // reset matrix
        matrix.reset();
        matrix.setRotate(bearing, bitmapX, bitmapY);
        // center bitmap on canvas
        matrix.postTranslate(centerX, centerY);
        // draw bitmap
        canvas.drawBitmap(bitmap, matrix, paint);
    }*/
}
