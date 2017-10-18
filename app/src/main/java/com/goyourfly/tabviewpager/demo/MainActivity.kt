package com.goyourfly.tabviewpager.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goyourfly.tabviewpager.AdapterProvider
import com.goyourfly.tabviewpager.LayoutManagerProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabViewPager.setup(
                arrayListOf("A", "B", "C"),
                layoutInflater.inflate(R.layout.layout_header, null),
                object:AdapterProvider(){
                    override fun onGetAdapter(position: Int): RecyclerView.Adapter<RecyclerView.ViewHolder> {
                        return MyAdapter()
                    }
                },
                object:LayoutManagerProvider(){
                    override fun onGetLayoutManager(position: Int): RecyclerView.LayoutManager {
                        return LinearLayoutManager(this@MainActivity)
                    }
                })
    }

    class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        }

        override fun getItemCount(): Int {
            return 100
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            Log.d("MainActivity","onCreateViewHolder")
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content,parent,false))
        }

        inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view){

        }

    }
}
