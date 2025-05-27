package com.blossy.flowerstore.presentation.topProducts.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentPopularBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import com.blossy.flowerstore.presentation.topProducts.viewmodel.PopularViewModel
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: PopularViewModel by viewModels()
    private lateinit var searchProductAdapter: SearchProductAdapter

    private fun findNavController() = requireActivity().findNavController(R.id.nav_host_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchPopularProducts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopularBinding.inflate(inflater, container, false)
        setUpUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTopProducts()
        setOnClickListener()
    }

    private fun setUpUI() = with(binding) {
        searchProductAdapter = SearchProductAdapter { product ->
            val action = PopularFragmentDirections.actionPopularFragmentToProductDetailFragment(product.id, "home")
            findNavController().navigate(action)
        }
        recyclerViewProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchProductAdapter
        }
    }

    private fun observeTopProducts() {
        collectState(viewModel.popularUiState) { state ->
            binding.progressOverlay.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            searchProductAdapter.submitList(state.productItems)
            if (state.error.isNotBlank())  {
                requireContext().toast(message = state.error)
                Log.d(TAG, "observeTopProducts: ${state.error}")
            }
        }

    }


    fun setOnClickListener() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_popularFragment_to_mainFragment)
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "PopularFragment"
    }
}