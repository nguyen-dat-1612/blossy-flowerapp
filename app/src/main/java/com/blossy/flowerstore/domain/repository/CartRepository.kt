package com.blossy.flowerstore.domain.repository

import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.domain.model.request.AddToCartModel
import com.blossy.flowerstore.domain.model.request.UpdateCartModel
import com.blossy.flowerstore.domain.utils.Result

interface CartRepository {

    suspend fun getCart(): Result<List<CartItemModel>>

    suspend fun addToCart(addToCartModel: AddToCartModel): Result<List<CartItemModel>>

    suspend fun removeCart(productId: String): Result<Boolean>

    suspend fun updateCart(updateCartModel: UpdateCartModel): Result<Boolean>

    suspend fun clearCart(): Result<Boolean>

}