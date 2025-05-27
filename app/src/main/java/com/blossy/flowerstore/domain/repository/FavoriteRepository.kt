package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.domain.model.FavoriteModel
import com.blossy.flowerstore.domain.utils.Result

interface FavoriteRepository {

    suspend fun getFavoriteProducts(): Result<List<ProductModel>>
    suspend fun addFavoriteProduct(productId: String): Result<FavoriteModel>
    suspend fun removeFavoriteProduct(productId: String): Result<Boolean>
    suspend fun checkFavoriteProduct(productId: String): Result<Boolean>
}