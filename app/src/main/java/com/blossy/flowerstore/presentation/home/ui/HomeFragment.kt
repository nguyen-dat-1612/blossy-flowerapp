package com.blossy.flowerstore.presentation.home.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.blossy.flowerstore.databinding.FragmentHomeBinding
import com.blossy.flowerstore.utils.collectState
import com.blossy.flowerstore.presentation.home.adapter.CategoryAdapter
import com.blossy.flowerstore.presentation.home.adapter.ProductAdapter
import com.blossy.flowerstore.presentation.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blossy.flowerstore.R
import com.blossy.flowerstore.domain.model.CategoryModel
import com.blossy.flowerstore.presentation.common.MainFragmentDirections
import com.blossy.flowerstore.presentation.home.adapter.BannerAdapter
import com.blossy.flowerstore.utils.LocationHelper
import com.blossy.flowerstore.utils.loadImage
import com.blossy.flowerstore.utils.setOnSingleClickListener
import com.blossy.flowerstore.utils.toast

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadHomeData()
        requestLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservers()
        requestLocation()
    }

    private fun initViews() = with(binding) {
        viewPager.adapter = bannerAdapter
        setupAutoScroll()

        recyclerViewCategories.apply {
            adapter = categoryAdapter
            setHasFixedSize(true)
        }

        recyclerViewPopular.apply {
            adapter = productAdapter
            setHasFixedSize(true)
        }

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
        if (refresh) binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun requestLocation() {
        locationHelper.getLastLocation(
            onSuccess = { location ->
                location?.let {
                    viewModel.updateLocationText(requireContext(), it.latitude, it.longitude)
                } ?: run {
                    binding.locationText.text = getString(R.string.location_unavailable)
                }
            },
            onError = {
                binding.locationText.text = getString(R.string.location_error, it.message)
            }
        )
    }

    private fun setupObservers() = with(binding) {

        collectState(viewModel.homeUiState) {

            progressOverlayCategories.root.isVisible = if (it.isLoading && it.categories.isEmpty()) true else false
            progressOverlayPopular.root.isVisible = if (it.isLoading && it.products.isEmpty()) true else false

            if (it.categories.isNotEmpty()) categoryAdapter.submitList(it.categories)
            if (it.products.isNotEmpty()) productAdapter.submitList(it.products)
            if (it.user != null) {
                userName.text = it.user.name
                userAvatar.loadImage(it.user.avatar)
            }

            if (it.error.isNotBlank()) {
                requireContext().toast(it.error)
            }
            if (it.locationText.isEmpty()) {
                locationText.text = getString(R.string.location_unavailable)
            } else {
                locationText.text = it.locationText
            }

        }
    }

    private fun navigateTo(actionId: Int) = findNavController().navigate(actionId)

    private fun navigateToCategoryProducts(category: CategoryModel) {
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
    }
}