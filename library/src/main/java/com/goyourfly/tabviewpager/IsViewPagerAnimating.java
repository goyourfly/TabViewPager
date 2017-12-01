package com.goyourfly.tabviewpager;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by gaoyufei on 2017/12/2.
 */

public class IsViewPagerAnimating {
    public static final boolean isAnimating(ViewPager viewPager){
        Class klass = ViewPager.class;
        Scroller scroller = null;
        try {
            Field field = klass.getDeclaredField("mScroller");
            field.setAccessible(true);
            scroller = (Scroller) field.get(viewPager);
            return !scroller.isFinished();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }
}
