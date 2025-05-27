package com.blossy.flowerstore.presentation.auth.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentForgotBinding
import com.blossy.flowerstore.presentation.auth.viewmodel.ForgotViewModel
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotFragment : Fragment() {

    private var _binding: FragmentForgotBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotViewModel by viewModels()
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotBinding.inflate(inflater, container, false);
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        observeForgotPassword()
    }
    private fun setOnClickListener() {
        binding.resetButton.setOnClickListener {
            val email = binding.emailText.text.toString()
            if (email.isNotEmpty()) {
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.emailText.windowToken, 0)
                viewModel.forgotPassword(email)
            } else {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
        binding.backToLoginText.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeForgotPassword() {
        collectState(viewModel.forgotPassword) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressOverlay.root.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressOverlay.root.visibility = View.GONE
                    showSuccessDialog()
                }
                is UiState.Error -> {
                    binding.progressOverlay.root.visibility = View.GONE

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                UiState.Idle -> Unit

            }
        }
    }
    private fun showSuccessDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success, null)
        val messageText = dialogView.findViewById<TextView>(R.id.successMessage)
        messageText?.text = "A password reset link has been sent to your email."

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialog.window?.setLayout(
            (requireContext().resources.displayMetrics.widthPixels * 0.90).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            findNavController().popBackStack()
        }, 4000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}