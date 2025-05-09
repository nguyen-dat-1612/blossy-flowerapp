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
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.common.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

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

    fun observeState() {
        collectState(viewModel.uiState) { state ->
            when (state) {
                is UiState.Loading -> {
//                    showLoading()
                }

                is UiState.Success -> {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
//                    showSuccess(state.data)
                }

                is UiState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    Log.d("LoginFragment", "observeState: ${state.message}")
//                    showError(state.message)
                }

                UiState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    private fun setOnClickListener() {
        binding.loginButton.setOnClickListener {
            viewModel.login(
                email = binding.emailText.text.toString(),
                password = binding.passwordText.text.toString()
            )
        }
        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
        }
    }

    companion object {
    }
}