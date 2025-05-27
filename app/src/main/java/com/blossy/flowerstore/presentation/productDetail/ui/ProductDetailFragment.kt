package com.blossy.flowerstore.presentation.productDetail.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blossy.flowerstore.databinding.FragmentProductDetailBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.productDetail.adapter.ProductImagePagerAdapter
import com.blossy.flowerstore.presentation.productDetail.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blossy.flowerstore.R
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.toast

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: ProductDetailViewModel by viewModels()

    private val args: ProductDetailFragmentArgs by navArgs()
    private lateinit var productId: String
    private lateinit var selectedTab: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = args.productId
        selectedTab = args.selectedTab
        viewmodel.getProductDetail(productId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailBinding.inflate(layoutInflater)
        observe()
        onClickListener()
        return binding.root
    }

    private fun observe() = with(binding) {
        collectState(viewmodel.productDetailUiState) { state ->

            progressOverlay.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            if (state.product != null) {
                bindProductDetail(state.product)
            }

            if (state.error.isNotBlank()) {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
            }

            binding.btnFavorite.setImageResource(
                if (state.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

            if (state.addToCartSuccess is UiState.Success) {
                requireContext().toast("Product added to cart")
            }
        }
    }


    private fun bindProductDetail(product: ProductModel) = with(binding){
        val imageAdapter = ProductImagePagerAdapter(product.images)
        viewPager.adapter = imageAdapter
        dotsIndicator.setViewPager2(binding.viewPager)
        nameProduct.text = product.name
        ratingBar.rating = product.rating.toFloat()
        priceProduct.text = CurrencyFormatter.formatVND(product.price)
        descriptionProduct.text = product.description
        addToCartBtn.isEnabled = product.stock > 0
        addToCartBtn.text =
            if (product.stock > 0) "Add to Cart" else "Out of Stock"
        quantityText.text = "1"
        reviewsText.text = "${product.rating} (${product.numReviews} reviews)"
    }


    private fun onClickListener() = with(binding) {
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        addToCartBtn.setOnClickListener {
            val quantity = quantityText.text.toString().toIntOrNull() ?: 1
            viewmodel.addToCart(productId = productId, quantity = quantity)
        }

        minusQuantityButton.setOnClickListener {
            val currentQuantity = quantityText.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                quantityText.text = (currentQuantity - 1).toString()
            }
        }

        addQuantityButton.setOnClickListener {
            val currentQuantity = quantityText.text.toString().toIntOrNull() ?: 1
            quantityText.text = (currentQuantity + 1).toString()
        }

        btnFavorite.setOnClickListener {
            if (viewmodel.productDetailUiState.value.isFavorite) {
                viewmodel.removeFavorite(productId)
                btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                return@setOnClickListener
            } else {
                viewmodel.addFavorite(productId)
                btnFavorite.setImageResource(R.drawable.ic_favorite)
                return@setOnClickListener
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProductDetailFragment"
    }
}