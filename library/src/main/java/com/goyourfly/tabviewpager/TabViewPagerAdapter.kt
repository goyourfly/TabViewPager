package com.goyourfly.tabviewpager

import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by gaoyufei on 2017/10/18.
 * ViewPager的Adapter，主要负责初始化RecyclerView
 * 记录每个RecyclerView滑动位置
 */

class TabViewPagerAdapter(val tabs: Array<String>,
                          val tabViewPager: TabViewPager,
                          val itemDecoration: TabViewPagerItemDecoration,
                          val bindAdapter: (position: Int) ->  RecyclerView.Adapter<*>,
                          val bindLayoutManager:(position:Int) -> RecyclerView.LayoutManager) : PagerAdapter() {
    val map = mutableMapOf<Int, RecyclerView>()
    // 存储每个RecyclerView对应的滑动距离
    val headerTranslateMap = mutableMapOf<RecyclerView, Int>()

    override fun isViewFromObject(view: View?, any: Any?): Boolean {
        return view == any
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position]
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val recyclerView = RecyclerView(container.context)
        container.addView(recyclerView,ViewPager.LayoutParams.MATCH_PARENT,ViewPager.LayoutParams.MATCH_PARENT)
        recyclerView.addOnScrollListener(tabViewPager.scrollListener)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.adapter = bindAdapter(position)
        recyclerView.layoutManager = bindLayoutManager(position)

        tabViewPager.scrollTo(recyclerView, tabViewPager.headerTranslateY)
        map.put(position, recyclerView)
        headerTranslateMap.put(recyclerView, tabViewPager.headerTranslateY)

        return recyclerView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, view: Any) {
        if (view is View) {
            container?.removeView(view)
            map.remove(position)
            headerTranslateMap.remove(view)
        }
    }

    fun getRecyclerView(position: Int) = map[position]
}