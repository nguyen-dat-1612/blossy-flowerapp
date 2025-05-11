package com.blossy.flowerstore.presentation.checkout.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentCheckOutBinding
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.domain.model.ShippingAddress
import com.blossy.flowerstore.presentation.checkout.adapter.OrderItemAdapter
import com.blossy.flowerstore.presentation.checkout.viewmodel.CheckOutViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.shippingAddress.ui.ShippingAddressFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckOutFragment : Fragment() {
    private lateinit var viewModel: CheckOutViewModel
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var _binding: FragmentCheckOutBinding? = null
    private val binding get() = _binding!!

    private var selectedPaymentMethod = "MoMo"
    private val REQUEST_CODE_MOMO = 1001
    private val REQUEST_CODE_VNPAY = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        viewModel.getCart()
        viewModel.getDefaultAddress()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckOutBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeStates()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAddressChangeFromShippingScreen()
    }

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter(mutableListOf())
        binding.recyclerViewProducts.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeStates() {
        collectState(viewModel.getCartUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    orderItemAdapter.submitList(state.data)
                    val subtotal = state.data.sumOf { it.product.price * it.quantity }
                    binding.subtotalValue.text = "đ$subtotal"
                    binding.shippingValue.text = "đ30,000"
                    val total = subtotal + 30000
                    binding.totalValue.text = "đ$total"
                    binding.bottomTotalValue.text = "đ$total"
                }
                is UiState.Error -> showError(state.message)
                else -> Unit
            }
        }

        collectState(viewModel.getDefaultAddressUiState) { state ->
            handleAddressState(state)
        }

        collectState(viewModel.createOrderUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    val paymentUrl = state.data.nextSteps?.paymentEndpoint
                    val orderId = state.data.data?.id ?: return@collectState
                    if (paymentUrl == "/api/payments/momo/create") {
                        viewModel.createMomoPayment(orderId)
                    } else {
                        viewModel.createVNPAYPayment(orderId)
                    }
                }
                is UiState.Error -> showError(state.message)
                else -> Unit
            }
        }

        collectState(viewModel.createMomoPaymentUiState) { state ->
            when (state) {
                is UiState.Success -> openApp(state.data, "MoMo")
                is UiState.Error -> showError(state.message)
                else -> Unit
            }
        }

        collectState(viewModel.createVNPAYPaymentUiState) { state ->
            when (state) {
                is UiState.Success -> openApp(state.data, "VnPay")
                is UiState.Error -> showError(state.message)
                else -> Unit
            }
        }
    }

    private fun handleAddressState(state: UiState<Address>) {
        when (state) {
            is UiState.Success -> {
                if (viewModel.isAddressManuallySelected() == false) {
                    binding.recipientName.text = state.data.name
                    binding.recipientPhone.text = state.data.phone
                    binding.recipientAddress.text = state.data.address
                    Log.d("Status default", viewModel.isAddressManuallySelected().toString())
                    Log.d(TAG, "Address: ${state.data}")
                }
            }
            is UiState.Error -> showError(state.message)
            else -> Unit
        }
    }

    private fun observeAddressChangeFromShippingScreen() {

        val selectedName = arguments?.getString("selectedName")
        val selectedPhone = arguments?.getString("selectedPhone")
        val selectedAddress = arguments?.getString("selectedAddress")

        if (!selectedName.isNullOrEmpty() && !selectedPhone.isNullOrEmpty() && !selectedAddress.isNullOrEmpty()) {
            viewModel.setAddressManuallySelected(true)
            binding.recipientName.text = selectedName
            binding.recipientPhone.text = selectedPhone
            binding.recipientAddress.text = selectedAddress
        }
    }

    private fun setupClickListeners() {
        binding.momoPaymentOption.setOnClickListener {
            selectedPaymentMethod = "MoMo"
            binding.momoRadioButton.isChecked = true
            binding.vnpayRadioButton.isChecked = false
        }

        binding.vnpayPaymentOption.setOnClickListener {
            selectedPaymentMethod = "VNPAY"
            binding.vnpayRadioButton.isChecked = true
            binding.momoRadioButton.isChecked = false
        }

        binding.orderBtn.setOnClickListener {
            val cartState = viewModel.getCartUiState.value
            if (cartState !is UiState.Success) {
                showError("Giỏ hàng chưa sẵn sàng")
                return@setOnClickListener
            }

            if (!isAddressReady()) {
                showError("Vui lòng nhập địa chỉ giao hàng")
                return@setOnClickListener
            }

            val orderItems = cartState.data.map {
                com.blossy.flowerstore.data.remote.dto.OrderItemRequest(
                    productId = it.product.id,
                    quantity = it.quantity
                )
            }

            val shipping = ShippingAddress(
                name = binding.recipientName.text.toString(),
                phone = binding.recipientPhone.text.toString(),
                address = binding.recipientAddress.text.toString(),
                city = "Hồ Chí Minh",
                postalCode = "100000",
                country = "Việt Nam"
            )

            viewModel.createOrder(
                com.blossy.flowerstore.data.remote.dto.CreateOrderRequest(
                    orderItems = orderItems,
                    shippingAddress = shipping,
                    paymentMethod = selectedPaymentMethod
                )
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_checkOutFragment_to_mainFragment)
            findNavController().popBackStack()
        }

        binding.editAddressBtn.setOnClickListener {
            val action = CheckOutFragmentDirections.actionCheckOutFragmentToShippingAddressFragment(
                fromCheckout = true
            )
            findNavController().navigate(action)
        }
    }

    private fun openApp(paymentUrl: String, paymentMethod: String) {
        try {
            startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)), REQUEST_CODE_MOMO)
        } catch (e: ActivityNotFoundException) {
            val appName = if (paymentMethod == "MoMo") "MoMo" else "VNPay"
            showError("Vui lòng cài đặt ứng dụng $appName")
        }
    }

    private fun isAddressReady(): Boolean {
        return binding.recipientName.text.isNotBlank() &&
                binding.recipientPhone.text.isNotBlank() &&
                binding.recipientAddress.text.isNotBlank()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MOMO || requestCode == REQUEST_CODE_VNPAY) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO: Gọi API kiểm tra trạng thái đơn hàng nếu cần
            } else {
                showError("Thanh toán bị hủy hoặc thất bại")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CheckOutFragment"
    }
}