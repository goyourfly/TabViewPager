package com.goyourfly.tabviewpager.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.goyourfly.tabviewpager.TabViewPager;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 * Created by Administrator on 2017/10/18 0018.
 * MainActivityJava
 */

public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabViewPager tabViewPager = findViewById(R.id.tabViewPager);
        tabViewPager.setup(new String[]{"A", "B", "C"},
                View.inflate(this, R.layout.layout_header, null),
                true,
                new Function2<RecyclerView, Integer, Unit>() {
                    @Override
                    public Unit invoke(RecyclerView recyclerView, Integer integer) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivityJava.this));
                        recyclerView.setAdapter(new MainActivity.MyAdapter());
                        return null;
                    }
                });
    }

}
