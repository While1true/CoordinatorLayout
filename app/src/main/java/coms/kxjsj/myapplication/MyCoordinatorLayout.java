package coms.kxjsj.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by vange on 2017/12/7.
 */

public class MyCoordinatorLayout extends CoordinatorLayout implements ValueAnimator.AnimatorUpdateListener, AppBarLayout.OnOffsetChangedListener {
    private int scrolls = 0;
    //加载的位置
    private int middle = 0;
    //最大位置
    private int max = 0;
    ValueAnimator animation;
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
        if (animation == null) {
            animation = ValueAnimator.ofInt();
            animation.setInterpolator(new DecelerateInterpolator());
            animation.addUpdateListener(this);
        }
    }

    public int getScrolls() {
        return scrolls;
    }

    private void startAnimator(int from, int to) {

        animation.cancel();
        animation.setIntValues(from, to);
        final float distanceRatio = (float) (from - to) / getHeight();
        int duration = (int) ((distanceRatio + 1) * 150);
        animation.setDuration(duration);
        animation.start();
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (max == 0) {
            max = -h / 4;
        }
        if (middle == 0) {
            middle = h / 6;
        }
        if (flingMax == 0) {
            flingMax = -h / 8;
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
        if (TransYView == null) {
            TransYView = mScrollngView;
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int axes, int type) {

        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        if (target == mBottomView) {
            if (type == 0)
                mBottomBehavior.onNestedPreScroll(this, target, target, dx, dy, consumed, type);
            return;
        }
        int topAndBottomOffset = mAppbarBehavior == null ? 0 : mAppbarBehavior.getTopAndBottomOffset();
        boolean canscrollAppbar = false;
        boolean canscrollRefresh = false;
        int unconsume = dy - consumed[1];
        int tempconsumed = unconsume;
        if (scrolls != 0 && !isRefresh&&type==0) {
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
                if(topAndBottomOffset==-mAppbarLayout.getMeasuredHeightAndState()){
                    canscrollRefresh=true;
                }else {
                    canscrollAppbar = true;
                }
            }
        }
        if (canscrollRefresh) {
            scrolls += unconsume;
            if (scrolls > 0) {
                tempconsumed = -scrolls;
                scrolls = 0;
            }
//            int tempmax = type == ViewCompat.TYPE_TOUCH ? max : -flingMax;
//            if (scrolls < tempmax) {
//                scrolls = tempmax;
//            }
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
        if (target == mBottomView)
            return;
        //展开下拦截触摸
        System.out.println(type + "onNestedScroll");
        if (type == 0) {
            if (!isRefresh ) {
                animation.cancel();
            }
            if (dyUnconsumed != 0 && !isRefresh && topAndBottomOffset == 0) {
                scrolls += dyUnconsumed;
                if (scrolls > 0) {
                    scrolls = 0;
                }
                if (scrolls < max) {
                    scrolls = max;
                }
                if (callback != null) {
                    callback.pull(dyUnconsumed < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
                }
                if (TransYView != null) {
                    TransYView.setTranslationY(-scrolls);
                }
            }
        } else {
            if((isRefresh||animation.isRunning())&&dyUnconsumed<0&&topAndBottomOffset==0){
                    stopRecyclerview(target);
               return;
            }
            if (dyUnconsumed != 0&& topAndBottomOffset == 0) {
                scrolls += dyUnconsumed;
                if (scrolls > 0) {
                    scrolls = 0;
                }
                if (scrolls < flingMax) {
                    scrolls = flingMax;
                   stopRecyclerview(target);
                   onStopNestedScroll(target, 0);
                }
                System.out.println(target.getClass().getSimpleName());

                if (callback != null) {
                    callback.pull(dyUnconsumed < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
                }
                if (TransYView != null) {
                    TransYView.setTranslationY(-scrolls);
                }
            }
        }
    }

    private void stopRecyclerview(View target) {
        try {
            Method stopScrollersInternal = target.getClass().getDeclaredMethod("stopScrollersInternal");
            stopScrollersInternal.setAccessible(true);
            stopScrollersInternal.invoke(target);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes, int type) {
        super.onNestedScrollAccepted(child, target, nestedScrollAxes, type);
    }


    public void RefreshComplete() {
        if (mScrollngView.getTop()<=0&&callback != null) {
            callback.pull(PullCallback.PULLDownBack, 0);
            isRefresh=false;
        }else {
            SpringBack(middle, 0);
        }
    }

    public void OnRefresh() {
        SpringBack(0, middle);
    }

    private void SpringBack(int start, int end) {
        startAnimator(start, end);
    }

    @Override
    public void onStopNestedScroll(View target, int type) {
        super.onStopNestedScroll(target, type);
        System.out.println("onStopNestedScroll" + target.getClass().getSimpleName() + " " + target.getId());
        if (scrolls != 0 && !isRefresh && !animation.isRunning()) {
            int abs = Math.abs(scrolls);
            if (abs >= middle) {
                isRefresh = true;
                SpringBack(-scrolls, middle);
            } else {
                SpringBack(-scrolls, 0);
            }
        }
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        System.out.println(verticalOffset);
        /**
         * 正在刷新时的移动
         */
        if (scrolls != 0 && callback != null && isRefresh) {
            callback.pull(PullCallback.PULLOTHER, -scrolls);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        scrolls = -(int) animation.getAnimatedValue();
        mScrollngView.setTranslationY(-scrolls);
        if (callback != null) {
            callback.pull(PullCallback.PULLDownBack, -scrolls);
            if (-scrolls == middle && 1 == animation.getAnimatedFraction()) {
                isRefresh = true;
                callback.middle();
            }
            if (scrolls == 0 && 1 == animation.getAnimatedFraction()) {
                isRefresh = false;
            }
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
