package com.blossy.flowerstore.presentation.checkout.ui

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.data.remote.dto.CreateOrderRequest
import com.blossy.flowerstore.data.remote.dto.OrderItemRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.databinding.FragmentCheckOutBinding
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.model.ShippingAddress
import com.blossy.flowerstore.presentation.checkout.adapter.OrderItemAdapter
import com.blossy.flowerstore.presentation.checkout.viewmodel.CheckOutViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.utils.CurrencyFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckOutFragment : Fragment() {

    private val viewModel: CheckOutViewModel by viewModels()
    private val orderItemAdapter by lazy { OrderItemAdapter(mutableListOf()) }
    private var _binding: FragmentCheckOutBinding? = null
    private val binding get() = _binding!!
    private var noAddressDialog: Dialog? = null
    private var selectedPaymentMethod = PAYMENT_METHOD_MOMO
    private var tempAddressId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCart()
        viewModel.getDefaultAddress()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckOutBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeFragmentResults()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeStates()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewProducts.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeStates() {
        collectState(viewModel.getCartUiState) { state ->
            when (state) {
                is UiState.Success -> updateCartUI(state.data)
                is UiState.Error -> showError(ErrorMessages.CART_NOT_READY)
                else -> Unit
            }
        }

        collectState(viewModel.getAddressUiState) { state ->
            handleAddressState(state)
        }

        collectState(viewModel.createOrderUiState) { state ->
            when (state) {
                is UiState.Success -> handleOrderCreationSuccess(state.data)
                is UiState.Error -> showError(ErrorMessages.ORDER_CREATION_FAILED)
                else -> Unit
            }
        }

        collectState(viewModel.createMomoPaymentUiState) { state ->
            when (state) {
                is UiState.Success -> handlePayment(state.data, PAYMENT_METHOD_MOMO)
                is UiState.Error -> showError(ErrorMessages.PAYMENT_CREATION_FAILED)
                else -> Unit
            }
        }

        collectState(viewModel.createVNPAYPaymentUiState) { state ->
            when (state) {
                is UiState.Success -> handlePayment(state.data, PAYMENT_METHOD_VNPAY)
                is UiState.Error -> showError(ErrorMessages.PAYMENT_CREATION_FAILED)
                else -> Unit
            }
        }
    }

    private fun updateCartUI(cartItems: List<CartItem>) {
        orderItemAdapter.submitList(cartItems)
        val subtotal = cartItems.sumOf { it.product.price * it.quantity }
        val shippingFee = SHIPPING_FEE
        val total = subtotal + shippingFee

        with(binding) {
            subtotalValue.text = CurrencyFormatter.formatVND(subtotal)
            shippingValue.text = CurrencyFormatter.formatVND(shippingFee)
            totalValue.text = CurrencyFormatter.formatVND(total)
            bottomTotalValue.text = CurrencyFormatter.formatVND(total)
        }
    }

    private fun handleOrderCreationSuccess(orderResponse: OrderResponseWrapper) {
        val orderId = orderResponse.data?.id ?: return
        val paymentEndpoint = orderResponse.nextSteps?.paymentEndpoint ?: return

        when (paymentEndpoint) {
            MOMO_PAYMENT_ENDPOINT -> viewModel.createMomoPayment(orderId)
            else -> viewModel.createVNPAYPayment(orderId)
        }
    }

    private fun handleAddressState(state: AddressUiState) {

        when (state) {
            is AddressUiState.Success -> {
                updateAddressUI(state.data.name, state.data.phone, state.data.address)
                noAddressDialog?.dismiss()
            }
            is AddressUiState.NoAddress -> showNoAddressDialog()
            is AddressUiState.Error -> {
                showNoAddressDialog()
            }
            else -> Unit
        }
    }

    private fun updateAddressUI(name: String, phone: String, address: String)  {
        with(binding) {
            recipientName.text = name
            recipientPhone.text = phone
            recipientAddress.text = address
        }
    }

    private fun showNoAddressDialog() {
        if (noAddressDialog?.isShowing == true) return

        noAddressDialog = NoAddressDiaLog(
            requireContext(),
            onCancel = {
                noAddressDialog?.dismiss()
                findNavController().popBackStack()
            },
            onConfirm = {
                noAddressDialog?.dismiss()
                navigateToAddAddress()
            }
        ).apply { show() }
    }

    private fun navigateToAddAddress() {
        val action = CheckOutFragmentDirections
            .actionCheckOutFragmentToAddEditAddressFragment(action = "add", fromCheckout = true)
        findNavController().navigate(action)
    }

    private fun observeFragmentResults() {

        setFragmentResultListener("addressRequestKey") { _, bundle ->
            tempAddressId = bundle.getString("selectedAddressId")
        }

        setFragmentResultListener("addAddressRequestKey") { _, bundle ->
            tempAddressId = bundle.getString("newAddressId")
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            momoPaymentOption.setOnClickListener {
                selectPaymentMethod(PAYMENT_METHOD_MOMO)
            }

            vnpayPaymentOption.setOnClickListener {
                selectPaymentMethod(PAYMENT_METHOD_VNPAY)
            }

            orderBtn.setOnClickListener {
                processOrder()
            }

            btnBack.setOnClickListener {
                findNavController().navigate(R.id.action_checkOutFragment_to_mainFragment)
                findNavController().popBackStack()
            }

            editAddressBtn.setOnClickListener {
                val action = CheckOutFragmentDirections.actionCheckOutFragmentToShippingAddressFragment(fromCheckout = true)
                findNavController().navigate(action)
            }
        }
    }

    private fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method
        binding.momoRadioButton.isChecked = method == PAYMENT_METHOD_MOMO
        binding.vnpayRadioButton.isChecked = method == PAYMENT_METHOD_VNPAY
    }

    private fun processOrder() {
        val cartState = viewModel.getCartUiState.value
        if (cartState !is UiState.Success || cartState.data.isEmpty()) {
            showError(ErrorMessages.EMPTY_CART)
            return
        }

        val shippingAddress = getShippingAddress()
        if (shippingAddress == null) {
            showError(ErrorMessages.SHIPPING_ADDRESS_REQUIRED)
            return
        }

        if (selectedPaymentMethod.isBlank()) {
            showError(ErrorMessages.PAYMENT_METHOD_REQUIRED)
            return
        }

        val orderItems = cartState.data.map {
            OrderItemRequest(productId = it.product.id, quantity = it.quantity)
        }

        viewModel.createOrder(
            CreateOrderRequest(
                orderItems = orderItems,
                shippingAddress = shippingAddress,
                paymentMethod = selectedPaymentMethod
            )
        )
    }

    private fun getShippingAddress(): ShippingAddress? {
        val name = binding.recipientName.text.toString()
        val phone = binding.recipientPhone.text.toString()
        val address = binding.recipientAddress.text.toString()
        return if (name.isBlank() || phone.isBlank() || address.isBlank()) null
        else ShippingAddress(name, phone, address, "Hồ Chí Minh", "100000", "Việt Nam")
    }

    private fun handlePayment(paymentUrl: String, paymentMethod: String) {
        try {
            val requestCode = if (paymentMethod == PAYMENT_METHOD_MOMO) REQUEST_CODE_MOMO else REQUEST_CODE_VNPAY
            startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)), requestCode)
        } catch (e: ActivityNotFoundException) {
            val appName = if (paymentMethod == PAYMENT_METHOD_MOMO) "MoMo" else "VNPay"
            showError(ErrorMessages.APP_NOT_INSTALLED.format(appName))
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MOMO || requestCode == REQUEST_CODE_VNPAY) {
            if (resultCode != Activity.RESULT_OK) {
                showError(ErrorMessages.PAYMENT_FAILED)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CheckOutFragment"
        private const val REQUEST_CODE_MOMO = 1001
        private const val REQUEST_CODE_VNPAY = 1002
        private const val PAYMENT_METHOD_MOMO = "MoMo"
        private const val PAYMENT_METHOD_VNPAY = "VNPAY"
        private const val MOMO_PAYMENT_ENDPOINT = "/api/payments/momo/create"
        private const val SHIPPING_FEE = 30000.0
    }

    private object ErrorMessages {
        const val CART_NOT_READY = "Cart not ready"
        const val EMPTY_CART = "Cart is empty"
        const val SHIPPING_ADDRESS_REQUIRED = "Shipping address required"
        const val PAYMENT_METHOD_REQUIRED = "Please select a payment method"
        const val ORDER_CREATION_FAILED = "Unable to create order"
        const val PAYMENT_CREATION_FAILED = "Unable to create payment"
        const val PAYMENT_FAILED = "Payment failed"
        const val APP_NOT_INSTALLED = "%s not installed"
    }
}