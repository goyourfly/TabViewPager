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
    private val STEP_1 = 1
    private val STEP_2 = 2
    private val STEP_NONE = 0

    private val TAG = "TabViewPager"
    // 作为Header的容器
    private val headBox = LinearLayout(context)
    // TabLayout
    private val tabLayout = TabLayout(context)
    // ViewPager
    private val viewPager = ViewPager(context)

    private lateinit var viewPagerAdapter: TabViewPagerAdapter

    internal var headerTranslateY = 0

    private var builder: Builder = Builder()

    private var step = STEP_NONE


    internal val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
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

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 记录当前RecyclerView滑动位置
            var translateY = viewPagerAdapter.headerTranslateMap[recyclerView]
            if (translateY == null)
                return
            translateY += dy
            viewPagerAdapter.headerTranslateMap.put(recyclerView, translateY)

            // 获取最大滑动距离
            val headerMaxHeight = if (builder.headerView == null) 0 else builder.headerView!!.measuredHeight

            // 边界检测
            if (translateY < 0 && dy >= 0)
                return
            if (translateY > headerMaxHeight && dy <= 0)
                return

            Log.d(TAG, "[$headerMaxHeight]translateY:$translateY,headerTranslateY:$headerTranslateY,dy:$dy")

            if (dy < 0 && translateY > headerTranslateY)
                return

            headerTranslateY += dy
            if (headerTranslateY < 0) {
                headerTranslateY = 0
            } else if (headerTranslateY > headerMaxHeight) {
                headerTranslateY = headerMaxHeight
            }

            // 移动Header
            headBox.translationY = (-headerTranslateY).toFloat()
            if (builder.parallax) {
                builder.headerView?.translationY = (headerTranslateY / 2).toFloat()
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, style: Int) : super(context, attr, style)

    init {
        headBox.orientation = LinearLayout.VERTICAL
        tabLayout.setBackgroundColor(Color.WHITE)
    }

    internal fun getHeaderHeight(): Int {
        return headBox.measuredHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        if (step == STEP_1) {
            measureStep1()
        }
        measureChild(headBox, widthMeasureSpec, MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.UNSPECIFIED))
        measureChild(viewPager, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(wSize, hSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (step == STEP_2) {
            measureStep2()
        }
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

    private fun measureStep1() {
        removeAllViews()
        headBox.removeAllViews()
        val subHeaderView = builder.headerView
        if (subHeaderView != null)
            headBox.addView(subHeaderView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headBox.addView(tabLayout, LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(headBox, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        step = STEP_2
        requestLayout()
    }

    private fun measureStep2() {
        val headerHeight = getHeaderHeight()

        val itemDecoration = TabViewPagerItemDecoration(headerHeight)
        val layoutManager = if (builder.layoutManager == null) {
            {
                LinearLayoutManager(context)
            }
        } else builder.layoutManager!!

        // 初始化ViewPager
        viewPagerAdapter = TabViewPagerAdapter(builder.tabs, this, itemDecoration, builder.provider!!, layoutManager)
        viewPager.adapter = viewPagerAdapter
        // 初始化ViewPager
        tabLayout.setupWithViewPager(viewPager)
        step = STEP_NONE

        requestLayout()
    }

    private fun setBuilder(builder: Builder) {
        this.builder = builder
        step = STEP_1
        requestLayout()
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
     * 获取TabLayout，你可以做些自定义的设置
     */
    fun getTabLayout() = tabLayout

    /**
     * 添加ViewPager的滑动事件
     */
    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        viewPager.addOnPageChangeListener(listener)
    }

    class Builder {
        internal var tabs = arrayOf<String>()
        internal var headerView: View? = null
        internal var parallax = false
        internal var provider: ((position: Int) -> RecyclerView.Adapter<*>)? = null
        internal var layoutManager: ((position: Int) -> RecyclerView.LayoutManager)? = null
        private var tabViewPager: TabViewPager? = null

        fun with(tabViewPager: TabViewPager): Builder {
            this.tabViewPager = tabViewPager
            return this
        }

        fun tabs(tabs: Array<String>): Builder {
            this.tabs = tabs
            return this
        }

        fun header(view: View): Builder {
            this.headerView = view
            return this
        }

        fun parallax(parallax: Boolean): Builder {
            this.parallax = parallax
            return this
        }

        fun layoutManager(provider: (position: Int) -> RecyclerView.LayoutManager): Builder {
            this.layoutManager = provider
            return this
        }

        fun adapterProvider(provider: (position: Int) -> RecyclerView.Adapter<*>): Builder {
            this.provider = provider
            return this
        }

        fun build() {
            if (tabViewPager == null)
                throw NullPointerException("You should call with(TabViewPager) before build")
            tabViewPager?.setBuilder(this)
        }
    }
}