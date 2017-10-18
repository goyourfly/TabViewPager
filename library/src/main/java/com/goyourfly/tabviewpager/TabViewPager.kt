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
 */

class TabViewPager : FrameLayout {
    // 作为Header的容器
    val headBox = LinearLayout(context)
    // TabLayout
    val tabLayout = TabLayout(context)
    // ViewPager
    val viewPager = ViewPager(context)

    lateinit var viewPagerAdapter: TabViewPagerAdapter

    var mHeaderHeight = -1

    var headerTranslateY = 0
    // 存储每个RecyclerView对应的滑动距离
    val headerTranslateMap = mutableMapOf<RecyclerView, Int>()

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // 记录当前RecyclerView滑动位置
            var translateY = headerTranslateMap[recyclerView]
            if (translateY == null)
                translateY = 0
            translateY += dy
            headerTranslateMap.put(recyclerView, translateY)

            // 获取最大滑动距离
            val headerMaxHeight = headBox.getChildAt(0).measuredHeight
            Log.d("TabViewPager", "HeaderMax:$headerMaxHeight,$translateY")
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

            headBox.translationY = (-headerTranslateY).toFloat()
            viewPagerAdapter.map.values
                    .filter { it != recyclerView }
                    .forEach { recycler ->
                        var scrollY = headerTranslateMap[recycler]
                        if (scrollY == null)
                            scrollY = 0

                        if (scrollY >= headerTranslateY)
                            return
                        scrollY = headerTranslateY
                        headerTranslateMap.put(recycler, scrollY)

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

    fun getHeaderHeight(): Int {
        if (mHeaderHeight <= 0) {
            mHeaderHeight = headBox.measuredHeight
        }
        return mHeaderHeight
    }

    fun scrollTo(recycler: RecyclerView, headerTranslateY: Int) {
        val needMoveOffset = -headerTranslateY

        if (recycler.layoutManager is LinearLayoutManager) {
            (recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, needMoveOffset)
        } else if (recycler.layoutManager is StaggeredGridLayoutManager) {
            (recycler.layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(0, needMoveOffset)
        }
    }

    fun setup(tabs: ArrayList<String>,
              subHeaderView: View,
              adapterProvider: AdapterProvider,
              layoutManagerProvider: LayoutManagerProvider) {
        headBox.removeAllViews()
        headBox.addView(subHeaderView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headBox.addView(tabLayout, LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // 初始化ViewPager
        viewPagerAdapter = TabViewPagerAdapter(tabs, this, adapterProvider, layoutManagerProvider)
        viewPager.adapter = viewPagerAdapter
        // 初始化ViewPager
        tabLayout.setupWithViewPager(viewPager)
        headBox.post {
            viewPagerAdapter.map.values.forEach {
                it.setPadding(0, getHeaderHeight(), 0, 0)
                scrollTo(it,headerTranslateY)
            }
        }
    }

    /**
     * 获取对应位置的RecyclerView
     */
    fun getRecyclerView(position: Int) = viewPagerAdapter.getRecyclerView(position)
}