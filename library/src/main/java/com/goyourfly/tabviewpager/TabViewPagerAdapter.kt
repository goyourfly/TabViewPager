package com.goyourfly.tabviewpager

import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by gaoyufei on 2017/10/18.
 */

class TabViewPagerAdapter(val tabs: ArrayList<String>,
                          val tabViewPager: TabViewPager,
                          val adapterProvider: AdapterProvider,
                          val layoutManagerProvider: LayoutManagerProvider) : PagerAdapter() {
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
        recyclerView.layoutManager = layoutManagerProvider.onGetLayoutManager(position)
        recyclerView.adapter = adapterProvider.onGetAdapter(position)
        recyclerView.setPadding(0,tabViewPager.getHeaderHeight(),0,0)
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