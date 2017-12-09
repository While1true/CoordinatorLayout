package coms.kxjsj.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by vange on 2017/12/7.
 */

public class BottomSheetUtils {
    /**
     * 两段折叠
     * @param
     */
    public static void setBottomRecyclerView2Collapse(final BottomSheetBehavior sheetBehavior,ViewPager viewPager) {
        final int peekHeight = sheetBehavior.getPeekHeight();
        CoordinatorLayout.LayoutParams viewPagerLayoutParams=null;
        CoordinatorLayout.Behavior behavior=null;
        if(viewPager!=null){
            viewPagerLayoutParams = (CoordinatorLayout.LayoutParams) viewPager.getLayoutParams();
            behavior = viewPagerLayoutParams.getBehavior();
        }
        final CoordinatorLayout.Behavior behavior1=behavior;
        final CoordinatorLayout.LayoutParams viewPagerLayout=viewPagerLayoutParams;
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setPeekHeight(peekHeight*2);


                }
                if(newState==BottomSheetBehavior.STATE_COLLAPSED){
                    resetPeekHeight(sheetBehavior,peekHeight);
                }


                /**
                 * 防止appbar跟随
                 */
                if(newState==BottomSheetBehavior.STATE_DRAGGING||newState==BottomSheetBehavior.STATE_SETTLING){
                    if(viewPagerLayout!=null) {
                        viewPagerLayout.setBehavior(null);
                    }

                }else {
                    if(viewPagerLayout!=null) {
                        viewPagerLayout.setBehavior(behavior1);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        /**
         * 切换会展开bottom的bug
         */
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    /**
     * 实现两段折叠
     * @param sheetBehavior
     * @param peekHeight
     */
    private static void resetPeekHeight(BottomSheetBehavior sheetBehavior,int peekHeight) {
        if(sheetBehavior.getPeekHeight()!=peekHeight) {
            try {
                Field mState = sheetBehavior.getClass().getDeclaredField("mState");
                mState.setAccessible(true);
                mState.setInt(sheetBehavior,BottomSheetBehavior.STATE_EXPANDED);
                sheetBehavior.setPeekHeight(peekHeight);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
