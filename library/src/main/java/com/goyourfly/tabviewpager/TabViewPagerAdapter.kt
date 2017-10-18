package com.goyourfly.tabviewpager

import android.support.v4.view.PagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by gaoyufei on 2017/10/18.
 * ViewPager的Adapter，主要负责初始化RecyclerView
 */

class TabViewPagerAdapter(val tabs: Array<String>,
                          val tabViewPager: TabViewPager,
                          val bindAdapter: (recycler:RecyclerView,position:Int) -> Unit) : PagerAdapter() {
    val map = mutableMapOf<Int, RecyclerView>()
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
        container.addView(recyclerView)
        recyclerView.addOnScrollListener(tabViewPager.scrollListener)
        recyclerView.clipToPadding = false
        recyclerView.setPadding(0,tabViewPager.getHeaderHeight(),0,0)
        bindAdapter(recyclerView,position)

        tabViewPager.scrollTo(recyclerView,tabViewPager.headerTranslateY)
        map.put(position, recyclerView)
        return recyclerView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, view: Any) {
        if (view is View) {
            container?.removeView(view)
            map.remove(position)
        }
    }

    fun getRecyclerView(position: Int) = map[position]
}