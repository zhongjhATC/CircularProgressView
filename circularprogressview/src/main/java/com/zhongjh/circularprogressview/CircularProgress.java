package com.zhongjh.circularprogressview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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

/**
 * @author zhongjh
 */
public class CircularProgress extends FrameLayout implements View.OnClickListener {

    public final static String TAG = CircularProgress.class.getSimpleName();

    /**
     * 用于比较测量后是否不一样，如果不一样重新生成
     */
    private int mMeasuredWidth, mMeasuredHeight;

    /**
     * 控件的直径，因为为了确保圆形形成能整个浏览，而这个值取宽高最小的为基准。
     */
    private int mDiameter = 0;
    /**
     * 整个控件根据它是否启用进度，否则只是一个普通的按钮
     */
    public boolean mIsProgress = true;

    /**
     * 矩形
     */
    public RectF mRect;
    /**
     * 铺满样式的矩形
     */
    public RectF mRectFullStyle;
    /**
     * 主色调颜色
     */
    public int mColorPrimary;
    /**
     * 副色调颜色
     */
    public int mColorPrimaryVariant;
    /**
     * 铺满模式的进度颜色值
     */
    public int mColorFullProgress;
    /**
     * 当前状态
     */
    public int mState = CircularProgressState.STOP;
    /**
     * 是否铺满的样式
     */
    public boolean mIsFullStyle = false;

    /**
     * 外圈的圆形图片控件
     */
    private ImageView mFullCircleImage;
    /**
     * 填充的圆形图片控件
     */
    private ImageView mFillCircleImage;
    /**
     * 弧形，用于进度显示控件
     */
    private ImageView mArcImage;
    /**
     * 弧形，用于进度显示控件,用于铺满模式
     */
    private ImageView mArcImage360;
    /**
     * 功能按钮图片控件
     */
    private ImageView mFunctionButtonImage;
    /**
     * 第二次进度的控件
     */
    private OuterRingProgress mOuterRingProgress;

    /**
     * 播放图片
     */
    private Drawable mDrawablePlay;
    /**
     * 完成图片
     */
    private Drawable mDrawableDone;

    /**
     * 外圈的圆形颜料
     */
    public Paint mStrokePaint;
    /**
     * 填充的圆形颜料
     */
    private Paint mFillPaint;

    /**
     * 外圈的旋转动画
     */
    private RotateAnimation mAnimaArcRotation;
    /**
     * 转换动画的资源id
     */
    private int mDrawablePlayId, mAvdPlayToStopId, mAvdStopToPlayId;
    /**
     * 播放转换暂停的动画
     */
    private AnimatedVectorDrawableCompat mAnimaPlayToStop;
    /**
     * 暂停转换播放的动画
     */
    private AnimatedVectorDrawableCompat mAnimaStopToPlay;
    /**
     * 显示完成的动画
     */
    private ScaleAnimation mAnimaScaleShowDone;
    /**
     * 显示完成的动画合集
     */
    private AnimationSet mAnimaShowDown;

    /**
     * 事件
     */
    private CircularProgressListener mCircularProgressListener;

    Bitmap.Config mConf = Bitmap.Config.ARGB_8888;

    public CircularProgress(@NonNull Context context) {
        super(context);
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initArray(context, attrs);
        initPaint();
        initialise();
    }

