package com.goyourfly.tabviewpager.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goyourfly.tabviewpager.TabViewPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val header = View.inflate(this, R.layout.layout_header, null)
        TabViewPager.Builder
                .with(tabViewPager)
                .header(header)
                .parallax(true)
                .adapter(DefaultViewPagerAdapter(supportFragmentManager))
                .build()
    }
}
