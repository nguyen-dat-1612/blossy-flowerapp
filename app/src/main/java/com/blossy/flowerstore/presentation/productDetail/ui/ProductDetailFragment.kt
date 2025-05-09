package com.blossy.flowerstore.presentation.productDetail.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blossy.flowerstore.databinding.FragmentProductDetailBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.productDetail.adapter.ProductImagePagerAdapter
import com.blossy.flowerstore.presentation.productDetail.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blossy.flowerstore.R
import kotlinx.coroutines.selects.select
import okhttp3.internal.wait

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private val args: ProductDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewmodel: ProductDetailViewModel
    private lateinit var productId: String
    private lateinit var selectedTab: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this)[ProductDetailViewModel::class.java]
        productId = args.productId
        selectedTab = args.selectedTab
        if (productId != null) {
            Log.d("ProductDetailFragment", "Product ID: $productId")
        } else {
            Log.d("ProductDetailFragment", "Product ID is null")
        }
        viewmodel.getProductDetail(productId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(layoutInflater)
        observe()
        onClickListener()
        return binding.root
    }

    private fun observe() {
        collectState(viewmodel.productDetailUiState) { state ->
            when (state) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    binding.product = state.data
                    Log.d("ProductDetailFragment", "Success: ${state.data}")
                    val imageAdapter = ProductImagePagerAdapter(state.data.images)

                    binding.apply {
                        viewPager.adapter = imageAdapter
                        dotsIndicator.setViewPager2(binding.viewPager)
                        nameProduct.text = state.data.name
                        ratingBar.rating = state.data.rating.toFloat()
                        priceProduct.text = "₫${state.data.price}"
                        descriptionProduct.text = state.data.description
                        stockStatus.text =
                            if (state.data.stock > 0) {
                                if (state.data.stock <= 5) {
                                    stockStatus.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.stock_low
                                        )
                                    )  // Màu cam
                                    "Low Stock"
                                } else {
                                    stockStatus.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.green_dark
                                        )
                                    )  // Màu xanh lá
                                    "In Stock"
                                }
                            } else {
                                stockStatus.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.stock_out
                                    )
                                )  // Màu đỏ
                                "Out of Stock"
                            }
                        addToCartBtn.isEnabled = state.data.stock > 0
                        addToCartBtn.text =
                            if (state.data.stock > 0) "Add to Cart" else "Out of Stock"
                        quantityText.text = "1"
                        addToCartBtn.setOnClickListener {
                            val quantity = quantityText.text.toString().toIntOrNull() ?: 1
                            viewmodel.addToCart(productId = productId, quantity = quantity)
                        }
                        reviews.text = "${state.data.rating} (${state.data.numReviews} reviews)"
                    }

                }

                is UiState.Error -> {
                    Log.e("ProductDetailFragment", "Error: ${state.message}")
                    // Handle error
                }

                is UiState.Idle -> {

                }

                else -> {

                }
            }
        }

        collectState(viewmodel.addToCartUiState) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Show loading indicator
                }

                is UiState.Success -> {
                    // Handle success
                    Toast.makeText(requireContext(), "Product added to cart", Toast.LENGTH_LONG)
                        .show()
                }

                is UiState.Error -> {
                    Log.e("ProductDetailFragment", "Error: ${state.message}")
                    // Handle error
                }

                else -> {

                }
            }
        }

        collectState(viewmodel.checkFavoriteUiState) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Show loading indicator
                }
                is UiState.Success -> {
                    binding.btnFavorite.setImageResource(
                        if (state.data) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                UiState.Idle -> {

                }
            }
        }
    }


    private fun onClickListener() {
        binding.btnBack.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_main)
            val bundle = bundleOf("selectedTab" to selectedTab)
            if (selectedTab == "home") {
                navController.navigate(R.id.action_productDetailFragment_to_mainFragment)
            } else if (selectedTab == "search") {
                navController.navigate(R.id.action_productDetailFragment_to_searchFragment, bundle)
            } else if (selectedTab == "cartProduct") {
                navController.navigate(R.id.action_productDetailFragment_to_categoryProductFragment, bundle)
            }
        }
        binding.addToCartBtn.setOnClickListener {
            val quantity = binding.quantityText.text.toString().toIntOrNull() ?: 1
            viewmodel.addToCart(productId = productId, quantity = quantity)
        }

        binding.minusQuantityButton.setOnClickListener {
            val currentQuantity = binding.quantityText.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                binding.quantityText.text = (currentQuantity - 1).toString()
            }
        }

        binding.addQuantityButton.setOnClickListener {
            val currentQuantity = binding.quantityText.text.toString().toIntOrNull() ?: 1
            binding.quantityText.text = (currentQuantity + 1).toString()
        }

        binding.btnFavorite.setOnClickListener {
            if (viewmodel.checkFavoriteUiState.value is UiState.Success) {
                val isFavorite = (viewmodel.checkFavoriteUiState.value as UiState.Success<Boolean>).data
                if (isFavorite) {
                    viewmodel.removeFavorite(productId)
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                    return@setOnClickListener
                } else {
                    viewmodel.addFavorite(productId)
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                    return@setOnClickListener
                }
            }
        }

    }

    companion object {

    }
}