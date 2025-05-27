package com.blossy.flowerstore.presentation.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.blossy.flowerstore.databinding.FragmentFilterBottomSheetBinding
import com.blossy.flowerstore.domain.model.CategoryModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.search.adapter.CategoryFilterAdapter
import com.blossy.flowerstore.presentation.search.viewmodel.FilterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yazantarifi.slider.RangeSliderListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: FilterViewModel
    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    var onFilterApplied: ((Set<String>, Pair<Int, Int>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        viewModel.getPriceRange()
        viewModel.getCategories()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservers()
        loadData()
    }

    private fun initViews() {

        binding.slider.onAddRangeListener(object : RangeSliderListener {
            override fun onRangeProgress(fromValue: Float, toValue: Float, isFromUser: Boolean) {
                if (isFromUser) {
                    binding.priceMin.text = formatPrice(fromValue.toInt())
                    binding.priceMax.text = formatPrice(toValue.toInt())
                }
            }

            override fun onThumbMovement(value: Float, thumbIndex: Int, isFromUser: Boolean) {

            }
        })
        binding.resetButton.setOnClickListener {
            viewModel.resetFilters()
            resetSliderToDefault()
            binding.recyclerViewCategories.adapter?.notifyDataSetChanged()
        }
        binding.applyButton.setOnClickListener {
            val selectedCategories = viewModel.selectedCategories.value
            val priceFrom = binding.slider.getSliderFromValue().toInt()
            val priceTo = binding.slider.getSliderToValue().toInt()
            val priceRange = priceFrom to priceTo

            onFilterApplied?.invoke(selectedCategories, priceRange)
            dismiss()
        }
    }
    private fun setupObservers() {
        collectState(viewModel.categoryUiState) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.Success -> setupCategories(state.data)
                is UiState.Error -> showError(state.message)
                UiState.Idle -> Unit
            }
        }

        collectState(viewModel.priceRange) { (min, max) ->
            updatePriceSlider(min, max)
        }
    }

     private fun loadData() {
        viewModel.getCategories()
        viewModel.getPriceRange()
    }

    private fun showLoading() {
        // Hiển thị loading indicator nếu cần
    }

    private fun setupCategories(categories: List<CategoryModel>) {
        val adapter = CategoryFilterAdapter(
            categories = categories,
            selectedCategories = viewModel.selectedCategories.value,
            onCategorySelected = { categoryId ->
                viewModel.toggleCategorySelection(categoryId)
            }
        )
        binding.recyclerViewCategories.adapter = adapter
    }

    private fun updatePriceSlider(min: Int, max: Int) {
        binding.slider.onUpdateRangeValues(min.toFloat(), max.toFloat())
        binding.priceMin.text = formatPrice(min)
        binding.priceMax.text = formatPrice(max)
    }

    private fun resetSliderToDefault() {
        val (min, max) = viewModel.priceRange.value
        binding.priceMin.text = formatPrice(min)
        binding.priceMax.text = formatPrice(max)
    }

    private fun applyFilters() {
        val selectedCategories = viewModel.selectedCategories.value
        val priceFrom = binding.slider.getSliderFromValue().toInt()
        val priceTo = binding.slider.getSliderToValue().toInt()
        val priceRange = priceFrom to priceTo

        // Log dữ liệu được gửi đi
        Log.d("FilterBottomSheet", "Selected categories: $selectedCategories")
        Log.d("FilterBottomSheet", "Price range: $priceRange")

        onFilterApplied?.invoke(selectedCategories, priceRange)
        dismiss()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun formatPrice(amount: Int): String {
        return "$${amount}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}