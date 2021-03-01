package com.zhongjh.circularprogressview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CircularProgress extends FrameLayout implements View.OnClickListener {

    public int mPix = 0; // 取出当前最大正方形数值
    public RectF mRect; // 矩形
    public int mColor = Color.rgb(0, 161, 234); // 整体颜色
    public int mPaddingTop, mPaddingLeft, mPaddingRight, mPaddingBotton = 0; // 间距
    private int mState = CircularProgressState.STOP; // 当前状态

    private ImageView mFullCircleImage; // 外圈的圆形图片控件
    private ImageView mFillCircleImage; // 填充的圆形图片控件
    private ImageView mArcImage; // 弧形，用于进度显示控件
    private ImageView mPlayImage; // 播放图片控件
    private OuterRingProgress mOuterRingProgress; // 第二次进度的控件

    private Drawable mDrawablePlay; // 播放图片
    private Drawable mDrawableStop; // 暂停图片

    private Paint strokeColor; // 外圈的圆形颜料
    private Paint fillColor; // 填充的圆形颜料

    private RotateAnimation arcRotation; // 外圈的旋转动画
    private AnimationSet in, out; // 进入和消失的动画合集
    private ScaleAnimation new_scale_in, scale_in, scale_out;
    private AlphaAnimation fade_in, fade_out;


    public CircularProgress(@NonNull Context context) {
        super(context);
        setOnClickListener(this);
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        displayMetrics();
        initialise();
        initPaint();
        init();
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        displayMetrics();
        initialise();
        initPaint();
        initAnimation();
        init();
    }

    /**
     * 初始化
     */
    private void initialise() {

        mFullCircleImage = new ImageView(getContext());
        mFillCircleImage = new ImageView(getContext());
        mArcImage = new ImageView(getContext());
        mPlayImage = new ImageView(getContext());

        mFullCircleImage.setClickable(false);
        mFillCircleImage.setClickable(false);
        mArcImage.setClickable(false);
        mPlayImage.setClickable(false);
    }

    /**
     * 初始化颜料颜色
     */
    private void initPaint() {
        strokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeColor.setAntiAlias(true);
        strokeColor.setColor(mColor);
        // 画外线，所以使用Stroke模式
        strokeColor.setStrokeWidth(4);
        strokeColor.setStyle(Paint.Style.STROKE);

        // 填充模式
        fillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillColor.setColor(mColor);
        fillColor.setStyle(Paint.Style.FILL_AND_STROKE);
        fillColor.setAntiAlias(true);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        arcRotation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        arcRotation.setDuration(1000);

        in = new AnimationSet(true);
        out = new AnimationSet(true);

        out.setInterpolator(new AccelerateDecelerateInterpolator());
        in.setInterpolator(new AccelerateDecelerateInterpolator());

        scale_in = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale_out = new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        new_scale_in = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        fade_in = new AlphaAnimation(0.0f, 1.0f);
        fade_out = new AlphaAnimation(1.0f, 0.0f);

        new_scale_in.setDuration(200);
        scale_in.setDuration(150);
        scale_out.setDuration(150);
        fade_in.setDuration(150);
        fade_out.setDuration(150);

        in.addAnimation(scale_in);
        in.addAnimation(fade_in);
        out.addAnimation(fade_out);
        out.addAnimation(scale_out);
    }

    /**
     * 初始化动画事件，动画事件结合
     */
    private void initAnimationListener() {
        // 旋转动画
        arcRotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 轮到play开始动画
                mPlayImage.startAnimation(out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // play消失动画
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 隐藏
                mPlayImage.setVisibility(View.GONE);

                // 显示stop按钮
                mPlayImage.setImageDrawable(mDrawableStop);
                mPlayImage.setVisibility(View.VISIBLE);
                mPlayImage.startAnimation(in);

                // 隐藏进度圈，显示第二个进度圈
                mArcImage.setVisibility(View.GONE);
                mFullCircleImage.setVisibility(View.VISIBLE);
//                cusview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 取出当前最大正方形数值
     */
    private void displayMetrics() {
        DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float scarea = width * height;
        mPix = (int) Math.sqrt(scarea * 0.0217);
    }

    public void init() {
        // 整个布局
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        // 画一个矩形
        float startx = (float) (mPix * 0.05);
        float endx = (float) (mPix * 0.95);
        float starty = (float) (mPix * 0.05);
        float endy = (float) (mPix * 0.95);
        mRect = new RectF(startx, starty, endx, endy);

        // 色彩模式
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        initFullCircleImage(conf);
        initFillCircleImage(conf);
        initArcImage(conf);
        initDrawablePlay();
        initDrawableStop();

        addView(mFullCircleImage, lp);
        addView(mFillCircleImage, lp);
        addView(mArcImage, lp);
        addView(mPlayImage, lp);
        mFillCircleImage.setVisibility(View.GONE); // 初始化的填满圆形是隐藏的
    }

    /**
     * 初始化外圈图形
     */
    private void initFullCircleImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap fullCircleBmp = Bitmap.createBitmap(mPix, mPix, conf);
        // 创建一个画布
        Canvas fullCircleCanvas = new Canvas(fullCircleBmp);
        // 画一个圆形
        fullCircleCanvas.drawArc(mRect, 0, 360, false, strokeColor);
        mFullCircleImage.setImageBitmap(fullCircleBmp);
    }

    /**
     * 初始化填充的圆形
     */
    private void initFillCircleImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap fillCircleBmp = Bitmap.createBitmap(mPix, mPix, conf);
        // 创建一个画布
        Canvas fillCircleCanvas = new Canvas(fillCircleBmp);
        // 画一个圆形
        fillCircleCanvas.drawArc(mRect, 0, 360, false, fillColor);
        mFillCircleImage.setImageBitmap(fillCircleBmp);
    }

    /**
     * 初始化一个弧形，用于进度显示
     */
    private void initArcImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap arcBmp = Bitmap.createBitmap(mPix, mPix, conf);
        // 创建一个画布
        Canvas arcCanvas = new Canvas(arcBmp);
        arcCanvas.drawArc(mRect, -80, 340, false, strokeColor);
        mArcImage.setImageBitmap(arcBmp);
    }

    /**
     * 初始化播放图片
     */
    private void initDrawablePlay() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mDrawablePlay = getContext().getDrawable(R.drawable.ic_baseline_play_arrow_24);
        } else {
            mDrawablePlay = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_play_arrow_24);
        }
        mPlayImage.setColorFilter(mColor);
        mPlayImage.setPadding(DisplayMetricsUtils.dip2px(6), DisplayMetricsUtils.dip2px(6),
                DisplayMetricsUtils.dip2px(6), DisplayMetricsUtils.dip2px(6)); // 上下间距
        mPlayImage.setImageDrawable(mDrawablePlay);
    }

    /**
     * 初始化暂停图片
     */
    private void initDrawableStop(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mDrawableStop = getContext().getDrawable(R.drawable.ic_baseline_stop_24);
        } else {
            mDrawableStop = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_stop_24);
        }
    }

    @Override
    public void onClick(View v) {
        // 启动动画
        animation();
    }

    /**
     * 动画开始
     */
    public void animation() {
        if (mState == CircularProgressState.STOP) {
            mState = CircularProgressState.PLAY;
            // 显示进度
            mFullCircleImage.setVisibility(View.GONE);
            mArcImage.setVisibility(View.VISIBLE);
            mArcImage.startAnimation(arcRotation);
        }
    }

}
