package com.blossy.flowerstore.presentation.shippingAddress.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blossy.flowerstore.databinding.FragmentAddEditAddressBinding
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.blossy.flowerstore.R
import com.blossy.flowerstore.data.remote.dto.AddressResponse
import com.blossy.flowerstore.domain.model.Address
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.shippingAddress.viewmodel.AddEditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditAddressFragment : Fragment() {

    private var _binding: FragmentAddEditAddressBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditAddressFragmentArgs by navArgs()
    private lateinit var viewModel: AddEditViewModel
    private lateinit var address: Address
    private lateinit var action: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddEditViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditAddressBinding.inflate(inflater, container, false)
        setOnClickListener()
        observe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = args.action

        if (action == "edit" && args.address != null) {
            address = args.address!!
            binding.titleText.text = "Update Address"
            binding.nameInput.setText(address.name)
            binding.phoneInput.setText(address.phone)
            binding.addressInput.setText(address.address)
            if (address.isDefault) {
                binding.isDefaultCheckbox.visibility = GONE
            }
            binding.deleteButton.visibility = VISIBLE
        } else {
            binding.titleText.text = "Add New Address"
            binding.deleteButton.visibility = GONE
        }

    }

    fun setOnClickListener() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_addEditAddressFragment_to_shippingAddressFragment)
            findNavController().popBackStack()
        }

        binding.saveButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val phone = binding.phoneInput.text.toString()
            val addressText = binding.addressInput.text.toString()
            val isDefault = binding.isDefaultCheckbox.isChecked

            if (name.isEmpty() || phone.isEmpty() || addressText.isEmpty()) {
                Toast.makeText(context, "Please fill in all information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (action.equals("add")) {
                val newAddress = AddressResponse(null, name, phone, addressText, isDefault)
                viewModel.addAddress(newAddress)
            } else {
                val newAddress = AddressResponse(address.id, name, phone, addressText, isDefault)
                viewModel.updateAddress(newAddress)
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteAddress(address.id!!)
        }
    }

    private fun observe() {
        collectState(viewModel.uiState) {state ->
            when(state) {
                is UiState.Loading -> {
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                }
                is UiState.Success -> {
                    findNavController().navigate(R.id.action_addEditAddressFragment_to_shippingAddressFragment)
                    findNavController().popBackStack()
                }
                is UiState.Error -> {
                    Log.d(TAG, "Error: ${state.message}")
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddEditAddressFragment"
    }
}