package com.goyourfly.tabviewpager

import android.support.v7.widget.RecyclerView

/**
 * Created by gaoyufei on 2017/10/18.
 */
abstract class LayoutManagerProvider {
    abstract fun onGetLayoutManager(position:Int):RecyclerView.LayoutManager;
}