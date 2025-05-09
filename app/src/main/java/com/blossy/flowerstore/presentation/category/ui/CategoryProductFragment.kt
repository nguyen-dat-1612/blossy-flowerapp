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
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCategoryProductBinding
import com.blossy.flowerstore.presentation.category.viewmodel.CategoryProductViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import com.blossy.flowerstore.presentation.shippingAddress.ui.AddEditAddressFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel


@AndroidEntryPoint
class CategoryProductFragment : Fragment() {

    private lateinit var binding: FragmentCategoryProductBinding
    private lateinit var viewModel: CategoryProductViewModel
    private lateinit var searchProductAdapter: SearchProductAdapter
    private val args: CategoryProductFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryProductViewModel::class.java)
        val category = args.category
        viewModel.loadProducts(category = category.id);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryProductBinding.inflate(inflater, container, false)
        binding.brandText.text = args.category.name
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

    fun observeData() {
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



    companion object {

    }
}