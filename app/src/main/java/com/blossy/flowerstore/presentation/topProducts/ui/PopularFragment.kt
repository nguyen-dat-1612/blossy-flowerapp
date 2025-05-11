package com.blossy.flowerstore.presentation.topProducts.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCategoryProductBinding
import com.blossy.flowerstore.databinding.FragmentPopularBinding
import com.blossy.flowerstore.presentation.category.viewmodel.CategoryProductViewModel
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.home.adapter.ProductAdapter
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import com.blossy.flowerstore.presentation.topProducts.viewmodel.PopularViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : Fragment() {

    private lateinit var binding: FragmentPopularBinding
    private lateinit var viewModel: PopularViewModel
    private lateinit var searchProductAdapter: SearchProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PopularViewModel::class.java)
        viewModel.fetchPopularProducts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPopularBinding.inflate(inflater, container, false)
        searchProductAdapter = SearchProductAdapter() { product ->
//                        val navController = requireActivity().findNavController(R.id.nav_host_main)
//                        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(product.id, "home")
//                        navController.navigate(action)
        }
        binding.recyclerViewProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchProductAdapter
        }
        observeTopProducts()
        setOnClickListener()
        return binding.root
    }

    private fun observeTopProducts() {
        collectState(viewModel.productUiState) { state ->
            when (state) {
                is UiState.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    Log.d("HomeFragment", "Success: ${state.data}")
//                    binding.progressBar.visibility = View.GONE
                    searchProductAdapter.submitList(state.data)
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

    fun setOnClickListener() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_popularFragment_to_mainFragment)
            findNavController().popBackStack()
        }
    }

    companion object {

    }
}