package com.blossy.flowerstore.presentation.account.ui

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentChangePasswordBinding
import com.blossy.flowerstore.presentation.account.viewmodel.ChangePasswordViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        observe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }
    private fun setOnClickListener() = with(binding) {
        btnBack.setOnClickListener {
            findNavController().apply {
                navigate(R.id.action_changePasswordFragment_to_mainFragment)
                popBackStack()
            }
        }
        // Toggle visibility for Current Password
        setupPasswordToggle(
            binding.currentPasswordInput,
            binding.currentPasswordToggle
        )

        // Toggle visibility for New Password
        setupPasswordToggle(
            binding.newPasswordInput,
            binding.newPasswordToggle
        )

        // Toggle visibility for Re-Type New Password
        setupPasswordToggle(
            binding.reNewPasswordInput,
            binding.reNewPasswordToggle
        )

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordInput.text.toString().trim()
            val newPassword = newPasswordInput.text.toString().trim()
            val reNewPassword = reNewPasswordInput.text.toString().trim()

            // Clear errors
            currentPasswordInput.error = null
            newPasswordInput.error = null
            reNewPasswordInput.error = null

            var hasError = false

            if (currentPassword.isEmpty()) {
                currentPasswordInput.error = "Please enter your current password"
                hasError = true
            }

            if (newPassword.isEmpty()) {
                newPasswordInput.error = "Please enter a new password"
                hasError = true
            } else if (newPassword.length < 6) {
                newPasswordInput.error = "Please confirm your new password"
                hasError = true
            }

            if (reNewPassword.isEmpty()) {
                reNewPasswordInput.error = "Passwords do not match"
                hasError = true
            } else if (newPassword != reNewPassword) {
                reNewPasswordInput.error = "Mật khẩu không khớp"
                hasError = true
            }

            if (currentPassword == newPassword && currentPassword.isNotEmpty()) {
                newPasswordInput.error = "New password must be different from the current password"
                hasError = true
            }

            if (!hasError) {
                viewModel.changePassword(currentPassword, newPassword)
            }
        }

    }

    private fun setupPasswordToggle(editText: EditText, toggle: ImageView) {
        toggle.setOnClickListener {
            val isHidden = editText.transformationMethod is PasswordTransformationMethod
            if (isHidden) {
                editText.transformationMethod = null
                toggle.setImageResource(R.drawable.ic_eye_on)
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                toggle.setImageResource(R.drawable.ic_eye_off)
            }
            editText.setSelection(editText.text?.length ?: 0)
        }
    }

    private fun observe() {
        collectState(viewModel.updatePassword) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                    findNavController().apply {
                        navigate(R.id.action_changePasswordFragment_to_mainFragment)
                        popBackStack()
                    }
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    companion object {

    }
}