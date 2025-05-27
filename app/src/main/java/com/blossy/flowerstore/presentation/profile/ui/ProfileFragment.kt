package com.blossy.flowerstore.presentation.profile.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentProfileBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.profile.viewmodel.ProfileViewModel
import com.blossy.flowerstore.utils.loadImage
import com.blossy.flowerstore.utils.toast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUserProfile()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
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
                id = (viewModel.profileUiState.value.user)!!.id,
                name = fullNameInput.text.toString(),
                email = emailInput.text.toString()
            )
        }
    }

    private fun observeData() = with(binding){
        collectState(viewModel.profileUiState) { state ->

            progressOverlay.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            if (state.errorMessage.isNotBlank()) {
                requireContext().toast(state.errorMessage)
            }

            if (state.user != null){
                profileImage.loadImage(state.user.avatar)
                userName.text = state.user.name
                fullNameInput.setText(state.user.name)
                emailInput.setText(state.user.email)
            }

            when(state.updateProfileState) {
                is UiState.Success -> {
                    requireContext().toast("Update Success")
                }
                is UiState.Error -> {
                    requireContext().toast("Update Failed")
                }
                else -> {}
            }
        }

    }
    companion object {

    }
}