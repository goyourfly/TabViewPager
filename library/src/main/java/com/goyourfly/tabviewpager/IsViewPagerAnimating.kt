package com.goyourfly.tabviewpager

import android.support.v4.view.ViewPager
import android.widget.Scroller

/**
 * Created by gaoyufei on 2017/12/2.
 * 这个类用来判断ViewPager是否正在做动画
 */

object IsViewPagerAnimating {
    fun isAnimating(viewPager: ViewPager): Boolean {
        val klass = ViewPager::class.java
        var scroller: Scroller? = null
        try {
            val field = klass.getDeclaredField("mScroller")
            field.isAccessible = true
            scroller = field.get(viewPager) as Scroller
            return !scroller.isFinished
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }
}
