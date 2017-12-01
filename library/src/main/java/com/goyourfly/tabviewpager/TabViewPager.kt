package com.goyourfly.tabviewpager

import android.content.Context
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
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
    private val STEP_END = 3
    private val STEP_NONE = 0

    private val TAG = "TabViewPager"
    // 作为Header的容器
    private val headBox = LinearLayout(context)
    // TabLayout
    private val tabLayout = TabLayout(context)
    // ViewPager
    private val viewPager = ViewPager(context)

    internal var headerTranslateY = 0

    private lateinit var builder: Builder

    private var step = STEP_NONE

    private var adapter: BaseTabViewPagerAdapter? = null


    val onScrollListener = object : BaseTabViewPagerFragment.OnScrollListener {
        override fun onScroll(position: Int, dx: Int, dy: Int) {
            doScroll(position, dx, dy)
        }

        override fun onScrollStateChanged(position: Int, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                doAfterScroll(position)
            }
        }
    }

    fun doScroll(position: Int, dx: Int, dy: Int) {
        if (adapter == null)
            return

        val fragment = adapter!!.getFragment(position)!!
        val translateY = fragment.scrollY

        // 获取最大滑动距离
        val headerMaxHeight = if (builder.headerView == null) 0 else (builder.headerView!!.measuredHeight)

        Log.d(TAG, "onScrolled[$position],scrollY:$translateY,headerY:$headerTranslateY,headerMaxY:$headerMaxHeight,dy:$dy")

        if (dy > 0 && translateY < headerTranslateY) {
            return
        }

        headerTranslateY += dy
        if (headerTranslateY < -headerMaxHeight) {
            headerTranslateY = -headerMaxHeight
        } else if (headerTranslateY > 0) {
            headerTranslateY = 0
        }

        // 移动Header
        headBox.translationY = (headerTranslateY).toFloat()
        if (builder.parallax) {
            builder.headerView?.translationY = -(headerTranslateY / 2).toFloat()
        }
    }

    fun doAfterScroll(position: Int) {
        adapter?.getUnVisibleFragment(position)?.forEach {
            if (it.scrollY > headerTranslateY) {
                it.scrollTo(0, headerTranslateY)
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
        if (step != STEP_NONE) {
            measureChild(headBox, widthMeasureSpec, MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.UNSPECIFIED))
            measureChild(viewPager, widthMeasureSpec, heightMeasureSpec)
        }
        setMeasuredDimension(wSize, hSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (step == STEP_2) {
            post {
                measureStep2()
            }
        }
    }


    private fun measureStep1() {
        removeAllViews()
        headBox.isClickable = true
        headBox.removeAllViews()
        val subHeaderView = builder.headerView
        if (subHeaderView != null)
            headBox.addView(subHeaderView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headBox.addView(tabLayout, LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        viewPager.id = R.id.tab_view_pager
        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(headBox, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        step = STEP_2
        requestLayout()
    }

    private fun measureStep2() {
        adapter = builder.adapter
        adapter?.setTabViewPager(this)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        step = STEP_END
        requestLayout()
    }

    private fun setBuilder(builder: Builder) {
        this.builder = builder
        step = STEP_1
        requestLayout()
    }

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

    abstract class BaseTabViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragmentMap = mutableMapOf<Int, BaseTabViewPagerFragment>()
        private var tabViewPager: TabViewPager? = null

        abstract override fun getItem(position: Int): BaseTabViewPagerFragment

        internal fun setTabViewPager(tabViewPager: TabViewPager) {
            this.tabViewPager = tabViewPager
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as BaseTabViewPagerFragment
            fragment.setup(position, tabViewPager!!, tabViewPager!!.onScrollListener)
            fragmentMap.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            super.destroyItem(container, position, `object`)
            fragmentMap.remove(position)
        }

        fun getFragment(position: Int): BaseTabViewPagerFragment? {
            return fragmentMap[position]
        }

        fun getUnVisibleFragment(position: Int): List<BaseTabViewPagerFragment>
                = fragmentMap.values.filter { it != getFragment(position) }
    }

    var scrollVertical = false
    var downX = 0F
    var downY = 0F

    var isViewPagerAnimating = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!builder.dispatchTouch)
            return super.dispatchTouchEvent(ev)

        if (isViewPagerAnimating || IsViewPagerAnimating.isAnimating(viewPager)) {
            isViewPagerAnimating = true
            if (ev.action == MotionEvent.ACTION_UP
                    || ev.action == MotionEvent.ACTION_CANCEL) {
                isViewPagerAnimating = false
            }
            return super.dispatchTouchEvent(ev)
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                scrollVertical = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(ev.x - downX)
                val dy = Math.abs(ev.y - downY)
                if (!scrollVertical
                        && (dy > dx) && dy > ViewConfiguration.getTouchSlop()) {
                    scrollVertical = true
                    val fakeDownEvent = MotionEvent.obtain(ev)
                    fakeDownEvent.action = MotionEvent.ACTION_DOWN
                    fakeDownEvent.setLocation(fakeDownEvent.x, fakeDownEvent.y - 2 * ViewConfiguration.getTouchSlop())
                    viewPager.dispatchTouchEvent(fakeDownEvent)
                    fakeDownEvent.recycle()
                }
            }
        }
        if (scrollVertical) {
            viewPager.dispatchTouchEvent(ev)
            if (ev.action == MotionEvent.ACTION_UP
                    || ev.action == MotionEvent.ACTION_CANCEL) {
                val fakeCancelEvent = MotionEvent.obtain(ev)
                fakeCancelEvent.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(fakeCancelEvent)
                fakeCancelEvent.recycle()
            }
            return true
        } else {
            return super.dispatchTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!builder.dispatchTouch)
            return super.onInterceptTouchEvent(ev)
        return scrollVertical
    }

    class Builder(val tabViewPager: TabViewPager) {
        internal var headerView: View? = null
        internal var parallax = false
        internal var dispatchTouch = false
        internal var adapter: BaseTabViewPagerAdapter? = null

        companion object {
            fun with(tabViewPager: TabViewPager): Builder {
                return Builder(tabViewPager)
            }
        }

        fun header(view: View): Builder {
            this.headerView = view
            return this
        }

        fun dispatchTouch(dispatchTouch: Boolean): Builder {
            this.dispatchTouch = dispatchTouch
            return this
        }

        fun parallax(parallax: Boolean): Builder {
            this.parallax = parallax
            return this
        }

        fun adapter(adapter: BaseTabViewPagerAdapter): Builder {
            this.adapter = adapter
            return this
        }

        fun build() {
            tabViewPager.setBuilder(this)
        }
    }
}