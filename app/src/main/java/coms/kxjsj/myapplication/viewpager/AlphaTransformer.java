package coms.kxjsj.myapplication.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by vange on 2017/12/6.
 */

public class AlphaTransformer implements ViewPager.PageTransformer {
    ViewPager viewPager;
    float excursion=-1;
    private float MINALPHA=0.8f;
    int width=-1;
    @Override
    public void transformPage(View page, float position) {
        if(excursion==-1){
            excursion=-position;
            width=page.getWidth();
        }
        position=position+excursion;
        if (position < -1 || position > 1) {
            page.setAlpha(MINALPHA);
            page.setScaleX(MINALPHA);
            page.setScaleY(MINALPHA);
            page.setTranslationX(width*0.25f*Math.signum(-position));
        }else {
            //不透明->半透明
            if (position < 0) {//[0,-1]
                float alpha = MINALPHA + (1 + position) * (1 - MINALPHA);
                page.setAlpha(alpha);
                page.setScaleX(alpha);
                page.setScaleY(alpha);
                page.setTranslationX(width*(1-alpha)/2);
            } else {//[1,0]
                //半透明->不透明
                float alpha = MINALPHA + (1 - position) * (1 - MINALPHA);
                page.setAlpha(alpha);
                page.setScaleX(alpha);
                page.setScaleY(alpha);
                page.setTranslationX(width*(alpha-1)/2);
            }
        }
    }
}