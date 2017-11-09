package com.warm.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by warm on 17/6/13.
 */

public class WaveView extends View {

    private Paint wavePaint;
    private Path path;
    private PointF mPoint;

    /**
     * 控件宽高
     */
    protected int viewWidth, viewHeight;

    /**
     * 真实可以绘制的宽高,除去padding
     */
    protected int mWidth, mHeight;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        wavePaint=new Paint();
        wavePaint.setColor(Color.BLUE);
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);

        path=new Path();
        mPoint=new PointF();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(measureWidthSpec(widthMeasureSpec), measureHeightSpec(heightMeasureSpec));

    }

    private int measureWidthSpec(int spec) {
        int result = dp2px(300);
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = Math.min(result, size);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return result;
    }

    private int measureHeightSpec(int spec) {
        int result = dp2px(200);
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = Math.min(result, size);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        mWidth = viewWidth - getPaddingLeft() - getPaddingRight();
        mHeight = viewHeight - getPaddingTop() - getPaddingBottom();
        mPoint.x=0;
        mPoint.y=mHeight/4;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.moveTo(0,mHeight/2);
        for (int i=1;i<5;i++) {
            path.quadTo(mPoint.x, mHeight/4, mWidth, mHeight / 2);
        }
        path.lineTo(mWidth,mHeight);
        path.lineTo(0,mHeight);
        path.lineTo(0,0);
        canvas.drawPath(path,wavePaint);

    }

    /**
     * @param dipValue
     * @return
     */
    public int dp2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void run(){
        animation(mWidth/2,3000);
    }

    /**
     * 属性动画
     */
    protected void animation(float value,int animationTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,value);
        valueAnimator.setDuration(animationTime);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPoint.x = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }
}
