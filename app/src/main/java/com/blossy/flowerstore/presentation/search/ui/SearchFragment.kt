package com.blossy.flowerstore.presentation.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentSearchBinding
import com.blossy.flowerstore.domain.model.response.ProductListModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.search.adapter.SearchHistoryAdapter
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import com.blossy.flowerstore.presentation.search.state.SearchUiState
import com.blossy.flowerstore.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var searchProductAdapter: SearchProductAdapter

    private var isProgrammaticTextChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToMain()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearchInput()
        setupScrollListener()
        setupListeners()
        observeUiState()

        return binding.root
    }

    private fun navigateBackToMain() {
        findNavController().navigate(R.id.action_searchFragment_to_mainFragment)
        findNavController().popBackStack()
    }

    private fun setupRecyclerView() {
        searchHistoryAdapter = SearchHistoryAdapter { keyword ->
            binding.searchInput.setText(keyword)
            performSearch(keyword)
        }

        searchProductAdapter = SearchProductAdapter { product ->
            val action = SearchFragmentDirections
                .actionSearchFragmentToProductDetailFragment("search", product.id)
            findNavController().navigate(action)
        }
    }

    private fun setupSearchInput() {
        binding.apply {
            searchInput.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = binding.searchInput.text.toString()
                    if (query.isNotEmpty()) performSearch(query)
                    true
                } else false
            }

            searchInput.addTextChangedListener {
                val newText = it?.toString()
                if (!isProgrammaticTextChange && newText.isNullOrEmpty() && searchViewModel.isInSearchMode) {
                    resetToHistoryView()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                navigateBackToMain()
            }
            clearAll.setOnClickListener {
                searchViewModel.clearSearchHistory()
            }
            btnFilter.setOnClickListener {
                showFilterDialog()
            }
        }
    }

    private fun performSearch(query: String) {
        searchViewModel.setSearchMode(true, query)
        searchViewModel.saveSearchQuery(query)
        searchViewModel.searchProducts(keyword = query)
    }

    private fun resetToHistoryView() {
        isProgrammaticTextChange = true
        try {
            binding.searchInput.setText("")
            searchViewModel.resetToHistoryMode()
        } finally {
            isProgrammaticTextChange = false
        }
    }

    private fun showFilterDialog() {
        val filterDialog = FilterBottomSheetFragment().apply {
            onFilterApplied = { categories, priceRange ->
                searchViewModel.searchProducts(
                    keyword = binding.searchInput.text.toString(),
                    categories = categories,
                    minPrice = priceRange.first,
                    maxPrice = priceRange.second
                )
            }
        }
        filterDialog.show(parentFragmentManager, filterDialog.tag)
    }

    private fun setupScrollListener() {
        binding.recyclerViewHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val uiState = searchViewModel.uiState.value
                if (uiState.isInSearchMode && recyclerView.layoutManager is GridLayoutManager) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    val isNearBottom = (lastVisibleItemPosition >= totalItemCount - 5)
                    val hasMinimumItems = totalItemCount >= 1

                    if (uiState.canLoadMore && !uiState.isLoading && hasMinimumItems && isNearBottom) {
                        searchViewModel.loadMoreProducts()
                    }
                }
            }
        })
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: SearchUiState) {
        // Update search input
        if (binding.searchInput.text.toString() != state.lastSearchQuery) {
            binding.searchInput.setText(state.lastSearchQuery)
        }

        // Update view mode
        if (state.isInSearchMode) {
            setupSearchResultsView()
            handleProductsState(state.products)
        } else {
            setupHistoryView()
            handleHistoryState(state.searchHistory)
        }

        // Update loading state
        binding.progress.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE
    }

    private fun setupSearchResultsView() {
        binding.apply {
            searchHistoryTitle.text = "Results"
            clearAll.visibility = View.GONE
            if (recyclerViewHistory.adapter != searchProductAdapter) {
                recyclerViewHistory.adapter = searchProductAdapter
                recyclerViewHistory.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }
    }

    private fun setupHistoryView() {
        binding.apply {
            progress.root.visibility = View.GONE
            recyclerViewHistory.visibility = View.VISIBLE
            clearAll.visibility = View.VISIBLE
            searchHistoryTitle.text = "Search History"

            recyclerViewHistory.adapter = searchHistoryAdapter
            recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleProductsState(productsState: UiState<ProductListModel>) {
        when (productsState) {
            is UiState.Loading -> {
                binding.progress.root.visibility = View.VISIBLE
            }
            is UiState.Success -> {
                binding.progress.root.visibility = View.GONE
                binding.recyclerViewHistory.visibility = View.VISIBLE
                searchProductAdapter.submitList(productsState.data.products)
            }
            is UiState.Error -> {
                binding.progress.root.visibility = View.GONE
                binding.recyclerViewHistory.visibility = View.VISIBLE
                Toast.makeText(requireContext(), productsState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("SearchFragment", "Product state: ${productsState::class.simpleName}")
            }
        }
    }
    private fun handleHistoryState(historyState: UiState<List<String>>) {
        when (historyState) {
            is UiState.Success -> {
                searchHistoryAdapter.submitList(historyState.data)
            }
            else -> {}
        }
    }

    companion object {
        private const val TAG = "SearchFragment"
    }
}