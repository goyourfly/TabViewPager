# TabViewPager
[ ![Download](https://api.bintray.com/packages/goyourfly/maven/TabViewPager/images/download.svg) ](https://bintray.com/goyourfly/maven/TabViewPager/_latestVersion) <a href="http://www.methodscount.com/?lib=com.goyourfly%3Atabviewpager%3A%2B"><img src="https://img.shields.io/badge/Methods and size-core: 96 | 30 KB-e91e63.svg"/></a>





TabViewPager，通过简单的接口实现ViewPager，Head，TabLayout，RecyclerView组合展示

### Demo
<img src="img/display.gif"/>

### Compile

[ ![Download](https://api.bintray.com/packages/goyourfly/maven/TabViewPager/images/download.svg) ](https://bintray.com/goyourfly/maven/TabViewPager/_latestVersion)

````java
compile 'com.goyourfly:tabviewpager:latestVersion'
````

### Usage

* xml

````xml
<com.goyourfly.tabviewpager.TabViewPager
        android:id="@+id/tabViewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
````

* code


````java
// Load a header layout
val header = View.inflate(this, R.layout.layout_header, null)
// Use TabViewPager.Builder() to init
TabViewPager.Builder()
        .with(tabViewPager)
        .tabs(arrayOf("A", "B", "C", "D", "E", "F", "G"))// tab array
        .header(header)// header view
        .parallax(false)// if use parallax
        .layoutManager {
            LinearLayoutManager(this)// layout manager
        }
        .adapterProvider {
            MyAdapter()// recyclerView adapter
        }.build() // final step, build
````
