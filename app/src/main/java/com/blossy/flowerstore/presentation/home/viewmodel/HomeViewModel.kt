package com.blossy.flowerstore.presentation.home.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blossy.flowerstore.domain.usecase.category.GetCategoriesUseCase
import com.blossy.flowerstore.domain.usecase.product.GetTopProductsUseCase
import com.blossy.flowerstore.domain.usecase.user.GetUseProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.presentation.home.state.HomeUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getTopProductsUseCase: GetTopProductsUseCase,
    private val getUseProfileUseCase: GetUseProfileUseCase
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState


    fun loadHomeData() {
        _homeUiState.value  = HomeUiState(isLoading = true)
        viewModelScope.launch {

            supervisorScope {
                val catDeferred = async {withContext(Dispatchers.IO) { getCategoriesUseCase()}}
                val prodDeferred = async {withContext(Dispatchers.IO) {getTopProductsUseCase()}}
                val userDeferred = async {withContext(Dispatchers.IO) {getUseProfileUseCase()}}

                val carResult = catDeferred.await()
                val prodResult = prodDeferred.await()
                val userResult = userDeferred.await()

                val newState = _homeUiState.value.copy(
                    isLoading = false,
                    categories = when(carResult) {
                        is Result.Success -> carResult.data
                        is Result.Error -> {
                            setError(carResult.message)
                            emptyList()
                        } else -> emptyList()
                    },
                    products = when(prodResult) {
                        is Result.Success -> prodResult.data
                        is Result.Error -> {
                            setError(prodResult.message)
                            emptyList()
                        }
                        else -> emptyList()
                    },
                    user = when(userResult) {
                        is Result.Success -> userResult.data
                        is Result.Error -> {
                            setError(userResult.message)
                            null
                        }
                        else -> null
                    }
                )
                _homeUiState.value = newState
            }
        }
    }
    fun updateLocationText(context: Context, lat: Double, lng: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                val locationText = addresses?.firstOrNull()?.toLocationText() ?: "Unknown"
                _homeUiState.update { it.copy(locationText = locationText) }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val address = geocoder.getFromLocation(lat, lng, 1)?.firstOrNull()
                    val locationText = address?.toLocationText() ?: "Unknown"
                    _homeUiState.update { it.copy(locationText = locationText) }
                }
                catch (e: CancellationException) {
                    _homeUiState.update { it.copy(locationText = "Location error") }
                } catch (e: IOException) {
                    _homeUiState.update { it.copy(locationText = "Location error") }
                }
            }
        }
    }

    private fun Address.toLocationText(): String {
        return listOfNotNull(subAdminArea, adminArea, countryName).joinToString(", ")
    }

    private fun setError(message: String) {
        if (message.isNotBlank()) {
            _homeUiState.value = _homeUiState.value.copy(
                error = message
            )
        }
    }
}
