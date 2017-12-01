package com.goyourfly.tabviewpager.demo

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goyourfly.tabviewpager.BaseTabViewPagerFragment

/**
 * Created by gaoyufei on 2017/12/1.
 * 默认Fragment，只做演示用
 */

internal class DefaultViewPagerFragment() : BaseTabViewPagerFragment() {
    override fun getRecyclerView(): RecyclerView? {
        return view as RecyclerView?
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return RecyclerView(context)
    }

    var canLoadMoreData = true
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecyclerView()?.layoutManager = LinearLayoutManager(context)
        val adapter = DefaultAdapter()
        adapter.addData(arrayListOf("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"))
//        adapter.addData(arrayListOf("1", "1", "1"))
//        adapter.addData(arrayListOf("1", "1", "1", "1"))
        getRecyclerView()?.adapter = adapter


        getRecyclerView()?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                // 加载更多
//                if (dy > 0 && canLoadMoreData) {
//                    val linearLayoutManager = recyclerView!!.layoutManager as LinearLayoutManager
//                    val visibleItemCount = linearLayoutManager.getChildCount();
//                    val totalItemCount = linearLayoutManager.getItemCount();
//                    val firstVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//                    if ((visibleItemCount + firstVisibleItems) >= totalItemCount) {
//                        canLoadMoreData = false
//                        handler.postDelayed({
//                            adapter.addData(arrayListOf("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"))
//                            adapter.notifyDataSetChanged()
//                            canLoadMoreData = true
//                        }, 1000)
//                    }
//                }
            }
        })
    }

    class DefaultAdapter : RecyclerView.Adapter<DefaultAdapter.DefaultViewHolder>() {
        val list = mutableListOf<String>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
            return DefaultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false))
        }

        override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return list.size
        }


//        override fun getItemViewType(position: Int): Int {
//            return if(position % 10 == 0) 1 else 2
//        }

        fun addData(data: List<String>) {
            list.addAll(data)
        }

        fun setData(data: List<String>) {
            this.list.clear()
            this.list.addAll(data)
        }


        class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
    }
}