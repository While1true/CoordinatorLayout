package coms.kxjsj.myapplication;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vange on 2017/12/7.
 */

public class MyCoordinatorLayout extends CoordinatorLayout implements DynamicAnimation.OnAnimationUpdateListener, DynamicAnimation.OnAnimationEndListener, AppBarLayout.OnOffsetChangedListener {
    private int scrolls = 0;
    //加载的位置
    private int middle = 0;
    //最大位置
    private int max = 0;
    SpringAnimation animation;
    private boolean isRefresh = false;

    private int flingMax = 0;

    private View TransYView;
    private View mScrollngView, mAppbarLayout, mBottomView;
    private BottomSheetBehavior mBottomBehavior;
    private AppBarLayout.Behavior mAppbarBehavior;
    private AppBarLayout.ScrollingViewBehavior mScrollingBehavior;

    public MyCoordinatorLayout(Context context) {
        this(context, null);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getScrolls() {
        return scrolls;
    }

    private SpringAnimation init() {
        SpringAnimation animation = new SpringAnimation(TransYView, SpringAnimation.TRANSLATION_Y, 0);
        animation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        animation.addUpdateListener(this);
        animation.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM - 300);
        animation.addEndListener(this);
        return animation;
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (max == 0) {
            max = -h / 2;
        }
        if (middle == 0) {
            middle = h / 4;
        }
        if (flingMax == 0) {
            flingMax = h / 6;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            CoordinatorLayout.LayoutParams LayoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            Behavior behavior = LayoutParams.getBehavior();
            if (behavior != null) {
                if (behavior instanceof AppBarLayout.ScrollingViewBehavior) {
                    mScrollngView = view;
                    mScrollingBehavior = (AppBarLayout.ScrollingViewBehavior) behavior;
                } else if (behavior instanceof AppBarLayout.Behavior) {
                    mAppbarLayout = view;
                    ((AppBarLayout) mAppbarLayout).addOnOffsetChangedListener(this);
                    mAppbarBehavior = (AppBarLayout.Behavior) behavior;
                } else if (behavior instanceof BottomSheetBehavior) {
                    mBottomView = view;
                    mBottomBehavior = (BottomSheetBehavior) behavior;
                }
            }
        }
        if(TransYView==null){
            TransYView=mScrollngView;
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int axes, int type) {
        if(!isRefresh){
            animation.cancel();
        }
        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        if (null != mBottomView && target == mBottomView) {
            mBottomBehavior.onNestedPreScroll(this, target, target, dx, dy, consumed, type);
            return;
        }

        int topAndBottomOffset = mAppbarBehavior == null ? 0 : mAppbarBehavior.getTopAndBottomOffset();
        boolean canscrollAppbar = false;
        boolean canscrollRefresh = false;
        int unconsume = dy - consumed[1];
        int tempconsumed = unconsume;
        if (scrolls != 0&& !isRefresh&&type==ViewCompat.TYPE_TOUCH) {
            //下拉
            if (dy < 0) {
                if (topAndBottomOffset == 0) {
                    canscrollRefresh = true;
                } else {
                    canscrollAppbar = true;
                }
            }
            //回来
            else {
                canscrollRefresh = true;
            }
        } else {
            if (dy > 0) {
                canscrollAppbar = true;
            }
        }
        if (canscrollRefresh) {
            scrolls += unconsume;
            if (scrolls > 0) {
                tempconsumed = -scrolls;
                scrolls = 0;
            }
            int tempmax = type == ViewCompat.TYPE_TOUCH ? max : -flingMax;
            if (scrolls < tempmax) {
                scrolls = tempmax;
            }
            System.out.println(type + "-onNestedPreScroll--" + scrolls);
            consumed[1] = consumed[1] + tempconsumed;
            if (callback != null) {
                callback.pull(dy < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
            }
            if (TransYView != null) {
                TransYView.setTranslationY(-scrolls);
            }
        }
//        if(topAndBottomOffset>0&&dy>0){
//            mAppbarBehavior.setTopAndBottomOffset(mAppbarBehavior.getTopAndBottomOffset()-dy);
//
//        }

        if (canscrollAppbar) {
            super.onNestedPreScroll(target, dx, tempconsumed, consumed, type);
        }
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        int topAndBottomOffset = mAppbarBehavior == null ? 0 : mAppbarBehavior.getTopAndBottomOffset();
        //展开下拦截触摸
        if (dyUnconsumed != 0 && !isRefresh && topAndBottomOffset == 0) {
            scrolls += dyUnconsumed;
            if (scrolls > 0) {
                scrolls = 0;
            }
            int tempmax = type == ViewCompat.TYPE_TOUCH ? max : -flingMax;
            if (scrolls < tempmax) {
                scrolls = tempmax;
                //模拟点击事件取消动画
                if (type == ViewCompat.TYPE_NON_TOUCH) {
                    MotionEvent obtain = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                    target.onTouchEvent(obtain);
                    obtain.recycle();
                }
            }
            System.out.println(type + "-onNestedScroll--" + scrolls);
            if (callback != null) {
                callback.pull(dyUnconsumed < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
            }
            if (TransYView != null) {
                TransYView.setTranslationY(-scrolls);
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes, int type) {
        if (animation == null) {
            animation = init();
        }
        super.onNestedScrollAccepted(child, target, nestedScrollAxes, type);
    }


    public void RefreshComplete() {
        SpringBack(middle, 0);
    }

    public void OnRefresh() {
        SpringBack(0, middle);
    }

    private void SpringBack(int start, int end) {
        if (animation == null) {
            animation = init();
        }
        if (scrolls != 0) {
            animation.cancel();
            animation.getSpring().setFinalPosition(end);
            animation.setStartValue(start);
            animation.start();
        }
    }

    @Override
    public void onStopNestedScroll(View target, int type) {
        System.out.println("onStopNestedScroll"+type);
        if (scrolls != 0 && !isRefresh) {
            int abs = Math.abs(scrolls);
            if (abs >= middle) {
                isRefresh=true;
                SpringBack(-scrolls, middle);
            } else {
                SpringBack(-scrolls, 0);
            }
        }
        super.onStopNestedScroll(target, type);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private PullCallback callback;

    public void setPullCallback(PullCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
        scrolls = -(int) value;
        if (callback != null) {
            callback.pull(PullCallback.PULLDownBack, (int) value);
            if (value == middle && 0 == velocity) {
                isRefresh = true;
                callback.middle();
            }
            if (value == 0 && 0 == velocity) {
                isRefresh = false;
            }
        }
    }

    @Override
    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        scrolls = -(int) value;
        System.out.println(value + "end");
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        System.out.println(verticalOffset);
        /**
         * 正在刷新时的移动
         */
        if (scrolls != 0 && callback != null&&isRefresh) {
            callback.pull(PullCallback.PULLOTHER,-scrolls);
        }
    }

    interface PullCallback {
        //下拉 appbar拉 回退
        int PULLDOWN = -1, PULLOTHER = 0, PULLDownBack = 1;

        void pull(int dy, int scroll);

        void middle();
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public View getmScrollngView() {
        return mScrollngView;
    }

    public AppBarLayout getmAppbarLayout() {
        return (AppBarLayout) mAppbarLayout;
    }

    public View getmBottomView() {
        return mBottomView;
    }

    public BottomSheetBehavior getmBottomBehavior() {
        return mBottomBehavior;
    }

    public AppBarLayout.Behavior getmAppbarBehavior() {
        return mAppbarBehavior;
    }

    public AppBarLayout.ScrollingViewBehavior getmScrollingBehavior() {
        return mScrollingBehavior;
    }

    public int getMiddle() {
        return middle;
    }

    public void setMiddle(int middle) {
        this.middle = middle;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getFlingMax() {
        return flingMax;
    }

    public void setFlingMax(int flingMax) {
        this.flingMax = flingMax;
    }

    public View getTransYView() {
        return TransYView;
    }

    public void setTransYView(View TransYView) {
        this.TransYView = TransYView;
    }
}
