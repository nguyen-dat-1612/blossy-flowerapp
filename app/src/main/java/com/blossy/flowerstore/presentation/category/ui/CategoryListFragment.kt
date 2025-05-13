package com.blossy.flowerstore.presentation.category.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCategoryListBinding
import com.blossy.flowerstore.presentation.category.adapter.CategoryListAdapter
import com.blossy.flowerstore.presentation.category.viewmodel.CategoryViewModel
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.home.adapter.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private lateinit var binding: FragmentCategoryListBinding
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        categoryViewModel.loadCategories()

        categoryAdapter = CategoryListAdapter { category ->
            val action = CategoryListFragmentDirections.actionCategoryListFragmentToCategoryProductFragment(category)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        observe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun observe() {
        collectState(categoryViewModel.categoryUiState) {
            when (it) {
                is UiState.Loading -> {
                    // Show loading state
                }
                is UiState.Success -> {
                    categoryAdapter.submitList(it.data)
                    binding.recyclerViewCategories.adapter = categoryAdapter
                }
                is UiState.Error -> {
                    // Show error state
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().apply {
                navigate(R.id.action_categoryListFragment_to_mainFragment)
                popBackStack()
            }
        }
    }

    companion object {
        private const val TAG = "CategoryListFragment"
    }
}