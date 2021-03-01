package com.zhongjh.circularprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * 外圈进度View
 */
public class OuterRingProgress extends View {

    private CircularProgress mCircularProgress;
    private Paint mPaint;
    RectF mRect;
    float mStartAngle;
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
        initPaint();
        initRect();
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
        float startx = (float) (mCircularProgress.mPix * 0.05);
        float endx = (float) (mCircularProgress.mPix * 0.95);
        float starty = (float) (mCircularProgress.mPix * 0.05);
        float endy = (float) (mCircularProgress.mPix * 0.95);
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
        int desiredWidth = mCircularProgress.mPix;
        int desiredHeight = mCircularProgress.mPix;
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
