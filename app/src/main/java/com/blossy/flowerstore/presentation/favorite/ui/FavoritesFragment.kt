package com.blossy.flowerstore.presentation.favorite.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentFavoritesBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.favorite.adapter.FavoritesAdapter
import com.blossy.flowerstore.presentation.favorite.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesViewModel: FavoritesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        favoritesViewModel.loadFavoriteProducts()
        favoritesAdapter = FavoritesAdapter(
            onFavoriteClicked = { product ->
                favoritesViewModel.isFavorite(product.id)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.recyclerViewFavorites.adapter = favoritesAdapter
        observe()
        return binding.root
    }

    fun observe() {
        collectState(favoritesViewModel.favoriteProducts) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Có thể show loading UI ở đây
                }
                is UiState.Success -> {
                    // Cập nhật dữ liệu cho adapter khi có dữ liệu thành công
                    favoritesAdapter.submitList(state.data)
                }
                is UiState.Error -> {
                    // Xử lý lỗi nếu có
                }
                else -> {
                    // Trường hợp khác (nếu có)
                }
            }
        }

        collectState(favoritesViewModel.isFavorite) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Có thể show loading UI ở đây
                }
                is UiState.Success -> {

                }
                is UiState.Error -> {
                    // Xử lý lỗi nếu có
                }
                else -> {}
            }
        }
    }
    companion object {
    }
}