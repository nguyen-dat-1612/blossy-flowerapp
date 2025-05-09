package com.blossy.flowerstore.presentation.account.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentAccountBinding
import com.blossy.flowerstore.presentation.checkout.ui.CheckOutFragmentDirections
import com.blossy.flowerstore.presentation.common.MainFragmentDirections


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)


        binding.profile.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_main)
            navController.navigate(R.id.action_mainFragment_to_profileFragment)
        }
//        binding.orderHistory.setOnClickListener {
//            findNavController().navigate(R.id.action_accountFragment_to_orderHistoryFragment)
//        }
        binding.shippingAddress.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_main)
            val action = MainFragmentDirections.actionMainFragmentToShippingAddressFragment(
                fromCheckout = false
            )

            navController.navigate(action)
        }
        return binding.root
    }

    companion object {

    }
}