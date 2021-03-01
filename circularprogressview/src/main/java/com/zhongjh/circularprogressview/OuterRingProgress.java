package com.zhongjh.circularprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 外圈进度View
 */
public class OuterRingProgress extends View {

    private final CircularProgress mCircularProgress;
    public int mPix = 0; // 取出当前最大正方形数值
    private Paint mPaint;
    RectF mRect;
    float mStartAngle = -90;
    float mSweepAngle;

    public OuterRingProgress(Context context, CircularProgress circularProgress) {
        super(context);
        mCircularProgress = circularProgress;
        init();
    }

    /**
     * 设置当前进度
     */
    public void setProgress(int progress) {
        if (progress >= 100) {
            reset();
        } else {
            mSweepAngle = (float) (progress * 3.6);
        }
        invalidate();
    }

    /**
     * 重置
     */
    public void reset() {
        mSweepAngle = 0;
        mStartAngle = -90;
    }

    private void init() {
        initPix();
        initPaint();
        initRect();
    }

    private void initPix() {
        DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float scarea = width * height;
        mPix = (int) Math.sqrt(scarea * 0.0217);
    }

    /**
     * 初始化颜料
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mCircularProgress.mColor);
        mPaint.setStrokeWidth(7);
    }

    /**
     * 初始化矩阵
     */
    private void initRect() {
        float startx = (float) (mPix * 0.05);
        float endx = (float) (mPix * 0.95);
        float starty = (float) (mPix * 0.05);
        float endy = (float) (mPix * 0.95);
        mRect = new RectF(startx, starty, endx, endy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mRect, mStartAngle, mSweepAngle, false, mPaint);
    }

    /**
     * 处理最大化的正方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = mPix;
        int desiredHeight = mPix;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }


        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }
}
