package com.blossy.flowerstore.presentation.checkout.ui

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.data.remote.dto.CartOrderDTO
import com.blossy.flowerstore.data.remote.dto.CreateOrderDTO
import com.blossy.flowerstore.data.remote.dto.OrderItemRequest
import com.blossy.flowerstore.data.remote.utils.OrderResponseWrapper
import com.blossy.flowerstore.databinding.FragmentCheckOutBinding
import com.blossy.flowerstore.domain.model.ShippingAddressModel
import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.domain.model.request.CartOrderModel
import com.blossy.flowerstore.domain.model.request.CreateOrderModel
import com.blossy.flowerstore.presentation.checkout.adapter.OrderItemAdapter
import com.blossy.flowerstore.presentation.checkout.viewmodel.CheckOutViewModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.setOnSingleClickListener
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckOutFragment : Fragment() {

    private val viewModel: CheckOutViewModel by viewModels()
    private val orderItemAdapter by lazy { OrderItemAdapter(mutableListOf()) }
    private var _binding: FragmentCheckOutBinding? = null
    private val binding get() = _binding!!
    private var noAddressDialog: Dialog? = null
    private var selectedPaymentMethod = PAYMENT_METHOD_MOMO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCheckOut()
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

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.checkOutFragment)
        val resultLiveData = navBackStackEntry.savedStateHandle.getLiveData<Bundle>("payment_result")

        resultLiveData.observe(viewLifecycleOwner) { bundle ->
            val status = bundle.getString("paymentStatus")
            val orderId = bundle.getString("orderId")

            val bundle = Bundle().apply {
                putString("paymentStatus", status)
                putString("orderId", orderId)
            }

            findNavController().navigate(R.id.action_checkOutFragment_to_paymentResultFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewProducts.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeStates() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    Log.d(TAG, "observeStates: $state")
                    progressOverlay.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.error.isNotBlank()) {
                        showError(state.error)
                    }

                    if (!state.isLoading) {
                        if (state.selectedAddress != null) {
                            updateAddressUI(state.selectedAddress.name, state.selectedAddress.phone, state.selectedAddress.address)
                        } else {
                            binding.recipientName.text = ""
                            binding.recipientPhone.text = ""
                            binding.recipientAddress.text = ""
                            if (!viewModel.getCheck()) {
                                showNoAddressDialog()
                            }
                        }
                    }
                    if (state.cartState != null) {
                        updateCartUI(state.cartState)
                    }

                    if (state.paymentUrl != null && !viewModel.getPayment()) {
                        handlePayment(state.paymentUrl, selectedPaymentMethod)
                        viewModel.setPayment(true)
                    }

                }
            }
        }
    }

    private fun updateCartUI(cartItems: List<CartItemModel>) {
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
                viewModel.setCheck(true)
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
            val id = bundle.getString("selectedAddressId")
            if (id != null) {
                viewModel.getAddressById(id)
            }
        }

        setFragmentResultListener("addAddressRequestKey") { _, bundle ->
            val selectedAddressId = bundle.getString("newAddressId")
            if (selectedAddressId != null) {
                Log.d(TAG, "observeFragmentResults: $selectedAddressId")
                viewModel.getAddressById(selectedAddressId)
            } else {
                viewModel.setCheck(false)
            }
        }

        setFragmentResultListener("checkAddressRequestKey") { _, bundle ->
            val checkAddress = bundle.getBoolean("checkAddress")
            if (checkAddress) {
                Log.d(TAG, "observeFragmentResults: $checkAddress")
                viewModel.getAddressById(viewModel.uiState.value.selectedAddress?.id!!)
                viewModel
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        momoPaymentOption.setOnClickListener {
            selectPaymentMethod(PAYMENT_METHOD_MOMO)
        }

        vnpayPaymentOption.setOnClickListener {
            selectPaymentMethod(PAYMENT_METHOD_VNPAY)
        }

        orderBtn.setOnSingleClickListener {
            requireContext().toast("Click order")
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

    private fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method
        binding.momoRadioButton.isChecked = method == PAYMENT_METHOD_MOMO
        binding.vnpayRadioButton.isChecked = method == PAYMENT_METHOD_VNPAY
    }

    private fun processOrder() {
        val selectedAddress = viewModel.uiState.value.selectedAddress
        val shippingAddress = selectedAddress?.let {
            ShippingAddressModel(it.name, it.phone, it.address)
        } ?: run {
            showError(ErrorMessages.SHIPPING_ADDRESS_REQUIRED)
            return
        }

        if (selectedPaymentMethod.isBlank()) {
            showError(ErrorMessages.PAYMENT_METHOD_REQUIRED)
            return
        }

        val cartItems = viewModel.uiState.value.cartState?.map {
            CartOrderModel(productId = it.product.id, quantity = it.quantity)
        }
        if (viewModel.uiState.value.cartState != null) {

            viewModel.createOrder(
                CreateOrderModel(
                    cartItems = cartItems!!,
                    shippingAddress = shippingAddress,
                    paymentMethod = selectedPaymentMethod
                )
            )
        }

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
        const val SHIPPING_ADDRESS_REQUIRED = "Shipping address required"
        const val PAYMENT_METHOD_REQUIRED = "Please select a payment method"
        const val ORDER_CREATION_FAILED = "Unable to create order"
        const val PAYMENT_CREATION_FAILED = "Unable to create payment"
        const val PAYMENT_FAILED = "Payment failed"
        const val APP_NOT_INSTALLED = "%s not installed"
    }
}