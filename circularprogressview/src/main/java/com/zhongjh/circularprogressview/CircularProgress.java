package com.zhongjh.circularprogressview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class CircularProgress extends FrameLayout implements View.OnClickListener {

    public final static String TAG = CircularProgress.class.getSimpleName();

    public int mPix = 0; // 取出当前最大正方形数值
    public RectF mRect; // 矩形
    public int mColor = Color.rgb(0, 161, 234); // 整体颜色
    public int mColorWhite = Color.rgb(255, 255, 255); // 整体颜色
    public int mPaddingTop, mPaddingLeft, mPaddingRight, mPaddingBotton = 0; // 间距
    public int mState = CircularProgressState.STOP; // 当前状态

    private ImageView mFullCircleImage; // 外圈的圆形图片控件
    private ImageView mFillCircleImage; // 填充的圆形图片控件
    private ImageView mArcImage; // 弧形，用于进度显示控件
    private ImageView mPlayImage; // 播放图片控件
    private OuterRingProgress mOuterRingProgress; // 第二次进度的控件

    private Drawable mDrawablePlay; // 播放图片
    private Drawable mDrawableDone; // 完成图片

    private Paint strokeColor; // 外圈的圆形颜料
    private Paint fillColor; // 填充的圆形颜料

    private RotateAnimation mAnimatArcRotation; // 外圈的旋转动画
    private AnimatedVectorDrawableCompat mAnimatPlayToStop; // 播放转换暂停的动画
    private ScaleAnimation mAnimatScaleShowDone; // 显示完成的动画
    private AnimationSet mAnimatShowDonw; // 显示完成的动画合集

    private CircularProgressListener mCircularProgressListener; // 事件

    public CircularProgress(@NonNull Context context) {
        super(context);
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        initAll();
    }

    /**
     * 赋值事件
     */
    public void setCircularProgressListener(CircularProgressListener circularProgressListener) {
        mCircularProgressListener = circularProgressListener;
    }

    /**
     * 设置进度值
     */
    public void setProgress(Integer progress) {
        if (mState == CircularProgressState.PLAY)
            mOuterRingProgress.setProgress(progress);
    }

    /**
     * 重置
     */
    public void reset() {
        Log.d(TAG, "reset");
        mOuterRingProgress.reset();
        mOuterRingProgress.setVisibility(View.GONE);
        mPlayImage.setImageDrawable(mDrawablePlay);
        mPlayImage.setColorFilter(mColor);
        mFillCircleImage.setVisibility(View.GONE);
        mArcImage.setVisibility(View.GONE);
        mFullCircleImage.setVisibility(View.VISIBLE);
        mState = CircularProgressState.STOP;
    }

    /**
     * 初始化所有
     */
    private void initAll() {
        // 只new一次
        if (getMeasuredWidth() > 0 && mRect == null) {
            float roundWidth = 8;// 圆环的宽度
            int centreW = getMeasuredWidth() / 2; // 获取圆心的x坐标
            int centreH = getMeasuredHeight() / 2; // 获取圆心的y坐标
            int radius = (int) (centreW - roundWidth / 2); //圆环的半径
            mRect = new RectF(0, 0, getWidth(), getHeight());

            setOnClickListener(this);
            displayMetrics();
            initialise();
            initPaint();
            initAnimation();
            initAnimationListener();
            init();
        }
    }

    /**
     * 初始化
     */
    private void initialise() {
        mFullCircleImage = new ImageView(getContext());
        mFillCircleImage = new ImageView(getContext());
        mArcImage = new ImageView(getContext());
        mPlayImage = new ImageView(getContext());
        mOuterRingProgress = new OuterRingProgress(getContext(), this);

        mFullCircleImage.setClickable(false);
        mFillCircleImage.setClickable(false);
        mArcImage.setClickable(false);
        mPlayImage.setClickable(false);
        mOuterRingProgress.setClickable(false);
    }

    /**
     * 初始化颜料颜色
     */
    private void initPaint() {
        strokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeColor.setAntiAlias(true);
        strokeColor.setColor(mColor);
        // 画外线，所以使用Stroke模式
        strokeColor.setStrokeWidth(2);
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
        mAnimatArcRotation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimatArcRotation.setDuration(1000);

        mAnimatPlayToStop = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd_play_to_stop);

        mAnimatShowDonw = new AnimationSet(true);

        mAnimatShowDonw.setInterpolator(new AccelerateDecelerateInterpolator());

        ScaleAnimation scale_in = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimatScaleShowDone = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        AlphaAnimation fade_in = new AlphaAnimation(0.0f, 1.0f);

        mAnimatScaleShowDone.setDuration(200);
        scale_in.setDuration(150);
        fade_in.setDuration(150);

        mAnimatShowDonw.addAnimation(scale_in);
        mAnimatShowDonw.addAnimation(fade_in);
    }

    /**
     * 初始化动画事件，动画事件结合
     */
    private void initAnimationListener() {
        // 旋转动画
        mAnimatArcRotation.setAnimationListener(mAnimatArcRotationListener);

        // 播放转换暂停动画
        mAnimatPlayToStop.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {

            @Override
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
            }

            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                mState = CircularProgressState.PLAY;
                // 隐藏第一个进度圈，显示第二个进度圈
                mArcImage.setVisibility(View.GONE);
                mFullCircleImage.setVisibility(View.VISIBLE);
                mOuterRingProgress.setVisibility(View.VISIBLE);

                mCircularProgressListener.onStart();
            }
        });

        // 进度完成后，显示完成的动画,跳出一个勾图标
        mAnimatScaleShowDone.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "done");
                mState = CircularProgressState.DONE;
                mCircularProgressListener.onDone();
                mOuterRingProgress.setVisibility(View.GONE);
                mPlayImage.setVisibility(View.VISIBLE);
                mPlayImage.setImageDrawable(mDrawableDone);
                mPlayImage.setColorFilter(mColorWhite);
                mPlayImage.startAnimation(mAnimatShowDonw);
            }
        });
    }

    /**
     * 旋转动画，单独抽出来是为了后来可以复用
     */
    private final Animation.AnimationListener mAnimatArcRotationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            // 开始了播放状态中
            mState = CircularProgressState.PREPARE;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // 轮到按钮开始动画
            mPlayImage.setImageDrawable(mAnimatPlayToStop);
            mAnimatPlayToStop.start();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

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
//        float startx = (float) (mPix * 0.02);
//        float endx = (float) (mPix * 0.98);
//        float starty = (float) (mPix * 0.02);
//        float endy = (float) (mPix * 0.98);
//        mRect = new RectF(2, 2, mPix-2, mPix-2);

        // 色彩模式
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        initFullCircleImage(conf);
        initFillCircleImage(conf);
        initArcImage(conf);
        initDrawablePlay();
        initDrawableDone();

        addView(mFullCircleImage, lp);
        addView(mFillCircleImage, lp);
        addView(mArcImage, lp);
        addView(mPlayImage, lp);
        addView(mOuterRingProgress, lp);
        mFillCircleImage.setVisibility(View.GONE); // 初始化的填满圆形是隐藏的
    }

    /**
     * 初始化外圈图形
     */
    private void initFullCircleImage(Bitmap.Config conf) {
        // 创建一个bitmap
        Bitmap fullCircleBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), conf);
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
        Bitmap fillCircleBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), conf);
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
        Bitmap arcBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), conf);
        // 创建一个画布
        Canvas arcCanvas = new Canvas(arcBmp);
        arcCanvas.drawArc(mRect, -80, 340, false, strokeColor);
        mArcImage.setImageBitmap(arcBmp);
    }

    /**
     * 初始化播放图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initDrawablePlay() {
        mDrawablePlay = getContext().getDrawable(R.drawable.ic_baseline_play_arrow_24);
        mPlayImage.setColorFilter(mColor);
        mPlayImage.setPadding(DisplayMetricsUtils.dip2px(6), DisplayMetricsUtils.dip2px(6),
                DisplayMetricsUtils.dip2px(6), DisplayMetricsUtils.dip2px(6)); // 上下间距
        mPlayImage.setImageDrawable(mDrawablePlay);
    }

    /**
     * 初始化完成图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initDrawableDone() {
        mDrawableDone = getContext().getDrawable(R.drawable.ic_baseline_done_24);
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
            Log.d(TAG, "play");
            // 显示进度
            mFullCircleImage.setVisibility(View.GONE);
            mArcImage.setVisibility(View.VISIBLE);
            mAnimatArcRotation.setAnimationListener(mAnimatArcRotationListener);
            mAnimatArcRotation.setRepeatCount(0);
            mArcImage.startAnimation(mAnimatArcRotation);
        } else if (mState == CircularProgressState.PLAY) {
            Log.d(TAG, "stopin");
            mState = CircularProgressState.STOPIN;
            // 一直旋转
            // 隐藏第二个进度圆形
            mOuterRingProgress.setVisibility(View.GONE);
            // 隐藏外圈
            mFullCircleImage.setVisibility(View.GONE);
            // 显示第一个进度弧形
            mArcImage.setVisibility(View.VISIBLE);
            // 开始了动画
            mAnimatArcRotation.setRepeatCount(Animation.INFINITE);
            mAnimatArcRotation.setAnimationListener(null);
            mArcImage.startAnimation(mAnimatArcRotation);
            // 触发停止请求
            mCircularProgressListener.onStop();

        }
    }

    /**
     * 进度完成
     */
    public void progressComplete() {
        mPlayImage.setVisibility(View.GONE);
        mFillCircleImage.setVisibility(View.VISIBLE);
        // 开始了完成动画
        mFillCircleImage.startAnimation(mAnimatScaleShowDone);
    }

}
