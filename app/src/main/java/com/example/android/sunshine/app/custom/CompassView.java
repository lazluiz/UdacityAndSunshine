package com.example.android.sunshine.app.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.example.android.sunshine.app.R;

/**
 * Created by Luiz F. Lazzarin on 31/05/2016.
 * Email: lf.lazzarin@gmail.com
 * Github: /luizfelippe
 */

public class CompassView extends View {

    private Paint mCompassPaint;
    private Paint mTextPaint;
    private Paint mNeedlePaint;

    private int mFontSize;

    private float mDirection;

    public CompassView(Context context) {
        super(context);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mFontSize = 20;

        mCompassPaint = new Paint();
        mCompassPaint.setColor(Color.BLACK);
        mCompassPaint.setStyle(Paint.Style.STROKE);
        mCompassPaint.setStrokeWidth(2f);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(mFontSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mNeedlePaint = new Paint();
        mNeedlePaint.setColor(Color.RED);
        mNeedlePaint.setStrokeWidth(2f);
    }

    public void setDirection(float degrees) {
        // We need to convert to Radians before making sin and cos calculations
        mDirection = degrees * ((float) Math.PI / 180);

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int m = 5;
        int r;
        if (w > h) {
            r = h / 2;
        } else {
            r = w / 2;
        }

        // Compass
        canvas.drawCircle(w / 2, h / 2, r, mCompassPaint);

        // Compass center
        canvas.drawCircle(w / 2, h / 2, r / 10, mCompassPaint);

        // Directions
        canvas.drawText("N", r, mFontSize, mTextPaint);
        canvas.drawText("S", r, (r * 2) - m, mTextPaint);
        canvas.drawText("E", (r * 2) - mFontSize + m, r + m, mTextPaint);
        canvas.drawText("W", mFontSize - m, r + m, mTextPaint);

        // Needle
        canvas.drawLine(
                w / 2,
                h / 2,
                (float) (w / 2 + r * Math.sin(mDirection)),
                (float) (h / 2 - r * Math.cos(mDirection)),
                mNeedlePaint
        );

    }
}
