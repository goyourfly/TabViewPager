# TabViewPager
[ ![Download](https://api.bintray.com/packages/goyourfly/maven/TabViewPager/images/download.svg) ](https://bintray.com/goyourfly/maven/TabViewPager/_latestVersion) <a href="http://www.methodscount.com/?lib=com.goyourfly%3Atabviewpager%3A%2B"><img src="https://img.shields.io/badge/Methods and size-core: 96 | 30 KB-e91e63.svg"/></a>





TabViewPager，通过简单的接口实现ViewPager，Head，TabLayout组合展示

### Demo
<img src="img/display.gif"/>

### Compile

[ ![Download](https://api.bintray.com/packages/goyourfly/maven/TabViewPager/images/download.svg) ](https://bintray.com/goyourfly/maven/TabViewPager/_latestVersion)

````java
compile 'com.goyourfly:tabviewpager:latestVersion'
````

### Usage

##### xml

````xml
<com.goyourfly.tabviewpager.TabViewPager
        android:id="@+id/tabViewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
````

##### code

* 1.定义Fragment

````java
/** 
 * 自定义一个Fragment 继承自 BaseTabViewPagerFragment
 * 你可以在这个Fragment中加载数据
 */
class MyFragment() : BaseTabViewPagerFragment() {
	// 重写这个唯一的抽象方法，返回RecyclerView
    override fun getRecyclerView(): RecyclerView? {
        return recyclerView
    }
    ...
}

````

* 2.定义ViewPager的Adapter

````java
// 自定义ViewPagerAdapter必须继承BaseTabViewPagerAdapter
class MyViewPagerAdapter(fm: FragmentManager)
    : TabViewPager.BaseTabViewPagerAdapter(fm) {
    // 要显示的Tab
    val tabs = arrayOf("Tab1", "Tab2", "Tab3", "Tab4")
    override fun getItem(position: Int): BaseTabViewPagerFragment {
    	// 返回上面定义的Fragment
        return MyFragment()
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position]
    }
}
````

* 3.最后一步，初始化TabViewPager

````java
// 加载头布局
val header = View.inflate(this, R.layout.layout_header, null)
// 初始化TabViewPager
TabViewPager.Builder
        .with(tabViewPager)
        .header(header)
        .parallax(true)
        .dispatchTouch(true) // 一般不需要打开，如果Header中有横向滑动的View并且有事件冲突时打开
        .adapter(MyViewPagerAdapter(FragmentManager))// 绑定Adapter
        .build()
````
