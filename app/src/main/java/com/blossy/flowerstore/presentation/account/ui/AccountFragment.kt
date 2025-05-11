package com.blossy.flowerstore.presentation.account.ui

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentAccountBinding
import com.blossy.flowerstore.presentation.account.viewmodel.AccountViewModel
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseMessaging: FirebaseMessaging
    private val viewModel: AccountViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        // Khởi tạo trạng thái từ SharedPreferences
        initSwitchStates()

        // Thiết lập sự kiện cho các switch
        setupSwitches()

        observe()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseMessaging = FirebaseMessaging.getInstance()
        navController = requireActivity().findNavController(R.id.nav_host_main)
        setOnClickListener();
    }

    fun setOnClickListener() {
        binding.profile.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_profileFragment)
        }
        binding.orderHistory.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_orderHistoryFragment)
        }
        binding.shippingAddress.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_main)
            val action = MainFragmentDirections.actionMainFragmentToShippingAddressFragment(
                fromCheckout = false
            )
            navController.navigate(action)
        }
    }

    private fun initSwitchStates() {
        // Lấy trạng thái đã lưu hoặc dùng giá trị mặc định
        binding.pushNotificationsSwitch.isChecked = sharedPreferences.getBoolean("push_notifications", true)
        binding.locationServicesSwitch.isChecked = sharedPreferences.getBoolean("location_services", true)
        binding.darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

        // Áp dụng dark mode ngay lập tức nếu cần
        if (binding.darkModeSwitch.isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setupSwitches() {
        // Push Notifications Switch
        binding.pushNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("push_notifications", isChecked).apply()

            binding.pushNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    enablePushNotifications()
                } else {
                    disablePushNotifications()
                }
                showNotificationStatusToast(isChecked)
                saveNotificationPreference(isChecked)
            }
        }
        // Location Services Switch
        binding.locationServicesSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("location_services", isChecked).apply()

            if (isChecked) {
                // Kiểm tra quyền truy cập vị trí
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    // Đã có quyền, có thể bật dịch vụ vị trí
                    enableLocationServices()
                } else {
                    // Yêu cầu quyền
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION)
                }
            }

            Toast.makeText(context, "Location services ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Dark Mode Switch
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Yêu cầu activity recreate để áp dụng theme mới
            requireActivity().recreate()
        }
    }
    private fun enablePushNotifications() {
        try {
            firebaseMessaging.token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "FCM Token: $token")
                    sendTokenToServer(token)
                } else {
                    handleFcmError(task.exception)
                }
            }
        } catch (e: Exception) {
            handleFcmError(e)
        }
    }

    private fun disablePushNotifications() {
        try {
            firebaseMessaging.deleteToken().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    handleFcmError(task.exception)
                }
            }
        } catch (e: Exception) {
            handleFcmError(e)
        }
    }

    private fun sendTokenToServer(token: String) {
        viewModel.updateFcmToken(token)
    }

    private fun handleFcmError(exception: Exception?) {
        Log.e("FCM", "FCM operation failed", exception)
        binding.pushNotificationsSwitch.isChecked = false
        Toast.makeText(
            requireContext(),
            "Failed to update notification settings",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNotificationStatusToast(isEnabled: Boolean) {
        val message = if (isEnabled) "Notifications enabled" else "Notifications disabled"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveNotificationPreference(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean("push_notifications", isEnabled).apply()
    }

    private fun enableLocationServices() {
        // Khởi tạo FusedLocationProviderClient
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Xử lý vị trí hiện tại nếu cần
                }
        } catch (e: SecurityException) {
            Log.e("Location", "Error getting location: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Quyền được cấp, bật dịch vụ vị trí
                    enableLocationServices()
                } else {
                    // Quyền bị từ chối, tắt switch
                    binding.locationServicesSwitch.isChecked = false
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observe() {
        collectState(viewModel.updateFcm) {
            when(it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    Log.d(TAG, "observe: ${it.data}")
                }
                is UiState.Error -> {
                    Log.d(TAG, "observe: ${it.message}")
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
        private const val TAG = "AccountFragment"
    }
}