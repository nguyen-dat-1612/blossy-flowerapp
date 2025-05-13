package com.blossy.flowerstore.presentation.profile.ui

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
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentProfileBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.profile.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.getUserProfile()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        observeData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() = with(binding) {
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
            findNavController().popBackStack()
        }
        editBtn.setOnClickListener {
            fullNameInput.isEnabled = true
            emailInput.isEnabled = true
            passwordInput.isEnabled = true
            editProfileImage.visibility = View.GONE
            editBtn.visibility = View.GONE
            confirmBtn.visibility = View.VISIBLE
        }

        binding.confirmBtn.setOnClickListener{
            fullNameInput.isEnabled = false
            emailInput.isEnabled = false
            passwordInput.isEnabled = false
            confirmBtn.visibility = View.GONE
            editProfileImage.visibility = View.VISIBLE
            editBtn.visibility = View.VISIBLE
            viewModel.updateUserProfile(
                id = (viewModel.userProfileUiState.value as UiState.Success).data.id,
                name = fullNameInput.text.toString(),
                email = emailInput.text.toString()
            )
        }
    }

    private fun observeData() {
        collectState(viewModel.userProfileUiState) { state ->
            when (state) {

                is UiState.Loading -> {}
                is UiState.Success -> {
                    Log.d("HomeFragment", "Success: ${state.data}")
                    // Handle success state
                    Glide.with(binding.profileImage.context)
                        .load(state.data.avatar)
                        .into(binding.profileImage)

                    binding.apply {
                        userName.text = state.data.name
                        fullNameInput.setText(state.data.name)
                        emailInput.setText(state.data.email)
                    }

                }

                is UiState.Error -> {
                    Log.e("HomeFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    // Handle error state
                }
                is UiState.Idle -> {
                    // Handle idle state
                }
            }
        }

        collectState(viewModel.updateProfileUiState) {
            when (it) {
                is UiState.Loading -> {}

                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Update Success", Toast.LENGTH_SHORT).show()
                    Log.d("HomeFragment", "Success: ${it.data}")
                    // Handle success state
                    Glide.with(binding.profileImage.context)
                        .load(it.data.avatar)
                        .into(binding.profileImage)
                    binding.apply {
                        userName.text = it.data.name
                        fullNameInput.setText(it.data.name)
                        emailInput.setText(it.data.email)
                    }
                }
                is UiState.Error -> {
                    Log.e("HomeFragment", "Error: ${it.message}")
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    // Handle error state
                }
                else -> {}
            }
        }
    }


    companion object {

    }
}