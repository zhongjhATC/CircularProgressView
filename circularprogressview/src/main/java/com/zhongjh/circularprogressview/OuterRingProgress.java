package com.zhongjh.circularprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        initPix();
        initPaint();
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
//        mPaint.setColor(Color.rgb(0, 161, 234));
        mPaint.setStrokeWidth(8);
    }

    /**
     * 初始化矩阵
     */
    private void initRect() {
        // 只new一次
        if (getMeasuredWidth() > 0 && mRect == null) {
            float roundWidth = 8;// 圆环的宽度
            int centreW = getMeasuredWidth() / 2; // 获取圆心的x坐标
            int centreH = getMeasuredHeight() / 2; // 获取圆心的y坐标
            int radius = (int) (centreW - roundWidth / 2); //圆环的半径
            mRect = new RectF(centreW - radius, centreH - radius, centreW
                    + radius, centreH + radius);
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
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            //将layout_width的值给width
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            //将控件的默认值100给width
            width = mPix;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = mPix;
        }
        //将得到的宽高传入控件
        setMeasuredDimension(width, height);
        initRect();
    }

}
