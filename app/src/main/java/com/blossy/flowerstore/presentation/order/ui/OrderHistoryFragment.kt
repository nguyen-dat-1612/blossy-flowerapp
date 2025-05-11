package com.blossy.flowerstore.presentation.order.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentOrderHistoryBinding
import com.blossy.flowerstore.presentation.order.adapter.OrderPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OrderPagerAdapter
    private val tabTitles = arrayOf("Pending", "Processing", "Shipped", "Delivered", "Cancelled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        adapter = OrderPagerAdapter(this)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_orderHistoryFragment_to_mainFragment)
            findNavController().popBackStack()
        }

        return binding.root
    }

}