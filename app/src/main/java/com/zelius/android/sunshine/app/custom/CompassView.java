package com.zelius.android.sunshine.app.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.zelius.android.sunshine.app.R;

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
    private int mPadding;

    private float mDirection;

    public CompassView(Context context) {
        super(context);
        init(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context c, @Nullable AttributeSet attrs) {
        if(attrs != null){
            TypedArray a = c.obtainStyledAttributes(attrs,R.styleable.compass_view);
            String textSizeStr = a.getString(R.styleable.compass_view_textSize);

            if (textSizeStr == null || textSizeStr.isEmpty()){
                mFontSize = 22;
            } else{
                float scaledDensity = c.getResources().getDisplayMetrics().scaledDensity;
                mFontSize = (int) (Float.valueOf(textSizeStr) * scaledDensity);
            }

            a.recycle();
        } else{
            mFontSize = 22;
        }
        mPadding = 20;

        mCompassPaint = new Paint();
        mCompassPaint.setColor(ContextCompat.getColor(c, R.color.sunshine_blue));
        mCompassPaint.setStyle(Paint.Style.STROKE);
        mCompassPaint.setStrokeWidth(6f);
        mCompassPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(c, R.color.grey));
        mTextPaint.setTextSize(mFontSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mNeedlePaint = new Paint();
        mNeedlePaint.setColor(ContextCompat.getColor(c, R.color.dark_grey));
        mNeedlePaint.setStrokeWidth(2f);
        mNeedlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setDirection(float degrees) {
        // We need to convert to Radians before making sin and cos calculations
        mDirection = degrees * ((float) Math.PI / 180);

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth() - mPadding * 2;
        int h = getHeight() - mPadding * 2;
        int m = mFontSize / 3; // magic number
        int r;
        if (w > h) {
            r = h / 2;
        } else {
            r = w / 2;
        }

        // Compass
        canvas.drawCircle(w / 2 + mPadding, h / 2 + mPadding, r, mCompassPaint);

        // Compass center
        canvas.drawCircle(w / 2 + mPadding, h / 2 + mPadding, r / 10, mCompassPaint);

        // Directions
        canvas.drawText("N", r + mPadding, mFontSize + mPadding, mTextPaint);
        canvas.drawText("S", r + mPadding, (r * 2) - m + mPadding, mTextPaint);
        canvas.drawText("E", (r * 2) - mFontSize + m + mPadding, r + m + mPadding, mTextPaint);
        canvas.drawText("W", mFontSize - m + mPadding, r + m + mPadding, mTextPaint);

        // Needle
        canvas.drawLine(
                w / 2 + mPadding,
                h / 2 + mPadding,
                (float) (w / 2 + mPadding + r * Math.sin(mDirection)),
                (float) (h / 2 + mPadding - r * Math.cos(mDirection)),
                mNeedlePaint
        );

        // Needle start
        canvas.drawCircle(w / 2 + mPadding, h / 2 + mPadding, mNeedlePaint.getStrokeWidth() * 2, mNeedlePaint);

    }
}
