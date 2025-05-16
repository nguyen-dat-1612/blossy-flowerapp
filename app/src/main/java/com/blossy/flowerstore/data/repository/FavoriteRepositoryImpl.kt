package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.FavoriteApi
import com.blossy.flowerstore.data.remote.dto.FavoriteResponse
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.repository.FavoriteRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteApi: FavoriteApi
): FavoriteRepository {

    override suspend fun getFavoriteProducts(): Result<List<Product>> = withTimeout(TIMEOUT){
        safeApiCall {
            favoriteApi.getFavoriteProducts().toResult { dtoList ->
                dtoList.map { it.copy(isFavorite = true) }
            }
        }
    }

    override suspend fun addFavoriteProduct(productId: String): Result<FavoriteResponse> = withTimeout(TIMEOUT) {
        safeApiCall {
            favoriteApi.addFavoriteProduct(productId).toResult() { response ->
                response.copy(productId = productId)
            }
        }
    }

    override suspend fun removeFavoriteProduct(productId: String): Result<Boolean> = withTimeout(TIMEOUT) {
        safeApiCall {
            favoriteApi.removeFavoriteProduct(productId).toResult()
        }
    }

    override suspend fun checkFavoriteProduct(productId: String): Result<Boolean> = withTimeout(TIMEOUT) {
        safeApiCall {
            favoriteApi.checkFavoriteProduct(productId).toResult()
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}