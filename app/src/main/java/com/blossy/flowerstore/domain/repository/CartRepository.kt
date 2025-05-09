package com.blossy.flowerstore.domain.repository

import android.text.BoringLayout
import com.blossy.flowerstore.data.remote.dto.AddtoCartRequest
import com.blossy.flowerstore.data.remote.dto.CartResponse
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.domain.utils.Result

interface CartRepository {

    suspend fun getCart(): Result<List<CartItem>>

    suspend fun addToCart(productId: String, quantity: Int): Result<List<CartItem>>

    suspend fun removeCart(productId: String): Result<Boolean>

    suspend fun updateCart(productId: String, quantity: Int): Result<Boolean>

    suspend fun clearCart(): Result<Boolean>

}