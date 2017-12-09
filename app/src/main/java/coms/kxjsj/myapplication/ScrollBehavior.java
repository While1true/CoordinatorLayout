package coms.kxjsj.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vange on 2017/12/7.
 */

public class ScrollBehavior extends AppBarLayout.Behavior {
    public ScrollBehavior() {
        super();
    }

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if(ev.getY()<child.getMeasuredHeight()) {
            ev.offsetLocation(0, child.getMeasuredHeight());
           ((ViewPager) (((MyCoordinatorLayout) parent).getmScrollngView())).onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(parent,child,ev);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        if (target == ((MyCoordinatorLayout) coordinatorLayout).getmBottomView() && dyUnconsumed < 0 && type == ViewCompat.TYPE_NON_TOUCH) {
            return;
        }
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        if (target == ((MyCoordinatorLayout) coordinatorLayout).getmBottomView() && dy < 0 && type == ViewCompat.TYPE_NON_TOUCH) {
            return;
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        if (target == ((MyCoordinatorLayout) coordinatorLayout).getmBottomView() && velocityY < 0) {
            return true;
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
}
