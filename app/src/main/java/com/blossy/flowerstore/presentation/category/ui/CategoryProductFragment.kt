package com.blossy.flowerstore.presentation.category.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.blossy.flowerstore.databinding.FragmentCategoryProductBinding
import com.blossy.flowerstore.presentation.category.viewmodel.CategoryViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.blossy.flowerstore.R

@AndroidEntryPoint
class CategoryProductFragment : Fragment() {

    private var _binding: FragmentCategoryProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchProductAdapter: SearchProductAdapter
    private val args: CategoryProductFragmentArgs by navArgs()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = args.category
        val set = setOf(category.id)
        categoryViewModel.loadProducts(categories = set);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryProductBinding.inflate(inflater, container, false)
        setUpUI()
        observeData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setUpUI() = with(binding) {
        titleText.text = args.category.name
        searchProductAdapter = SearchProductAdapter() { product ->
            val action = CategoryProductFragmentDirections
                .actionCategoryProductFragmentToProductDetailFragment(product.id, "cartProduct")
            findNavController().navigate(action)
        }
        recyclerViewProduct.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchProductAdapter
        }
    }

    private fun observeData() {
        collectState(categoryViewModel.categoryUiState) { state ->
            when (val getProducts = state.getProductsState) {
                is UiState.Loading -> { binding.progressOverlay.root.visibility = View.VISIBLE }
                is UiState.Success -> {
                    binding.progressOverlay.root.visibility = View.GONE
                    searchProductAdapter.submitList(getProducts.data.products)
                }
                is UiState.Error -> { binding.progressOverlay.root.visibility = View.GONE }
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {
        private const val TAG = "CategoryProductFragment"
    }
}