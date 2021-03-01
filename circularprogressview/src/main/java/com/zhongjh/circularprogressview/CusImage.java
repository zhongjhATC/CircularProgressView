
package com.zhongjh.circularprogressview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

@SuppressLint("ViewConstructor")
public class CusImage extends View {

	private Paint myPaint;
	private float startAngle;
	float sweepAngle;
	private int flag = 0;
	RectF rect;
	private final CircularProgress m;
	int pix = 0;

	public CusImage(Context context, CircularProgress m) {
		super(context);
		this.m = m;
		init();
	}

	private void init() {
		myPaint = new Paint();
		DisplayMetrics metrics = getContext().getResources()
				.getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		float scarea = width * height;
		pix = (int) Math.sqrt(scarea * 0.0217);

		myPaint.setAntiAlias(true);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setColor(Color.rgb(0, 161, 234));
		myPaint.setStrokeWidth(7);

		float startx = (float) (pix * 0.05);
		float endx = (float) (pix * 0.95);
		float starty = (float) (pix * 0.05);
		float endy = (float) (pix * 0.95);
		rect = new RectF(startx, starty, endx, endy);
	}

	public void setProgress(int progress) {
		//Updating progress arc
		sweepAngle = (float) (progress * 3.6);
	}

	public void reset() {
		//Resetting progress arc
		sweepAngle = 0;
		startAngle = -90;
		flag = 1;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int desiredWidth = pix;
		int desiredHeight = pix;
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

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(rect, startAngle, sweepAngle, false, myPaint);
		startAngle = -90;

		if (sweepAngle < 360 && flag == 0) {
			invalidate();
		} else if (flag == 1) {
			sweepAngle = 0;
			flag = 0;
			invalidate();
		} else {
			sweepAngle = 0;
		}
	}
}
