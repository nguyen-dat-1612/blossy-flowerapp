package com.blossy.flowerstore.presentation.account.ui

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
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
import com.blossy.flowerstore.utils.clearError
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.utils.hasMinLength
import com.blossy.flowerstore.utils.isNotEmptyOrShowError
import com.blossy.flowerstore.utils.matches
import com.blossy.flowerstore.utils.toast
import com.blossy.flowerstore.utils.validateChangePasswordInputs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding?= null
    private val binding: FragmentChangePasswordBinding get() = _binding!!
    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setOnClickListener()
    }
    private fun setOnClickListener() = with(binding) {
        btnBack.setOnClickListener {
            findNavController().apply {
                navigate(R.id.action_changePasswordFragment_to_mainFragment)
                popBackStack()
            }
        }
        setupPasswordToggle(currentPasswordInput, currentPasswordToggle)
        setupPasswordToggle(newPasswordInput, newPasswordToggle)
        setupPasswordToggle(reNewPasswordInput, reNewPasswordToggle)

        changePasswordButton.setOnClickListener {
            if(validateChangePasswordInputs(currentPasswordInput, newPasswordInput, reNewPasswordInput)) {
                val currentPassword = currentPasswordInput.text.toString().trim()
                val newPassword = newPasswordInput.text.toString().trim()
                viewModel.changePassword(
                    currentPassword,
                    newPassword
                )
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

    private fun observe() = with(binding) {
        collectState(viewModel.updatePassword) {
            when (it) {
                is UiState.Loading -> { progressOverlay.root.visibility = View.VISIBLE }
                is UiState.Success -> {
                    requireContext().toast("Password changed successfully")
                    findNavController().apply {
                        navigate(R.id.action_changePasswordFragment_to_mainFragment)
                        popBackStack()
                    }
                }
                is UiState.Error -> {
                    progressOverlay.root.visibility = View.VISIBLE
                    requireContext().toast(it.message)
                }
                else -> {}
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}