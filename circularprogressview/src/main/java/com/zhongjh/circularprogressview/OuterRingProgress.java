package com.zhongjh.circularprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * 外圈进度View
 *
 * @author zhongjh
 */
public class OuterRingProgress extends View {

    private final CircularProgress mCircularProgress;
    public Paint mPaint;
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
            // 完成进度
            mCircularProgress.progressComplete();
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

    public void init() {
        initPaint();
    }

    /**
     * 初始化颜料
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        if (mCircularProgress.mIsFullStyle) {
            mPaint.setColor(mCircularProgress.mColorFullProgress == 0 ?
                    Color.argb(127, 255, 0, 255) : mCircularProgress.mColorFullProgress);
            mPaint.setStrokeWidth(mCircularProgress.mStrokePaint.getStrokeWidth());
        } else {
            mPaint.setColor(mCircularProgress.mColorPrimary);
            mPaint.setStrokeWidth(mCircularProgress.mStrokePaint.getStrokeWidth() * 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCircularProgress.mIsFullStyle) {
            canvas.drawArc(mCircularProgress.mRectFullStyle, mStartAngle, mSweepAngle, false, mPaint);
        } else {
            canvas.drawArc(mCircularProgress.mRect, mStartAngle, mSweepAngle, false, mPaint);
        }
    }

}