    public CircularProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArray(context, attrs);
        initPaint();
        initialise();
    }

    /**
     * 处理最大化的正方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "getWidth:" + getWidth());
        Log.d(TAG, "getMeasuredWidth:" + getMeasuredWidth());
        Log.d(TAG, "getMeasuredHeight:" + getMeasuredHeight());
        Log.d(TAG, "widthMeasureSpec:" + widthMeasureSpec);
        Log.d(TAG, "heightMeasureSpec:" + heightMeasureSpec);
        initAll();
    }

    // region 公开API

    /**
     * 清除动画，防止内存泄露
     */
    public void onDestroy() {
        if (mAnimaArcRotation != null) {
            mAnimaArcRotation.cancel();
        }
        if (mAnimaScaleShowDone != null) {
            mAnimaScaleShowDone.cancel();
        }
        if (mAnimaShowDown != null) {
            mAnimaShowDown.cancel();
        }
        if (mAnimaPlayToStop != null) {
            mAnimaPlayToStop.stop();
            mAnimaPlayToStop.unregisterAnimationCallback(mAnimaPlayToStopCallback);
        }
        if (mAnimaStopToPlay != null) {
            mAnimaStopToPlay.stop();
            mAnimaStopToPlay.unregisterAnimationCallback(mAnimaStopToPlayCallback);
        }
        if (mCircularProgressListener != null) {
            mCircularProgressListener = null;
        }
    }

    /**
     * 设置是否进度模式
     *
     * @param isProgress 默认为true,如果为false则是一个普通的button
     */
    public void setProgressMode(boolean isProgress) {
        mIsProgress = isProgress;
    }

    /**
     * 设置铺满样式
     *
     * @param isFullStyle 是/否
     */
    public void setFullStyle(boolean isFullStyle) {
        mIsFullStyle = isFullStyle;
        if (isFullStyle) {
            // 按钮也改成副色调
            mFunctionButtonImage.setColorFilter(mColorPrimaryVariant);
            mOuterRingProgress.init();
        } else {
            // 跟上面的相反
            mFunctionButtonImage.setColorFilter(mColorPrimary);
        }
        initFullCircleImage();
        initArcImage();
    }

    /**
     * 设置主色调颜色
     *
     * @param color 颜色值
     */
    public void setPrimaryColor(int color) {
        mColorPrimary = getContext().getResources().getColor(color);
        mStrokePaint.setColor(mColorPrimary);
        mFillPaint.setColor(mColorPrimary);
        mOuterRingProgress.init();

        initFullCircleImage();
        initArcImage();
        initFillCircleImage();

        if (!mIsFullStyle) {
            mFunctionButtonImage.setColorFilter(mColorPrimary);
        }
    }

    /**
     * 修改副色调颜色
     *
     * @param color 颜色值
     */
    public void setPrimaryVariantColor(int color) {
        mColorPrimaryVariant = getContext().getResources().getColor(color);
    }

    /**
     * 修改铺满模式下的进度颜色
     *
     * @param color 颜色值
     */
    public void setFullProgressColor(int color) {
        mColorFullProgress = getContext().getResources().getColor(color);
        mOuterRingProgress.init();
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
        if (mState == CircularProgressState.PLAY) {
            mOuterRingProgress.setProgress(progress);
        }
    }

    /**
     * 添加进度值
     */
    public void addProgress(Integer progress) {
        if (mState == CircularProgressState.PLAY) {
            mOuterRingProgress.addProgress(progress);
        }
    }

    /**
     * 获取当前进度值
     */
    public int getCurrentProgress() {
        return mOuterRingProgress.mCurrentProgress;
    }

    /**
     * 重置
     */
    public void reset() {
        Log.d(TAG, "reset");
        mOuterRingProgress.reset();
        mOuterRingProgress.setVisibility(View.GONE);
        if (mFunctionButtonImage.getDrawable() == mAnimaPlayToStop) {
            // 轮到按钮开始动画
            mFunctionButtonImage.setImageDrawable(mAnimaStopToPlay);
            mAnimaStopToPlay.start();
        } else {
            mFunctionButtonImage.setImageDrawable(mDrawablePlay);
        }
        if (mIsFullStyle) {
            mFunctionButtonImage.setColorFilter(mColorPrimaryVariant);
        } else {
            mFunctionButtonImage.setColorFilter(mColorPrimary);
        }
        mFillCircleImage.setVisibility(View.GONE);
        mArcImage.clearAnimation();
        mArcImage.setVisibility(View.GONE);
        mArcImage360.setVisibility(View.GONE);
        mFullCircleImage.setVisibility(View.VISIBLE);
        mState = CircularProgressState.STOP;
    }

    /**
     * 设置功能图片并且重置
     *
     * @param drawablePlay  播放图片
     * @param avdPlayToStop 播放图片 转换成 暂停图片 的动画
     * @param avdStopToPlay 暂停图片 转换成 播放图片 的动画
     */
    public void setFunctionImage(int drawablePlay, int avdPlayToStop, int avdStopToPlay) {
        mDrawablePlayId = drawablePlay;
        mAvdPlayToStopId = avdPlayToStop;
        mAvdStopToPlayId = avdStopToPlay;
        initDrawablePlay();
        initAnimation();
    }

    // endregion

    /**
     * 初始化有关属性
     */
    private void initArray(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularProgress);
        mColorPrimary = ta.getColor(R.styleable.CircularProgress_circularPrimaryColor, context.getResources().getColor(R.color.circula_progress_color_primary));
        mColorPrimaryVariant = ta.getColor(R.styleable.CircularProgress_circularPrimaryVariantColor, context.getResources().getColor(R.color.circula_progress_color_primary_variant));

        mDrawablePlayId = ta.getResourceId(R.styleable.CircularProgress_circularDrawablePlayId, R.drawable.ic_baseline_play_arrow_24);
        mAvdPlayToStopId = ta.getResourceId(R.styleable.CircularProgress_circularAvdPlayToStopId, R.drawable.avd_play_to_stop);
        mAvdStopToPlayId = ta.getResourceId(R.styleable.CircularProgress_circularAvdStopToPlayId, R.drawable.avd_stop_to_play);
        if (mDrawablePlayId == R.drawable.ic_baseline_play_arrow_24 ||
                mAvdPlayToStopId == R.drawable.avd_play_to_stop ||
                mAvdStopToPlayId == R.drawable.avd_stop_to_play) {
            // 必须3个都设置值，如果其中一个不设置，那么就都是默认值
            mDrawablePlayId = R.drawable.ic_baseline_play_arrow_24;
            mAvdPlayToStopId = R.drawable.avd_play_to_stop;
            mAvdStopToPlayId = R.drawable.avd_stop_to_play;
        }
        ta.recycle();
    }

    /**
     * 初始化所有
     */
    private void initAll() {
        // 每次测量都重新绘制view
        if (mMeasuredWidth != getMeasuredWidth() || mMeasuredHeight != getMeasuredHeight()) {
            mMeasuredWidth = getMeasuredWidth();
            mMeasuredHeight = getMeasuredHeight();
            Log.d(TAG, "重新生成initAll");
            mDiameter = Math.min(getMeasuredWidth(), getMeasuredHeight());
            // 圆环的宽度
            float roundWidth = mStrokePaint.getStrokeWidth() * 2;
            // 获取圆心的x坐标
            int centreW = getMeasuredWidth() / 2;
            // 获取圆心的y坐标
            int centreH = getMeasuredHeight() / 2;
            // 圆环的半径
            int radius = (int) ((mDiameter / 2) - (roundWidth * 2));
            mRect = new RectF(centreW - radius, centreH - radius, centreW
                    + radius, centreH + radius);


            mRectFullStyle = new RectF(mRect.left + mStrokePaint.getStrokeWidth() / 2,
                    mRect.top + mStrokePaint.getStrokeWidth() / 2,
                    mRect.right - mStrokePaint.getStrokeWidth() / 2,
                    mRect.bottom - mStrokePaint.getStrokeWidth() / 2);

            setOnClickListener(this);
            initAnimation();
            initView();
        }
    }

    /**
     * 初始化
     */
    private void initialise() {
        mFullCircleImage = new ImageView(getContext());
        mFillCircleImage = new ImageView(getContext());
        mArcImage = new ImageView(getContext());
        mArcImage360 = new ImageView(getContext());
        mFunctionButtonImage = new ImageView(getContext());
        mOuterRingProgress = new OuterRingProgress(getContext(), this);

        mFullCircleImage.setClickable(false);
        mFillCircleImage.setClickable(false);
        mArcImage.setClickable(false);
        mArcImage360.setClickable(false);
        mFunctionButtonImage.setClickable(false);
        mOuterRingProgress.setClickable(false);
    }

    /**
     * 初始化颜料颜色
     */
    private void initPaint() {
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(mColorPrimary);
        // 画外线，所以使用Stroke模式，最小值为4
        mStrokePaint.setStrokeWidth(Math.max(4, Integer.parseInt(String.valueOf(mDiameter / 56))));
        mStrokePaint.setStyle(Paint.Style.STROKE);

        // 填充模式
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(mColorPrimary);
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFillPaint.setAntiAlias(true);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        mAnimaArcRotation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimaArcRotation.setDuration(300);

        mAnimaPlayToStop = AnimatedVectorDrawableCompat.create(getContext(), mAvdPlayToStopId);
        mAnimaStopToPlay = AnimatedVectorDrawableCompat.create(getContext(), mAvdStopToPlayId);

        mAnimaShowDown = new AnimationSet(true);

        mAnimaShowDown.setInterpolator(new AccelerateDecelerateInterpolator());

        ScaleAnimation scaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimaScaleShowDone = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);

        mAnimaScaleShowDone.setDuration(200);
        scaleIn.setDuration(150);
        fadeIn.setDuration(150);

        mAnimaShowDown.addAnimation(scaleIn);
        mAnimaShowDown.addAnimation(fadeIn);

        initAnimationListener();
    }

    /**
     * 初始化动画事件，动画事件结合
     */
    private void initAnimationListener() {
        // 旋转动画
        mAnimaArcRotation.setAnimationListener(mAnimaArcRotationListener);

        // 播放图片 转换成 暂停图片 的动画
        mAnimaPlayToStop.registerAnimationCallback(mAnimaPlayToStopCallback);

        mAnimaStopToPlay.registerAnimationCallback(mAnimaStopToPlayCallback);

        // 进度完成后，显示完成的动画,跳出一个勾图标
        mAnimaScaleShowDone.setAnimationListener(new Animation.AnimationListener() {

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
                mArcImage360.setVisibility(View.GONE);
                mFunctionButtonImage.setVisibility(View.VISIBLE);
                mFunctionButtonImage.setImageDrawable(mDrawableDone);
                mFunctionButtonImage.setColorFilter(mColorPrimaryVariant);
                mFunctionButtonImage.startAnimation(mAnimaShowDown);
            }
        });
    }

    /**
     * 旋转动画，单独抽出来是为了后来可以复用
     */
    private final Animation.AnimationListener mAnimaArcRotationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            // 开始了播放状态中
            mState = CircularProgressState.PREPARE;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // 轮到按钮开始动画
            mFunctionButtonImage.setImageDrawable(mAnimaPlayToStop);
            mAnimaPlayToStop.start();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private final Animatable2Compat.AnimationCallback mAnimaPlayToStopCallback = new Animatable2Compat.AnimationCallback() {

        @Override
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            mState = CircularProgressState.PLAY;
            if (mIsFullStyle) {
                // 如果是铺满模式，显示360的进度圈
                mArcImage360.setVisibility(View.VISIBLE);
            }
            // 隐藏第一个进度圈
            mArcImage.setVisibility(View.GONE);
            // 显示外圈
            mFullCircleImage.setVisibility(View.VISIBLE);
            // 显示第二个进度圈
            mOuterRingProgress.setVisibility(View.VISIBLE);

            mCircularProgressListener.onStart();
        }
    };

    private final Animatable2Compat.AnimationCallback mAnimaStopToPlayCallback = new Animatable2Compat.AnimationCallback() {
        @Override
        public void onAnimationStart(Drawable drawable) {
            super.onAnimationStart(drawable);
        }
    };

    /**
     * 初始化并且添加view
     */
    private void initView() {
        // 整个布局
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        initFullCircleImage();
        initFillCircleImage();
        initArcImage();
        initArcImage360();
        initDrawablePlay();
        initDrawableDone();
        removeAllViews();
        addView(mFullCircleImage, lp);
        addView(mFillCircleImage, lp);
        addView(mArcImage, lp);
        addView(mArcImage360, lp);
        LayoutParams lp2 = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lp2.setMargins((int) (mDiameter * 0.10), (int) (mDiameter * 0.10),
                (int) (mDiameter * 0.10), (int) (mDiameter * 0.10));
        addView(mFunctionButtonImage, lp2);
        addView(mOuterRingProgress, lp);
        // 初始化的第一个进度是隐藏的
        mArcImage.setVisibility(View.GONE);
        // 初始化的第一个进度360是隐藏的
        mArcImage360.setVisibility(View.GONE);
        // 初始化的填满圆形是隐藏的
        mFillCircleImage.setVisibility(View.GONE);
    }

    /**
     * 初始化外圈图形
     */
    private void initFullCircleImage() {
        if (mDiameter > 0) {
            // 创建一个bitmap
            Bitmap fullCircleBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), mConf);
            // 创建一个画布
            Canvas fullCircleCanvas = new Canvas(fullCircleBmp);
            // 画一个圆形
            if (mIsFullStyle) {
                fullCircleCanvas.drawArc(mRect, 0, 360, false, mFillPaint);
            } else {
                fullCircleCanvas.drawArc(mRect, 0, 360, false, mStrokePaint);
            }
            mFullCircleImage.setImageBitmap(fullCircleBmp);
        }
    }

    /**
     * 初始化填充的圆形
     */
    private void initFillCircleImage() {
        if (mDiameter > 0) {
            // 创建一个bitmap
            Bitmap fillCircleBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), mConf);
            // 创建一个画布
            Canvas fillCircleCanvas = new Canvas(fillCircleBmp);
            // 画一个圆形
            fillCircleCanvas.drawArc(mRect, 0, 360, false, mFillPaint);
            mFillCircleImage.setImageBitmap(fillCircleBmp);
        }
    }

    /**
     * 初始化一个弧形，用于进度显示
     */
    private void initArcImage() {
        if (mDiameter > 0) {
            // 创建一个bitmap
            Bitmap arcBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), mConf);
            // 创建一个画布
            Canvas arcCanvas = new Canvas(arcBmp);
            if (mIsFullStyle) {
                Paint strokePaint = new Paint();
                strokePaint.setAntiAlias(true);
                strokePaint.setColor(mColorPrimaryVariant);
                strokePaint.setStrokeWidth(mStrokePaint.getStrokeWidth());
                strokePaint.setStyle(Paint.Style.STROKE);
                if (mState == CircularProgressState.PLAY) {
                    arcCanvas.drawArc(mRectFullStyle, 0, 360, false, strokePaint);
                } else {
                    arcCanvas.drawArc(mRectFullStyle, -80, 340, false, strokePaint);
                }
            } else {
                mStrokePaint.setColor(mColorPrimary);
                arcCanvas.drawArc(mRect, -80, 340, false, mStrokePaint);
            }
            mArcImage.setImageBitmap(arcBmp);
        }
    }

    /**
     * 初始化一个弧形360覆盖，用于铺满模式的空白轮廓进度框
     */
    private void initArcImage360() {
        if (mDiameter > 0) {
            // 创建一个bitmap
            Bitmap arcBmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), mConf);
            // 创建一个画布
            Canvas arcCanvas = new Canvas(arcBmp);
            Paint strokePaint = new Paint();
            strokePaint.setAntiAlias(true);
            strokePaint.setColor(mColorPrimaryVariant);
            strokePaint.setStrokeWidth(mStrokePaint.getStrokeWidth());
            strokePaint.setStyle(Paint.Style.STROKE);
            RectF rect = new RectF(mRect.left + mStrokePaint.getStrokeWidth() / 2,
                    mRect.top + mStrokePaint.getStrokeWidth() / 2,
                    mRect.right - mStrokePaint.getStrokeWidth() / 2,
                    mRect.bottom - mStrokePaint.getStrokeWidth() / 2);
            arcCanvas.drawArc(rect, 0, 360, false, strokePaint);

            mArcImage360.setImageBitmap(arcBmp);
        }
    }

    /**
     * 初始化播放图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initDrawablePlay() {
        mDrawablePlay = getContext().getDrawable(mDrawablePlayId);
        if (mIsFullStyle) {
            mFunctionButtonImage.setColorFilter(mColorPrimaryVariant);
        } else {
            mFunctionButtonImage.setColorFilter(mColorPrimary);
        }
        // 上下间距
        mFunctionButtonImage.setPadding(DisplayMetricsUtils.dip2px(6), DisplayMetricsUtils.dip2px(6),
                DisplayMetricsUtils.dip2px(6), DisplayMetricsUtils.dip2px(6));
        mFunctionButtonImage.setImageDrawable(mDrawablePlay);
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
        if (mIsProgress) {
            // 进度模式下，启动动画
            mCircularProgressListener.onClickByProgressMode();
            animation();
        } else {
            mCircularProgressListener.onClickByGeneralMode();
        }
    }

    /**
     * 动画开始
     */
    private void animation() {
        if (mState == CircularProgressState.STOP) {
            Log.d(TAG, "play");
            // 显示进度
            if (!mIsFullStyle) {
                mFullCircleImage.setVisibility(View.GONE);
            }
            mArcImage.setVisibility(View.VISIBLE);
            mAnimaArcRotation.setAnimationListener(mAnimaArcRotationListener);
            mAnimaArcRotation.setRepeatCount(0);
            mArcImage.startAnimation(mAnimaArcRotation);
        } else if (mState == CircularProgressState.PLAY) {
            Log.d(TAG, "stopin");
            mState = CircularProgressState.STOPIN;
            // 一直旋转
            // 隐藏第二个进度圆形
            mOuterRingProgress.setVisibility(View.GONE);
            // 隐藏外圈
            if (!mIsFullStyle) {
                mFullCircleImage.setVisibility(View.GONE);
            }
            // 显示第一个进度弧形
            mArcImage.setVisibility(View.VISIBLE);
            // 开始了动画
            mAnimaArcRotation.setRepeatCount(Animation.INFINITE);
            mAnimaArcRotation.setAnimationListener(null);
            mArcImage.startAnimation(mAnimaArcRotation);
            // 触发停止请求
            mCircularProgressListener.onStop();

        }
    }

    /**
     * 进度完成
     */
    public void progressComplete() {
        mFunctionButtonImage.setVisibility(View.GONE);
        mFillCircleImage.setVisibility(View.VISIBLE);
        // 开始了完成动画
        mFillCircleImage.startAnimation(mAnimaScaleShowDone);
    }

}
