package com.warm.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * Created by warm on 17/6/9.
 */

public class PullToRefreshLayout extends ViewGroup {

    private static final String TAG = "PullToRefreshLayout--";

    private View mHeader;
    private View mTarget;

    private int state;

    private float downY;

    /**
     * 记录不可滑动之后的滑动距离，用于给加载动画
     */
    private float moveY;

    /**
     * 头部的高度
     */
    private int headerHeight = 200;


    /**
     * 处理滑动
     */
    private Scroller mScroller;


    private OnRefreshListener onRefreshListener;

    private void setState(int state) {
        this.state = state;

        if (mHeader instanceof OnPullStateChange)
            ((OnPullStateChange) mHeader).pullState(state);

        if (onPullStateChange != null) {
            onPullStateChange.pullState(state);
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private OnPullStateChange onPullStateChange;

    public void setOnPullStateChange(OnPullStateChange onPullStateChange) {
        this.onPullStateChange = onPullStateChange;
    }

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureViews();

        int measureWidth = doMeasureWidth(widthMeasureSpec);
        int measureHeight = doMeasureHeight(heightMeasureSpec);

        Log.d(TAG, "onMeasure: " + MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY));

//        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
//        mHeader.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        setMeasuredDimension(measureWidth, measureHeight);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

    }

    private int doMeasureWidth(int measureSpec) {

        int result = dp2px(getContext(), 300);
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = mTarget.getMeasuredWidth();
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        return result;
    }

    private int doMeasureHeight(int measureSpec) {

        int result = dp2px(getContext(), 300);
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = mHeader.getMeasuredHeight();
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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureViews();
        if (changed) {
            int left = getPaddingLeft();
            int top = getPaddingTop();

            top -= mHeader.getMeasuredHeight();
            headerHeight = mHeader.getMeasuredHeight();

            Log.d(TAG, "onLayout: " + getChildCount());

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                Log.d(TAG, "onLayout: " + child);

                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());

                top += child.getMeasuredHeight();
            }
        }
    }

    /**
     * 是否是向下
     *
     * @param y
     * @return
     */
    private boolean isDown(float y) {
        return y - downY > 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (state == State.REFRESHING) {
                    return false;
                }

                break;
            case MotionEvent.ACTION_MOVE:


                Log.d(TAG, "dispatchTouchEvent: isDown=" + isDown(ev.getY()));
                if (isDown(ev.getY()) && !canChildScrollUp()) {
                    //处理位置
                    if (downY == 0) {
                        downY = ev.getY();
                    }
                    moveY = ev.getY() - downY;
                    Log.d(TAG, "dispatchTouchEvent: YYYY=" + moveY);

                    scrollTo(0, (int) (-moveY / 2.5));

                    return true;

                } else {
                    downY = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //当滑动距离大于，最小距离（mHeader的高度）的时候，刷新，小于的时候回到顶端
                downY = 0;
                if (Math.abs(getScrollY()) != 0) {
                    if (-getScrollY() >= headerHeight) {
                        //可以加载了
                        startScroll(getScrollY(), Math.abs(getScrollY()) - headerHeight);
                        setState(State.REFRESHING);
                        if (onRefreshListener != null) {
                            onRefreshListener.refresh();
                        }
                    } else {
                        //不可以加载
                        startScroll(getScrollY(), -getScrollY());
                        setState(State.END);
                    }
                } else {
                    setState(State.END);
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startScroll(int y, int dy, int duration) {
        mScroller.startScroll(0, y, 0, dy, duration);
        invalidate();
    }

    private void startScroll(int y, int dy) {
        startScroll(y, dy, 250);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }

        Log.d(TAG, "computeScroll2: " + getScrollY());
        //当滑动距离大于，最小距离（mHeader的高度）的时候，刷新，小于的时候回到顶端
        if (-getScrollY() >= headerHeight) {
            //可以加载了
            setState(State.PUSH_OK);
        } else {
            //不可以加载
            setState(State.PUSH_NO_OK);
        }

        if (mHeader instanceof OnPullStateChange)
        ((OnPullStateChange) mHeader).pulling(-getScrollY());

        if (onPullStateChange != null) {
            onPullStateChange.pulling(-getScrollY());
        }
    }


    /**
     * 判断是否可以上滑
     * 在APi14之后，官方提供了canScrollVertically(int direction)方法，所以API14之后判断非常方便，官方文档的解释如下：
     * 当direction>0时，判断是否可以下滑，当direction<0时，判断是否可以上滑
     */
    public boolean canChildScrollUp() {
        return mTarget.canScrollVertically(-1);

    }


    public void refreshStart() {
        //可以加载了
        startScroll(getScrollY(), Math.abs(getScrollY()) - headerHeight);
        setState(State.REFRESHING);
        if (onRefreshListener != null) {
            onRefreshListener.refresh();
        }
    }

    public void refreshEnd() {
        startScroll(getScrollY(), -getScrollY());
        setState(State.END);

    }


    /**
     * 刷新中，在这个借口中进行刷新的操作
     */
    public interface OnRefreshListener {
        void refresh();

    }



    /**
     * @param context
     * @param dipValue
     * @return
     */
    public int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    private void ensureViews() {
        if (mTarget == null || mHeader == null) {
            if (getChildCount() > 2) {
                throw new IllegalStateException("只能有两个自控件");
            }
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof OnPullStateChange)) {
                    mTarget = child;
                } else {
                    mHeader = child;
                }
            }
        }
    }

}
