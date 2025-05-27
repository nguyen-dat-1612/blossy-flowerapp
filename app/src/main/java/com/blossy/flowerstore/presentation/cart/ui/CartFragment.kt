package com.blossy.flowerstore.presentation.cart.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCartBinding
import com.blossy.flowerstore.domain.model.PriceDetailItemModel
import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.presentation.cart.adapter.CartAdapter
import com.blossy.flowerstore.presentation.cart.adapter.PriceDetailAdapter
import com.blossy.flowerstore.presentation.cart.viewmodel.CartViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var priceDetailAdapter: PriceDetailAdapter
    private var isPriceDetailsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel.getCart()
        setUpAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeCart()
        setOnClickListeners()
    }

    private fun setUpAdapter() {
        cartAdapter = CartAdapter(
            onDeleteClicked = { cartItem ->
                cartViewModel.deleteCartItem(cartItem.product.id)
            },
            onQuantityChanged = { cartItem, newQuantity ->
                cartViewModel.updateCartItemQuantity(cartItem.product.id, newQuantity)
            }
        )
        priceDetailAdapter = PriceDetailAdapter(emptyList())
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewCartItem.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        recyclerViewPriceDetail.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = priceDetailAdapter
        }
    }

    private fun observeCart() {
        collectState(cartViewModel.cartUiState) { state ->
            binding.progressBar.root.visibility = if (state.isLoadingCart) View.VISIBLE else View.GONE

            if (state.cartItems.isEmpty()) {
                binding.emptyState.root.visibility = View.VISIBLE
            } else {
                binding.emptyState.root.visibility = View.GONE
                cartAdapter.submitList(state.cartItems)
                updatePriceSummary(state.cartItems)
            }

            when (val updateState = state.updateCartState) {
                is UiState.Success -> {
                    showToast("Item updated successfully")
                }

                is UiState.Error -> {
                    showError(updateState.message)
                    showToast("Update failed: ${updateState.message}")
                }

                else -> {}
            }

            when (val removeState = state.removeCartState) {
                is UiState.Success -> {
                    showToast("Item removed successfully")
                }

                is UiState.Error -> {
                    showError(removeState.message)
                    showToast("Delete failed: ${removeState.message}")
                }

                else -> {}
            }

            state.errorMessage?.let {
                showToast("Error: $it")
                Log.e(TAG, "CartUiState error: $it")
            }
        }
    }

    private fun setOnClickListeners() = with(binding){
        checkOutButton.setOnClickListener {
            if (cartAdapter.currentList.isEmpty()) {
                showToast("Your cart is empty")
                return@setOnClickListener
            } else {
                findNavController().navigate(R.id.action_main_to_checkOutFragment)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            cartViewModel.getCart()
            swipeRefreshLayout.isRefreshing = false
        }

        togglePriceDetails.setOnClickListener {
            isPriceDetailsVisible = !isPriceDetailsVisible
            priceDetailsContainer.visibility = if (isPriceDetailsVisible) View.VISIBLE else View.GONE
            togglePriceDetails.text = if (isPriceDetailsVisible) "Hide Price Details" else "View Price Details"
        }
    }

    private fun updatePriceSummary(items: List<CartItemModel>) {
        val priceDetails = items.map {
            PriceDetailItemModel(it.product.name, it.quantity, it.quantity * it.product.price)
        }
        priceDetailAdapter.updateList(priceDetails)

        val subTotalAmount = calculateTotal(items)
        val totalAmount = subTotalAmount

        binding.subTotal.text = CurrencyFormatter.formatVND(subTotalAmount)
        binding.total.text = CurrencyFormatter.formatVND(totalAmount)
    }

    private fun findNavController() = requireActivity().findNavController(R.id.nav_host_main)

    private fun calculateTotal(items: List<CartItemModel>): Double {
        return items.sumOf { it.product.price * it.quantity }
    }

    private fun showToast(message: String) {
        requireContext().toast(message)
    }

    private fun showError(message: String) {
        Log.e(TAG, "observeCart: Error occurred: $message")
    }

    companion object {
        private const val TAG = "CartFragment"
    }
}