package coms.kxjsj.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vange on 2017/12/7.
 */

public class ScrollBehavior extends CoordinatorLayout.Behavior {
    public ScrollBehavior() {
        super();
    }

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent,child,dependency);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }
}
