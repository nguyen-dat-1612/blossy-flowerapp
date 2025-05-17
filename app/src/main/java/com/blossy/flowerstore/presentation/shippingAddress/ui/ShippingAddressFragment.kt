package com.blossy.flowerstore.presentation.shippingAddress.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentShippingAddressBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.shippingAddress.adapter.AddressAdapter
import com.blossy.flowerstore.presentation.shippingAddress.viewmodel.ShippingAddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAddressFragment : Fragment() {

    private lateinit var binding: FragmentShippingAddressBinding
    private lateinit var viewModel: ShippingAddressViewModel
    private val args: ShippingAddressFragmentArgs by navArgs()
    private lateinit var addressAdapter: AddressAdapter

    private  var fromCheckout: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ShippingAddressViewModel::class.java)
        viewModel.getUserAddresses()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        viewModel.getUserAddresses()
        fromCheckout = args.fromCheckout

        addressAdapter = AddressAdapter(fromCheckout,
            onAddressSelected = { position ->
                if (fromCheckout) {
                    binding.selectAddressButton.isEnabled = true
                }
            },
            onEditClick = { address ->
                val action = ShippingAddressFragmentDirections
                    .actionShippingAddressFragmentToAddEditAddressFragment(
                        action = "edit",
                        address = address
                    )
                findNavController().navigate(action)
            }
        )
        binding.apply {
            addressRecyclerView.layoutManager = LinearLayoutManager(context)
            addressRecyclerView.adapter = addressAdapter
        }
        binding.addressRecyclerView.adapter = addressAdapter


        binding.selectAddressButton.visibility = if (fromCheckout) View.VISIBLE else View.GONE
        binding.selectAddressButton.isEnabled = false


        observeData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() = with(binding) {
        btnBack.setOnClickListener {
            if (fromCheckout) {
                findNavController().navigate(R.id.action_shippingAddressFragment_to_checkOutFragment)
                findNavController().popBackStack()
            } else {
                findNavController().navigate(R.id.action_shippingAddressFragment_to_mainFragment)
                findNavController().popBackStack()
            }
        }

        selectAddressButton.setOnClickListener {
            val selectedAddress = addressAdapter.getSelectedAddress()
            if (selectedAddress?.id != null) {
                val result = Bundle().apply {
                    putString("selectedAddressId", selectedAddress.id)
                }
                setFragmentResult("addressRequestKey", result)
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Please select an address", Toast.LENGTH_SHORT).show()
            }
        }

        addAddressBtn.setOnClickListener {
            val action = ShippingAddressFragmentDirections
                .actionShippingAddressFragmentToAddEditAddressFragment(
                    action = "add"
                )
            findNavController().navigate(action)
        }

    }

    private fun observeData() {
        collectState(viewModel.addresses) { state ->
            when (state) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    val addresses = state.data
                    addressAdapter.submitList(addresses)

                }

                is UiState.Error -> {

                    val errorMessage = state.message
                    // Show error message
                }

                else -> {}
            }
        }
    }
    companion object {
        const val TAG = "ShippingAddressFragment"
    }
}