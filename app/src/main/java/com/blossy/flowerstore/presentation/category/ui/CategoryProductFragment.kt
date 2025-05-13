package com.blossy.flowerstore.presentation.category.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.blossy.flowerstore.databinding.FragmentCategoryProductBinding
import com.blossy.flowerstore.presentation.category.viewmodel.CategoryViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.blossy.flowerstore.R

@AndroidEntryPoint
class CategoryProductFragment : Fragment() {

    private lateinit var binding: FragmentCategoryProductBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var searchProductAdapter: SearchProductAdapter
    private val args: CategoryProductFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        val category = args.category
        val set = setOf(category.id)
        viewModel.loadProducts(categories = set);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryProductBinding.inflate(inflater, container, false)
        binding.titleText.text = args.category.name
        searchProductAdapter = SearchProductAdapter() { product ->
            val action = CategoryProductFragmentDirections
                .actionCategoryProductFragmentToProductDetailFragment(product.id, "cartProduct")
            findNavController().navigate(action)
        }
        binding.recyclerViewProduct.adapter = searchProductAdapter
        binding.recyclerViewProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        observeData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun observeData() {
        collectState(viewModel.productsUiState) { state ->
            when (state) {
                is UiState.Loading -> {
//                    binding.progress.root.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
//                    binding.progress.root.visibility = View.GONE
                    searchProductAdapter.submitList(state.data.products)
                }

                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
//                    binding.progress.root.visibility = View.GONE
                }
                else -> {}
            }

        }
    }

    private fun setOnClickListener() {
        binding.btnBack.setOnClickListener {
            findNavController().apply {
                navigate(R.id.action_categoryProductFragment_to_categoryListFragment)
                popBackStack()
            }
        }
    }



    companion object {

    }
}