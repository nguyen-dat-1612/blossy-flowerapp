package com.blossy.flowerstore.presentation.shippingAddress.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blossy.flowerstore.R
import com.blossy.flowerstore.data.remote.dto.AddressDTO
import com.blossy.flowerstore.databinding.FragmentAddEditAddressBinding
import com.blossy.flowerstore.domain.model.AddressModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.shippingAddress.viewmodel.ShippingAddressViewModel
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditAddressFragment : Fragment() {

    private var _binding: FragmentAddEditAddressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShippingAddressViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private val args: AddEditAddressFragmentArgs by navArgs()
    private var fromCheckout: Boolean = false
    private var action: String = "add"
    private var addressId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = args.action
        fromCheckout = args.fromCheckout

        setupUI()
        setupListeners()
        observeViewModel()
    }

    private fun setupUI() {
        if (action == "edit" && args.address != null) {
            val address = args.address!!
            addressId = address.id

            binding.apply {
                titleText.text = getString(R.string.update_address)
                nameInput.setText(address.name)
                phoneInput.setText(address.phone)
                addressInput.setText(address.address)

                if (address.isDefault) {
                    isDefaultCheckbox.visibility = View.GONE
                    isDefaultCheckbox.isChecked = true
                }

                deleteButton.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                titleText.text = getString(R.string.add_new_address)
                deleteButton.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            saveButton.setOnClickListener {
                if (validateInputs()) {
                    saveAddress()
                }
            }

            deleteButton.setOnClickListener {
                addressId?.let { id ->
                    viewModel.deleteAddress(id)
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.nameInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val addressText = binding.addressInput.text.toString().trim()

        var isValid = true

        if (name.isEmpty()) {
            binding.nameInput.error = "Name cannot be empty"
            isValid = false
        } else {
            binding.nameInput.error = null
        }

        if (phone.isEmpty()) {
            binding.phoneInput.error = "Phone number cannot be empty"
            isValid = false
        } else {
            binding.phoneInput.error = null
        }

        if (addressText.isEmpty()) {
            binding.addressInput.error = "Address cannot be empty"
            isValid = false
        } else {
            binding.addressInput.error = null
        }

        return isValid
    }

    private fun saveAddress() {
        val name = binding.nameInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val addressText = binding.addressInput.text.toString().trim()
        val isDefault = binding.isDefaultCheckbox.isChecked

        val addressResponse = AddressModel(addressId, name, phone, addressText, isDefault)

        if (action == "add") {
            viewModel.addAddress(addressResponse)
        } else {
            viewModel.updateAddress(addressResponse)
        }
    }

    private fun observeViewModel() = with(binding){
        collectState(viewModel.shippingAddressUiState) { state ->

            when (state.addAddress) {
                is UiState.Loading -> {progressBar.root.visibility = View.VISIBLE}
                is UiState.Success -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        handleAddressSuccess(state.addAddress.data.id ?: "")
                        viewModel.resetAddAddressState()
                    }, 400)
                }
                is UiState.Error -> {
                    binding.progressBar.root.visibility = View.GONE
                    requireContext().toast(state.error)
                }
                else -> {}
            }

            when (state.deleteAddress) {
                is UiState.Loading -> {progressBar.root.visibility = View.VISIBLE}
                is UiState.Success -> {
                    requireContext().toast("Delete Address successfully")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                        viewModel.resetDeleteAddressState()
                    }, 400)
                }
                is UiState.Error -> {
                    progressBar.root.visibility = View.GONE
                    requireContext().toast(state.error)
                }
                else -> {}
            }

            when (state.updateAddress) {
                is UiState.Loading -> {progressBar.root.visibility = View.VISIBLE}
                is UiState.Success -> {
                    requireContext().toast("Address updated successfully")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 400)
                }
                is UiState.Error -> {
                    progressBar.root.visibility = View.GONE
                    requireContext().toast(state.error)
                }
                else -> {}
            }

        }
    }

    private fun handleAddressSuccess(addressId: String) {
        requireContext().toast("Add new address successfully")

        if (fromCheckout) {
            val result = Bundle().apply {
                putString("newAddressId", addressId)
            }
            setFragmentResult("addAddressRequestKey", result)
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddEditAddressFragment"
    }
}