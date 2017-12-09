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
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by vange on 2017/12/7.
 */

public class ScrollBehavior extends AppBarLayout.Behavior {
    private boolean mIsBeingDragged;
    private int mActivePointerId = -1;
    private int mLastMotionY;
    private int mTouchSlop = -1;
    private Class<?> superclass;
    private Field fieldmIsBeingDragged;
    private Method scroll;
    private VelocityTracker mVelocityTracker;
    private Method fling;
    private Method animateOffsetTo;
    private Method canDragViewMethod;

    public ScrollBehavior() {
        super();
    }

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private void ensureVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }
    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if (mTouchSlop < 0) {
            mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }

        final int action = ev.getAction();
        // Shortcut since we're being dragged
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true;
        }

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View child1 = ((MyCoordinatorLayout) parent).getmBottomView();
                if (canDragView(child)&&parent.isPointInChildBounds(child, x, y)&&(child1!=null&&!parent.isPointInChildBounds(child1,x,y))) {
                    mLastMotionY = y;
                    mActivePointerId = ev.getPointerId(0);
                    ensureVelocityTracker();
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int activePointerId = mActivePointerId;
                if (activePointerId == -1) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }

                final int y = (int) ev.getY(pointerIndex);
                final int yDiff = Math.abs(y - mLastMotionY);
                if (yDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mIsBeingDragged = false;
                mActivePointerId = -1;
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(ev);
        }
        return mIsBeingDragged;
    }

    private Class getshortcut(AppBarLayout child) {
        try {
            if (superclass == null) {
                superclass = getClass().getSuperclass().getSuperclass();
            }
            if (fieldmIsBeingDragged == null) {
                fieldmIsBeingDragged = superclass.getDeclaredField("mIsBeingDragged");
            }
            fieldmIsBeingDragged.setAccessible(true);
            return superclass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if (mTouchSlop < 0) {
            mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                if (canDragView(child) && parent.isPointInChildBounds(child, x, y)) {
                    mLastMotionY = y;
                    mActivePointerId = ev.getPointerId(0);
                    ensureVelocityTracker();
                }else {
                    return false;
                }
                break;
            }


            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(0);
                if (activePointerIndex == -1) {
                    return false;
                }

                final int y = (int) ev.getY(activePointerIndex);
                int dy = mLastMotionY - y;

                if (!mIsBeingDragged && Math.abs(dy) > mTouchSlop) {
                    mIsBeingDragged = true;
                    if (dy > 0) {
                        dy -= mTouchSlop;
                    } else {
                        dy += mTouchSlop;
                    }
                }

                if (mIsBeingDragged) {
                    mLastMotionY = y;
                    System.out.println("dy:"+dy);
//                    tste();
                    // We're being dragged so scroll the ABL
                    Method method = getScrollMethod(child,"scroll");
                    try {
                         method.invoke(this, parent, child, dy, -child.getTotalScrollRange(), -((MyCoordinatorLayout) parent).getMax());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
//                    scroll(parent, child, dy, getMaxDragOffset(child), 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float yvel = mVelocityTracker.getYVelocity(mActivePointerId);
                    Method method=getAnimatorMethod(child);
                    try {
                        method.invoke(this,parent,child,0,yvel);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
//                    tste();
//                    Method method = getFlingMethod(child,"fling");
//                    try {
//                        method.invoke(this, parent,child,-child.getTotalScrollRange(),-((MyCoordinatorLayout) parent).getMax(), 0);
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                    fling(parent, child, -child.getTotalScrollRange(), 0, yvel);
                }
                // $FALLTHROUGH
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                mActivePointerId = -1;
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(ev);
        }

        return true;

    }

    private Method getAnimatorMethod(AppBarLayout child) {
        try {
            if(animateOffsetTo==null) {
                animateOffsetTo = getClass().getSuperclass().getDeclaredMethod("animateOffsetTo", CoordinatorLayout.class, AppBarLayout.
                        class, int.class, float.class);
                animateOffsetTo.setAccessible(true);
            }
            return animateOffsetTo;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean canDragView(AppBarLayout child){
        Method method = getcanDragViewMethod();
        try {
            return (boolean) method.invoke(this, child);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
    private Method getcanDragViewMethod() {
        try {
            if(canDragViewMethod ==null) {
                canDragViewMethod = getClass().getSuperclass().getDeclaredMethod("canDragView", AppBarLayout.class);
                canDragViewMethod.setAccessible(true);
            }
            return canDragViewMethod;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void tste() {
        Method[] declaredMethods = getClass().getSuperclass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            String name = declaredMethod.getName();
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            String cc="";
            for (Class<?> parameterType : parameterTypes) {
                String name1 = parameterType.getName();
                cc+=name1+"---";
            }
            System.out.println(name+":"+cc+"\n");
        }
    }


    private Method getScrollMethod(AppBarLayout child,String name) {
        try {
            if(fling ==null) {
                fling = getshortcut(child).getDeclaredMethod(name, CoordinatorLayout.class, View.
                        class, int.class, int.class, int.class);
                fling.setAccessible(true);
            }
            return fling;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Method getFlingMethod(AppBarLayout child,String name) {
        try {
            if(scroll==null) {
                scroll = getshortcut(child).getDeclaredMethod(name, CoordinatorLayout.class, View.
                        class, int.class, int.class, float.class);
                scroll.setAccessible(true);
            }
            return scroll;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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
