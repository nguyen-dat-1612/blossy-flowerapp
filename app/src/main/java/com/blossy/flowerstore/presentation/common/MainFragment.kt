package com.blossy.flowerstore.presentation.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.FragmentMainBinding
import com.blossy.flowerstore.databinding.FragmentProfileBinding


class MainFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentMainBinding
    private val args: MainFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedTab = arguments?.getString("selectedTab") ?: "home"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_tabs) as NavHostFragment
        navController = navHostFragment.navController

        // Kết nối BottomNav với NavController
        binding.bottomNavigation.setupWithNavController(navController)

//        // 3. Xử lý selected tab từ arguments (nếu có)
//        args.selectedTab?.let { tab ->
//            when (tab) {
//                "home" -> binding.bottomNavigation.selectedItemId = R.id.homeFragment
//                "favorites" -> binding.bottomNavigation.selectedItemId = R.id.favoritesFragment
//                "cart" -> binding.bottomNavigation.selectedItemId = R.id.cartFragment
//                "account" -> binding.bottomNavigation.selectedItemId = R.id.accountFragment
//            }
//        }

        return binding.root

    }

    // 4. Xử lý save/restore state khi config change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_TAB", binding.bottomNavigation.selectedItemId)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getInt("SELECTED_TAB")?.let {
            binding.bottomNavigation.selectedItemId = it
        }
    }

    companion object {

    }
}