package com.goyourfly.tabviewpager.demo

import android.support.v4.app.FragmentManager
import com.goyourfly.tabviewpager.BaseTabViewPagerFragment
import com.goyourfly.tabviewpager.TabViewPager


internal class DefaultViewPagerAdapter(fm: FragmentManager)
    : TabViewPager.BaseTabViewPagerAdapter(fm) {
    val tabs = arrayOf("Tab1", "Tab2", "Tab3", "Tab4")
    override fun getItem(position: Int): BaseTabViewPagerFragment {
        return DefaultViewPagerFragment()
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position]
    }
}