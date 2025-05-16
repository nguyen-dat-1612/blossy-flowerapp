package com.blossy.flowerstore.presentation.cart.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCartBinding
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.model.PriceDetailItem
import com.blossy.flowerstore.presentation.cart.adapter.CartAdapter
import com.blossy.flowerstore.presentation.cart.adapter.PriceDetailAdapter
import com.blossy.flowerstore.presentation.cart.viewmodel.CartViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

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
        collectState(cartViewModel.getCartUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    binding.progressBar.root.visibility = View.GONE
                    if (state.data.isEmpty()) binding.emptyState.root.visibility = View.VISIBLE
                    else binding.emptyState.root.visibility = View.GONE
                    cartAdapter.submitList(state.data)
                    updatePriceSummary(state.data)
                }

                is UiState.Error -> {
                    Log.e(TAG, "observeCart: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.w(TAG, "Received empty or null state")
                }
            }
        }
        collectState(cartViewModel.updateCartUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    cartAdapter.updateItemQuantity(cartViewModel.lastUpdatedProductId, cartViewModel.lastUpdatedQuantity)
                    updatePriceSummary(cartAdapter.currentList())
                    showToast("Item updated successfully")
                }
                is UiState.Error -> {
                    showError(state.message)
                    showToast("Update failed: ${state.message}")
                }
                else -> {}
            }
        }

        collectState(cartViewModel.removeCartUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    cartAdapter.removeItemByProductId(cartViewModel.lastRemovedProductId)
                    updatePriceSummary(cartAdapter.currentList())
                    showToast("Item removed successfully")
                }
                is UiState.Error -> {
                    showError(state.message)
                    showToast("Delete failed: ${state.message}")
                }
                else -> {}
            }
        }
    }

    private fun setOnClickListeners() = with(binding){
        checkOutButton.setOnClickListener { findNavController().navigate(R.id.action_main_to_checkOutFragment) }

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

    private fun updatePriceSummary(items: List<CartItem>) {
        val priceDetails = items.map {
            PriceDetailItem(it.product.name, it.quantity, it.quantity * it.product.price)
        }
        priceDetailAdapter.updateList(priceDetails)

        val subTotalAmount = calculateTotal(items)
        val totalAmount = subTotalAmount

        binding.subTotal.text = CurrencyFormatter.formatVND(subTotalAmount)
        binding.total.text = CurrencyFormatter.formatVND(totalAmount)
    }

    private fun findNavController() = requireActivity().findNavController(R.id.nav_host_main)

    private fun calculateTotal(items: List<CartItem>): Double {
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