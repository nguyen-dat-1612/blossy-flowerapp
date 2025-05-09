package com.blossy.flowerstore.presentation.home.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.databinding.FragmentHomeBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.home.adapter.CategoryAdapter
import com.blossy.flowerstore.presentation.home.adapter.ProductAdapter
import com.blossy.flowerstore.presentation.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.viewpager2.widget.ViewPager2
import com.blossy.flowerstore.R
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.home.adapter.BannerAdapter
import com.bumptech.glide.Glide

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var homeViewModel: HomeViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.loadHomeData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        observeCategories()
        observeTopProducts()
        observeUserProfile()
        val banners = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
        val adapter = BannerAdapter(banners)
        binding.viewPager.adapter = adapter
        autoScrollViewPager(binding.viewPager, banners.size)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickListener()
    }

    private fun onClickListener() {
        binding.searchClickOverlay.setOnClickListener {

            requireActivity().findNavController(R.id.nav_host_main).navigate(
                R.id.action_mainFragment_to_searchFragment,
            )
        }
        binding.notificationBtn.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_main)
            navController.navigate(R.id.action_mainFragment_to_notificationFragment)

        }
    }

    private fun autoScrollViewPager(viewPager: ViewPager2, itemCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        var index = 0
        val runnable = object : Runnable {
            override fun run() {
                index = (index + 1) % itemCount
                viewPager.setCurrentItem(index, true)
                handler.postDelayed(this, 3000) // 3 giây
            }
        }
        handler.post(runnable)
    }

    private fun observeCategories() {
        collectState(homeViewModel.categoryUiState) { state ->
            when (state) {
                is UiState.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    Log.d("HomeFragment", "Success: ${state.data}")
//                    binding.progressBar.visibility = View.GONE
                    categoryAdapter = CategoryAdapter(state.data, onItemClick = { category ->
                        val navController = requireActivity().findNavController(R.id.nav_host_main)
                        val action = MainFragmentDirections.actionMainFragmentToCategoryProductFragment(category)
                        navController.navigate(action)
                    })
                    binding.recyclerViewCategories.apply {
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        adapter = categoryAdapter
                    }
                }
                is UiState.Error -> {
                    Log.e("HomeFragment", "Error: ${state.message}")
//                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    // Handle error state
                }
                is UiState.Idle -> {
                    // Handle idle state
                }
            }

        }

    }

    private fun observeTopProducts() {
        collectState(homeViewModel.productUiState) { state ->
            when (state) {
                is UiState.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    Log.d("HomeFragment", "Success: ${state.data}")
//                    binding.progressBar.visibility = View.GONE
                    productAdapter = ProductAdapter(state.data) { product ->
                        val navController = requireActivity().findNavController(R.id.nav_host_main)
                        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(product.id, "home")
                        navController.navigate(action)
                    }
                    binding.recyclerViewPopular.apply {
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        adapter = productAdapter
                    }
                }

                is UiState.Error -> {
                    Log.e("HomeFragment", "Error: ${state.message}")
//                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    // Handle error state
                }

                is UiState.Idle -> {
                    // Handle idle state
                }
            }
        }

    }
    private fun observeUserProfile() {
        collectState(homeViewModel.userProfileUiState) { state ->
            when (state) {

                is UiState.Loading -> {}
                is UiState.Success -> {
                    Log.d("HomeFragment", "Success: ${state.data}")
                    // Handle success state
                    binding.userName.text = state.data.name
                    Glide.with(binding.userAvatar.context)
                        .load(state.data.avatar)
                        .into(binding.userAvatar)
                }

                is UiState.Error -> {
                    Log.e("HomeFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    // Handle error state
                }
                is UiState.Idle -> {
                    // Handle idle state
                }
            }
        }

    }

    companion object {

    }
}