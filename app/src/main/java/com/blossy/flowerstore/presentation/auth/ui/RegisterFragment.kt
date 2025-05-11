package com.blossy.flowerstore.presentation.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentRegisterBinding
import com.blossy.flowerstore.presentation.auth.viewmodel.RegisterViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.common.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        observeRegister()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()

    }

    private fun observeRegister() {
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
            viewModel.register(
                name = binding.fullNameText.text.toString(),
                email = binding.emailText.text.toString(),
                password = binding.passwordText.text.toString()
            )
        }
        binding.signInText.setOnClickListener {
            findNavController().popBackStack()
        }

    }
}