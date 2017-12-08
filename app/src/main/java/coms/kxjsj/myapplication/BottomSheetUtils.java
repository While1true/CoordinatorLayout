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

/**
 * Created by vange on 2017/12/7.
 */

public class BottomSheetUtils {
    /**
     * 两段折叠
     * @param recyclerView
     */
    public static void setBottomRecyclerView2Collapse(final RecyclerView recyclerView,ViewPager viewPager,final AppBarLayout layout) {
        final CoordinatorLayout.LayoutParams layoutParams= (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
        final CoordinatorLayout.LayoutParams viewPagerlayoutParams= (CoordinatorLayout.LayoutParams) viewPager.getLayoutParams();
        final BottomSheetBehavior sheetBehavior= (BottomSheetBehavior) layoutParams.getBehavior();
        final AppBarLayout.ScrollingViewBehavior viewPagersheetBehavior= (AppBarLayout.ScrollingViewBehavior) viewPagerlayoutParams.getBehavior();
        final int peekHeight = sheetBehavior.getPeekHeight();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(recyclerView.canScrollVertically(-1)&&dx<0){
                    layoutParams.setBehavior(null);
                }else if(layoutParams.getBehavior()==null){
                    layoutParams.setBehavior(sheetBehavior);
                }
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setPeekHeight(recyclerView.getMeasuredHeight()/2);

                    if(recyclerView.getTop()<=layout.getMeasuredHeight()){
                        layout.setExpanded(false,true);
                    }
                }
                if(newState==BottomSheetBehavior.STATE_COLLAPSED){
                    resetPeekHeight(sheetBehavior,peekHeight);
                }


                /**
                 * 防止appbar跟随
                 */
                if(newState==BottomSheetBehavior.STATE_DRAGGING||newState==BottomSheetBehavior.STATE_SETTLING){
                    viewPagerlayoutParams.setBehavior(null);

                }else {
                    viewPagerlayoutParams.setBehavior(viewPagersheetBehavior);
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
