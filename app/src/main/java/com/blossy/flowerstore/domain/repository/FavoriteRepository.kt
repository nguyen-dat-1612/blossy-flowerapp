package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.data.remote.dto.FavoriteResponse
import com.blossy.flowerstore.domain.model.Product
import com.blossy.flowerstore.domain.utils.Result

interface FavoriteRepository {

    suspend fun getFavoriteProducts(): Result<List<Product>>
    suspend fun addFavoriteProduct(productId: String): Result<FavoriteResponse>
    suspend fun removeFavoriteProduct(productId: String): Result<Boolean>
    suspend fun checkFavoriteProduct(productId: String): Result<Boolean>
}