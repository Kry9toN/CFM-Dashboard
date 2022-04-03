package com.dashboard.kotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_main_pages.*

class SpeedTestPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speed_test_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = object: FragmentStateAdapter(this){

            val urls = arrayListOf(
                "https://fast.com/zh/cn/",
                "https://speed.cloudflare.com/",
                "https://www.speedtest.net/"
            )

            override fun getItemCount() = 3

            override fun createFragment(position: Int) = WebViewPage().also {
                it.arguments = Bundle().apply {
                    putString("URL", urls[position])
                }
            }
        }

        viewPager.setCurrentItem(KV.getInt("SpeedTestIndex", 0), false)
        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    KV.putInt("SpeedTestIndex", position)
                }
            }
        )
        runCatching {
            val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(viewPager) as RecyclerView
            val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop * 6) //6 is empirical value

        }
    }
}