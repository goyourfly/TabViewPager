package com.goyourfly.tabviewpager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.ViewTreeObserver


/**
 * Created by gaoyufei on 2017/12/1.
 * 自定义一个子类，继承这个基类
 */

abstract class BaseTabViewPagerFragment : Fragment() {
    // RecyclerView当前位置，这个位置在第一个Item不可见时就没有意义了，
    // 所以最简单的做法是直接获取第一个元素的位置
    var scrollY = 0
    var position = -1
    lateinit var tabViewPager: TabViewPager
    // 回调给TabViewPager的滑动事件
    var tabViewPagerOnScrollListener: OnScrollListener? = null

    val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val llm = recyclerView.layoutManager
            val firstView = llm.findViewByPosition(0)
            if (firstView != null) {
                val top = llm.getDecoratedTop(firstView)
                if (scrollY != top) {
                    scrollY = top
                }
            }
            tabViewPagerOnScrollListener?.onScroll(position, dx, -dy)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            tabViewPagerOnScrollListener?.onScrollStateChanged(position, newState)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecyclerView()?.addOnScrollListener(onScrollListener)
        getRecyclerView()?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            var first = true
            override fun onDraw(c: Canvas?, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                // 为了在RecyclerView Layout完成之后执行滑动操作（Layout之前这个滑动没有响应）
                // 我本人试了N中方法，最后灵机一动，想到这个，虽然不太光彩，但是，好歹比用
                // Handler.postDelay(...)强
                if (first) {
                    first = false
                    parent.clipToPadding = false
                    val paddingBottom = getPaddingBottom(parent, state)
                    parent.setPadding(parent.paddingLeft, parent.paddingTop, parent.paddingRight, paddingBottom)
                    parent.post {
                        scrollTo(0, tabViewPager.headerTranslateY)
                    }
                }
            }

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                if (position == 0) {
                    outRect.top = tabViewPager.getHeaderHeight()
                }
            }

            fun getPaddingBottom(parent: RecyclerView, state: RecyclerView.State): Int {
                var childTotalHeight = 0
                // 是否只有一种类型的Adapter

                for (i in 0..(parent.childCount - 1)) {
                    childTotalHeight += parent.getChildAt(i).measuredHeight
                }
                var onlyOneType = true
                var lastType: Int = Int.MAX_VALUE
                // 超过50个，就不往下走了，大概就是一种类型了
                for (i in 0..Math.min(20, state.itemCount - 1)) {
                    if (lastType == Int.MAX_VALUE) {
                        lastType = parent.adapter.getItemViewType(i)
                        continue
                    }
                    val now = parent.adapter.getItemViewType(i)
                    if (now != lastType) {
                        onlyOneType = false
                    }
                }

                val perItemHeight = if (parent.childCount > 0) parent.getChildAt(0).measuredHeight else 0
                val totalItemHeight = state.itemCount * perItemHeight
                return if (onlyOneType
                        && totalItemHeight != 0
                        && totalItemHeight < parent.measuredHeight
                        && totalItemHeight > childTotalHeight) {
                    Math.max(parent.measuredHeight - totalItemHeight - tabViewPager.getTabLayout().measuredHeight, parent.paddingBottom)
                } else if (childTotalHeight < parent.measuredHeight) {
                    Math.max(parent.measuredHeight - childTotalHeight - tabViewPager.getTabLayout().measuredHeight, parent.paddingBottom)
                } else {
                    parent.paddingBottom
                }
            }
        })


    }

    /**
     * 初始化
     */
    fun setup(position: Int, tabViewPager: TabViewPager, onScrollListener: OnScrollListener) {
        this.position = position
        this.tabViewPager = tabViewPager
        this.tabViewPagerOnScrollListener = onScrollListener
    }

    fun scrollTo(position: Int, offset: Int) {
        val recycler = getRecyclerView() ?: return
        scrollY = offset
        val offsetY = offset
        if (recycler.layoutManager is LinearLayoutManager) {
            (recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offsetY)
        } else if (recycler.layoutManager is StaggeredGridLayoutManager) {
            (recycler.layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(position, offsetY)
        }
    }

    abstract fun getRecyclerView(): RecyclerView?

    override fun onDestroy() {
        super.onDestroy()
        getRecyclerView()?.removeOnScrollListener(onScrollListener)
    }

    /**
     * 暴露给TabViewPager的滚动事件
     */
    interface OnScrollListener {

        fun onScroll(position: Int, dx: Int, dy: Int)

        fun onScrollStateChanged(position: Int, newState: Int)

    }
}