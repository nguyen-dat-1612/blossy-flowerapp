package com.blossy.flowerstore.presentation.favorite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentFavoritesBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.favorite.adapter.FavoritesAdapter
import com.blossy.flowerstore.presentation.favorite.viewmodel.FavoritesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()

    private val favoritesAdapter  by lazy {
        FavoritesAdapter { product ->
            viewModel.toggleFavorite(product.id)
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeStates()
        viewModel.loadFavoriteProducts()
    }

    private fun setupUI() = with(binding) {
        recyclerViewFavorites.apply {
            adapter = this@FavoritesFragment.favoritesAdapter
            setHasFixedSize(true)
        }

        swipeRefreshLayout.apply {
            setOnRefreshListener { viewModel.loadFavoriteProducts() }
            setColorSchemeResources(R.color.primary, R.color.pink_dark, R.color.grey_dark)
        }
    }

    private fun observeStates() = with(binding) {
        collectState(viewModel.favoriteProducts) { state ->
            when (state) {
                is UiState.Loading -> progressBar.root.isVisible = !swipeRefreshLayout.isRefreshing

                is UiState.Success -> {
                    swipeRefreshLayout.isRefreshing = false
                    progressBar.root.isVisible = false
                    val isEmpty = state.data.isNullOrEmpty()
                    recyclerViewFavorites.isVisible = !isEmpty
                    emptyState.root.isVisible = isEmpty
                    favoritesAdapter.submitList(state.data)
                }

                is UiState.Error -> {
                    recyclerViewFavorites.isVisible = false
                    swipeRefreshLayout.isRefreshing = false
                    progressBar.root.isVisible = false
                    emptyState.root.isVisible = true
                    showError(state.message)
                }

                else -> Unit
            }
        }

        collectState(viewModel.toggleFavoriteState) { state ->
            when (state) {
                is UiState.Success -> {
                    val msgRes = R.string.removed_from_favorites
                    showSnackbar(getString(msgRes))
                }

                is UiState.Error -> showError(state.message)
                else -> Unit
            }
        }
    }

    private fun showError(message: String?) {
        binding.emptyState.apply {
            root.isVisible = true
            textView.text = message ?: getString(R.string.error_loading_data)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewFavorites.adapter = null
        _binding = null
    }
}
