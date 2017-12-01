package com.goyourfly.tabviewpager

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by gaoyufei on 2017/11/28.
 * 空白
 */

class TabViewPagerItemDecoration(val headerHeight:Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        if(position == 0){
            outRect.top = headerHeight
        }
    }
}