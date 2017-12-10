package coms.kxjsj.myapplication;

import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by ck on 2017/12/10.
 */

public class ViewUtils {
    private static final Pools.Pool<Rect> sRectPool = new Pools.SynchronizedPool<>(12);
    private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();

    /**
     * 某个点是否在某个parent的view上
     * @param parent
     * @param child
     * @param x
     * @param y
     * @return
     */
    public boolean isPointInChildBounds(ViewGroup parent,View child, int x, int y) {
        final Rect r = acquireTempRect();
        getDescendantRect(parent,child, r);
        try {
            return r.contains(x, y);
        } finally {
            releaseTempRect(r);
        }
    }
    private static void releaseTempRect(@NonNull Rect rect) {
        rect.setEmpty();
        sRectPool.release(rect);
    }
    @NonNull
    private static Rect acquireTempRect() {
        Rect rect = sRectPool.acquire();
        if (rect == null) {
            rect = new Rect();
        }
        return rect;
    }
    private static void getDescendantRect(ViewGroup parent,View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        offsetDescendantRect(parent, descendant, out);
    }
   private static void offsetDescendantRect(ViewGroup parent, View descendant, Rect rect) {
        Matrix m = sMatrix.get();
        if (m == null) {
            m = new Matrix();
            sMatrix.set(m);
        } else {
            m.reset();
        }

        offsetDescendantMatrix(parent, descendant, m);

        RectF rectF = sRectF.get();
        if (rectF == null) {
            rectF = new RectF();
            sRectF.set(rectF);
        }
        rectF.set(rect);
        m.mapRect(rectF);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }
    private static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != target) {
            final View vp = (View) parent;
            offsetDescendantMatrix(target, vp, m);
            m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }

        m.preTranslate(view.getLeft(), view.getTop());

        if (!view.getMatrix().isIdentity()) {
            m.preConcat(view.getMatrix());
        }
    }













    static class AnimateOffsetUtils{
        private static final int MAX_OFFSET_ANIMATION_DURATION = 600; // ms
        public AnimateOffsetUtils(View child){
            mViewOffsetHelper=new ViewOffsetHelper(child);
        }
        private  ViewOffsetHelper mViewOffsetHelper;
        private  ValueAnimator mOffsetAnimator;
        public void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                           final AppBarLayout child, final int offset, float velocity) {
            final int distance = Math.abs(mViewOffsetHelper.getTopAndBottomOffset() - offset);

            final int duration;
            velocity = Math.abs(velocity);
            if (velocity > 0) {
                duration = 3 * Math.round(1000 * (distance / velocity));
            } else {
                final float distanceRatio = (float) distance / child.getHeight();
                duration = (int) ((distanceRatio + 1) * 150);
            }

            animateOffsetWithDuration(offset, duration);
        }

        private void animateOffsetWithDuration(final int offset, final int duration) {
            final int currentOffset = mViewOffsetHelper.getTopAndBottomOffset();
            if (currentOffset == offset) {
                if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
                    mOffsetAnimator.cancel();
                }
                return;
            }

            if (mOffsetAnimator == null) {
                mOffsetAnimator = new ValueAnimator();
                mOffsetAnimator.setInterpolator(new DecelerateInterpolator());
                mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setHeaderTopBottomOffset((int) animation.getAnimatedValue(),Integer.MIN_VALUE,Integer.MAX_VALUE);
                    }
                });
            } else {
                mOffsetAnimator.cancel();
            }

            mOffsetAnimator.setDuration(Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
            mOffsetAnimator.setIntValues(currentOffset, offset);
            mOffsetAnimator.start();
        }
        private int setHeaderTopBottomOffset(int newOffset,
                                     int minOffset, int maxOffset) {
            final int curOffset = mViewOffsetHelper.getTopAndBottomOffset();
            int consumed = 0;

            if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
                // If we have some scrolling range, and we're currently within the min and max
                // offsets, calculate a new offset
                newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);

                if (curOffset != newOffset) {
                    mViewOffsetHelper.setTopAndBottomOffset(newOffset);
                    // Update how much dy we have consumed
                    consumed = curOffset - newOffset;
                }
            }

            return consumed;
        }



        static class ViewOffsetHelper {

            private final View mView;

            private int mLayoutTop;
            private int mLayoutLeft;
            private int mOffsetTop;
            private int mOffsetLeft;

            public ViewOffsetHelper(View view) {
                mView = view;
            }

            public void onViewLayout() {
                // Now grab the intended top
                mLayoutTop = mView.getTop();
                mLayoutLeft = mView.getLeft();

                // And offset it as needed
                updateOffsets();
            }

            private void updateOffsets() {
                ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
                ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));
            }

            /**
             * Set the top and bottom offset for this {@link android.support.design.widget.ViewOffsetHelper}'s view.
             *
             * @param offset the offset in px.
             * @return true if the offset has changed
             */
            public boolean setTopAndBottomOffset(int offset) {
                if (mOffsetTop != offset) {
                    mOffsetTop = offset;
                    updateOffsets();
                    return true;
                }
                return false;
            }

            /**
             * Set the left and right offset for this {@link android.support.design.widget.ViewOffsetHelper}'s view.
             *
             * @param offset the offset in px.
             * @return true if the offset has changed
             */
            public boolean setLeftAndRightOffset(int offset) {
                if (mOffsetLeft != offset) {
                    mOffsetLeft = offset;
                    updateOffsets();
                    return true;
                }
                return false;
            }

            public int getTopAndBottomOffset() {
                return mOffsetTop;
            }

            public int getLeftAndRightOffset() {
                return mOffsetLeft;
            }

            public int getLayoutTop() {
                return mLayoutTop;
            }

            public int getLayoutLeft() {
                return mLayoutLeft;
            }
        }
    }


}
