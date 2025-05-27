package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.mapper.toFavorite
import com.blossy.flowerstore.data.mapper.toProduct
import com.blossy.flowerstore.data.remote.api.FavoriteApi
import com.blossy.flowerstore.domain.repository.FavoriteRepository
import com.blossy.flowerstore.domain.utils.Result
import com.blossy.flowerstore.data.remote.utils.safeApiCall
import com.blossy.flowerstore.data.remote.utils.toResult
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.domain.model.FavoriteModel
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteApi: FavoriteApi
): FavoriteRepository {

    override suspend fun getFavoriteProducts(): Result<List<ProductModel>>{
        return safeApiCall {
            favoriteApi.getFavoriteProducts().toResult { dtoList ->
                dtoList.map { it.toProduct().copy(isFavorite = true) }
            }
        }
    }

    override suspend fun addFavoriteProduct(productId: String): Result<FavoriteModel>  {
        return safeApiCall {
            favoriteApi.addFavoriteProduct(productId).toResult() { response ->
                response.toFavorite().copy(productId = productId)
            }
        }
    }

    override suspend fun removeFavoriteProduct(productId: String): Result<Boolean>  {
        return safeApiCall {
            favoriteApi.removeFavoriteProduct(productId).toResult()
        }
    }

    override suspend fun checkFavoriteProduct(productId: String): Result<Boolean>  {
        return safeApiCall {
            favoriteApi.checkFavoriteProduct(productId).toResult()
        }
    }
}