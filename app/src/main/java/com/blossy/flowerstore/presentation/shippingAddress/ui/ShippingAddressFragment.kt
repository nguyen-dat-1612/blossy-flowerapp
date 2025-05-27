package com.blossy.flowerstore.presentation.shippingAddress.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentShippingAddressBinding
import com.blossy.flowerstore.presentation.shippingAddress.adapter.AddressAdapter
import com.blossy.flowerstore.presentation.shippingAddress.viewmodel.ShippingAddressViewModel
import com.blossy.flowerstore.utils.collectState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShippingAddressFragment : Fragment() {

    private var _binding: FragmentShippingAddressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShippingAddressViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private val args: ShippingAddressFragmentArgs by navArgs()
    private lateinit var addressAdapter: AddressAdapter

    private var fromCheckout: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fromCheckout = args.fromCheckout

        setupRecyclerView()
        setupUI()
        setupListeners()
        observeViewModel()

        viewModel.getUserAddresses()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            fromCheckout = fromCheckout,
            onAddressSelected = { position ->
                if (fromCheckout) {
                    binding.selectAddressButton.isEnabled = true
                }
            },
            onEditClick = { address ->
                val action = ShippingAddressFragmentDirections
                    .actionShippingAddressFragmentToAddEditAddressFragment(
                        action = "edit",
                        address = address,
                        fromCheckout = fromCheckout
                    )
                findNavController().navigate(action)
            }
        )

        binding.addressRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addressAdapter
        }
    }

    private fun setupUI() {
        binding.selectAddressButton.apply {
            visibility = if (fromCheckout) View.VISIBLE else View.GONE
            isEnabled = false
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {

                if (fromCheckout) {
                    val result = Bundle().apply {
                        putBoolean("checkAddress", true)
                    }
                    setFragmentResult("checkAddressRequestKey", result)
                }
                findNavController().popBackStack()
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
                        action = "add",
                        fromCheckout = fromCheckout
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun observeViewModel() = with(binding){
        collectState(viewModel.shippingAddressUiState) { state ->
            progressOverlay.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            if (state.addresses.isNotEmpty()) {
//                emptyAddressText.visibility = View.GONE
                addressAdapter.submitList(state.addresses)
            } else {
//                emptyAddressText.visibility = View.VISIBLE
            }

            if (state.error.isNotEmpty()) {
                Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ShippingAddressFragment"
    }
}