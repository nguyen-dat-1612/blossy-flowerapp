package com.blossy.flowerstore.presentation.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
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

        isSearching = searchViewModel.isInSearchMode()
        lastSearchQuery = searchViewModel.getLastSearchQuery()
        Log.d(TAG, "Retrieved state from ViewModel: isSearching=$isSearching, query='$lastSearchQuery'")

        if (!isSearching) {
            Log.d(TAG, "Loading search history (not in search mode)")
            searchViewModel.loadSearchHistory()
        }

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
        Log.d(TAG, "onCreateView")

        setupRecyclerView()
        setupSearchInput()
        setupScrollListener()
        setupListeners()
        observeSearchUiState()

        // Khôi phục trạng thái tìm kiếm từ ViewModel
        if (isSearching && lastSearchQuery.isNotEmpty()) {
            Log.d(TAG, "Restoring search view from ViewModel: query='$lastSearchQuery'")
            binding.searchInput.setText(lastSearchQuery)
            setupSearchResultsView()
            // Không cần tìm kiếm lại vì kết quả nên được lưu trong ViewModel
        } else {
            Log.d(TAG, "Setting up history view in onCreateView")
            setupHistoryView()
        }

        // Theo dõi lịch sử tìm kiếm
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchHistory.collect { history ->
                    if (!isSearching) {
                        searchHistoryAdapter.submitList(history)
                    }
                }
            }
        }

        return binding.root
    }

    private fun navigateBackToMain() {
        findNavController().navigate(R.id.action_searchFragment_to_mainFragment)
        findNavController().popBackStack()
    }

    private fun setupRecyclerView() {
        // Khởi tạo adapter
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
        // Xử lý sự kiện search bằng nhấn enter trên bàn phím
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        // Lắng nghe thay đổi thông tin trong chuỗi tìm kiếm, nếu rỗng thì trả về lịch sử tìm kiếm
        binding.searchInput.addTextChangedListener {
            val newText = it?.toString()
            if (newText.isNullOrEmpty() && isSearching) {
                setupHistoryView()
            }
        }
    }


    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            navigateBackToMain()
        }


        binding.clearAll.setOnClickListener {
            searchViewModel.clearSearchHistory()
        }

        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun performSearch(query: String) {
        Log.d(TAG, "performSearch: query='$query'")
        isSearching = true
        lastSearchQuery = query

        searchViewModel.setSearchMode(true)
        searchViewModel.setLastSearchQuery(query)

        setupSearchResultsView()
//        binding.progress.root.visibility = View.VISIBLE
        binding.recyclerViewHistory.visibility = View.GONE
        searchViewModel.searchProducts(keyword = query)
    }

    // Thiết lập giao diện cho kết quả tìm kiếm
    private fun setupSearchResultsView() {
        Log.d(TAG, "Setting up search results view")
        binding.searchHistoryTitle.text = "Results"
        binding.clearAll.visibility = View.GONE

        // Đảm bảo adapter là searchProductAdapter và layoutManager là GridLayoutManager
        if (binding.recyclerViewHistory.adapter != searchProductAdapter) {
            binding.recyclerViewHistory.adapter = searchProductAdapter
            binding.recyclerViewHistory.layoutManager = GridLayoutManager(requireContext(), 2)
            Log.d(TAG, "Changed to product adapter with GridLayoutManager")
        }
    }

    private fun setupHistoryView() {
        Log.d(TAG, "Setting up history view")
        isSearching = false
        lastSearchQuery = ""

        // Lưu trạng thái vào ViewModel
        searchViewModel.setSearchMode(false)
        searchViewModel.setLastSearchQuery("")

        binding.clearAll.visibility = View.VISIBLE
        binding.searchHistoryTitle.text = "Search History"

        try {// Đảm bảo adapter là searchHistoryAdapter và layoutManager là LinearLayoutManager
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
        if (binding.recyclerViewHistory.adapter != searchHistoryAdapter) {
            binding.recyclerViewHistory.adapter = searchHistoryAdapter
            binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
            Log.d(TAG, "Changed to history adapter with LinearLayoutManager")
        }

        searchViewModel.loadSearchHistory()
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

                Log.d(TAG, "Cuộn tải thêm dữ liệu")
                // Chỉ load thêm khi đang ở chế độ tìm kiếm (dùng GridLayoutManager)
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

                            // Giữ lại danh sách nếu search lại
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
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {
        private const val TAG = "SearchFragment"
    }
}