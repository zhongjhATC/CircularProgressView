//package com.zhongjh.circularprogressview;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//
//import static android.graphics.Paint.Cap.ROUND;
//import static android.graphics.Paint.Style.STROKE;
//
//public class TestView extends View {
//
//
//    public TestView(Context context) {
//        super(context);
//    }
//
//    public TestView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    //    在绘制的时候，往往需要开启抗锯齿来让图形和文字的边缘更加平滑。开启抗锯齿
////    很简单，只要在 new Paint() 的时候加上一个 ANTI_ALIAS_FLAG 参数就行：
//    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//
//    RectF        mRect = new RectF(100, 100, 100, 100);
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.rgb(0, 161, 234));
//        paint.setStrokeWidth(7);
//
//        canvas.drawArc(mRect, 0, 360, false, paint);
//
////        canvas.drawColor(Color.parseColor("#88880000"));// 半透明红色背景
////        // 绘制一个圆
////        paint.setAntiAlias(true); //设置抗锯齿开关
////        paint.setColor(Color.parseColor("#ff99ff"));//设置颜色
//////        setStyle(Style style) 这个方法设置的是绘制的 Style 。Style 具体来说有三
//////        种： FILL , STROKE 和 FILL_AND_STROKE 。FILL 是填充模式，STROKE 是画线模
//////        式（即勾边模式）， FILL_AND_STROKE 是两种模式一并使用：既画线又填充。它的
//////        默认值是 FILL ，填充模式。
////        paint.setStyle(STROKE);//是空心圆（或者叫环形）;
////        paint.setStrokeWidth(20); //设置线条宽度(圆的边线是STROKE可以)
////        paint.setStrokeCap(ROUND);//画点的园,因为线条不是封闭圆形所以设置这个属性线条两端会是园的
////        paint.setTextSize(20); //设置文字大小
////
////        //前两个参数是xy值后面参数是园的半径,paint是画
////        canvas.drawCircle(500, 300, 200, paint);
////
//////        left , top , right , bottom 是矩形四条边的坐标
////        //两种绘制矩形方法
////        RectF s = new RectF();//或者去掉F
////        s.set(300, 300, 500, 500);
////        canvas.drawRect(s, paint);
////        //canvas.drawRect(100, 100, 500, 500, paint);
////
//////        x 和 y 是点的坐标。点的大小可以通过 paint.setStrokeWidth(width) 来设
//////        置；点的形状可以通过 paint.setStrokeCap(cap) 来设置：ROUND 画出来是圆形
//////        的点，SQUARE 或 BUTT 画出来是方形的点。（点还有形状？是的，反正 Google 是
//////        这么说的，你要问问 Google 去，我也很懵逼。）
//////        canvas.drawPoint(50,50,paint); //画点
//////        drawPoints(~oat[] pts, int offset, int count,Paint paint) / drawPoints(~oat[] pts, Paintpaint) 画点（批
//////        ??
////        float[] points = {0, 0, 50, 50, 50, 100, 100, 50, 100, 100, 150};
////        // 绘制四个点：(50, 50) (50, 100) (100, 50) (100, 100)
////        canvas.drawPoints(points, 2, 8, paint);
////
//////        drawOval(~oat left, ~oat top, ~oat right, ~oat
//////                bottom, Paint paint) 画椭圆
////        //只能绘制横着的或者竖着的椭圆，不能绘制斜的（斜的倒是也可以，但不是直接使用 drawOval() ，而是配合几何变换，后面会讲到）。 left , top , right , bottom是这个椭圆的左、上、右、下四个边界点的坐
////        canvas.drawOval(400, 50, 700, 200, paint);
//////        另外，它还有一个重载方法 drawOval(RectF rect, Paint paint) ，让你可以直接填写 RectF 来绘制椭
////
//////        drawLine(~oat startX, ~oat startY, ~oatstopX, ~oat stopY, Paint paint)画线
//////        startX , startY , stopX , stopY 分别是线的起点和终点
////        canvas.drawLine(200, 200, 800, 500, paint);
////
//////        float[] points2 = {20, 20, 120, 20, 70, 20, 70, 120, 20, 120, 120, 120};
//////                canvas.drawLines(points2, paint);
////
//////        drawRoundRect(~oat left, ~oat top, ~oat
//////                right, ~oat bottom, ~oat rx, ~oat ry, Paint
//////                paint) 画圆角矩形
////        canvas.drawRoundRect(100, 400, 500, 700, 50, 50, paint);
////        //另外，它还有一个重载方drawRoundRect(RectF rect, float rx, float ry, Paint paint) ，让你可以直接填写 RectF 来绘制圆角矩
////
//////        drawArc(~oat left, ~oat top, ~oat right, ~oat
//////                bottom, ~oat startAngle, ~oat sweepAngle,
//////        boolean useCenter, Paint paint) 绘制弧形或扇
//////                形
//////        drawArc() 是使用一个椭圆来描述弧形的。left , top , right , bottom 描述的是这个弧形所在的椭圆；startAngle 是弧形的起始角度（x 轴的正向，即正右的方向，是 0 度的位置；顺时针为正角度，逆时针为负角度），sweepAngle 是弧形划过的角度；useCenter 表示是否连接到圆心，如果不连接到圆心，就是弧形，如果连接到圆心就是扇形
////        canvas.drawArc(200, 100 + 800, 800, 500 + 800, -110, 100, true, paint); // 绘制扇型(开始的角度是负数在圆形的上面)
////        canvas.drawArc(200, 100 + 800, 800, 500 + 800, 180, 60, false, paint); // 绘制不
////        canvas.drawArc(200, 100 + 800, 800, 500 + 800, 20, 140, false, paint); // 绘制弧形
//    }
//
//}
