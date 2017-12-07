package coms.kxjsj.myapplication;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatValueHolder;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vange on 2017/12/7.
 */

public class MyCoordinatorLayout extends CoordinatorLayout implements DynamicAnimation.OnAnimationUpdateListener, DynamicAnimation.OnAnimationEndListener {
    private int scrolls = 0;
    private int middle = 50;
    SpringAnimation animation;
    private boolean isRefresh = false;

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
        SpringAnimation animation = new SpringAnimation(findViewById(R.id.viewPager), SpringAnimation.SCROLL_Y, 0);
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
        //展开下拦截触摸
        if(BottomSheetBehavior.from(findViewById(R.id.bottomRecyclerview)).getState()==BottomSheetBehavior.STATE_EXPANDED){
            return;
        }
        int unconsume = dy - consumed[1];
        System.out.println((unconsume) + "onNestedPreScroll-----------");
        int tempconsumed = unconsume;
        if (scrolls != 0 && !isRefresh) {
            scrolls += unconsume;
            if (scrolls + unconsume > 0) {
                tempconsumed = -scrolls;
                scrolls = 0;
            }
            if (scrolls < -getHeight() / 2) {
                scrolls = -getHeight() / 2;
            }
            consumed[1] = consumed[1] + tempconsumed;
            if (callback != null) {
                callback.pull(dy < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
            }
            findViewById(R.id.viewPager).scrollTo(0, scrolls);
        }


        if (scrolls == 0||isRefresh) {
            super.onNestedPreScroll(target, dx, dy, consumed, type);
        }
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        System.out.println(dyUnconsumed + "--onNestedScroll");
        if (dyUnconsumed != 0 && !isRefresh) {
            scrolls += dyUnconsumed;
            if (scrolls + dyUnconsumed > 0) {
                scrolls = 0;
            }
            if (scrolls < -getHeight() / 2) {
                scrolls = -getHeight() / 2;
            }
            if (callback != null) {
                callback.pull(dyUnconsumed < 0 ? PullCallback.PULLDOWN : PullCallback.PULLDownBack, -scrolls);
            }
            findViewById(R.id.viewPager).scrollTo(0, scrolls);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        System.out.println("onNestedFling" + velocityY);
        if (!isRefresh) {
            if(velocityY<0&&!consumed) {
                animation.setStartVelocity(velocityY);
                SpringBack(scrolls, -middle/2);
            }
            return true;
        }
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes, int type) {
        if (animation != null) {
            animation.skipToEnd();
        }
        if (animation == null) {
            animation = init();
        }
        super.onNestedScrollAccepted(child, target, nestedScrollAxes, type);
    }


    public void RefreshComplete() {
        isRefresh = false;
        SpringBack(-middle, 0);
    }

    public void OnRefresh() {
        SpringBack(0, -middle);
    }

    private void SpringBack(int start, int end) {
        if (scrolls != 0) {
            animation.getSpring().setFinalPosition(end);
            animation.setStartValue(start);
            animation.start();
        }
    }

    @Override
    public void onStopNestedScroll(View target, int type) {

        if (scrolls != 0) {
            int abs = Math.abs(scrolls);
            if (abs >= middle / 2) {
                SpringBack(scrolls, -middle);
            } else {
                SpringBack(scrolls, 0);
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
        scrolls= (int) value;
        System.out.println(value + "--" + velocity);
        if (callback != null) {
            callback.pull(PullCallback.PULLDownBack, -(int) value);
            if (value == -middle && 0 == velocity) {
                isRefresh = true;
                callback.middle();
            }
        }
    }

    @Override
    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        scrolls = (int) value;
        System.out.println(value+"end");
    }

    interface PullCallback {
        int PULLDOWN = -1, PULLDownBack = 1;

        void pull(int dy, int scroll);

        void middle();
    }
}
