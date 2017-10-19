package com.goyourfly.tabviewpager

import android.content.Context
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
 * Created by gaoyufei on 2017/10/18.
 * 简单的接口实现ViewHeader，ViewPager，TabLayout和RecyclerView
 * 配合使用时，滑动时，TabLayout驻留在屏幕顶部的功能
 */

class TabViewPager : FrameLayout {
    // 作为Header的容器
    val headBox = LinearLayout(context)
    // HeaderView
    var headerView: View? = null
    // TabLayout
    val tabLayout = TabLayout(context)
    // ViewPager
    val viewPager = ViewPager(context)

    lateinit var viewPagerAdapter: TabViewPagerAdapter

    var mHeaderHeight = -1

    var headerTranslateY = 0


    // Header滑动是否Parallax
    var parallax = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // 记录当前RecyclerView滑动位置
            var translateY = viewPagerAdapter.headerTranslateMap[recyclerView]
            if (translateY == null)
                return
            translateY += dy
            viewPagerAdapter.headerTranslateMap.put(recyclerView, translateY)

            // 获取最大滑动距离
            val headerMaxHeight = if (headerView == null) 0 else headerView!!.measuredHeight

            // 边界检测
            if (translateY < 0)
                return
            if (translateY > headerMaxHeight && dy < 0)
                return

            headerTranslateY += dy
            if (headerTranslateY < 0) {
                headerTranslateY = 0
            } else if (headerTranslateY > headerMaxHeight) {
                headerTranslateY = headerMaxHeight
            }

            // 移动Header
            headBox.translationY = (-headerTranslateY).toFloat()
            if (parallax) {
                headerView?.translationY = (headerTranslateY / 2).toFloat()
            }
            // 更新其他RecyclerView滑动距离
            viewPagerAdapter.map.values
                    .filter { it != recyclerView }
                    .forEach { recycler ->
                        var scrollY = viewPagerAdapter.headerTranslateMap[recycler]
                        if (scrollY == null)
                            scrollY = 0

                        if (scrollY >= headerTranslateY)
                            return
                        scrollTo(recycler, headerTranslateY)
                    }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, style: Int) : super(context, attr, style)

    init {
        addView(viewPager)
        addView(headBox, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headBox.orientation = LinearLayout.VERTICAL
        tabLayout.setBackgroundColor(Color.WHITE)
    }

    internal fun getHeaderHeight(): Int {
        if (mHeaderHeight <= 0) {
            mHeaderHeight = headBox.measuredHeight
        }
        return mHeaderHeight
    }

    internal fun scrollTo(recycler: RecyclerView, headerTranslateY: Int) {
        viewPagerAdapter.headerTranslateMap.put(recycler, headerTranslateY)
        val needMoveOffset = -headerTranslateY

        if (recycler.layoutManager is LinearLayoutManager) {
            (recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, needMoveOffset)
        } else if (recycler.layoutManager is StaggeredGridLayoutManager) {
            (recycler.layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(0, needMoveOffset)
        }
    }

    fun setup(tabs: Array<String>,
              subHeaderView: View?,
              parallax: Boolean = false,
              bindAdapter: (recycler: RecyclerView, position: Int) -> Unit) {
        this.parallax = parallax
        headBox.removeAllViews()
        headerView = subHeaderView
        if (subHeaderView != null)
            headBox.addView(subHeaderView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headBox.addView(tabLayout, LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // 初始化ViewPager
        viewPagerAdapter = TabViewPagerAdapter(tabs, this, bindAdapter)
        viewPager.adapter = viewPagerAdapter
        // 初始化ViewPager
        tabLayout.setupWithViewPager(viewPager)
        headBox.post {
            viewPagerAdapter.map.values.forEach {
                it.setPadding(0, getHeaderHeight(), 0, 0)
                scrollTo(it, headerTranslateY)
            }
        }
    }

    /**
     * 获取对应位置的RecyclerView
     */
    fun getRecyclerView(position: Int) = viewPagerAdapter.getRecyclerView(position)

    /**
     * 获取RecyclerView的Adapter
     */
    fun getAdapter(position: Int) = getRecyclerView(position)?.adapter

    /**
     * 添加ViewPager的滑动事件
     */
    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        viewPager.addOnPageChangeListener(listener)
    }
}