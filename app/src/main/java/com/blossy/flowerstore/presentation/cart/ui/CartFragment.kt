package com.blossy.flowerstore.presentation.cart.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    private lateinit var priceDetailAdapter: PriceDetailAdapter
    private var isPriceDetailsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        cartViewModel.getCart()
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

        // Khởi tạo adapter rỗng ban đầu
        cartAdapter = CartAdapter(mutableListOf(),
            onDeleteClicked = { cartItem ->
                cartViewModel.deleteCartItem(cartItem.product.id)
            },
            onQuantityChanged = { cartItem, newQuantity ->
                cartViewModel.updateCartItemQuantity(cartItem.product.id, newQuantity)

            })

        binding.recyclerViewCartItem.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCartItem.adapter = cartAdapter


        priceDetailAdapter = PriceDetailAdapter(emptyList())
        binding.recyclerViewPriceDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPriceDetail.adapter = priceDetailAdapter
        observeCart()
        setOnClickListeners()
    }

    private fun observeCart() {
        collectState(cartViewModel.getCartUiState) { state ->
            when (state) {
                is UiState.Loading -> {
                    // binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {

                    Log.d(TAG, "Data received: ${state.data.size} items")
                    cartAdapter.submitList(state.data)

                    val priceDetails = state.data.map {
                        PriceDetailItem(it.product.name, it.quantity, it.quantity * it.product.price)
                    }
                    priceDetailAdapter.updateList(priceDetails)

                    val subTotalAmount = calculateSubtotal(state.data)
                    val totalAmount = calculateTotal(subTotalAmount)

                    val vnLocale = Locale("vi", "VN")
                    val currencyFormat = NumberFormat.getCurrencyInstance(vnLocale)
                    currencyFormat.maximumFractionDigits = 0 // Không có .00
                    currencyFormat.currency = Currency.getInstance("VND")

                    val formattedSubTotal = currencyFormat.format(subTotalAmount)
                    val formattedTotal = currencyFormat.format(totalAmount)

                    binding.subTotal.text = formattedSubTotal
                    binding.total.text = formattedTotal

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
                    cartViewModel.getCart()
                }
                is UiState.Error -> {
                    Log.e(TAG, "observeCart: ${state.message}")
                    Toast.makeText(requireContext(), "Cập nhật thất bại: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        collectState(cartViewModel.removeCartUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    cartViewModel.getCart()
                }
                is UiState.Error -> {
                    Log.e(TAG, "observeCart: ${state.message}")
                    Toast.makeText(requireContext(), "Xóa thất bại: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setOnClickListeners() {
        binding.checkOutButton.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_main).navigate(
                R.id.action_main_to_checkOutFragment
            )
        }
        // Toggle Price Details
        binding.togglePriceDetails.setOnClickListener {
            isPriceDetailsVisible = !isPriceDetailsVisible
            binding.priceDetailsContainer.visibility = if (isPriceDetailsVisible) View.VISIBLE else View.GONE
            binding.togglePriceDetails.text = if (isPriceDetailsVisible) "Hide Price Details" else "View Price Details"
        }
//        binding.btnBack.setOnClickListener {
//            findNavController().navigate(R.id.homeFragment)
//        }
    }


    private fun calculateTotal(subTotalAmount: Double): Double {
        return subTotalAmount + 0.0f
    }
    private fun calculateSubtotal(items: List<CartItem>): Double {
        return items.sumOf { it.product.price * it.quantity }
    }
    companion object {
        private const val TAG = "CartFragment"
    }
}