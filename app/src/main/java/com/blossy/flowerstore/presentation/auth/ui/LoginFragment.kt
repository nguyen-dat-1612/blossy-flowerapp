package com.blossy.flowerstore.presentation.auth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentLoginBinding
import com.blossy.flowerstore.presentation.auth.viewmodel.LoginViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.common.MainActivity
import com.blossy.flowerstore.utils.isValidEmail
import com.blossy.flowerstore.utils.setChildrenEnabled
import com.blossy.flowerstore.utils.setOnSingleClickListener
import com.blossy.flowerstore.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private val email get() = binding.emailText.text.toString().trim()
    private val password get() = binding.passwordText.text.toString().trim()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        observeState()

    }

    private fun observeState() {
        collectState(viewModel.uiState) { state ->
            when (state) {
                is UiState.Loading -> {
                    setLoading(true)
                }
                is UiState.Success -> startMainActivity()
                is UiState.Error -> {
                    setLoading(false)
                    requireContext().toast(state.message)
                    Log.d("LoginFragment", "observeState: ${state.message}")
                }
                UiState.Idle -> Unit
            }
        }
    }

    private fun setOnClickListener() {
        binding.loginButton.setOnSingleClickListener {
            if (validateInputs()) viewModel.login( email, password)
        }
        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
        }
    }


    private fun setLoading(isLoading: Boolean) {
        binding.progressOverlay.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.formContainer.setChildrenEnabled(!isLoading)
    }

    private fun validateInputs(): Boolean =  when {
        email.isEmpty() -> showInvalid("Email is required")
        password.isEmpty() -> showInvalid("Password is required")
        !email.isValidEmail() -> showInvalid("Invalid email")
        else -> true
    }

    private fun showInvalid(message: String): Boolean {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        return false
    }

    private fun startMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }
}