package com.goyourfly.tabviewpager.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val header = View.inflate(this,R.layout.layout_header,null)
        tabViewPager.setup(
                arrayOf("A", "B", "C", "D", "E", "F", "G"),
                header,
                true, { recycler, position ->
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = MyAdapter()
        })
    }

    class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            // bind data
        }

        override fun getItemCount(): Int {
            return 100
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false))
        }

        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
