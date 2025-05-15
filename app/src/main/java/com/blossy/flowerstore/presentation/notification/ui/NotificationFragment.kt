package com.blossy.flowerstore.presentation.notification.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentNotificationBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.notification.adapter.NotificationAdapter
import com.blossy.flowerstore.presentation.notification.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchNotifications()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        notificationAdapter = NotificationAdapter() {

        }
        binding.recyclerViewNotifications.adapter = notificationAdapter
        observe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_mainFragment)
            findNavController().popBackStack()
        }
    }

    private fun observe() {
        collectState(viewModel.notifications) {
            when(it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    Log.d("NotificationFragment", "observe: ${it.data}")
                    notificationAdapter.submitList(it.data)
                }
                is UiState.Error -> {

                } else -> {

                }
            }
        }
    }
    companion object {

    }
}