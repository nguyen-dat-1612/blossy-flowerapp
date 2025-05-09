package com.blossy.flowerstore.presentation.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
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

//        setupActionBarWithNavController(navController)
//        handleDeepLinks(intent)
    }


//    private fun handleDeepLinks(intent: Intent) {
//        intent.data?.let { uri ->
//            when (uri.host) {
//                "search" -> navController.navigate(
//                    Uri.parse("app://main/search"),
//                    NavOptions.Builder()
//                        .setPopUpTo(R.id.mainFragment, false)
//                        .build()
//                )
//                "checkOut" -> navController.navigate(
//                    Uri.parse("app://main/checkOut"),
//                    NavOptions.Builder()
//                        .setPopUpTo(R.id.mainFragment, false)
//                        .build()
//                )
//                // Xử lý các Deep Links khác
//            }
//        }
//    }


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

                navController.popBackStack(R.id.checkOutFragment, false)
                navController.navigate(R.id.paymentResultFragment, args)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}