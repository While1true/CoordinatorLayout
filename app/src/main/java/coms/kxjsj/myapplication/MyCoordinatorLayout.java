package coms.kxjsj.myapplication;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vange on 2017/12/7.
 */

public class MyCoordinatorLayout extends CoordinatorLayout implements DynamicAnimation.OnAnimationUpdateListener, DynamicAnimation.OnAnimationEndListener {
    private int scrolls = 0;
    //加载的位置
    private int middle = 50;
    //最大位置
    private int max = -100;
    SpringAnimation animation;
    private boolean isRefresh = false;

    private int flingMax = 20;

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
        SpringAnimation animation = new SpringAnimation(findViewById(R.id.viewPager), SpringAnimation.TRANSLATION_Y, 0);
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
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {

        if(target  instanceof NestedScrollView){
            System.out.println(dy);
        }


        AppBarLayout appbar = findViewById(R.id.appbar);
        if (target.getId() == R.id.bottomRecyclerview) {
            if (target.getTop() <= appbar.getMeasuredHeight()) {
                (((CoordinatorLayout.LayoutParams) appbar.getLayoutParams()).getBehavior()).onNestedPreScroll(this, appbar, target, dx, dy, consumed, type);
            }
            BottomSheetBehavior<View> sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomRecyclerview));
            sheetBehavior.onNestedPreScroll(this, target, target, dx, dy, consumed, type);

            return;
        }

        LayoutParams layoutParams = (LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behaviorlayout = (AppBarLayout.Behavior) layoutParams.getBehavior();
        int topAndBottomOffset = behaviorlayout.getTopAndBottomOffset();
        boolean canscrollAppbar = false;
        boolean canscrollRefresh = false;
        int unconsume = dy - consumed[1];
        int tempconsumed = unconsume;
        if (scrolls != 0 && !isRefresh) {
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
            if (dy > 0 && topAndBottomOffset > -appbar.getMeasuredHeight()) {
                canscrollAppbar = true;
                if(callback!=null){
                    callback.pull(PullCallback.PULLDownBack,-scrolls);
                }
            }
        }
        if (canscrollRefresh) {
            scrolls += unconsume;
            if (scrolls> 0) {
                tempconsumed = -scrolls;
                scrolls = 0;
            }
            int tempmax = type == ViewCompat.TYPE_TOUCH ? max : -flingMax;
            if (scrolls < tempmax) {
                scrolls = tempmax;
            }
            consumed[1] = consumed[1] + tempconsumed;
            if (callback != null) {
                callback.pull(dy < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
            }
            findViewById(R.id.viewPager).setTranslationY(-scrolls);
        }

        if (canscrollAppbar) {
            super.onNestedPreScroll(target, dx, tempconsumed, consumed, type);
        }
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        View appbar = findViewById(R.id.appbar);
        LayoutParams layoutParams = (LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behaviorlayout = (AppBarLayout.Behavior) layoutParams.getBehavior();
        int topAndBottomOffset = behaviorlayout.getTopAndBottomOffset();
        //展开下拦截触摸
        if (dyUnconsumed != 0 && !isRefresh && topAndBottomOffset == 0) {
            scrolls += dyUnconsumed;
            if (scrolls> 0) {
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
            if (callback != null) {
                callback.pull(dyUnconsumed < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
            }
            findViewById(R.id.viewPager).setTranslationY(-scrolls);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes, int type) {
        if (animation == null) {
            animation = init();
        }
        super.onNestedScrollAccepted(child, target, nestedScrollAxes, type);
    }


    public void RefreshComplete() {
        isRefresh = false;
        SpringBack(middle, 0);
    }

    public void OnRefresh() {
        SpringBack(0, middle);
    }

    private void SpringBack(int start, int end) {
        if (scrolls != 0) {
            animation.cancel();
            animation.getSpring().setFinalPosition(end);
            animation.setStartValue(start);
            animation.start();
        }
    }

    @Override
    public void onStopNestedScroll(View target, int type) {

        if (scrolls != 0&&!isRefresh) {
            int abs = Math.abs(scrolls);
            if (abs >= middle / 2) {
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
        System.out.println(value + "--" + velocity);
        if (callback != null) {
            callback.pull(PullCallback.PULLDownBack, (int) value);
            if (value == middle && 0 == velocity) {
                isRefresh = true;
                callback.middle();
            }
        }
    }

    @Override
    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        scrolls = -(int) value;
        System.out.println(value + "end");
    }

    interface PullCallback {
        int PULLDOWN = -1, PULLDownBack = 1;

        void pull(int dy, int scroll);

        void middle();
    }
}
