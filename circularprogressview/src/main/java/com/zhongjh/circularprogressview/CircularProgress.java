package com.zhongjh.circularprogressview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CircularProgress extends FrameLayout implements View.OnClickListener {


    public int pix = 0; // 像素单位
    public RectF rect; // 矩形

    private ImageView FullCircleImage; // 外圈的圆形图片
    private ImageView FillCircleImage; // 填充的圆形图片
    private ImageView arcImage; // 弧形，用于进度显示

    private Paint strokeColor; // 外圈的圆形颜料
    private Paint fillColor; // 填充的圆形颜料

    public CircularProgress(@NonNull Context context) {
        super(context);
        setOnClickListener(this);
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        initialise();
        initPaint();
        displayMetrics();
        init();
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        initialise();
        initPaint();
        displayMetrics();
        init();
    }

    /**
     * 初始化
     */
    private void initialise() {

        FullCircleImage = new ImageView(getContext());
        FillCircleImage = new ImageView(getContext());
        arcImage = new ImageView(getContext());

        FullCircleImage.setClickable(false);
        FillCircleImage.setClickable(false);
        arcImage.setClickable(false);
    }

    /**
     * 初始化颜料颜色
     */
    private void initPaint() {
        strokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeColor.setAntiAlias(true);
        strokeColor.setColor(Color.rgb(0, 161, 234));
        // 画外线，所以使用Stroke模式
        strokeColor.setStrokeWidth(3);
        strokeColor.setStyle(Paint.Style.STROKE);

        // 填充模式
        fillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillColor.setColor(Color.rgb(0, 161, 234));
        fillColor.setStyle(Paint.Style.FILL_AND_STROKE);
        fillColor.setAntiAlias(true);
    }

    /**
     * 设置分辨率等数值
     */
    private void displayMetrics() {
        DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float scarea = width * height;
        pix = (int) Math.sqrt(scarea * 0.0217);
    }

    public void init() {
        // 整个布局
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 10, 10, 10); // 上下间距

        // 画一个矩形
        float startx = (float) (pix * 0.05);
        float endx = (float) (pix * 0.95);
        float starty = (float) (pix * 0.05);
        float endy = (float) (pix * 0.95);
        rect = new RectF(startx, starty, endx, endy);

        // 色彩模式
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        initFullCircleImage(conf);
        initFillCircleImage(conf);
        initArcImage(conf);

//        addView(FullCircleImage, lp);
//        addView(FillCircleImage, lp);
        addView(arcImage, lp);
    }

    /**
     * 初始化外圈图形
     */
    private void initFullCircleImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap fullCircleBmp = Bitmap.createBitmap(pix, pix, conf);
        // 创建一个画布
        Canvas fullCircleCanvas = new Canvas(fullCircleBmp);
        // 画一个圆形
        fullCircleCanvas.drawArc(rect, 0, 360, false, strokeColor);
        FullCircleImage.setImageBitmap(fullCircleBmp);
    }

    /**
     * 初始化填充的圆形
     */
    private void initFillCircleImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap fillCircleBmp = Bitmap.createBitmap(pix, pix, conf);
        // 创建一个画布
        Canvas fillCircleCanvas = new Canvas(fillCircleBmp);
        // 画一个圆形
        fillCircleCanvas.drawArc(rect, 0, 360, false, fillColor);
        FillCircleImage.setImageBitmap(fillCircleBmp);
    }

    /**
     * 初始化一个弧形，用于进度显示
     */
    private void initArcImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap arcBmp = Bitmap.createBitmap(pix, pix, conf);
        // 创建一个画布
        Canvas arcCanvas = new Canvas(arcBmp);
        arcCanvas.drawArc(rect, -80, 340, false, strokeColor);
        arcImage.setImageBitmap(arcBmp);
    }

    @Override
    public void onClick(View v) {

    }

}
