package com.blossy.flowerstore.presentation.order.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentDetailOrderBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.order.adapter.OrderItemsAdapter
import com.blossy.flowerstore.presentation.order.viewmodel.DetailOrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailOrderFragment : Fragment() {

    private lateinit var binding: FragmentDetailOrderBinding
    private val viewModel: DetailOrderViewModel by viewModels()
    private lateinit var orderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getString("orderId") ?: ""
        orderId.let { viewModel.getOrderById(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailOrderBinding.inflate(inflater, container, false)
        observe()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }
    private fun observe() {
        collectState(viewModel.order) { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    val order = state.data
                    binding.apply {
                        recipientName.text = order.shippingAddress.name + " (+84) " + order.shippingAddress.phone
                        address.text = order.shippingAddress.address
                        subtotal.text = order.totalPrice.toString()
                        orderId.text = order.id
                        shippingFee.text = order.shippingPrice.toString()
                        totalPrice.text = order.totalPrice.toString()
                        orderItemsRecyclerView.adapter = OrderItemsAdapter(order.orderItems)
                        paymentMethod.text = order.paymentMethod
                        paymentTime.text = order.paymentResult?.updateTime
                        orderTime.text = order.createdAt
//                        shippingTime.text = order.shippingTime
//                        completionTime.text = order.
//                        if (order.isCompleted) {
//                            completionTime.text = order.completionTime
//                        } else {
//                            completionTime.visibility = View.GONE
//                        }

                        if (order.isPaid) {
                            paymentTime.text = order.paymentResult?.updateTime
                        } else {
                            paymentTime.visibility = View.GONE
                        }

//                        if (order.isShipped) {
//                            shippingTime.text = order.shippingTime
//                        } else {
//                            shippingTime.visibility = View.GONE
//                        }

//                        if (order.isDelivered) {
//                            completionTime.text = order.completionTime
//                        } else {
//                            completionTime.visibility = View.GONE
//                        }

                        if (order.status == "canceled") {
                            actionButtons.visibility = View.GONE
                        } else {
                            actionButtons.visibility = View.VISIBLE
                        }
                        if (order.status == "pending" || order.status == "processing") {
                            cancelOrderButton.visibility = View.VISIBLE
                            cancelOrderButton.setOnClickListener {
//                                viewModel.cancelOrder(order.id)
                            }
                        }
                        if (order.status == "shipped") {
                            trackButton.visibility = View.VISIBLE
                        }

                        if (order.status == "delivered") {
                            trackButton.visibility = View.VISIBLE
                        }

                    }
                }
                is UiState.Error -> {
//                    binding.progressBar.visibility = View.GONE
//                    binding.emptyText.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    fun setOnClickListeners() {
        binding.btnBack.setOnClickListener {
//            findNavController().navigate(R.id.action_detailOrderFragment_to_orderHistoryFragment)
            findNavController().popBackStack()
        }
        binding.copyButton.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.orderId.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

    }
}