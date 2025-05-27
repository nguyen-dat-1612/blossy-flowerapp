package com.blossy.flowerstore.presentation.account.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentAccountBinding
import com.blossy.flowerstore.databinding.FragmentHomeBinding
import com.blossy.flowerstore.presentation.account.viewmodel.AccountViewModel
import com.blossy.flowerstore.presentation.auth.ui.AuthActivity
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.utils.manager.FcmManager
import com.blossy.flowerstore.utils.LocationHelper
import com.blossy.flowerstore.utils.manager.SettingsManager
import com.blossy.flowerstore.utils.toast
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var settingsManager: SettingsManager
    private lateinit var fcmManager: FcmManager
    private lateinit var locationHelper: LocationHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        settingsManager = SettingsManager(requireContext())
        fcmManager = FcmManager(FirebaseMessaging.getInstance())
        locationHelper = LocationHelper(requireContext())

        initSwitchStates()
        setupSwitches()
        observe()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.nav_host_main)
        setOnClickListener();
    }

    fun setOnClickListener() = with(binding) {
        profile.setOnClickListener {  navController.navigate(R.id.action_mainFragment_to_profileFragment) }
        orderHistory.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_orderHistoryFragment) }
        shippingAddress.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToShippingAddressFragment(fromCheckout = false)
            navController.navigate(action)
        }
        changePassword.setOnClickListener {
             navController.navigate(R.id.action_mainFragment_to_changePasswordFragment)
        }
    }

    private fun initSwitchStates() = with(binding) {
        pushNotificationsSwitch.isChecked = settingsManager.isPushEnabled
        locationServicesSwitch.isChecked = settingsManager.isLocationEnabled
        darkModeSwitch.isChecked = settingsManager.isDarkMode

        AppCompatDelegate.setDefaultNightMode(
            if (settingsManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupSwitches() = with(binding) {
        pushNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isPushEnabled = isChecked
            if (isChecked) {
                fcmManager.enableFCM(
                    onSuccess = {viewModel.updateFcmToken(it)},
                    onError = {handleFcmError(it)}
                )
            } else {
                fcmManager.disableFCM { handleFcmError(it) }
            }
            showToast(if (isChecked) "Notifications enabled" else "Notifications disabled")
        }


        locationServicesSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isLocationEnabled = isChecked

            if (isChecked) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    locationHelper.getLastLocation(
                        onSuccess = { location ->

                        },
                        onError = { exception ->
                            Log.e("Location", "Error getting location: ${exception.message}")
                        }
                    )
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
                }
            }
            showToast("Location services ${if (isChecked) "enabled" else "disabled"}")
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isDarkMode = isChecked

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            requireActivity().recreate()
        }

        logoutAccount.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showToast(message: String) {
        requireContext().toast(message)
    }

    private fun handleFcmError(exception: Exception?) {
        Log.e(TAG, "FCM failed", exception)
        binding.pushNotificationsSwitch.isChecked = false
        showToast("Failed to update FCM settings")
    }



    private fun observe() {
        collectState(viewModel.updateFcm) {
            when(it) {
                is UiState.Success ->  Log.d(TAG, "observe: ${it.data}")
                is UiState.Error -> Log.d(TAG, "observe: ${it.message}")
                else -> {}
            }
        }
        collectState(viewModel.logout) {
            when(it) {
                is UiState.Success ->  Log.d(TAG, "observe: ${it.data}")
                is UiState.Error -> Log.d(TAG, "observe: ${it.message}")
                else -> {}
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            locationHelper.getLastLocation(
                onSuccess = { /* handle location */ },
                onError = { Log.e(TAG, "Location error: ${it.message}") }
            )
        } else {
            binding.locationServicesSwitch.isChecked = false
            showToast("Location permission denied")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
        private const val TAG = "AccountFragment"
    }
}