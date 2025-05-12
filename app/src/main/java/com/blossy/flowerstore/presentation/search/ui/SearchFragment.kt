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
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.search.adapter.SearchHistoryAdapter
import com.blossy.flowerstore.presentation.search.adapter.SearchProductAdapter
import com.blossy.flowerstore.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var searchProductAdapter: SearchProductAdapter
    private var isSearching = false
    private var lastSearchQuery: String = ""
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: savedInstanceState is ${if (savedInstanceState == null) "null" else "not null"}")
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        if (!isSearching) {
            Log.d(TAG, "Loading search history (not in search mode)")
            searchViewModel.loadSearchHistory()
        }

        isSearching = searchViewModel.isInSearchMode()
        lastSearchQuery = searchViewModel.getLastSearchQuery()
        Log.d(TAG, "Retrieved state from ViewModel: isSearching=$isSearching, query='$lastSearchQuery'")
        Log.d(TAG, "Search history state: ${searchViewModel.searchHistory.value}")

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
        observeSearchUiState()

        if (isSearching && lastSearchQuery.isNotEmpty()) {
            binding.searchInput.setText(lastSearchQuery)
            setupSearchResultsView()
        } else {
            setupHistoryView()
        }

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
                if (newText.isNullOrEmpty() && isSearching) {
                    setupHistoryView()
                }
                searchViewModel.loadSearchHistory()
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
        Log.d(TAG, "performSearch: query='$query'")
        isSearching = true
        lastSearchQuery = query

        searchViewModel.setSearchMode(true)
        searchViewModel.setLastSearchQuery(query)
        searchViewModel.saveSearchQuery(query)
        setupSearchResultsView()
//        binding.progress.root.visibility = View.VISIBLE
        binding.recyclerViewHistory.visibility = View.GONE
        searchViewModel.searchProducts(keyword = query)
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
        isSearching = false
        lastSearchQuery = ""

        searchViewModel.apply {
            setSearchMode(false)
            setLastSearchQuery("")
        }
        binding.apply {
            searchInput.setText("")
            progress.root.visibility = View.GONE
            recyclerViewHistory.visibility = View.VISIBLE
            clearAll.visibility = View.VISIBLE
            searchHistoryTitle.text = "Search History"

            recyclerViewHistory.adapter = searchHistoryAdapter
            recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())

            searchViewModel.loadSearchHistory()
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

                if (isSearching && recyclerView.layoutManager is GridLayoutManager) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10) {
                        searchViewModel.loadMoreProducts()
                    }
                }
            }
        })
    }
    private fun observeSearchUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.productsUiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            isLoading = true
//                            binding.progress.root.visibility = View.VISIBLE
                        }
                        is UiState.Success -> {
                            isLoading = false
                            binding.progress.root.visibility = View.GONE
                            binding.recyclerViewHistory.visibility = View.VISIBLE

                            if (isSearching) {
                                searchProductAdapter.submitList(state.data.products)
                            }
                        }
                        is UiState.Error -> {
                            isLoading = false
                            binding.progress.root.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        UiState.Idle -> {
                            isLoading = false
                            binding.progress.root.visibility = View.GONE
                        }
                    }
                }
            }
        }
        collectState(searchViewModel.searchHistory) {
            when (it) {
                is UiState.Success -> {
                    searchHistoryAdapter.submitList(it.data)
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val TAG = "SearchFragment"
    }
}