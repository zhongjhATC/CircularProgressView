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

    private final CircularProgress mCircularProgress;
    public Paint mPaint;
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

    private void init() {
        initPaint();
    }

    /**
     * 初始化颜料
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mCircularProgress.mColorPrimary);
    }

    /**
     * 初始化矩阵
     */
    private void initRect() {
        // 只new一次
        if (getMeasuredWidth() > 0 && mRect == null) {
            if (mCircularProgress.mIsFullStyle) {
                Paint strokePaint = new Paint();
                strokePaint.setAntiAlias(true);
                strokePaint.setColor(mCircularProgress.mColorPrimaryVariant);
                strokePaint.setStrokeWidth(((float) getMeasuredWidth() / 14));
                strokePaint.setStyle(Paint.Style.STROKE);

                float roundWidth = (float) (mPaint.getStrokeWidth() * 1.5);// 圆环的宽度
                int centreW = getMeasuredWidth() / 2; // 获取圆心的x坐标
                int centreH = getMeasuredHeight() / 2; // 获取圆心的y坐标
                int radius = (int) (centreW - roundWidth * 4); //圆环的半径
                mRect = new RectF(centreW - radius, centreH - radius, centreW
                        + radius, centreH + radius);
            } else {
                mPaint.setStrokeWidth(((float) getMeasuredWidth() / 14));
                float roundWidth = (float) (mPaint.getStrokeWidth() * 0.5);// 圆环的宽度
                int centreW = getMeasuredWidth() / 2; // 获取圆心的x坐标
                int centreH = getMeasuredHeight() / 2; // 获取圆心的y坐标
                int radius = (int) (centreW - roundWidth * 1.5); //圆环的半径
                mRect = new RectF(centreW - radius, centreH - radius, centreW
                        + radius, centreH + radius);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRect != null)
            canvas.drawArc(mRect, mStartAngle, mSweepAngle, false, mPaint);
    }

    /**
     * 处理最大化的正方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initRect();
    }

}
