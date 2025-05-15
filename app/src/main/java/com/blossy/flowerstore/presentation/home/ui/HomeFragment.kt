package com.blossy.flowerstore.presentation.home.ui

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.blossy.flowerstore.databinding.FragmentHomeBinding
import com.blossy.flowerstore.presentation.common.UiState
import com.blossy.flowerstore.presentation.common.collectState
import com.blossy.flowerstore.presentation.home.adapter.CategoryAdapter
import com.blossy.flowerstore.presentation.home.adapter.ProductAdapter
import com.blossy.flowerstore.presentation.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.viewpager2.widget.ViewPager2
import com.blossy.flowerstore.R
import com.blossy.flowerstore.domain.model.Category
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.home.adapter.BannerAdapter
import com.blossy.flowerstore.utils.LocationHelper
import com.blossy.flowerstore.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val locationHelper by lazy { LocationHelper(requireContext()) }

    private val categoryAdapter by lazy { CategoryAdapter(::navigateToCategoryProducts) }
    private val productAdapter by lazy { ProductAdapter { navigateToProductDetail(it.id, SOURCE_HOME) } }
    private val bannerAdapter by lazy { BannerAdapter(listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)) }

    private var scrollPosition = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var autoScrollRunnable: Runnable

    companion object {
        private const val SOURCE_HOME = "home"
        private const val AUTO_SCROLL_DELAY = 3000L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservers()
        loadData()
        requestLocation()
    }

    private fun initViews() = with(binding) {
        // Banner setup
        viewPager.adapter = bannerAdapter
        setupAutoScroll()

        // RecyclerViews setup
        recyclerViewCategories.apply {
            adapter = categoryAdapter
            setHasFixedSize(true)
        }

        recyclerViewPopular.apply {
            adapter = productAdapter
            setHasFixedSize(true)
        }

        // Click listeners
        searchClickOverlay.setOnSingleClickListener { navigateTo(R.id.action_mainFragment_to_searchFragment) }
        notificationBtn.setOnSingleClickListener { navigateTo(R.id.action_mainFragment_to_notificationFragment) }
        seeAllPopular.setOnSingleClickListener { navigateTo(R.id.action_mainFragment_to_popularFragment) }
        seeAllCategories.setOnSingleClickListener { navigateTo(R.id.action_mainFragment_to_categoryListFragment) }

        swipeRefreshLayout.setOnRefreshListener {
            loadData(refresh = true)
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            scrollPosition = scrollView.scrollY
        }
    }

    private fun setupAutoScroll() {
        autoScrollRunnable = object : Runnable {
            override fun run() {
                binding.viewPager.currentItem = (binding.viewPager.currentItem + 1) % bannerAdapter.itemCount
                handler.postDelayed(this, AUTO_SCROLL_DELAY)
            }
        }
        handler.post(autoScrollRunnable)
    }

    private fun loadData(refresh: Boolean = false) {
        viewModel.loadHomeData()
        if (refresh) binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setupObservers() {
        collectState(viewModel.categoryUiState) { state ->
            binding.progressOverlayCategories.root.isVisible = state is UiState.Loading
            if (state is UiState.Success) categoryAdapter.submitList(state.data)
        }

        collectState(viewModel.productUiState) { state ->
            binding.progressOverlayPopular.root.isVisible = state is UiState.Loading
            if (state is UiState.Success) productAdapter.submitList(state.data)
        }

        collectState(viewModel.userProfileUiState) { state ->
            when (state) {
                is UiState.Success -> {
                    binding.userName.text = state.data.name
                    Glide.with(this)
                        .load(state.data.avatar)
                        .circleCrop()
                        .into(binding.userAvatar)
                }
                is UiState.Error -> Log.e("HomeFragment", "Profile error: ${state.message}")
                else -> Unit
            }
        }
    }

    private fun requestLocation() {
        locationHelper.getLastLocation(
            onSuccess = { location ->
                location?.let {
                    resolveAddress(it.latitude, it.longitude)
                } ?: run {
                    binding.locationText.text = getString(R.string.location_unavailable)
                }
            },
            onError = {
                binding.locationText.text = getString(R.string.location_error, it.message)
            }
        )
    }

    private fun resolveAddress(lat: Double, lng: Double) = lifecycleScope.launch(Dispatchers.IO) {
        try {
            val address = Geocoder(requireContext(), Locale.getDefault())
                .getFromLocation(lat, lng, 1)
                ?.firstOrNull()

            withContext(Dispatchers.Main) {
                address?.let {
                    val locationText = listOfNotNull(
                        it.subAdminArea,
                        it.adminArea,
                        it.countryName
                    ).joinToString(", ")

                    binding.locationText.text = locationText.ifEmpty {
                        getString(R.string.location_unknown)
                    }
                } ?: run {
                    binding.locationText.text = getString(R.string.location_unknown)
                }
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                binding.locationText.text = getString(R.string.geocoder_error)
            }
        }
    }

    private fun navigateTo(actionId: Int) = findNavController().navigate(actionId)

    private fun navigateToCategoryProducts(category: Category) {
        val action = MainFragmentDirections.actionMainFragmentToCategoryProductFragment(category)
        findNavController().navigate(action)
    }

    private fun navigateToProductDetail(productId: String, source: String) {
        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(productId, source)
        findNavController().navigate(action)
    }

    private fun findNavController() = requireActivity().findNavController(R.id.nav_host_main)

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
        _binding = null
    }
}