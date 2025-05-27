package com.blossy.flowerstore.presentation.common

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_main) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NavDebug", "Navigated to: ${destination.label}")

            val fragments = supportFragmentManager
                .findFragmentById(R.id.nav_host_main)
                ?.childFragmentManager
                ?.fragments

            fragments?.forEach {
                Log.d("NavDebug", "Current fragment in stack: ${it::class.java.simpleName}")
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 1)
                Log.d(TAG, "onCreate: Requesting POST_NOTIFICATIONS permission")
            }
        }


    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun onResume() {
        super.onResume()
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "myapp" && uri.host == "payment-result") {
                val status = uri.getQueryParameter("status") ?: "unknown"
                val orderId = uri.getQueryParameter("orderId") ?: ""
                val amount = uri.getQueryParameter("amount") ?: "0"
                val transactionId = uri.getQueryParameter("transactionId") ?: ""

                val navController = findNavController(R.id.nav_host_main)

                val args = bundleOf(
                    "paymentStatus" to status,
                    "orderId" to orderId,
                    "amount" to amount,
                    "transactionId" to transactionId
                )
                Log.d(TAG, "handleDeepLink: $args")

                navController.currentBackStackEntry?.savedStateHandle?.set("payment_result", args)
//                navController.navigate(
//                    R.id.paymentResultFragment,
//                    args,
//                    NavOptions.Builder()
//                        .setPopUpTo(R.id.checkOutFragment, true)
//                        .build()
//                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}