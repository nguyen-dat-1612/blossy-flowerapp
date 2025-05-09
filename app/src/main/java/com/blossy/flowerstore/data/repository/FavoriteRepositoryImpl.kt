package com.blossy.flowerstore.data.repository

import com.blossy.flowerstore.data.remote.api.FavoriteApi
import com.blossy.flowerstore.data.remote.dto.FavoriteResponse
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.repository.FavoriteRepository
import com.blossy.flowerstore.domain.utils.Result
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteApi: FavoriteApi
): FavoriteRepository {
    override suspend fun getFavoriteProducts(): Result<List<Product>> {
        return try {
            val response = favoriteApi.getFavoriteProducts()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.Success(body.data?.map {
                        product -> product.copy(isFavorite = true)
                    } ?: emptyList())
                } else {
                    Result.Error(body?.message ?: "Failed to get favorite products")
                }
            } else {
                Result.Error(response.message() ?: "Failed to get favorite products")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get favorite products")
        }
    }

    override suspend fun addFavoriteProduct(productId: String): Result<FavoriteResponse> {
        return try {
            val response = favoriteApi.addFavoriteProduct(productId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error(body!!.message ?: "Failed to add favorite product")
                }
            } else {
                Result.Error(response.message() ?: "Failed to add favorite product")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add favorite product")
        }
    }

    override suspend fun removeFavoriteProduct(productId: String): Result<Boolean> {
        return try {
            val response = favoriteApi.removeFavoriteProduct(productId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.Success(true)
                } else {
                    Result.Error(body?.message ?: "Failed to remove favorite product")
                }
            } else {
                Result.Error(response.message() ?: "Failed to remove favorite product")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove favorite product")
        }
    }

    override suspend fun checkFavoriteProduct(productId: String): Result<Boolean> {
        return try {
            val response = favoriteApi.checkFavoriteProduct(productId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.Success(body.data) // <- Dữ liệu thật
                } else {
                    Result.Error(body?.message ?: "Failed to check favorite product")
                }
            } else {
                Result.Error(response.message() ?: "Failed to check favorite product")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to check favorite product")
        }
    }
}