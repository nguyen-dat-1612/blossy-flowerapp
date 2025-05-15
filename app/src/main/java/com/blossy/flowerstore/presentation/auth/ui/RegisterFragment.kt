package com.blossy.flowerstore.presentation.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentRegisterBinding
import com.blossy.flowerstore.presentation.auth.viewmodel.RegisterViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.common.MainActivity
import com.blossy.flowerstore.utils.isValidEmail
import com.blossy.flowerstore.utils.setChildrenEnabled
import com.blossy.flowerstore.utils.setOnSingleClickListener
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    private val name get() = binding.fullNameText.text.toString().trim()
    private val email get() = binding.emailText.text.toString().trim()
    private val password get() = binding.passwordText.text.toString().trim()
    private val confirmPassword get() = binding.confirmPasswordText.text.toString().trim()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeRegister()
        setOnClickListener()

    }

    private fun observeRegister() {
        collectState(viewModel.uiState) { state ->
            when (state) {
                is UiState.Loading -> setLoading(true)
                is UiState.Success -> navigateToMain()
                is UiState.Error -> {
                    setLoading(false)
                    showToast(state.message)
                }
                UiState.Idle -> Unit
            }
        }
    }

    private fun setOnClickListener() {
        binding.registerButton.setOnSingleClickListener  {
            if (validInputs()) viewModel.register(name, email, password)
        }
        binding.signInText.setOnSingleClickListener  {
            findNavController().popBackStack()
        }
    }

    private fun validInputs(): Boolean = when {
        name.isEmpty() -> showInvalid(binding.fullNameText, "Name is required")
        email.isEmpty() -> showInvalid(binding.emailText, "Email is required")
        password.isEmpty() -> showInvalid(binding.passwordText,"Password is required")
        confirmPassword.isEmpty() -> showInvalid(binding.confirmPasswordText,"Confirm password is required")
        !email.isValidEmail() -> showInvalid(binding.emailText, "Invalid email")
        password != confirmPassword -> showInvalid(binding.confirmPasswordText, "Password doesn't match")
        else -> true
    }

    private fun showInvalid(field: EditText, message: String): Boolean {
        field.error = message
        return false
    }

    private fun showToast(message: String) {
        requireContext().toast(message)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressOverlay.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.formContainer.setChildrenEnabled(!isLoading)
    }

    private fun navigateToMain() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
            requireActivity().finish()
        }
    }
}