package com.blossy.flowerstore.presentation.checkout.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentPaymentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentResultFragment : Fragment() {

    private var _binding: FragmentPaymentResultBinding? = null
    private val binding get() = _binding!!

    private val args: PaymentResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val status = args.paymentStatus
        val orderId = args.orderId

        binding.resultTitle.text = if (status == "success") "Your order is successfully purchased" else "Your order is failed purchased"
        binding.resultDescription.text =
            "Your order is being prepared to be shipped to\nyou on time\n\n" +
                    "Order code: $orderId"

        binding.backToHomeBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_paymentResultFragment_to_mainFragment,
            )

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}