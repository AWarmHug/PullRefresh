package com.warm.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * Created by warm on 17/6/9.
 */

public class PullToRefreshLayout extends ViewGroup {

    private static final String TAG = "PullToRefreshLayout";

    private View mTarget;

    private int state;

    /**
     * 模拟高度
     */
    private int height = 200;


    /**
     * 处理滑动
     */
    private Scroller mScroller;


    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;


    private OnRefreshListener onRefreshListener;

    private void setState(int state) {
        this.state = state;
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
        mScroller = new Scroller(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = doMeasureWidth(widthMeasureSpec);
        int measureHeight = doMeasureHeight(heightMeasureSpec);

        setMeasuredDimension(measureWidth, measureHeight);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "111onMeasure: ");


    }

    private int doMeasureWidth(int measureSpec) {

        int result = dp2px(getContext(), 300);
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:

                result = getChildAt(0).getMeasuredWidth();
                Log.d(TAG, "doMeasureWidth: " + result);
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
                result = getChildAt(0).getMeasuredHeight();
                Log.d(TAG, "doMeasureHeight: " + result);
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
        int left = getPaddingLeft();
        int top = getPaddingTop();
//        if (changed) {

        top -= getChildAt(0).getMeasuredHeight();
        height = getChildAt(0).getMeasuredHeight();

        Log.d(TAG, "onLayout: " + getChildCount());

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Log.d(TAG, "onLayout: " + child);

            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());

            top += child.getMeasuredHeight();

        }
//        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
//            if (/*getChildAt(i) instanceof RecyclerView ||*/ getChildAt(i) instanceof AbsListView || getChildAt(i) instanceof ScrollView || getChildAt(i) instanceof WebView) {
                mTarget = getChildAt(1);
//                break;
//            }
        }

    }


    private float downY;

    /**
     * 记录不可滑动之后的滑动距离，用于给加载动画
     */
    private float YY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent: ");

                if ((ev.getY() - downY) > 0 && Math.abs(ev.getY() - downY) > mTouchSlop) {
                    if (!canChildScrollUp()) {
                        Log.d(TAG, "onInterceptTouchEvent: 不可以上滑");
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                //处理位置
                YY = event.getY() - downY;
                if (getScrollY() <= 0&&mTarget.getY()>=0) {
                    scrollTo(0, (int) -YY / 2);
                }
                //当滑动距离大于，最小距离（mHeader的高度）的时候，刷新，小于的时候回到顶端
                if (-getScrollY() >= height) {
                    //可以加载了
                    setState(State.PUSH_OK);
                } else {
                    //不可以加载
                    setState(State.PUSH_NO_OK);
                }


                break;
            case MotionEvent.ACTION_UP:


                Log.d(TAG, "onTouchEvent: " + getScrollY());

                //当滑动距离大于，最小距离（mHeader的高度）的时候，刷新，小于的时候回到顶端
                if (-getScrollY() >= height) {

                    //可以加载了
                    startScroll(getScrollY(), Math.abs(getScrollY()) - height);
                    setState(State.REFRESHING);
                    if (onRefreshListener != null) {
                        onRefreshListener.refresh();
                    }


                } else {
                    //不可以加载
                    startScroll(getScrollY(), -getScrollY());
                    setState(State.END);
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    private void startScroll(int y, int dy) {
        mScroller.startScroll(0, y, 0, dy, 250);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
        Log.d(TAG, "computeScroll1: "+mScroller.getCurrY());
        Log.d(TAG, "computeScroll2: " + getScrollY());

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
        return mTarget.canScrollVertically( -1);

    }


    public void refreshStart() {
        //可以加载了
        startScroll(getScrollY(), Math.abs(getScrollY()) - height);
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
     * 下拉状态改变，{@link State}
     */
    interface OnPullStateChange {
        void pullState(int state);

        void pulling(float y);
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
}
