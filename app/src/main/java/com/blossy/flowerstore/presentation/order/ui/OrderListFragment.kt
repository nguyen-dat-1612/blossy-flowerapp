package com.blossy.flowerstore.presentation.order.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentOrderStatusBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.order.adapter.OrderHistoryAdapter
import com.blossy.flowerstore.presentation.order.viewmodel.OrderHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.wait

@AndroidEntryPoint
class OrderListFragment : Fragment() {

    private lateinit var binding: FragmentOrderStatusBinding
    private val viewModel: OrderHistoryViewModel by viewModels()
    private lateinit var adapter: OrderHistoryAdapter

    private var status: Int = 0

    companion object {
        private const val ARG_STATUS = "order_status"

        fun newInstance(status: Int): OrderListFragment {
            val fragment = OrderListFragment()
            val args = Bundle()
            args.putInt(ARG_STATUS, status)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = arguments?.getInt(ARG_STATUS) ?: 0
        adapter = OrderHistoryAdapter(onItemClicked = { item ->
                val action = OrderHistoryFragmentDirections.actionOrderHistoryFragmentToDetailOrderFragment(item.id)
                findNavController().navigate(action)
            },
            onCancelClicked = { item ->
                CancelOrderDialog(requireContext()) { reason ->
                    viewModel.cancelOrder(item.id, reason)
                }.show()
            },
            onConfirmClicked = { item ->
                ConfirmOrderReceivedDialog(requireContext()) {
                    viewModel.confirmOrder(item.id)
                }.show()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        binding.orderRecyclerView.adapter = adapter
        observe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (status) {
            0 -> loadOrders("pending")
            1 -> loadOrders("processing")
            2 -> loadOrders("shipped")
            3 -> loadOrders("delivered")
            4 -> loadOrders("cancelled")
        }
        Log.d("TAGTAGTAG on onViewCreated", "onViewCreated: $status")
    }

    private fun loadOrders(status: String) {
        viewModel.fetchOrderHistory(status, 1, 10, true, "desc")
    }
    private fun observe() {
        collectState(viewModel.orderHistory) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyText.visibility = View.GONE
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
        collectState(viewModel.cancelOrder) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Order Cancelled", Toast.LENGTH_SHORT).show()
                    viewModel.removeOrderFromList(it.data.id)
                    adapter.submitList((viewModel.orderHistory.value as? UiState.Success)?.data ?: emptyList())
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                else -> {}

            }
        }

        collectState(viewModel.confirmOrder) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Order Confirmed", Toast.LENGTH_SHORT).show()
                    viewModel.confirmOrderFromList(it.data.id)
                    adapter.submitList((viewModel.orderHistory.value as? UiState.Success)?.data ?: emptyList())
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }


    }

}