package com.blossy.flowerstore.presentation.order.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blossy.flowerstore.presentation.order.ui.OrderListFragment

class OrderPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return OrderListFragment.newInstance(position)
    }
}