package com.blossy.flowerstore.presentation.auth.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentForgotBinding


class ForgotFragment : Fragment() {

    private lateinit var binding: FragmentForgotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotBinding.inflate(inflater, container, false);
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }
    private fun setOnClickListener() {
        binding.forgotPasswordButton.setOnClickListener {
            val email = binding.emailText.text.toString()

            // Kiểm tra xem email có được nhập hay không
            if (email.isNotEmpty()) {

            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {

    }
}