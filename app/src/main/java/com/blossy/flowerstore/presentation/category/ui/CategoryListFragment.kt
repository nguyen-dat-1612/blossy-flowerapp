package com.blossy.flowerstore.presentation.category.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCategoryListBinding
import com.blossy.flowerstore.databinding.FragmentFavoritesBinding
import com.blossy.flowerstore.presentation.category.adapter.CategoryListAdapter
import com.blossy.flowerstore.presentation.category.viewmodel.CategoryViewModel
import com.blossy.flowerstore.utils.collectState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private  val categoryViewModel: CategoryViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setOnClickListeners()
    }


    private fun setUpRecyclerView() {
        categoryAdapter = CategoryListAdapter { category ->
            val action = CategoryListFragmentDirections.actionCategoryListFragmentToCategoryProductFragment(category)
            findNavController().navigate(action)
        }
        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun observe() {
        collectState(categoryViewModel.categoryUiState) { state ->
            binding.progressBar.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            if (state.categories.isEmpty()) {
                binding.emptyState.root.visibility = View.VISIBLE
            } else {
                binding.emptyState.root.visibility = View.GONE
                categoryAdapter.submitList(state.categories)
                binding.recyclerViewCategories.adapter = categoryAdapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CategoryListFragment"
    }
}